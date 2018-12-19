package com.shbj.intentfilterdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shbj.intentfilterdemo.book.Book;
import com.shbj.intentfilterdemo.book.IBookManager;
import com.shbj.intentfilterdemo.book.IOnNewBookArrivedListener;
import com.shbj.intentfilterdemo.service.BookService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "client1";
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookManager = IBookManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            MainActivity.this.newBookArrived(newBook);
        }
    };

    private IBookManager mBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnAction = findViewById(R.id.btn_action);
        Button btnBindService = findViewById(R.id.btn_bindService);
        Button btnAddBook = findViewById(R.id.btn_addBook);
        Button btnRegisterListener = findViewById(R.id.btn_registerListener);
        Button btnUnRegisterListener = findViewById(R.id.btn_unRegisterListener);
        btnAction.setOnClickListener(this);
        btnBindService.setOnClickListener(this);
        btnAddBook.setOnClickListener(this);
        btnRegisterListener.setOnClickListener(this);
        btnUnRegisterListener.setOnClickListener(this);
    }

    private void newBookArrived(Book newBook) {
        Log.d(TAG, "newBookArrived: " + newBook.getBookId() + " " + newBook.getBookName());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        if (id == R.id.btn_action) {
            intent = new Intent();
            intent.setAction("com.shbj.intentfilterdemo2.action.a");
            intent.addCategory("com.shbj.intentfilterdemo2.category.a");
//            File directory = Environment.getExternalStorageDirectory();
//            String name = directory.getName();
//            StringBuilder stringBuilder=new StringBuilder();
//            stringBuilder.append("file://")
//                    .append(name)
//                    .append("abc.jpg");
//            Log.d("MainActivity", stringBuilder.toString());
//            File file = new File(directory,"ifd");
//            intent.setDataAndType(Uri.parse(stringBuilder.toString()),"image/jpg");
            ComponentName componentName = intent.resolveActivity(this.getPackageManager());
            if (componentName == null) {
                Toast.makeText(this, "未找到匹配Activity！", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(intent);
            }
        } else if (id == R.id.btn_bindService) {
            intent = new Intent(this, BookService.class);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        } else if (id == R.id.btn_addBook) {
            try {
                List<Book> bookList = mBookManager.getBookList();
                if (bookList != null) {
                    int bookId = bookList.size() + 1;
                    Book book = new Book(bookId, "第一类新书-" + String.valueOf(bookId));
                    mBookManager.addBook(book);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.btn_registerListener) {
            try {
                mBookManager.registerIOnNewBookArrivedListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btn_unRegisterListener) {
            try {
                mBookManager.unRegisterIOnNewBookArrivedListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
