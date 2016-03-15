package pl.nkg.biblospk.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import pl.nkg.biblospk.services.BiblosService;
import pl.nkg.biblospk.services.NotifyService;

public class BootReceiver extends BroadcastReceiver {
    private static final int PERIOD = 3 * 60 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60000,
                PERIOD,
                pi);
    }
}
