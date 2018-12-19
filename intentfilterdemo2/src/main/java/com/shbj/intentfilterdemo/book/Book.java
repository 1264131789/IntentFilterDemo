package com.shbj.intentfilterdemo.book;

/*
 *  @项目名：  IntentFilterDemo 
 *  @包名：    com.shbj.intentfilterdemo.book
 *  @文件名:   Book
 *  @创建者:   shenbinjian
 *  @创建时间:  2018/12/4 18:15
 *  @描述：    TODO
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private int mBookId;
    private String mBookName;

    protected Book(Parcel in) {
        mBookId = in.readInt();
        mBookName = in.readString();
    }

    public Book(){

    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public int getBookId() {
        return mBookId;
    }

    public void setBookId(int bookId) {
        mBookId = bookId;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        mBookName = bookName;
    }

    public Book(int bookId, String bookName) {
        mBookId = bookId;
        mBookName = bookName;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mBookId);
        dest.writeString(mBookName);
    }
}
