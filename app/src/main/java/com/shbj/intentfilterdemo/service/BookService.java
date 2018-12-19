package com.shbj.intentfilterdemo.service;

/*
 *  @项目名：  IntentFilterDemo 
 *  @包名：    com.shbj.intentfilterdemo.service
 *  @文件名:   BookService
 *  @创建者:   shenbinjian
 *  @创建时间:  2018/12/4 22:22
 *  @描述：    TODO
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shbj.intentfilterdemo.book.Book;
import com.shbj.intentfilterdemo.book.IBookManager;
import com.shbj.intentfilterdemo.book.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookService extends Service {
    private final String TAG = "service";
    private final CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList();
    private final CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList();
    private boolean isDestory = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(0, "c语言"));
        mBookList.add(new Book(1, "c++语言"));
        Log.d(TAG, "onCreate: ");
//        new Thread(new NewBookArrivedWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return new BookBinder().asBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        isDestory = true;
    }

    private void registerListener(IOnNewBookArrivedListener listener) {
        IBinder iBinder = listener.asBinder();
        for (IOnNewBookArrivedListener l :
                mListenerList) {
            if (l.asBinder() == iBinder) {
                Log.d(TAG, "registerListener: already exists!");
                return;
            }
        }
        mListenerList.add(listener);
        Log.d(TAG, "register this listener!");
    }

    private void unRegisterListener(IOnNewBookArrivedListener listener) {
        IBinder iBinder = listener.asBinder();
        for (IOnNewBookArrivedListener l :
                mListenerList) {
            if (l.asBinder() == iBinder) {
                mListenerList.remove(l);
                Log.d(TAG, "unRegister this listener!");
            } else {
                Log.d(TAG, "not found this listener!");
            }
        }

    }

    private List<Book> getBookList() {
        return mBookList;
    }

    private void addBook(Book book) throws RemoteException {
        Log.d(TAG, book.getBookName());
        mBookList.add(book);
        for (IOnNewBookArrivedListener listener :
                mListenerList) {
            listener.onNewBookArrived(book);
        }
    }

    private class BookBinder extends IBookManager.Stub {

        @Override
        public void addBook(Book book) throws RemoteException {
            BookService.this.addBook(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return BookService.this.getBookList();
        }

        @Override
        public void registerIOnNewBookArrivedListener(IOnNewBookArrivedListener listener) throws RemoteException {
            BookService.this.registerListener(listener);
        }

        @Override
        public void unRegisterIOnNewBookArrivedListener(IOnNewBookArrivedListener listener) throws RemoteException {
            BookService.this.unRegisterListener(listener);
        }
    }

    private class NewBookArrivedWorker implements Runnable {

        @Override
        public void run() {
            while (!BookService.this.isDestory) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int i = mBookList.size() + 1;
                Book book = new Book(i, "new book " + String.valueOf(i));
                try {
                    addBook(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
