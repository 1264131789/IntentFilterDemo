package com.shbj.intentfilterdemo2.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shbj.intentfilterdemo.book.Book;
import com.shbj.intentfilterdemo.book.IBookManager;
import com.shbj.intentfilterdemo.book.IOnNewBookArrivedListener;
import com.shbj.intentfilterdemo2.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "client2";
    private ServiceConnection mBookConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mBookManagerService = IBookManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: " + Thread.currentThread().getName());
            unbindService(this);
            Intent intent = new Intent();
            intent.setPackage("com.shbj.intentfilterdemo");
            intent.setAction("com.shbj.intentfilterdemo.action.service.book");
            bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
    };

    private ServiceConnection mMessengerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(this);
            Intent intent = new Intent();
            intent.setPackage("com.shbj.intentfilterdemo");
            intent.setAction("com.shbj.intentfilterdemo.action.service.messenger");
            bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
    };

    private IBookManager mBookManagerService;
    private IOnNewBookArrivedListener mIOnNewBookArrivedListener;
    private Messenger mMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnBindService = findViewById(R.id.btn_bind_service);
        Button btnAddBook = findViewById(R.id.btn_add_book);
        Button btnGetBookList = findViewById(R.id.btn_get_book_list);
        Button btnRegisterListener = findViewById(R.id.btn_registerListener);
        Button btnUnRegisterListener = findViewById(R.id.btn_unRegisterListener);
        Button btnStartProviderActivity = findViewById(R.id.btn_start_provider_activity);
        Button btnStartMessengerActivity = findViewById(R.id.btn_start_messenger_activity);
        Button btnSendMessage = findViewById(R.id.btn_send_message);
        Button btnConnectServer = findViewById(R.id.btn_connect_server);

        btnBindService.setOnClickListener(this);
        btnAddBook.setOnClickListener(this);
        btnGetBookList.setOnClickListener(this);
        btnRegisterListener.setOnClickListener(this);
        btnUnRegisterListener.setOnClickListener(this);
        btnStartProviderActivity.setOnClickListener(this);
        btnStartMessengerActivity.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);
        btnConnectServer.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_bind_service) {
            Intent intent = new Intent();
            intent.setPackage("com.shbj.intentfilterdemo");
            intent.setAction("com.shbj.intentfilterdemo.action.service.book");
            bindService(intent, mBookConnection, Context.BIND_AUTO_CREATE);
        } else if (id == R.id.btn_add_book) {
            try {
                List<Book> bookList = mBookManagerService.getBookList();
                int bookId = bookList.size() + 1;
                Book book = new Book(bookId, "第二类新书-" + bookId);
                mBookManagerService.addBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btn_get_book_list) {
            try {
                List<Book> bookList = mBookManagerService.getBookList();
                for (Book book :
                        bookList) {
                    Log.d(TAG, book.getBookId() + " : " + book.getBookName());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btn_registerListener) {
            try {
                if (mIOnNewBookArrivedListener == null) {
                    mIOnNewBookArrivedListener = new ClientNewBookArrivedBinderListener();
                }
                mBookManagerService.registerIOnNewBookArrivedListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btn_unRegisterListener) {
            try {
                mBookManagerService.unRegisterIOnNewBookArrivedListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btn_start_provider_activity) {
            Intent intent = new Intent(this, ProviderActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_start_messenger_activity) {
            Intent intent = new Intent();
            intent.setPackage("com.shbj.intentfilterdemo");
            intent.setAction("com.shbj.intentfilterdemo.action.service.messenger");
            bindService(intent, mMessengerConnection, Context.BIND_AUTO_CREATE);
        } else if (id == R.id.btn_send_message) {
            Message message = Message.obtain();
            message.what = 1;
            message.replyTo = mClientMessenger;
            Bundle bundle = new Bundle();
            bundle.putString("client", "客户端请求!");
            message.setData(bundle);
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btn_connect_server) {
            Intent intent = new Intent();
            intent.setPackage("com.shbj.intentfilterdemo");
            intent.setAction("com.shbj.intentfilterdemo.action.service.server");
            startService(intent);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connectServer();
                }
            }).start();
        }
    }

    private void connectServer() {
        try {
            Socket socket = new Socket("localhost", 8919);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            Scanner in = new Scanner(inputStream, "utf-8");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, "utf-8"));
            while (in.hasNextLine()) {
                String nextLine = in.nextLine();
                Log.d(TAG, "in: " + nextLine);
                String s = "BYE";
                out.println(s);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newBookArrived(Book newBook) {
        Log.d(TAG, "newBookArrived: " + newBook.getBookId() + " " + newBook.getBookName());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (mBookConnection != null) {
            unbindService(mBookConnection);
        }
        if (mMessengerConnection != null) {
            unbindService(mMessengerConnection);
        }
    }

    private class ClientNewBookArrivedBinderListener extends IOnNewBookArrivedListener.Stub {

        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            MainActivity.this.newBookArrived(newBook);
        }
    }

    @SuppressLint("HandlerLeak")
    private static Handler sClientHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    Bundle data = msg.getData();
                    String service = data.getString("service");
                    Log.d(TAG, service);
                    break;
                default:

                    break;
            }
        }
    };

    private Messenger mClientMessenger = new Messenger(sClientHandler);
}
