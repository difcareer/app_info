package com.andr0day.appinfo.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static Object lock = new Object();
    private static DbHelper instance;
    private static final String DB_NAME = "app_info";
    private static final String TABLE_ISOLATE = "isoloate";
    private static final String ISOLATE_PKG = "pkg";
    private static final int VERSION = 1;

    public static DbHelper getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new DbHelper(context);
            }
            return instance;
        }
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_ISOLATE + "(" + ISOLATE_PKG + " varchar(500) not null);";
        sqLiteDatabase.execSQL(sql);
    }

    public void insertIsolate(String pkg) {
        ContentValues values = new ContentValues();
        values.put(ISOLATE_PKG, pkg);
        getWritableDatabase().insert(TABLE_ISOLATE, null, values);
    }

    public void deleteIsolate(String pkg) {
        getWritableDatabase().delete(TABLE_ISOLATE, ISOLATE_PKG + "=?", new String[]{pkg});
    }

    public boolean existIsolate(String pkg) {
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + TABLE_ISOLATE + " where " + ISOLATE_PKG + "=?", new String[]{pkg});
        if (cursor != null && cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
