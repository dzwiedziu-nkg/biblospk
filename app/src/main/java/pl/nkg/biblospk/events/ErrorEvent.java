package pl.nkg.biblospk.events;

public class ErrorEvent {
    private CharSequence mErrorMessage;

    public ErrorEvent() {
    }

    public ErrorEvent(CharSequence errorMessage) {
        mErrorMessage = errorMessage;
    }

    public CharSequence getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(CharSequence errorMessage) {
        mErrorMessage = errorMessage;
    }
}
