package pl.nkg.biblospk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import pl.nkg.biblospk.data.Book;

public class BookListFragment extends ListFragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OnFragmentInteractionListener mListener;
    private Boolean mRefreshing;
    private Book[] mBooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);
        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());
        mSwipeRefreshLayout.addView(listFragmentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                emitRefreshBookList();
            }
        });

        if (mBooks != null) {
            refreshList(mBooks, true);
        }

        if (mRefreshing != null) {
            setRefreshing(mRefreshing);
        }

        return mSwipeRefreshLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refreshList(Book[] books, boolean force) {

        if (mBooks != null && !force) {
            return;
        }

        mBooks = books;
        if (getActivity() != null) {
            setListAdapter(new BookListAdapter(getActivity(), books));
        }
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }

    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    public void setRefreshing(final boolean refreshing) {
        mRefreshing = refreshing;
        if (mSwipeRefreshLayout != null) {

            //workaround of the bug: https://code.google.com/p/android/issues/detail?id=77712#c10
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });

        }
    }

    public void setColorScheme(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
        mSwipeRefreshLayout.setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Book book = (Book) getListAdapter().getItem(position);
        emitListItemClick(book);
    }

    private void emitListItemClick(Book book) {
        if (mListener != null) {
            mListener.onListItemClick(book);
        }
    }

    private void emitRefreshBookList() {
        if (mListener != null) {
            mListener.onRefreshBookList(true);
        }
    }

    private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

        public ListFragmentSwipeRefreshLayout(Context context) {
            super(context);
        }

        @Override
        public boolean canChildScrollUp() {
            final ListView listView = getListView();
            return listView.getVisibility() == View.VISIBLE && canListViewScrollUp(listView);
        }
    }

    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }

    public interface OnFragmentInteractionListener {
        void onRefreshBookList(boolean force);

        void onListItemClick(Book book);
    }
}
