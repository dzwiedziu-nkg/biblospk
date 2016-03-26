package pl.nkg.biblospk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.nkg.biblospk.Utils;
import pl.nkg.biblospk.services.BiblosService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BiblosService.startServiceRefresh(context, Utils.checkWiFi(context), true);
    }
}
