package pl.nkg.biblospk.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.MyApplication;
import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.client.BiblosClient;
import pl.nkg.biblospk.client.InvalidCredentialsException;
import pl.nkg.biblospk.events.AccountRefreshedEvent;
import pl.nkg.biblospk.events.ErrorEvent;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;


public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {

    private final static String STATE_CLOSE_IF_LOGGED = "close";

    private LoginFragment mLoginFragment;
    private MyApplication mApplication;
    private boolean mCloseIfLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (MyApplication) getApplication();

        if (savedInstanceState == null) {
            PreferencesProvider preferencesProvider = ((MyApplication)getApplication()).getPreferencesProvider();
            mLoginFragment =  LoginFragment.newInstance(preferencesProvider.getPrefLogin(), preferencesProvider.getPrefPassword());
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mLoginFragment)
                    .commit();
        } else {
            mLoginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    @Override
    public void onLoginClick(String login, String password) {
        mCloseIfLogged = true;
        BiblosService.startService(this, true, true, login, password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginFragment.setError(mApplication.getServiceStatus().getError());
        mLoginFragment.setRunning(mApplication.getServiceStatus().isRunning());
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mCloseIfLogged && !mApplication.getServiceStatus().isRunning() && mApplication.getServiceStatus().getError() == null) {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_CLOSE_IF_LOGGED, mCloseIfLogged);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCloseIfLogged = savedInstanceState.getBoolean(STATE_CLOSE_IF_LOGGED, false);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onEventMainThread(StatusUpdatedEvent event) {
        boolean running = event.getServiceStatus().isRunning();
        mLoginFragment.setRunning(running);
        if (!running) {
            if (event.getServiceStatus().getError() == null) {
                mApplication.getPreferencesProvider().setPrefLogin(mLoginFragment.getLogin());
                mApplication.getPreferencesProvider().setPrefPassword(mLoginFragment.getPassword());
                finish();
            } else {
                mLoginFragment.setError(event.getServiceStatus().getError());
            }
        }
    }
}
