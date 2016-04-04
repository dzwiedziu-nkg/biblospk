package pl.nkg.biblospk;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.AccountDownloadedEvent;
import pl.nkg.biblospk.events.ErrorEvent;
import pl.nkg.biblospk.services.ServiceStatus;

public class GlobalState {
    private Account mAccount;
    private ServiceStatus mServiceStatus;
    private PreferencesProvider mPreferencesProvider;
    private boolean mLogged = false;
    private long lastChangedTime = System.currentTimeMillis();

    public GlobalState(PreferencesProvider preferencesProvider) {
        mServiceStatus = new ServiceStatus();
        mPreferencesProvider = preferencesProvider;

        Account account = new Account();
        if (preferencesProvider.loadAccountProperties(account)) {
            account.loadBooksList();
            mAccount = account;
        }

        EventBus.getDefault().register(this);
    }


    public Account getAccount() {
        return mAccount;
    }

    public ServiceStatus getServiceStatus() {
        return mServiceStatus;
    }

    public PreferencesProvider getPreferencesProvider() {
        return mPreferencesProvider;
    }

    public long getLastChangedTime() {
        return lastChangedTime;
    }

    public void onEventMainThread(AccountDownloadedEvent event) {
        if (event.getAccount() != null && !event.getAccount().equalBookState(mAccount)) {
            lastChangedTime = System.currentTimeMillis();
        }

        mAccount = event.getAccount();
        mServiceStatus.setError(null);
        mServiceStatus.turnOff();
        if (mAccount != null) {
            mPreferencesProvider.storeAccountProperties(mAccount);
            mAccount.storeBooksList();
            mLogged = true;
        }
        mPreferencesProvider.setLastChecking(System.currentTimeMillis());
        mPreferencesProvider.setLastChecked(System.currentTimeMillis());
    }

    public void onEventMainThread(ErrorEvent event) {
        if (event.getErrorMessage() != null) {
            mServiceStatus.setError(event.getErrorMessage());
            mPreferencesProvider.setLastChecking(System.currentTimeMillis());
        }
        mServiceStatus.turnOff();
    }

    /**
     * Book list must be download because is too old.
     * @return true - must be, false - not
     */
    public boolean isBookListTooOld() {
        return DateUtils.isSameDay(new Date(), new Date(mPreferencesProvider.getLastChecked()));
    }

    /**
     * Book list must be download because never downloaded.
     * @return true - must be, false - not
     */
    public boolean isBookListDownloaded() {
        return mAccount != null;
    }

    /**
     * Book list must be download because never downloaded or is too old.
     * @return true - must be, false - not
     */
    public boolean isNeedToUpdate() {
        return !isBookListDownloaded() || isBookListTooOld();
    }

    /**
     * User is logged.
     * @return true - logged, false - no logged
     */
    public boolean isValidCredentials() {
        return mPreferencesProvider.containsLoginAndPassword();
    }

    public void logout(boolean full) {
        mPreferencesProvider.cleanAccountPropertiesAndCredentials(full);
        if (mAccount != null) {
            mAccount.wipeBookList();
        }
        mAccount = null;
    }

    public boolean isLogged() {
        return mLogged;
    }
}
