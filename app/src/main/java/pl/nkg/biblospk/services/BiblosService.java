package pl.nkg.biblospk.services;

import org.apache.commons.lang3.StringUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;

import de.greenrobot.event.EventBus;
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

    public BiblosService() {
        super("BiblosService");
    }

    public static void startService(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        PreferencesProvider preferencesProvider = application.getPreferencesProvider();
        startService(context, preferencesProvider.getPrefLogin(), preferencesProvider.getPrefPassword());
    }

    public static void startService(Context context, String login, String password) {

        MyApplication application = (MyApplication) context.getApplicationContext();
        if (application.getServiceStatus().isRunning()) {
            Log.w(TAG, "Service already running");
            return;
        }

        application.getServiceStatus().turnOn();

        Intent intent = new Intent(context, BiblosService.class);
        intent.putExtra(TAG_LOGIN, login);
        intent.putExtra(TAG_PASSWORD, password);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d("serci", "stareted");
        //SystemClock.sleep(30000);

        String login = intent.getStringExtra(TAG_LOGIN);
        String password = intent.getStringExtra(TAG_PASSWORD);

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            PreferencesProvider preferencesProvider = ((MyApplication)getApplication()).getPreferencesProvider();
            login = preferencesProvider.getPrefLogin();
            password = preferencesProvider.getPrefPassword();
        }

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
