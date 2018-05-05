package com.example.user.fingertips;

//SwipeLengthSpeedCurvature.java
import android.util.Log;

import com.example.user.fingertips.db.RawPointData;
import com.example.user.fingertips.db.RawPointRepo;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SwipeLengthSpeedCurvature
{
    public static final String TAG = "DDONG";
    public ArrayList<Float> pointsX1, pointsY1, pointsX2, pointsY2;
    //public ArrayList <RawDataFeatures> list;

    public ArrayList<RawPointData> Points1, Points2;

    public SwipeLengthSpeedCurvature(){
        pointsX1 = new ArrayList<>();
        pointsY1 = new ArrayList<>();
        pointsX2 = new ArrayList<>();
        pointsY2 = new ArrayList<>();
        //list = new ArrayList<>();

        Points1 = new ArrayList<>();
        Points2 = new ArrayList<>();
    }
    public void setPoints1(long currentTime, float pointX, float pointY) {
        RawPointData rawData = new RawPointData();
        if(Points1.size() < 3) {
            rawData.TimeStamp = currentTime;
            rawData.ValueX = pointX;
            rawData.ValueY = pointY;
            Points1.add(rawData);
        }
        else {
            int size = Points1.size()-1;

            if(Points1.get(size).ValueX != pointX && Points1.get(size-1).ValueX != pointX) {
                if(Points1.get(size).ValueY != pointY && Points1.get(size-1).ValueY != pointY) {
                    rawData.TimeStamp = currentTime;
                    rawData.ValueX = pointX;
                    rawData.ValueY = pointY;
                    Points1.add(rawData);
                }
            }
        }
    }
    public void setPoints2(long currentTime, float pointX, float pointY) {
        RawPointData rawData = new RawPointData();
        if(Points2.size() < 3) {
            rawData.TimeStamp = currentTime;
            rawData.ValueX = pointX;
            rawData.ValueY = pointY;
            Points2.add(rawData);
        }
        else {
            int size = Points2.size()-1;

            if(Points2.get(size).ValueX != pointX && Points2.get(size-1).ValueX != pointX) {
                if(Points2.get(size).ValueY != pointY && Points2.get(size-1).ValueY != pointY) {
                    rawData.TimeStamp = currentTime;
                    rawData.ValueX = pointX;
                    rawData.ValueY = pointY;
                    Points2.add(rawData);
                }
            }
        }
    }
    public void saveRawData1(int index, String type) {
        for(RawPointData data : Points1) {
            data.idx = index;
            data.Type = type;
        }
    }
    public void saveRawData2(int index, String type) {
        for(RawPointData data : Points2) {
            data.idx = index;
            data.Type = type;
        }
    }

    public double getLength1()
    {
        double final_length = 0;
        if(Points1.size() > 1)
        {
            for(int i = 1; i < Points1.size(); i++)
            {
                double len = calLength(Points1.get(i - 1).ValueX, Points1.get(i - 1).ValueY, Points1.get(i).ValueX, Points1.get(i).ValueY);
                final_length += len;
            }
        }
        return final_length;
    }
    public double getLength2()
    {
        double final_length = 0;
        if(Points2.size() > 1)
        {
            for(int i = 1; i < Points2.size(); i++)
            {
                double len = calLength(Points2.get(i - 1).ValueX, Points2.get(i - 1).ValueY, Points2.get(i).ValueX, Points2.get(i).ValueY);
                final_length += len;
            }
        }
        return final_length;
    }
    public float calCurvatures1()
    {
        float cur;
        Float dx = Points1.get(Points1.size()-1).ValueX - Points1.get(0).ValueX;
        Float dy = Points1.get(Points1.size()-1).ValueY - Points1.get(0).ValueY;

        Float d2x = (Points1.get(Points1.size()-1).ValueX - Points1.get((Points1.size()-1) / 2).ValueX) + (Points1.get(0).ValueX - Points1.get((Points1.size()-1) / 2).ValueX);
        Float d2y = (Points1.get(Points1.size()-1).ValueY - Points1.get((Points1.size()-1) / 2).ValueY) + (Points1.get(0).ValueY - Points1.get((Points1.size()-1) / 2).ValueY);

        cur = (float)(Math.abs(dx * d2y - dy * d2x) / Math.sqrt(Math.pow(dx * dx + dy * dy, 3)));
        if(cur > 0) return cur;
        else return 0;
    }
    public float calCurvatures2()
    {
        float cur;
        Float dx = Points2.get(Points2.size()-1).ValueX - Points2.get(0).ValueX;
        Float dy = Points2.get(Points2.size()-1).ValueY - Points2.get(0).ValueY;

        Float d2x = (Points2.get(Points2.size()-1).ValueX - Points2.get((Points2.size()-1) / 2).ValueX) + (Points2.get(0).ValueX - Points2.get((Points2.size()-1) / 2).ValueX);
        Float d2y = (Points2.get(Points2.size()-1).ValueY - Points2.get((Points2.size()-1) / 2).ValueY) + (Points2.get(0).ValueY - Points2.get((Points2.size()-1) / 2).ValueY);

        cur = (float)(Math.abs(dx * d2y - dy * d2x) / Math.sqrt(Math.pow(dx * dx + dy * dy, 3)));
        if(cur > 0) return cur;
        else return 0;
    }
    public double calLength(Float xp, Float yp, Float x, Float y)
    {
        double temp = (x - xp) * (x - xp) + (y - yp) * (y - yp);
        temp = (float)Math.sqrt(temp);
        return temp;
    }
    public String changeURL(String URL)
    {
        String change_URL;
        StringTokenizer token = new StringTokenizer(URL, "/");
        token.nextToken();
        change_URL = token.nextToken();
        change_URL = change_URL.replace(".", "_");
        return change_URL;
    }
}