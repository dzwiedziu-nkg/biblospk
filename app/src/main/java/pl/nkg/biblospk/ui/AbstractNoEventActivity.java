package pl.nkg.biblospk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import pl.nkg.biblospk.GlobalState;
import pl.nkg.biblospk.MyApplication;

/**
 * Created by nkg on 26.03.16.
 */
public class AbstractNoEventActivity extends AppCompatActivity {
    protected GlobalState mGlobalState;
    protected boolean mIsReloaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlobalState = ((MyApplication) getApplication()).getGlobalState();
        mIsReloaded = savedInstanceState != null;
    }
}
