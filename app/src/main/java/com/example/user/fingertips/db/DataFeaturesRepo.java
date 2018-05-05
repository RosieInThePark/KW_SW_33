package com.example.user.fingertips.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataFeaturesRepo {
    DBHelper dbHelper;

    public DataFeaturesRepo(Context context) {
        dbHelper = DBHelper.getDBHelper(context);
    }

    public static void init_table(SQLiteDatabase db) {
        String TABLE = "CREATE TABLE IF NOT EXISTS " + InputDataFeatures.TABLE_NAME + " ("
                + InputDataFeatures.KEY_idx + " INTEGER PRIMARY KEY autoincrement, "
                + InputDataFeatures.KEY_TimeStamp + " LONG, "
                + InputDataFeatures.KEY_WebName + " TEXT, "
                + InputDataFeatures.KEY_Type + " TEXT, "
                + InputDataFeatures.KEY_DownPosX1 + " FLOAT, "
                + InputDataFeatures.KEY_DownPosY1 + " FLOAT, "
                + InputDataFeatures.KEY_DownConSize1 + " FLOAT, "
                + InputDataFeatures.KEY_UpPosX1 + " FLOAT, "
                + InputDataFeatures.KEY_UpPosY1 + " FLOAT, "
                + InputDataFeatures.KEY_UpConSize1 + " FLOAT, "
                + InputDataFeatures.KEY_DownPosX2 + " FLOAT, "
                + InputDataFeatures.KEY_DownPosY2 + " FLOAT, "
                + InputDataFeatures.KEY_DownConSize2 + " FLOAT, "
                + InputDataFeatures.KEY_UpPosX2 + " FLOAT, "
                + InputDataFeatures.KEY_UpPosY2 + " FLOAT, "
                + InputDataFeatures.KEY_UpConSize2 + " FLOAT, "
                + InputDataFeatures.KEY_SwipeLength + " FLOAT, "
                + InputDataFeatures.KEY_SwipeSpeed + " FLOAT, "
                + InputDataFeatures.KEY_SwipeCurvature + " FLOAT, "
                + InputDataFeatures.KEY_ZoomLength1 + " FLOAT, "
                + InputDataFeatures.KEY_ZoomLength2 + " FLOAT, "
                + InputDataFeatures.KEY_ZoomSpeed1 + " FLOAT, "
                + InputDataFeatures.KEY_ZoomSpeed2 + " FLOAT, "
                + InputDataFeatures.KEY_ZoomCurvature1 + " FLOAT, "
                + InputDataFeatures.KEY_ZoomCurvature2 + " FLOAT, "
                + InputDataFeatures.KEY_ClickGap + " FLOAT);";

        db.execSQL(TABLE);
    }
    public int insert(InputDataFeatures data) {
        SQLiteDatabase db = dbHelper.openDB();
        ContentValues values = new ContentValues();

        values.put(InputDataFeatures.KEY_TimeStamp, data.TimeStamp);
        values.put(InputDataFeatures.KEY_WebName, data.WebName);
        values.put(InputDataFeatures.KEY_Type, data.Type);

        values.put(InputDataFeatures.KEY_DownPosX1, data.DownPosX1);
        values.put(InputDataFeatures.KEY_DownPosY1, data.DownPosY1);
        values.put(InputDataFeatures.KEY_DownConSize1, data.DownConSize1);
        values.put(InputDataFeatures.KEY_UpPosX1, data.UpPosX1);
        values.put(InputDataFeatures.KEY_UpPosY1, data.UpPosY1);
        values.put(InputDataFeatures.KEY_UpConSize1, data.UpConSize1);

        values.put(InputDataFeatures.KEY_DownPosX2, data.DownPosX2);
        values.put(InputDataFeatures.KEY_DownPosY2, data.DownPosY2);
        values.put(InputDataFeatures.KEY_DownConSize2, data.DownConSize2);
        values.put(InputDataFeatures.KEY_UpPosX2, data.UpPosX2);
        values.put(InputDataFeatures.KEY_UpPosY2, data.UpPosY2);
        values.put(InputDataFeatures.KEY_UpConSize2, data.UpConSize2);

        values.put(InputDataFeatures.KEY_SwipeLength, data.SwipeLength);
        values.put(InputDataFeatures.KEY_SwipeSpeed, data.SwipeSpeed);
        values.put(InputDataFeatures.KEY_SwipeCurvature, data.SwipeCurvature);

        values.put(InputDataFeatures.KEY_ZoomLength1, data.ZoomLength1);
        values.put(InputDataFeatures.KEY_ZoomLength2, data.ZoomLength2);
        values.put(InputDataFeatures.KEY_ZoomSpeed1, data.ZoomSpeed1);
        values.put(InputDataFeatures.KEY_ZoomSpeed2, data.ZoomSpeed2);
        values.put(InputDataFeatures.KEY_ZoomCurvature1, data.ZoomCurvature1);
        values.put(InputDataFeatures.KEY_ZoomCurvature2, data.ZoomCurvature2);

        values.put(InputDataFeatures.KEY_ClickGap, data.ClickGap);

        long id = db.insert(InputDataFeatures.TABLE_NAME, null, values);
        return (int)id;
    }
}
