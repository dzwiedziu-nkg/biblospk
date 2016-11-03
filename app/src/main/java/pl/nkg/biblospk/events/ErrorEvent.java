package pl.nkg.biblospk.events;

public class ErrorEvent {
    private CharSequence mErrorMessage;
    private Throwable mException;
    private boolean mNeedContact;

    public ErrorEvent() {
    }

    public ErrorEvent(CharSequence errorMessage, Throwable exception, boolean needContact) {
        mErrorMessage = errorMessage;
        mException = exception;
        mNeedContact = needContact;
    }

    public CharSequence getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(CharSequence errorMessage) {
        mErrorMessage = errorMessage;
    }

    public Throwable getException() {
        return mException;
    }

    public void setException(Throwable exception) {
        mException = exception;
    }

    public boolean isNeedContact() {
        return mNeedContact;
    }

    public void setNeedContact(boolean needContact) {
        mNeedContact = needContact;
    }
}
