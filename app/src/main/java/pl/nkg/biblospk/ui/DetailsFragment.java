package pl.nkg.biblospk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.data.Book;

public class DetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Book mBook;

    @Bind(R.id.details_list)
    LinearLayout mDetailsListLayout;
    @Bind(R.id.btn_prolong)
    Button mProlongButton;
    @Bind(R.id.btn_cancel_reservation)
    Button mCancelReservationButton;
    @Bind(R.id.titleTextView)
    TextView mTitleTextView;
    @Bind(R.id.authorsTextView)
    TextView mAuthorsTextView;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    public void setBook(Book book) {
        this.mBook = book;
        updateView();
    }

    @OnClick(R.id.btn_prolong)
    public void onProlongClick() {
        emitProlong();
    }

    @OnClick(R.id.btn_cancel_reservation)
    public void onCancelReservationClick() {
        emitCancelReservation();
    }

    private void updateView() {
        int priority = mBook.checkBookPriority(new Date());
        int priorityColor;
        switch (priority) {
            case 0:
                priorityColor = ContextCompat.getColor(getContext(), R.color.colorGood);
                break;

            case 1:
                priorityColor = ContextCompat.getColor(getContext(), R.color.colorInfo);
                break;

            case 2:
                priorityColor = ContextCompat.getColor(getContext(), R.color.colorWarning);
                break;

            default:
                priorityColor = ContextCompat.getColor(getContext(), R.color.colorError);
        }

        int statusColor = 0;
        switch (mBook.getCategory()) {
            case Book.CATEGORY_LEND:
                statusColor = ContextCompat.getColor(getContext(), R.color.colorInfo);
                break;

            case Book.CATEGORY_WAITING:
                statusColor = ContextCompat.getColor(getContext(), R.color.colorGood);
                break;

            case Book.CATEGORY_BOOKED:
                statusColor = ContextCompat.getColor(getContext(), R.color.colorWarning);
                break;
        }

        mDetailsListLayout.removeAllViews();
        //mDetailsListLayout.addView(makeDetailItem(R.string.label_title, mBook.getTitle()));
        //mDetailsListLayout.addView(makeDetailItem(R.string.label_author, mBook.getAuthors()));
        mTitleTextView.setText(mBook.getTitle());
        mAuthorsTextView.setText(mBook.getAuthors());

        if (mBook.getCategory() == Book.CATEGORY_LEND) {
            mDetailsListLayout.addView(makeDetailItem(R.string.label_signature, mBook.getSignature()));
            mDetailsListLayout.addView(makeDetailItem(R.string.label_due_date, Book.DUE_DATE_FORMAT.format(mBook.getDueDate()), priorityColor));

            if (mBook.getAvailableProlongs() > 0) {
                mDetailsListLayout.addView(makeDetailItem(R.string.label_prolongs, getString(R.string.value_prolongs, mBook.getAvailableProlongs(), mBook.getAllProlongs())));
            } else {
                mDetailsListLayout.addView(makeDetailItem(R.string.label_prolongs, getString(R.string.value_prolongs_0), ContextCompat.getColor(getContext(), R.color.colorError)));
            }

            mCancelReservationButton.setVisibility(View.GONE);
            mProlongButton.setVisibility(View.VISIBLE);
            mProlongButton.setEnabled(mBook.getAvailableProlongs() > 0);
        }

        if (mBook.getCategory() == Book.CATEGORY_BOOKED || mBook.getCategory() == Book.CATEGORY_WAITING) {
            mDetailsListLayout.addView(makeDetailItem(R.string.label_request_date, Book.DUE_DATE_FORMAT.format(mBook.getRequestDate())));

            if (mBook.getCategory() == Book.CATEGORY_WAITING) {
                mDetailsListLayout.addView(makeDetailItem(R.string.label_expire_date, Book.DUE_DATE_FORMAT.format(mBook.getDueDate())));
                mCancelReservationButton.setVisibility(View.GONE);
            }

            mDetailsListLayout.addView(makeDetailItem(R.string.label_rental, mBook.getRental()));

            mProlongButton.setVisibility(View.GONE);
        }

        if (mBook.getCategory() == Book.CATEGORY_BOOKED) {
            mDetailsListLayout.addView(makeDetailItem(R.string.label_queue, getString(R.string.value_queue, mBook.getQueue())));
            mCancelReservationButton.setVisibility(View.VISIBLE);
        }

        mDetailsListLayout.addView(makeDetailItem(R.string.label_status, getResources().getStringArray(R.array.array_status)[mBook.getCategory()], statusColor));
    }

    private RelativeLayout makeDetailItem(int nameId, CharSequence value) {
        return makeDetailItem(nameId, value, null);
    }

    private RelativeLayout makeDetailItem(int nameId, CharSequence value, Integer color) {
        RelativeLayout layout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.item_details, null, false);
        TextView nameView = ButterKnife.findById(layout, R.id.name);
        nameView.setText(getString(nameId));
        TextView valueView = ButterKnife.findById(layout, R.id.value);
        valueView.setText(value);

        if (color != null) {
            valueView.setTextColor(color);
        }

        return layout;
    }

    private void emitProlong() {
        if (mListener != null) {
            mListener.onProlong();
        }
    }

    private void emitCancelReservation() {
        if (mListener != null) {
            mListener.onCancelReservation();
        }
    }

    public interface OnFragmentInteractionListener {
        void onProlong();

        void onCancelReservation();
    }
}
