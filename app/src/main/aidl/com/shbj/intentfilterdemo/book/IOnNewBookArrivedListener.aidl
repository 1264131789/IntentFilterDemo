// IOnNewBookArrivedListener.aidl
package com.shbj.intentfilterdemo.book;
import com.shbj.intentfilterdemo.book.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
