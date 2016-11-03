package pl.nkg.biblospk.events;

public class ErrorEvent {
    private CharSequence mErrorMessage;
    private Throwable mException;

    public ErrorEvent() {
    }

    public ErrorEvent(CharSequence errorMessage, Throwable exception) {
        mErrorMessage = errorMessage;
        mException = exception;
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
}
