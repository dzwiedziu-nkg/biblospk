/*
TODO: prolong all expired books feature (from notify and activity)
TODO: select books to prolong in activity
TODO: hide notify today feature (come back notify on the next day)
TODO: working from external memory
TODO: settings activity: sticky notify, update duration, etc. (low priority)
TODO: icon on action bar in details activity
TODO: snack bar instead toast
TODO: backup agent
TODO: about activity (open when first run)

Debug TODOs:
TODO: switch Debug Mode on/off, when on mode then:
TODO: sandbox mode - prolong and cancellation as dry
TODO: set manually ToDay date
 */
package pl.nkg.biblospk;

import com.activeandroid.app.Application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.client.WebClient;
import pl.nkg.biblospk.events.CanceledEvent;
import pl.nkg.biblospk.events.RenewedEvent;
import pl.nkg.biblospk.events.WipeDataEvent;
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

        BiblosService.startServiceRefresh(this, false, false);
        NotifyService.startService(this);
        registerAlarm();

        EventBus.getDefault().register(this);
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

    public void onEventMainThread(RenewedEvent renewedEvent) {
        int textId = 0;

        if (renewedEvent.isAllRenewed()) {
            textId = renewedEvent.getRenewedList().size() == 1 ? R.string.toast_prolong : R.string.toast_prolong_all;
        } else if (renewedEvent.isNoneRenewed()) {
            textId = renewedEvent.getRenewsList().size() == 1 ? R.string.toast_prolong_not : R.string.toast_prolong_no;
        } else {
            textId = R.string.toast_prolong_some;
        }

        Toast.makeText(this, textId, Toast.LENGTH_LONG).show();
    }

    public void onEventMainThread(CanceledEvent canceledEvent) {
        Toast.makeText(this, canceledEvent.isSuccess() ? R.string.toast_cancel : R.string.toast_cancel_not, Toast.LENGTH_LONG).show();
    }

    public void onEventMainThread(WipeDataEvent wipeDataEvent) {
        mGlobalState.logout(false);
    }
}
