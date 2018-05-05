package com.example.user.fingertips.db;

public class RawPointData {
    public static final String TABLE_NAME = "raw_point_data";

    public static final String KEY_idx = "idx";
    public static final String KEY_TimeStamp = "TimeStamp";
    public static final String KEY_Type = "Type";
    public static final String KEY_ValueX = "ValueX";
    public static final String KEY_ValueY = "ValueY";

    public int idx;
    public long TimeStamp;
    public float ValueX, ValueY;
    public String Type;
}
