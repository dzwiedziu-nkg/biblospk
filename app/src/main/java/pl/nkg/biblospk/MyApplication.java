package pl.nkg.biblospk;

import com.activeandroid.app.Application;
import android.net.wifi.WifiConfiguration;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.client.WebClient;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.AccountRefreshedEvent;
import pl.nkg.biblospk.events.ErrorEvent;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;
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

        Account account = new Account();
        if (mPreferencesProvider.loadAccountProperties(account)) {
            account.loadBooksList();
            mAccount = account;
        }
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
        if (mAccount != null) {
            mPreferencesProvider.storeAccountProperties(mAccount);
            mAccount.storeBooksList();
        }
        mPreferencesProvider.setLastChecking(System.currentTimeMillis());
        mPreferencesProvider.setLastChecked(System.currentTimeMillis());

        BiblosService.startService(this, false, false);
    }

    public void onEventMainThread(ErrorEvent event) {
        if (event.getErrorMessage() != null) {
            mServiceStatus.setError(event.getErrorMessage());
            mPreferencesProvider.setLastChecking(System.currentTimeMillis());
        }
        mServiceStatus.turnOff();
    }
}
