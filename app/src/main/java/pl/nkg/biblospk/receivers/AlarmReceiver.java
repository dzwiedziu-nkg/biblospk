package pl.nkg.biblospk.receivers;

import org.greenrobot.eventbus.EventBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.nkg.biblospk.events.UpdateNotifyEvent;
import pl.nkg.biblospk.services.BiblosService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BiblosService.startServiceRefresh(context, false, true);
        EventBus.getDefault().post(new UpdateNotifyEvent());
    }
}
