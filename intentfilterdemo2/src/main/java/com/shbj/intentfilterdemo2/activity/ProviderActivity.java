package com.shbj.intentfilterdemo2.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shbj.intentfilterdemo.book.Book;
import com.shbj.intentfilterdemo2.R;

public class ProviderActivity extends AppCompatActivity implements View.OnClickListener {

    private ContentResolver mContentResolver;
    private Uri mUri;
    private static final String TAG = "provider_client";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        mContentResolver = getContentResolver();
        mUri = Uri.parse("content://com.shbj.intentfilterdemo.authorities.provider.book/book");
        Button btnQuery = findViewById(R.id.btn_query);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnInsert = findViewById(R.id.btn_insert);
        Button btnDelete = findViewById(R.id.btn_delete);
        btnQuery.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_query) {
            String[] pros = new String[]{"_id", "name"};
            Cursor cursor = mContentResolver.query(mUri, pros, null, null, null);
            while (cursor.moveToNext()) {
                Book book = new Book();
                book.setBookId(cursor.getInt(0));
                book.setBookName(cursor.getString(1));
                Log.d(TAG, "book: " + book.getBookId() + " " + book.getBookName());
            }
            cursor.close();
        } else if (id == R.id.btn_update) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", "html");
            int update = mContentResolver.update(mUri, contentValues, "name=?", new String[]{"c++"});
        } else if (id == R.id.btn_insert) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", "ios");
            Uri uri = mContentResolver.insert(mUri, contentValues);
        } else if (id == R.id.btn_delete) {
            int delete = mContentResolver.delete(mUri, "name=?", new String[]{"ios"});
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
