package pl.nkg.biblospk.events;

import pl.nkg.biblospk.data.Account;

public class AccountDownloadedEvent {
    private Account mAccount;

    public AccountDownloadedEvent() {
    }

    public AccountDownloadedEvent(Account account) {
        mAccount = account;
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        mAccount = account;
    }
}
