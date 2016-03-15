package pl.nkg.biblospk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.GlobalState;
import pl.nkg.biblospk.MyApplication;

public abstract class AbstractActivity extends AppCompatActivity {

    protected GlobalState mGlobalState;
    protected boolean mIsReloaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlobalState = ((MyApplication)getApplication()).getGlobalState();
        mIsReloaded = savedInstanceState != null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
