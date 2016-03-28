package pl.nkg.biblospk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.Utils;
import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.data.Book;
import pl.nkg.biblospk.events.StatusUpdatedEvent;
import pl.nkg.biblospk.services.BiblosService;

public class MainActivity extends AbstractActivity implements BookListFragment.OnFragmentInteractionListener {

    private static final int RESULT_LOGIN = 1;

    @Bind(R.id.tab_layout) TabLayout mTabLayout;

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private TabLayout.Tab mLendTab;
    private TabLayout.Tab mWaitingTab;
    private TabLayout.Tab mBookedTab;
    private boolean mFirstResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFirstResume = true;

        mLendTab = mTabLayout.newTab();
        mWaitingTab = mTabLayout.newTab();
        mBookedTab = mTabLayout.newTab();

        mTabLayout.addTab(mLendTab);
        mTabLayout.addTab(mWaitingTab);
        mTabLayout.addTab(mBookedTab);

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new PagerAdapter
                (getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            private boolean mRefreshing;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                refreshList(true);
                getBookListFragment(tab.getPosition()).setRefreshing(mRefreshing);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mRefreshing = getBookListFragment(tab.getPosition()).isRefreshing();
                getBookListFragment(tab.getPosition()).setRefreshing(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshList(false);
            }
        });
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
                getCurrentPageFragment().setRefreshing(true);
                onRefreshBookList(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Utils.checkWiFi(this)) {
            BiblosService.startServiceRefresh(this, true, true);
        }
    }

    @Override
    public void onRefreshBookList(boolean force) {
        BiblosService.startServiceRefresh(this, force, true);
    }

    @Override
    public void onListItemClick(Book book) {
        DetailsActivity.startActivity(this, book);
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

        if (mFirstResume) {
            refreshList(false);
            mFirstResume = false;
        }

        getCurrentPageFragment().setRefreshing(mGlobalState.getServiceStatus().isRunning());

        if (!mGlobalState.isValidCredentials() && !mIsReloaded) {
            showLoginActivity();
        }
    }

    private BookListFragment getCurrentPageFragment() {
        return getBookListFragment(mViewPager.getCurrentItem());
    }

    private BookListFragment getBookListFragment(int pos) {
        return (BookListFragment) mAdapter.instantiateItem(mViewPager, pos);
    }

    private List<Book> getBooksByTab() {
        Account account = mGlobalState.getAccount();

        switch (mViewPager.getCurrentItem()) {
            case 0:
                return account.getBooks(true, false, false);

            case 1:
                return account.getBooks(false, true, false);

            case 2:
                return account.getBooks(false, false, true);
        }

        return null;
    }

    private void refreshList(boolean force) {
        if (mGlobalState.isBookListDownloaded()) {
            getCurrentPageFragment().refreshList(Account.getSortedBookArray(getBooksByTab(), new Date()), true);
            double due = mGlobalState.getAccount().getDebts();
            String title = getResources().getString(R.string.title_activity_main_with_cash, due);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }

            mLendTab.setText(getResources().getString(R.string.tab_lend, mGlobalState.getAccount().getStats(0)));
            mWaitingTab.setText(getResources().getString(R.string.tab_waiting, mGlobalState.getAccount().getStats(1)));
            mBookedTab.setText(getResources().getString(R.string.tab_booked, mGlobalState.getAccount().getStats(2)));
        } else {
            mLendTab.setText(getText(R.string.tab_lend_0));
            mWaitingTab.setText(getText(R.string.tab_waiting_0));
            mBookedTab.setText(getText(R.string.tab_booked_0));
        }
        onRefreshBookList(false);
    }

    public void onEventMainThread(StatusUpdatedEvent event) {
        boolean running = event.getServiceStatus().isRunning();
        getCurrentPageFragment().setRefreshing(running);
        if (!running) {
            if (event.getServiceStatus().getError() != null) {
                Toast.makeText(this, event.getServiceStatus().getError(), Toast.LENGTH_LONG).show();
            } else {
                refreshList(true);
            }
        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 3) {
                return new BookListFragment();
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
