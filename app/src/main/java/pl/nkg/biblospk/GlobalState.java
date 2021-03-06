package pl.nkg.biblospk;

import org.acra.ACRA;
import org.apache.commons.lang3.time.DateUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.AccountDownloadedEvent;
import pl.nkg.biblospk.events.ErrorEvent;
import pl.nkg.biblospk.events.UpdateNotifyEvent;
import pl.nkg.biblospk.services.ServiceStatus;

public class GlobalState {
    public static final long NEED_REFRESH_WIFI = 30 * 60 * 1000;

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AccountDownloadedEvent event) {
        mLogged = event.getAccount() != null;

        if (event.getAccount() != null && !event.getAccount().equalBookState(mAccount)) {
            lastChangedTime = System.currentTimeMillis();

            mAccount = event.getAccount();
            if (mAccount != null) {
                mPreferencesProvider.storeAccountProperties(mAccount);
                mAccount.storeBooksList();
            }
        }

        mPreferencesProvider.setLastChecking(System.currentTimeMillis());
        mPreferencesProvider.setLastChecked(System.currentTimeMillis());

        mServiceStatus.setError(null, null, false);
        mServiceStatus.turnOff();
        EventBus.getDefault().post(new UpdateNotifyEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ErrorEvent event) {
        if (event.getErrorMessage() != null) {
            mServiceStatus.setError(event.getErrorMessage(), event.getException(), event.isNeedContact());
            mPreferencesProvider.setLastChecking(System.currentTimeMillis());
        }
        mServiceStatus.turnOff();

        if (ACRA.isInitialised()) {
            if (event.getException() != null) {
                ACRA.getErrorReporter().handleException(event.getException());
            }
        }
    }

    /**
     * Book list must be download because is too old.
     * @param hiPriority true - on WiFi connection
     * @return true - must be, false - not
     */
    public boolean isBookListTooOld(boolean hiPriority) {
        return !DateUtils.isSameDay(new Date(), new Date(mPreferencesProvider.getLastChecked())) || (hiPriority && mPreferencesProvider.getLastChecked() + NEED_REFRESH_WIFI < System.currentTimeMillis());
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
    public boolean isNeedToUpdate(boolean hiPriority) {
        return !isBookListDownloaded() || isBookListTooOld(hiPriority);
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
        mLogged = false;
        EventBus.getDefault().post(new AccountDownloadedEvent());
    }

    public boolean isLogged() {
        return mLogged;
    }

    public Date getCurrentDay() {
        return new Date();
    }
}
