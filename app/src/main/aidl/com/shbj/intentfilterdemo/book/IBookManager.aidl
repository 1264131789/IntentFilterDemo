// IBookManager.aidl
package com.shbj.intentfilterdemo.book;
import com.shbj.intentfilterdemo.book.Book;
import com.shbj.intentfilterdemo.book.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    void addBook(in Book book);

    List<Book> getBookList();

    void registerIOnNewBookArrivedListener(in IOnNewBookArrivedListener listener);
    
    void unRegisterIOnNewBookArrivedListener(in IOnNewBookArrivedListener listener);
}
