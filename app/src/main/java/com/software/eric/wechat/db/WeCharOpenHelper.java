package com.software.eric.wechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mzz on 2015/11/4.
 */
public class WeCharOpenHelper extends SQLiteOpenHelper{

    public static final String CREATE_MSG = "create table Msg (" +
            "id integer primary key autoincrement," +
            "from_id text," +
            "time text," +
            "content text)";

    public WeCharOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MSG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
