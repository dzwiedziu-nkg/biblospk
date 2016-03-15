package pl.nkg.biblospk;

import com.activeandroid.app.Application;

import pl.nkg.biblospk.client.WebClient;
import pl.nkg.biblospk.services.BiblosService;
import pl.nkg.biblospk.services.NotifyService;

public class MyApplication extends Application {

    private GlobalState mGlobalState;

    @Override
    public void onCreate() {
        super.onCreate();
        WebClient.initCookieHandler();
        mGlobalState = new GlobalState(new PreferencesProvider(this));

        BiblosService.startService(this, false, false);
        NotifyService.startService(this);
    }

    public GlobalState getGlobalState() {
        return mGlobalState;
    }
}
