package pl.nkg.biblospk.services;

import org.greenrobot.eventbus.EventBus;

import pl.nkg.biblospk.events.StatusUpdatedEvent;

public class ServiceStatus {
    private boolean mRunning = false;
    private CharSequence mError = null;
    private Throwable mException = null;
    private boolean mNeedContact;
    private long mTimeChanged = System.currentTimeMillis();

    public boolean isRunning() {
        return mRunning;
    }

    public CharSequence getError() {
        return mError;
    }

    public Throwable getException() {
        return mException;
    }

    public boolean isNeedContact() {
        return mNeedContact;
    }

    public long getTimeChanged() {
        return mTimeChanged;
    }

    public synchronized void turnOn() {
        mRunning = true;
        updateTimeChanged();
        emitStatusUpdatedEvent();
    }

    public synchronized void turnOff() {
        mRunning = false;
        updateTimeChanged();
        emitStatusUpdatedEvent();
    }

    public void setError(CharSequence error, Throwable exception, boolean needContact) {
        mError = error;
        mException = exception;
        mNeedContact = needContact;
        updateTimeChanged();
    }

    private synchronized void updateTimeChanged() {
        mTimeChanged = System.currentTimeMillis();
    }

    private void emitStatusUpdatedEvent() {
        EventBus.getDefault().post(new StatusUpdatedEvent(this));
    }
}
