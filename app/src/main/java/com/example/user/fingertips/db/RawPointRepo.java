package com.example.user.fingertips.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class RawPointRepo {
    private DBHelper dbHelper;

    public RawPointRepo(Context context) {
        dbHelper = DBHelper.getDBHelper(context);
    }
    public static void init_table(SQLiteDatabase db) {
        String TABLE = "create table if not exists " + RawPointData.TABLE_NAME + " ("
                + RawPointData.KEY_idx + " integer, "
                + RawPointData.KEY_TimeStamp + " long, "
                + RawPointData.KEY_Type + " text, "
                + RawPointData.KEY_ValueX + " float, "
                + RawPointData.KEY_ValueY + " float);";

        db.execSQL(TABLE);
    }
    public void insert(ArrayList<RawPointData> rawList) {
        SQLiteDatabase db = dbHelper.openDB();

        for(RawPointData rawData : rawList) {
            ContentValues values = new ContentValues();
            values.put(RawPointData.KEY_idx, rawData.idx);
            values.put(RawPointData.KEY_TimeStamp, rawData.TimeStamp);
            values.put(RawPointData.KEY_Type, rawData.Type);
            values.put(RawPointData.KEY_ValueX, rawData.ValueX);
            values.put(RawPointData.KEY_ValueY, rawData.ValueY);

            db.insert(RawPointData.TABLE_NAME, null, values);
        }
    }
}
