package pl.nkg.biblospk.services;

import org.apache.commons.lang3.StringUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;

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

public class BiblosService extends IntentService {

    private final static String TAG = BiblosService.class.getSimpleName();
    private final static String TAG_LOGIN = "login";
    private final static String TAG_PASSWORD = "password";
    private final static String TAG_FORCE = "force";
    private final static String TAG_QUIET = "quiet";

    public BiblosService() {
        super("BiblosService");
    }

    public static void startService(Context context, boolean force, boolean quiet) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        PreferencesProvider preferencesProvider = application.getGlobalState().getPreferencesProvider();
        startService(context, force, quiet, preferencesProvider.getPrefLogin(), preferencesProvider.getPrefPassword());
    }

    public static void startService(Context context, boolean force, boolean quiet, String login, String password) {

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
        intent.putExtra(TAG_LOGIN, login);
        intent.putExtra(TAG_PASSWORD, password);
        intent.putExtra(TAG_FORCE, force);
        intent.putExtra(TAG_QUIET, quiet);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String login = intent.getStringExtra(TAG_LOGIN);
        String password = intent.getStringExtra(TAG_PASSWORD);

        try {
            Account account = BiblosClient.loginAndFetchAccount(login, password);
            emitAccountDownloaded(account);
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

    private void emitAccountDownloaded(Account account) {
        Log.d(TAG, "Book list download finish");
        EventBus.getDefault().post(new AccountDownloadedEvent(account));
    }

    private void emitError(CharSequence errorMessage) {
        Log.d(TAG, "Book list download error: " + errorMessage);
        EventBus.getDefault().post(new ErrorEvent(errorMessage));
    }
}
