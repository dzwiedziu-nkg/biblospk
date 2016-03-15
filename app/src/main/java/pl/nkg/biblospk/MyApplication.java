package pl.nkg.biblospk;

import com.activeandroid.app.Application;
import android.net.wifi.WifiConfiguration;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.client.WebClient;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.AccountRefreshedEvent;
import pl.nkg.biblospk.events.ErrorEvent;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;
import pl.nkg.biblospk.services.ServiceStatus;

public class MyApplication extends Application {

    private GlobalState mGlobalState;

    @Override
    public void onCreate() {
        super.onCreate();
        WebClient.initCookieHandler();
        mGlobalState = new GlobalState(new PreferencesProvider(this));

        BiblosService.startService(this, false, false);
    }

    public GlobalState getGlobalState() {
        return mGlobalState;
    }
}
