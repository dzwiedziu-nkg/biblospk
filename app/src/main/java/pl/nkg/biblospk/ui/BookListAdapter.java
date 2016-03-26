package pl.nkg.biblospk.ui;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import pl.nkg.biblospk.R;
import pl.nkg.biblospk.data.Book;

public class BookListAdapter extends ArrayAdapter<Book> {

    private final Activity context;
    //private final Book[] values;

    public BookListAdapter(Activity context, Book[] objects) {
        super(context, R.layout.listitem_book_list, objects);
        this.context = context;
        //this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listitem_book_list, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mIconImageView = (ImageView) rowView.findViewById(R.id.iconImageView);
            viewHolder.mTitleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
            viewHolder.mAuthorsTextView = (TextView) rowView.findViewById(R.id.authorsTextView);
            viewHolder.mDueDateTextView = (TextView) rowView.findViewById(R.id.dueDateTextView);
            viewHolder.mProlongsTextView = (TextView) rowView.findViewById(R.id.prolongsTextView);
            rowView.setTag(viewHolder);
        }

        Book book = getItem(position);

        int prolongs = book.getAvailableProlongs();
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.mTitleTextView.setText(book.getTitle());
        holder.mAuthorsTextView.setText(book.getAuthors());

        if (book.getCategory() == Book.CATEGORY_LEND) {
            holder.mProlongsTextView.setText(String.format(Locale.getDefault(), "%d / %d", prolongs, book.getAllProlongs()));
        } else if (book.getCategory() == Book.CATEGORY_BOOKED) {
            holder.mProlongsTextView.setText(String.format(Locale.getDefault(), "#%d", book.getQueue()));
        } else {
            holder.mProlongsTextView.setText(StringUtils.split(book.getRental())[0]);
        }

        if (book.getCategory() == Book.CATEGORY_BOOKED) {
            holder.mDueDateTextView.setText(Book.DUE_DATE_FORMAT_SIMPLE.format(book.getRequestDate()));
        } else {
            holder.mDueDateTextView.setText(Book.DUE_DATE_FORMAT_SIMPLE.format(book.getDueDate()));
        }

        int priority = book.checkBookPriority(new Date());
        int color;
        switch (priority) {
            case 0:
                color = ContextCompat.getColor(context, R.color.colorGood);
                holder.mIconImageView.setImageResource(R.drawable.ic_good_book);
                break;

            case 1:
                color = ContextCompat.getColor(context, R.color.colorInfo);
                holder.mIconImageView.setImageResource(R.drawable.ic_warning_book);
                break;

            case 2:
                color = ContextCompat.getColor(context, R.color.colorWarning);
                holder.mIconImageView.setImageResource(R.drawable.ic_expired_book);
                break;

            default:
                color = ContextCompat.getColor(context, R.color.colorError);
                holder.mIconImageView.setImageResource(R.drawable.ic_critical_book);

        }

        holder.mDueDateTextView.setTextColor(color);

        TypedValue textColorSecondary = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, textColorSecondary, true);
        holder.mProlongsTextView.setTextColor(ContextCompat.getColor(context, prolongs == 0 ? R.color.colorError : textColorSecondary.resourceId));

        return rowView;
    }

    static class ViewHolder {
        public ImageView mIconImageView;
        public TextView mTitleTextView;
        public TextView mAuthorsTextView;
        public TextView mDueDateTextView;
        public TextView mProlongsTextView;
    }
}
