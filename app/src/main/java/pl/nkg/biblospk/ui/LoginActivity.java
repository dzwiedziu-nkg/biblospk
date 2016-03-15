package pl.nkg.biblospk.ui;

import android.os.Bundle;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.GlobalState;
import pl.nkg.biblospk.MyApplication;
import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;


public class LoginActivity extends AbstractActivity implements LoginFragment.OnFragmentInteractionListener {

    private final static String STATE_CLOSE_IF_LOGGED = "close";

    private LoginFragment mLoginFragment;
    private boolean mCloseIfLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            PreferencesProvider preferencesProvider = mGlobalState.getPreferencesProvider();
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
        mLoginFragment.setError(mGlobalState.getServiceStatus().getError());
        mLoginFragment.setRunning(mGlobalState.getServiceStatus().isRunning());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mCloseIfLogged && !mGlobalState.getServiceStatus().isRunning() && mGlobalState.getServiceStatus().getError() == null) {
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


    public void onEventMainThread(StatusUpdatedEvent event) {
        boolean running = event.getServiceStatus().isRunning();
        if (!running) {
            if (event.getServiceStatus().getError() == null) {
                mGlobalState.getPreferencesProvider().setPrefLogin(mLoginFragment.getLogin());
                mGlobalState.getPreferencesProvider().setPrefPassword(mLoginFragment.getPassword());
                finish();
            } else {
                mLoginFragment.setError(event.getServiceStatus().getError());
            }
        }
        mLoginFragment.setRunning(running);
    }
}
