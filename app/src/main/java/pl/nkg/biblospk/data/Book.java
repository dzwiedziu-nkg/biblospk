package pl.nkg.biblospk.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Book {

    private static final int DAY_ONE = 24 * 60 * 60 * 1000;
    private static final int DAY_EARLY = DAY_ONE * 5;

    public static final SimpleDateFormat DUE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    private int mBiblioNumber;
    private int mItem;

    private String mAuthors;
    private String mTitle;
    private long mBarCode;
    private String mSignature;
    private Date mDueDate;
    private int mAllProlongs;
    private int mAvailableProlongs;

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

    public int checkBookPriority(Date date) {
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

        return 5;
    }
}
