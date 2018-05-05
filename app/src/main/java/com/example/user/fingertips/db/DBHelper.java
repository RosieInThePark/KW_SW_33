package com.example.user.fingertips.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
* Use Singleton Pattern to use Database
*/
public class DBHelper extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "touch_data_database";
    public final static int DATABASE_VERSION = 1;
    public final static String TAG = "DBTEST";

    private static DBHelper mDBHelper;
    private static SQLiteDatabase mDB;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /*
    * The Function 'getDBHelper()' is called in 'OnResume()'
    * If the Database is opened, it is not closed until the app is stopped.
    */
    public static DBHelper getDBHelper(Context context) {
        Log.d(TAG, "CREATE SINGLE TONE & OPEN DB");
        if(mDBHelper==null) {
            mDBHelper = new DBHelper(context.getApplicationContext());
            mDB = mDBHelper.getWritableDatabase(); //open DB
        }
        return mDBHelper;
    }
    public SQLiteDatabase openDB() {
        if(!mDB.isOpen()) {
            mDBHelper.onOpen(mDB);
        }
        return mDB;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "CREATE TABLE");
        RawClickGapRepo.init_table(db);
        RawPointRepo.init_table(db);
        DataFeaturesRepo.init_table(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DELETE FROM " + RawClickGapData.TABLE_NAME);
        db.execSQL("DELETE FROM " + RawPointData.TABLE_NAME);
        db.execSQL("DELETE FROM " + InputDataFeatures.TABLE_NAME);

        Log.d(TAG, "UPGRADE");
    }
}
