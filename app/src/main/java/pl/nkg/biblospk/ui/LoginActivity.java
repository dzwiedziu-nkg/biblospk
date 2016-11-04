package pl.nkg.biblospk.ui;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.R;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginClick(String login, String password) {
        BiblosService.startServiceRefresh(this, true, true, login, password);
    }

    @Override
    public void onRulesClick() {
        startActivity(new Intent(this, RulesActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginFragment.setError(mGlobalState.getServiceStatus().getError(), mGlobalState.getServiceStatus().getException());
        mLoginFragment.setRunning(mGlobalState.getServiceStatus().isRunning());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mGlobalState.isValidCredentials()) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StatusUpdatedEvent event) {
        boolean running = event.getServiceStatus().isRunning();
        if (!running) {
            if (event.getServiceStatus().getError() == null) {
                mGlobalState.getPreferencesProvider().setPrefLogin(mLoginFragment.getLogin());
                mGlobalState.getPreferencesProvider().setPrefPassword(mLoginFragment.getPassword());
                finish();
            } else {
                Throwable exception = mGlobalState.getServiceStatus().getException();
                CharSequence errorMessage = event.getServiceStatus().getError();
                mLoginFragment.setError(errorMessage, exception);

                if (exception != null && event.getServiceStatus().isNeedContact()) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    exception.printStackTrace(pw);

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"nkg753@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "[BiblosPK] error");
                    i.putExtra(Intent.EXTRA_TEXT, errorMessage + "\r\n" + exception.getMessage() + "\r\n" + exception.getLocalizedMessage() + "\r\n\r\n" + sw.getBuffer().toString());
                    try {
                        startActivity(Intent.createChooser(i, errorMessage));
                    } catch (android.content.ActivityNotFoundException ex) {

                    }
                    try {
                        sw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        mLoginFragment.setRunning(running);
    }
}
