package pl.nkg.biblospk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import pl.nkg.biblospk.data.Book;

public class DetailsActivity extends AbstractNoEventActivity implements DetailsFragment.OnFragmentInteractionListener {

    private static final String BOOK_ID = "book_id";

    public static void startActivity(Context context, Book book) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(BOOK_ID, book.getId());
        context.startActivity(intent);
    }

    private DetailsFragment mDetailsFragment;
    private Book mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mDetailsFragment = DetailsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mDetailsFragment)
                    .commit();
        } else {
            mDetailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }

        long bookId = getIntent().getLongExtra(BOOK_ID, -1);
        mBook = mGlobalState.getAccount().getById(bookId);

        if (mBook == null) {
            finish();
        } /*else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mBook.getTitle());
            }
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBook == null) {
            return;
        }

        mDetailsFragment.setBook(mBook);
    }

    @Override
    public void onProlong() {
        Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancelReservation() {
        Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG).show();
    }
}
