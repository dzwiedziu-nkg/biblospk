package pl.nkg.biblospk.data;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Account {
    private String mCardNumber;
    private String mName;
    private int mBorrowerNumber;
    private float mDebts;
    private List<Book> mBookList = new ArrayList<>();

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getBorrowerNumber() {
        return mBorrowerNumber;
    }

    public void setBorrowerNumber(int borrowerNumber) {
        mBorrowerNumber = borrowerNumber;
    }

    public float getDebts() {
        return mDebts;
    }

    public void setDebts(float debts) {
        mDebts = debts;
    }

    public List<Book> getBookList() {
        return mBookList;
    }

    public void setBookList(List<Book> bookList) {
        mBookList = bookList;
    }

    public Book[] getSortedBookArray(Date toDay) {
        Book[] books = new Book[mBookList.size()];
        books = mBookList.toArray(books);
        Arrays.sort(books, new BookComparator(toDay));
        return books;
    }

    public void loadBooksList() {
        mBookList = new Select().from(Book.class).execute();
    }

    public void storeBooksList() {
        ActiveAndroid.beginTransaction();
        try {
            wipeBookList();
            for (Book book : mBookList) {
                book.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void wipeBookList() {
        new Delete().from(Book.class).execute();
    }

    public int countOfExpired(Date toDate) {
        int count = 0;
        for (Book book : mBookList) {
            if (book.checkBookPriority(toDate) > 0) {
                count++;
            }
        }
        return count;
    }

    public int countOfReady() {
        return 0; // TODO: to implement when ready are implemented
    }

    static class BookComparator implements Comparator<Book> {

        private final Date mToDay;

        BookComparator(Date toDay) {
            mToDay = toDay;
        }

        @Override
        public int compare(Book lhs, Book rhs) {
            int ret = compareInt(lhs.checkBookPriority(mToDay), rhs.checkBookPriority(mToDay));

            if (ret == 0) {
                ret = lhs.getDueDate().compareTo(rhs.getDueDate());
            }

            if (ret == 0) {
                ret = lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }

            return ret;
        }

        static int compareInt(int lhs, int rhs) {
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }
}
