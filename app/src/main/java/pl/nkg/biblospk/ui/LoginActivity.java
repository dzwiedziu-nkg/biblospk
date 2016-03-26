package pl.nkg.biblospk.ui;

import android.os.Bundle;

import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;


public class LoginActivity extends AbstractActivity implements LoginFragment.OnFragmentInteractionListener {

    private LoginFragment mLoginFragment;

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
        BiblosService.startServiceRefresh(this, true, true, login, password);
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
        if (mGlobalState.isValidCredentials()) {
            finish();
        }
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
