package com.example.user.fingertips.db;

public class InputDataFeatures {
    public static final String TABLE_NAME = "input_data_features";

    public static final String KEY_idx = "idx";
    public static final String KEY_TimeStamp = "TimeStamp";
    public static final String KEY_WebName = "WebName";
    public static final String KEY_Type = "Type";

    public static final String KEY_DownPosX1 = "DownPosX1";
    public static final String KEY_DownPosY1 = "DownPosY1";
    public static final String KEY_DownConSize1 = "DownConSize1";
    public static final String KEY_UpPosX1 = "UpPosX1";
    public static final String KEY_UpPosY1 = "UpPosY1";
    public static final String KEY_UpConSize1 = "UpConSize1";

    public static final String KEY_DownPosX2 = "DownPosX2";
    public static final String KEY_DownPosY2 = "DownPosY2";
    public static final String KEY_DownConSize2 = "DownConSize2";
    public static final String KEY_UpPosX2 = "UpPosX2";
    public static final String KEY_UpPosY2 = "UpPosY2";
    public static final String KEY_UpConSize2 = "UpConSize2";

    public static final String KEY_SwipeLength = "SwipeLength";
    public static final String KEY_SwipeSpeed = "SwipeSpeed";
    public static final String KEY_SwipeCurvature = "SwipeCurvature";

    public static final String KEY_ZoomLength1 = "ZoomLength1";
    public static final String KEY_ZoomLength2 = "ZoomLength2";
    public static final String KEY_ZoomSpeed1 = "ZoomSpeed1";
    public static final String KEY_ZoomSpeed2 = "ZoomSpeed2";
    public static final String KEY_ZoomCurvature1 = "ZoomCurvature1";
    public static final String KEY_ZoomCurvature2 = "ZoomCurvature2";

    public static final String KEY_ClickGap = "ClickGap";

    public int idx;
    public long TimeStamp;
    public String WebName, Type;
    public float DownPosX1, DownPosY1, DownConSize1;
    public float UpPosX1, UpPosY1, UpConSize1;
    public float DownPosX2, DownPosY2, DownConSize2;
    public float UpPosX2, UpPosY2, UpConSize2;
    public float SwipeLength, SwipeSpeed, SwipeCurvature;
    public float ZoomLength1, ZoomLength2, ZoomSpeed1, ZoomSpeed2, ZoomCurvature1, ZoomCurvature2;
    public float ClickGap;
}
