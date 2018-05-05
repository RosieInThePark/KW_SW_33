package com.example.user.fingertips.db;

public class RawClickGapData{
    public static final String TABLE_NAME = "raw_click_gap_data";

    public static final String KEY_idx = "idx";
    public static final String KEY_TimeStamp = "TimeStamp";
    public static final String KEY_RawGap = "RawGap";

    public int idx;
    public long RawGap, TimeStamp;
}
