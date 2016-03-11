package pl.nkg.biblospk.services;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.events.StatusUpdatedEvent;

public class ServiceStatus {
    private boolean mRunning = false;
    private CharSequence mError = null;
    private long mTimeChanged = System.currentTimeMillis();

    public boolean isRunning() {
        return mRunning;
    }

    public CharSequence getError() {
        return mError;
    }

    public long getTimeChanged() {
        return mTimeChanged;
    }

    public void turnOn() {
        mRunning = true;
        updateTimeChanged();
        emitStatusUpdatedEvent();
    }

    public void turnOff() {
        mRunning = false;
        updateTimeChanged();
        emitStatusUpdatedEvent();
    }

    public void setError(CharSequence error) {
        mError = error;
        updateTimeChanged();
    }

    private void updateTimeChanged() {
        mTimeChanged = System.currentTimeMillis();
    }

    private void emitStatusUpdatedEvent() {
        EventBus.getDefault().post(new StatusUpdatedEvent(this));
    }
}
