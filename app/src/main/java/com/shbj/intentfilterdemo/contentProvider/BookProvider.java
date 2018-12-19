package com.shbj.intentfilterdemo.contentProvider;

/*
 *  @项目名：  IntentFilterDemo 
 *  @包名：    com.shbj.intentfilterdemo.contentProvider
 *  @文件名:   BookProvider
 *  @创建者:   shenbinjian
 *  @创建时间:  2018/12/7 18:48
 *  @描述：    TODO
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.shbj.intentfilterdemo.db.BookDbOpenHelper;

public class BookProvider extends ContentProvider {
    private static final String TAG = "bookProvider";
    private static final String AUTHORITIES = "com.shbj.intentfilterdemo.authorities.provider.book";
    private static final Uri BOOK_URI = Uri.parse("content://" + AUTHORITIES + "/book");
    private static final Uri USER_URI = Uri.parse("content://" + AUTHORITIES + "/user");
    private static final int BOOK_CODE = 0;
    private static final int USER_CODE = 1;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITIES, "book", BOOK_CODE);
        URI_MATCHER.addURI(AUTHORITIES, "user", USER_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        initProvider();
        return true;
    }

    private void initProvider() {
        mDatabase = new BookDbOpenHelper(mContext).getWritableDatabase();
        mDatabase.execSQL("delete from " + BookDbOpenHelper.BOOK_TABLE_NAME);
        mDatabase.execSQL("delete from " + BookDbOpenHelper.USER_TABLE_NAME);
        mDatabase.execSQL("insert into " + BookDbOpenHelper.BOOK_TABLE_NAME + " values(3,'android')");
        mDatabase.execSQL("insert into " + BookDbOpenHelper.BOOK_TABLE_NAME + " values(4,'java')");
        mDatabase.execSQL("insert into " + BookDbOpenHelper.BOOK_TABLE_NAME + " values(5,'c++')");
        mDatabase.execSQL("insert into " + BookDbOpenHelper.USER_TABLE_NAME + " values(1,'jake',1)");
        mDatabase.execSQL("insert into " + BookDbOpenHelper.USER_TABLE_NAME + " values(2,'alice',0)");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupport URI: " + uri);
        }
        Cursor cursor = mDatabase.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupport URI: " + uri);
        }
        mDatabase.insert(tableName, null, values);
        mContext.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupport URI: " + uri);
        }
        int delete = mDatabase.delete(tableName, selection, selectionArgs);
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupport URI: " + uri);
        }
        int update = mDatabase.update(tableName, values, selection, selectionArgs);
        return update;
    }

    private String getTableName(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        String tableName = null;
        switch (match) {
            case BOOK_CODE:
                tableName = BookDbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_CODE:
                tableName = BookDbOpenHelper.USER_TABLE_NAME;
                break;
            default:

                break;
        }
        return tableName;
    }
}
