package pl.nkg.biblospk.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.GlobalState;
import pl.nkg.biblospk.MyApplication;
import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.client.BiblosClient;
import pl.nkg.biblospk.client.InvalidCredentialsException;
import pl.nkg.biblospk.client.ServerErrorException;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.AccountRefreshedEvent;
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
        PreferencesProvider preferencesProvider = globalState.getPreferencesProvider();

        if (globalState.getServiceStatus().isRunning()) {
            Log.w(TAG, "Service already running");
            return;
        }

        if (!force && DateUtils.isSameDay(new Date(), new Date(preferencesProvider.getLastChecked()))) {
            return;
        }

        if (!force && (StringUtils.isEmpty(login) || StringUtils.isEmpty(password))) {
            return;
        }

        Log.d(TAG, "Run service...");

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
        //boolean quiet = intent.getBooleanExtra(TAG_QUIET, false);

        //PreferencesProvider preferencesProvider = ((MyApplication)getApplication()).getPreferencesProvider();

        /*if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            login = preferencesProvider.getPrefLogin();
            password = preferencesProvider.getPrefPassword();
        }*/

        if (StringUtils.isEmpty(login)) {
            emitError(getText(R.string.error_empty_login));
            return;
        }

        if (StringUtils.isEmpty(password)) {
            emitError(getText(R.string.error_empty_password));
            return;
        }

        try {
            Account account = BiblosClient.loginAndFetchAccount(login, password);
            emitAccountUpdated(account);
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

    private void emitAccountUpdated(Account account) {
        EventBus.getDefault().post(new AccountRefreshedEvent(account));
    }

    private void emitError(CharSequence errorMessage) {
        EventBus.getDefault().post(new ErrorEvent(errorMessage));
    }
}
