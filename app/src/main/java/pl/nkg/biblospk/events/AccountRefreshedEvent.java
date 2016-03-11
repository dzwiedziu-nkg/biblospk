package pl.nkg.biblospk.events;

import pl.nkg.biblospk.data.Account;

public class AccountRefreshedEvent {
    private Account mAccount;

    public AccountRefreshedEvent() {
    }

    public AccountRefreshedEvent(Account account) {
        mAccount = account;
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        mAccount = account;
    }
}
