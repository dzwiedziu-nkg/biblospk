package pl.nkg.biblospk.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import pl.nkg.biblospk.R;
import pl.nkg.biblospk.data.Book;

public class BookListAdapter extends ArrayAdapter<Book> {

    private final Activity context;
    private final Book[] values;

    public BookListAdapter(Activity context, Book[] objects) {
        super(context, R.layout.listitem_book_list, objects);
        this.context = context;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listitem_book_list, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mTitleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
            viewHolder.mAuthorsTextView = (TextView) rowView.findViewById(R.id.authorsTextView);
            viewHolder.mDueDateTextView = (TextView) rowView.findViewById(R.id.dueDateTextView);
            rowView.setTag(viewHolder);
        }

        StringBuilder dueDate = new StringBuilder(Book.DUE_DATE_FORMAT.format(values[position].getDueDate()));
        dueDate.append(", ");

        Integer prolongs = values[position].getAvailableProlongs();
        if (prolongs > 0) {
            dueDate.append(context.getResources().getString(R.string.info_available_prolongs, prolongs));
        } else {
            dueDate.append(context.getText(R.string.info_no_available_prolongs));
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.mTitleTextView.setText(values[position].getTitle());
        holder.mAuthorsTextView.setText(values[position].getAuthors());
        holder.mDueDateTextView.setText(dueDate.toString());

        int priority = values[position].checkBookPriority(new Date());
        int color;
        switch (priority) {
            case 0:
                color = ContextCompat.getColor(context, R.color.colorGood);
                break;

            case 1:
                color = ContextCompat.getColor(context, R.color.colorInfo);
                break;

            case 2:
                color = ContextCompat.getColor(context, R.color.colorWarning);
                break;

            default:
                color = ContextCompat.getColor(context, R.color.colorError);

        }
        holder.mDueDateTextView.setTextColor(color);

        return rowView;
    }

    static class ViewHolder {
        public TextView mTitleTextView;
        public TextView mAuthorsTextView;
        public TextView mDueDateTextView;
    }
}
