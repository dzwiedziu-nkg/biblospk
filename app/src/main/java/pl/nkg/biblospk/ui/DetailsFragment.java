package pl.nkg.biblospk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.data.Book;

public class DetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Book mBook;

    @Bind(R.id.details_list)
    LinearLayout mDetailsListLayout;
    @Bind(R.id.prolongButton)
    Button mProlongButton;
    @Bind(R.id.cancelReservationButton)
    Button mCancelReservationButton;

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

    private void updateView() {
        mDetailsListLayout.removeAllViews();
        mDetailsListLayout.addView(makeDetailItem(R.string.label_title, mBook.getTitle()));
        mDetailsListLayout.addView(makeDetailItem(R.string.label_author, mBook.getAuthors()));
    }

    private RelativeLayout makeDetailItem(int nameId, CharSequence value) {
        RelativeLayout layout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.item_details, null, false);
        TextView nameView = ButterKnife.findById(layout, R.id.name);
        nameView.setText(getResources().getString(nameId));
        TextView valueView = ButterKnife.findById(layout, R.id.value);
        valueView.setText(value);
        return layout;
    }

    public interface OnFragmentInteractionListener {
        void onProlong();

        void onCancelReservation();
    }
}
