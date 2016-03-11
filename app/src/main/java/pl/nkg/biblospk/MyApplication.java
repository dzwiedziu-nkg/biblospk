package pl.nkg.biblospk;

import android.app.Application;
import android.net.wifi.WifiConfiguration;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.client.WebClient;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.AccountRefreshedEvent;
import pl.nkg.biblospk.events.ErrorEvent;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.ServiceStatus;

public class MyApplication extends Application {

    private PreferencesProvider mPreferencesProvider;
    private Account mAccount;
    private ServiceStatus mServiceStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        WebClient.initCookieHandler();
        mPreferencesProvider = new PreferencesProvider(this);
        mServiceStatus = new ServiceStatus();
        EventBus.getDefault().register(this);
    }

    public PreferencesProvider getPreferencesProvider() {
        return mPreferencesProvider;
    }

    public Account getAccount() {
        return mAccount;
    }

    public ServiceStatus getServiceStatus() {
        return mServiceStatus;
    }

    public void onEventMainThread(AccountRefreshedEvent event) {
        mAccount = event.getAccount();
        mServiceStatus.setError(null);
        mServiceStatus.turnOff();
    }

    public void onEventMainThread(ErrorEvent event) {
        mServiceStatus.setError(event.getErrorMessage());
        mServiceStatus.turnOff();
    }
}
