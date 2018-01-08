package com.chanlin.jetsencloud.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ChanLin on 2018/1/8.
 * jetsenCloud
 * TODO:
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static  DatabaseOpenHelper sInstance = null;
    public static final String DATABASE_NAME = "jetsen_cloud_database.db";
    public static final int DATABASE_VERSION = 1;
    public static DatabaseOpenHelper getsInstance(Context context){
        if(null == sInstance) {
            sInstance = new DatabaseOpenHelper(context);
        }
        return  sInstance;
    }
    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserInfoTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private void createUserInfoTable(SQLiteDatabase db){
        db.execSQL(DatabaseObject.);
    }
}
