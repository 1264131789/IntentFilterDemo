package com.shbj.intentfilterdemo.db;

/*
 *  @项目名：  IntentFilterDemo 
 *  @包名：    com.shbj.intentfilterdemo.db
 *  @文件名:   BookDbOpenHelper
 *  @创建者:   shenbinjian
 *  @创建时间:  2018/12/8 17:03
 *  @描述：    TODO
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "book.db";
    public static final String BOOK_TABLE_NAME = "book";
    public static final String USER_TABLE_NAME = "user";
    private static final int DB_VERSION = 1;
    private static final String CREATE_BOOK_TABLE = "create table if not exists "
            + BOOK_TABLE_NAME + "(_id integer primary key,name text)";
    private static final String CREATE_USER_TABLE = "create table if not exists "
            + USER_TABLE_NAME + "(_id integer primary key,name text,sex intger)";

    public BookDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
