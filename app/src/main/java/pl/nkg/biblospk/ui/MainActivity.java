package pl.nkg.biblospk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

import pl.nkg.biblospk.R;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;

public class MainActivity extends AbstractActivity implements BookListFragment.OnFragmentInteractionListener {

    private static final int RESULT_LOGIN = 1;

    private BookListFragment mBookListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mBookListFragment =  new BookListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mBookListFragment)
                    .commit();
        } else {
            mBookListFragment = (BookListFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                mGlobalState.logout();
                showLoginActivity();
                return true;

            case R.id.action_refresh:
                mBookListFragment.setRefreshing(true);
                onRefreshBookList(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefreshBookList(boolean force) {
        BiblosService.startService(this, force, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshList();
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, RESULT_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOGIN) {
            if (!mGlobalState.isValidCredentials()) {
                finish();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!mGlobalState.isValidCredentials() && !mIsReloaded) {
            showLoginActivity();
        }
    }

    private void refreshList() {
        if (mGlobalState.isBookListDownloaded()) {
            Account account = mGlobalState.getAccount();
            mBookListFragment.refreshList(Account.getSortedBookArray(account.getBooks(true), new Date()));
            double due = mGlobalState.getAccount().getDebts();
            String title = getResources().getString(R.string.title_activity_main_with_cash, due);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
        onRefreshBookList(false);
    }

    public void onEventMainThread(StatusUpdatedEvent event) {
        boolean running = event.getServiceStatus().isRunning();
        mBookListFragment.setRefreshing(running);
        if (!running) {
            if (event.getServiceStatus().getError() != null) {
                Toast.makeText(this, event.getServiceStatus().getError(), Toast.LENGTH_LONG).show();
            } else {
                refreshList();
            }
        }
    }
}
