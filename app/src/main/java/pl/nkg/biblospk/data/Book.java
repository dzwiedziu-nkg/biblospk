package pl.nkg.biblospk.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Table(name = "books")
public class Book extends Model {

    private static final int DAY_ONE = 24 * 60 * 60 * 1000;
    private static final int DAY_EARLY = DAY_ONE * 5;

    public static final int CATEGORY_LEND = 0; // wyporzyczone
    public static final int CATEGORY_BOOKED = 1; // zarezerwowane
    public static final int CATEGORY_WAITING = 2; // do odbioru

    public static final SimpleDateFormat DUE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat DUE_DATE_FORMAT_SIMPLE = new SimpleDateFormat("dd.MM", Locale.ENGLISH);

    @Column(name = "category")
    private int mCategory;

    @Column(name = "biblio_number")
    private int mBiblioNumber;

    @Column(name = "item")
    private int mItem;

    @Column(name = "authors")
    private String mAuthors;

    @Column(name = "title")
    private String mTitle;

    @Column(name = "barcode")
    private long mBarCode;

    @Column(name = "signature")
    private String mSignature;

    @Column(name = "request_date")
    private Date mRequestDate;

    @Column(name = "due_date")
    private Date mDueDate;

    @Column(name = "all_prolongs")
    private int mAllProlongs;

    @Column(name = "available_prolongs")
    private int mAvailableProlongs;

    @Column(name = "rental")
    private String mRental;

    @Column(name = "queue")
    private int mQueue;

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
        mCategory = category;
    }

    public int getBiblioNumber() {
        return mBiblioNumber;
    }

    public void setBiblioNumber(int biblioNumber) {
        mBiblioNumber = biblioNumber;
    }

    public int getItem() {
        return mItem;
    }

    public void setItem(int item) {
        mItem = item;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public void setAuthors(String authors) {
        mAuthors = authors;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getBarCode() {
        return mBarCode;
    }

    public void setBarCode(long barCode) {
        mBarCode = barCode;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    public Date getRequestDate() {
        return mRequestDate;
    }

    public void setRequestDate(Date requestDate) {
        mRequestDate = requestDate;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    public int getAllProlongs() {
        return mAllProlongs;
    }

    public void setAllProlongs(int allProlongs) {
        mAllProlongs = allProlongs;
    }

    public int getAvailableProlongs() {
        return mAvailableProlongs;
    }

    public void setAvailableProlongs(int availableProlongs) {
        mAvailableProlongs = availableProlongs;
    }

    public String getRental() {
        return mRental;
    }

    public void setRental(String rental) {
        mRental = rental;
    }

    public int getQueue() {
        return mQueue;
    }

    public void setQueue(int queue) {
        mQueue = queue;
    }

    public int checkBookPriority(Date date) {
        switch (mCategory) {
            case CATEGORY_LEND:
                long today = date.getTime();
                long due = mDueDate.getTime();

                if (today + DAY_EARLY < due) {
                    return 0;
                }

                if (today < due) {
                    return mAvailableProlongs > 0 ? 1 : 2;
                }

                if (today < due + DAY_EARLY) {
                    return mAvailableProlongs > 0 ? 2 : 3;
                }

                break;

            case CATEGORY_BOOKED:
                return 0;
        }

        return 5;
    }

    public boolean equalValues(Book book) {
        if (this == book) return true;
        if (book == null) return false;

        if (mCategory != book.mCategory) return false;
        if (mBiblioNumber != book.mBiblioNumber) return false;
        if (mItem != book.mItem) return false;
        if (mBarCode != book.mBarCode) return false;
        if (mAllProlongs != book.mAllProlongs) return false;
        if (mAvailableProlongs != book.mAvailableProlongs) return false;
        if (mQueue != book.mQueue) return false;
        if (mAuthors != null ? !mAuthors.equals(book.mAuthors) : book.mAuthors != null)
            return false;
        if (mTitle != null ? !mTitle.equals(book.mTitle) : book.mTitle != null) return false;
        if (mSignature != null ? !mSignature.equals(book.mSignature) : book.mSignature != null)
            return false;
        if (mRequestDate != null ? !mRequestDate.equals(book.mRequestDate) : book.mRequestDate != null)
            return false;
        if (mDueDate != null ? !mDueDate.equals(book.mDueDate) : book.mDueDate != null)
            return false;
        return mRental != null ? mRental.equals(book.mRental) : book.mRental == null;
    }
}
