package pl.nkg.biblospk.data;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import android.util.Log;

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
    private int[] mStats = new int[3];

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

    public int getStats(int category) {
        return mStats[category];
    }

    public void updateStats() {
        Arrays.fill(mStats, 0);
        for (Book book : mBookList) {
            mStats[book.getCategory()]++;
        }
    }

    public List<Book> getBooks(boolean lend, boolean waiting, boolean booked) {
        List<Book> books = new ArrayList<>();
        for (Book book : mBookList) {
            if ((lend && book.getCategory() == Book.CATEGORY_LEND)
                    || (waiting && book.getCategory() == Book.CATEGORY_WAITING)
                    || (booked && book.getCategory() == Book.CATEGORY_BOOKED)) {
                books.add(book);
            }
        }
        return books;
    }

    public static Book[] getSortedBookArray(List<Book> bookList, Date toDay) {
        Book[] books = new Book[bookList.size()];
        books = bookList.toArray(books);
        Arrays.sort(books, new BookComparator(toDay));
        return books;
    }

    public void loadBooksList() {
        mBookList = new Select().from(Book.class).execute();
        updateStats();
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
            if (book.getCategory() == Book.CATEGORY_LEND && book.checkBookPriority(toDate) > 0) {
                count++;
            }
        }
        return count;
    }

    public int countOfReady() {
        return getStats(Book.CATEGORY_WAITING);
    }

    public Book getById(long bookId) {
        for (Book book : mBookList) {
            if (book.getId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    static class BookComparator implements Comparator<Book> {

        private final Date mToDay;

        BookComparator(Date toDay) {
            mToDay = toDay;
        }

        @Override
        public int compare(Book lhs, Book rhs) {
            int ret = compareInt(rhs.checkBookPriority(mToDay), lhs.checkBookPriority(mToDay));

            if (ret == 0 && lhs.getDueDate() != null) {
                ret = lhs.getDueDate().compareTo(rhs.getDueDate());
            }

            if (ret == 0) {
                ret = lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }

            Log.d("cmp", lhs.getTitle() + " vs " + rhs.getTitle() + ": " + ret);

            return ret;
        }

        static int compareInt(int lhs, int rhs) {
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }

    public boolean equalBookState(Account account) {
        if (this == account) return true;
        if (account == null) return false;

        if (!Arrays.equals(mStats, account.mStats)) return false;
        if (Float.compare(account.mDebts, mDebts) != 0) return false;
        if (mBookList.size() != account.mBookList.size()) return false;

        for (int i = 0; i < mBookList.size(); i++) {
            if (!mBookList.get(i).equalValues(account.mBookList.get(i))) {
                return false;
            }
        }

        return true;
    }
}
