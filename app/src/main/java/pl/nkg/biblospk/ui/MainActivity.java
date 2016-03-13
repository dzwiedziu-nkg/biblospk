package pl.nkg.biblospk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.MyApplication;
import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.data.Book;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;

public class MainActivity extends AppCompatActivity implements BookListFragment.OnFragmentInteractionListener {

    private MyApplication mApplication;
    private BookListFragment mBookListFragment;
    private boolean mNeedToRefreshOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (MyApplication) getApplication();
        //setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mNeedToRefreshOnResume = true;
            PreferencesProvider preferencesProvider = ((MyApplication)getApplication()).getPreferencesProvider();
            mBookListFragment =  new BookListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mBookListFragment)
                    .commit();
        } else {
            mNeedToRefreshOnResume = false;
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
        Intent intent;

        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_refresh:
                mBookListFragment.setRefreshing(true);
                onRefreshBookList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mNeedToRefreshOnResume) {
            mNeedToRefreshOnResume = false;
            onRefreshBookList();
        }
    }

    @Override
    public void onRefreshBookList() {
        BiblosService.startService(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        refreshList();
    }

    private void refreshList() {
        Account account = mApplication.getAccount();
        if (account == null) {
            mNeedToRefreshOnResume = true;
        } else {
            mBookListFragment.refreshList(account.getBookList());
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
