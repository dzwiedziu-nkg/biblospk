package pl.nkg.biblospk;

import com.activeandroid.app.Application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import pl.nkg.biblospk.client.WebClient;
import pl.nkg.biblospk.receivers.AlarmReceiver;
import pl.nkg.biblospk.services.BiblosService;
import pl.nkg.biblospk.services.NotifyService;

public class MyApplication extends Application {

    private static final int PERIOD = 30 * 60 * 1000;

    private GlobalState mGlobalState;
    private boolean mAlarmRegistered = false;

    @Override
    public void onCreate() {
        super.onCreate();
        WebClient.initCookieHandler();
        mGlobalState = new GlobalState(new PreferencesProvider(this));

        BiblosService.startService(this, false, false);
        NotifyService.startService(this);
        registerAlarm();
    }

    public GlobalState getGlobalState() {
        return mGlobalState;
    }

    public void registerAlarm() {
        if (mAlarmRegistered) {
            return;
        }

        mAlarmRegistered = true;

        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);

        AlarmReceiver receiver = new AlarmReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_USER_PRESENT));

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60000,
                PERIOD,
                pi);
    }
}
