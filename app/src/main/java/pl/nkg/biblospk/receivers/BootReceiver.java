package pl.nkg.biblospk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.nkg.biblospk.MyApplication;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ((MyApplication) context.getApplicationContext()).registerAlarm();
    }
}
