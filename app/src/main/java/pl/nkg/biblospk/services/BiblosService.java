package pl.nkg.biblospk.services;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.GlobalState;
import pl.nkg.biblospk.MyApplication;
import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.client.BiblosClient;
import pl.nkg.biblospk.client.InvalidCredentialsException;
import pl.nkg.biblospk.client.ServerErrorException;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.AccountDownloadedEvent;
import pl.nkg.biblospk.events.ErrorEvent;
import pl.nkg.biblospk.events.RenewedEvent;

public class BiblosService extends IntentService {

    private final static String TAG = BiblosService.class.getSimpleName();
    private final static String TAG_ACTION = "action";
    private final static String TAG_LOGIN = "login";
    private final static String TAG_PASSWORD = "password";
    private final static String TAG_FORCE = "force";
    private final static String TAG_QUIET = "quiet";
    private final static String TAG_RENEW = "renew";

    private final static int ACTION_REFRESH = 0;
    private final static int ACTION_RENEW = 1;
    private final static int ACTION_CANCEL_RESERVATION = 2;

    public BiblosService() {
        super("BiblosService");
    }

    public static void startServiceRefresh(Context context, boolean force, boolean quiet) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        PreferencesProvider preferencesProvider = application.getGlobalState().getPreferencesProvider();
        startServiceRefresh(context, force, quiet, preferencesProvider.getPrefLogin(), preferencesProvider.getPrefPassword());
    }

    public static void startServiceRefresh(Context context, boolean force, boolean quiet, String login, String password) {

        GlobalState globalState = ((MyApplication) context.getApplicationContext()).getGlobalState();

        if (globalState.getServiceStatus().isRunning()) {
            Log.w(TAG, "Service already running");
            return;
        }

        if (!force && globalState.isNeedToUpdate()) {
            return;
        }

        if (!force && (StringUtils.isEmpty(login) || StringUtils.isEmpty(password))) {
            return;
        }

        Log.d(TAG, "Run service for download list of books");

        globalState.getServiceStatus().turnOn();

        Intent intent = new Intent(context, BiblosService.class);
        intent.putExtra(TAG_ACTION, ACTION_REFRESH);
        intent.putExtra(TAG_LOGIN, login);
        intent.putExtra(TAG_PASSWORD, password);
        intent.putExtra(TAG_FORCE, force);
        intent.putExtra(TAG_QUIET, quiet);
        context.startService(intent);
    }

    public static void startServiceRenew(Context context, List<Integer> renews) {

        GlobalState globalState = ((MyApplication) context.getApplicationContext()).getGlobalState();

        Log.d(TAG, "Run service for renew list of books");

        globalState.getServiceStatus().turnOn();

        Intent intent = new Intent(context, BiblosService.class);
        intent.putExtra(TAG_ACTION, ACTION_RENEW);
        intent.putExtra(TAG_RENEW, ArrayUtils.toPrimitive(renews.toArray(new Integer[renews.size()])));
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int action = intent.getIntExtra(TAG_ACTION, ACTION_REFRESH);

        try {
            switch (action) {
                case ACTION_REFRESH:
                    doLoginAndRefresh(intent);
                    break;

                case ACTION_RENEW:
                    doRenew(intent);
                    break;

                case ACTION_CANCEL_RESERVATION:
                    doCancelReservation(intent);
                    break;
            }
        } catch (IOException e) {
            emitError(getText(R.string.error_connection));
        } catch (ParseException e) {
            emitError(getText(R.string.error_parse));
        } catch (InvalidCredentialsException e) {
            emitError(getText(R.string.error_invalid_credentials));
        } catch (ServerErrorException e) {
            emitError(getText(R.string.error_server));
        } catch (Exception e) {
            Log.e(TAG, "Undefined error", e);
            emitError(getText(R.string.error_undefined));
        }
    }

    private void doRenew(Intent intent) throws ServerErrorException, InvalidCredentialsException, ParseException, IOException {
        GlobalState globalState = ((MyApplication) getApplication()).getGlobalState();

        String login = globalState.getPreferencesProvider().getPrefLogin();
        String password = globalState.getPreferencesProvider().getPrefPassword();
        if (!globalState.isLogged()) {
            BiblosClient.login(login, password);
        }

        List<Integer> renews = Arrays.asList(ArrayUtils.toObject(intent.getIntArrayExtra(TAG_RENEW)));
        List<Integer> renewed = BiblosClient.prolongBooks(globalState.getAccount().getBorrowerNumber(), renews);

        if (renewed == null) {
            emitError(getText(R.string.error_server));
        } else {
            emitRenewed(renews, renewed);
            doLoginAndRefresh(login, password);
        }
    }

    private void doCancelReservation(Intent intent) {
    }

    private void doLoginAndRefresh(Intent intent) throws IOException, ParseException, InvalidCredentialsException, ServerErrorException {
        String login = intent.getStringExtra(TAG_LOGIN);
        String password = intent.getStringExtra(TAG_PASSWORD);
        doLoginAndRefresh(login, password);
    }

    private void doLoginAndRefresh(String login, String password) throws IOException, ParseException, InvalidCredentialsException, ServerErrorException {
        Account account = BiblosClient.loginAndFetchAccount(login, password);
        emitAccountDownloaded(account);
    }

    private void emitAccountDownloaded(Account account) {
        Log.d(TAG, "Book list download finish");
        EventBus.getDefault().post(new AccountDownloadedEvent(account));
    }

    private void emitRenewed(List<Integer> renews, List<Integer> renewed) {
        Log.d(TAG, "Book renews finish");
        EventBus.getDefault().post(new RenewedEvent(renews, renewed));
    }

    private void emitError(CharSequence errorMessage) {
        Log.d(TAG, "Book list download error: " + errorMessage);
        EventBus.getDefault().post(new ErrorEvent(errorMessage));
    }
}
