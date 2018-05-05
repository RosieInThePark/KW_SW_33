package com.example.user.fingertips.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class RawClickGapRepo {
    DBHelper dbHelper;


    public RawClickGapRepo(Context context) {
        dbHelper = DBHelper.getDBHelper(context);
    }

    public static void init_table(SQLiteDatabase db) {
        String TABLE = "create table if not exists " + RawClickGapData.TABLE_NAME + " ("
                + RawClickGapData.KEY_idx + " integer, "
                + RawClickGapData.KEY_TimeStamp + " long, "
                + RawClickGapData.KEY_RawGap + " long)";

        db.execSQL(TABLE);
    }

    public void insert(ArrayList<RawClickGapData> list) {
        SQLiteDatabase db = dbHelper.openDB();
        for(RawClickGapData data : list) {
            ContentValues values = new ContentValues();

            values.put(RawClickGapData.KEY_idx, data.idx);
            values.put(RawClickGapData.KEY_TimeStamp, data.TimeStamp);
            values.put(RawClickGapData.KEY_RawGap, data.RawGap);

            db.insert(RawClickGapData.TABLE_NAME, null, values);
        }
    }
}
