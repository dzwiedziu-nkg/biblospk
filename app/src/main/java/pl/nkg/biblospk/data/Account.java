package pl.nkg.biblospk.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Account {
    private String mCardNumber;
    private String mName;
    private int mBorrowerNumber;
    private double mDue;
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

    public double getDue() {
        return mDue;
    }

    public void setDue(double due) {
        mDue = due;
    }

    public List<Book> getBookList() {
        return mBookList;
    }

    public void setBookList(List<Book> bookList) {
        mBookList = bookList;
    }
}
