package com.example.user.fingertips;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.example.user.fingertips.db.DataFeaturesRepo;
import com.example.user.fingertips.db.InputDataFeatures;
import com.example.user.fingertips.db.RawClickGapData;
import com.example.user.fingertips.db.RawClickGapRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RecordService extends AccessibilityService{
    public boolean touch_flag = false;
    public boolean gap_flag = false;
    public int on_touch = 0;
    public long startTime = 0;
    public long endTime = 0;
    public long timeDifference = 0;
    private SharedPreferences URL_ser;
    private SharedPreferences.Editor editor_ser;
    private String next_Url;
    private String DB_Url;
    static final String TAG = "DDONG";
    float clickGapSum = 0;
    int clickGapCounter = 0;
    ArrayList<Integer> clickGapList;
    ArrayList<RawClickGapData> CGList;
    InputDataFeatures inputData;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        URL_ser = getSharedPreferences("url", Context.MODE_PRIVATE);
        next_Url = URL_ser.getString("Here", "None");

        if(!next_Url.equals("None")) {
            StringTokenizer token = new StringTokenizer(next_Url, "/");
            token.nextToken();
            DB_Url = token.nextToken();
            DB_Url = DB_Url.replace(".'", "_");
            Log.i(TAG, "DB이름 : " + DB_Url);
            editor_ser = URL_ser.edit();
            editor_ser.remove("Here");
            editor_ser.apply();
        }

        if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            on_touch = 1;
            if(!touch_flag) {
                if(!gap_flag) {
                    clickGapList = new ArrayList<>();
                    CGList = new ArrayList<>();
                    inputData = new InputDataFeatures();

                    gap_flag = true;
                    startTime = System.currentTimeMillis();
                    inputData.TimeStamp = startTime;
                    inputData.WebName = DB_Url;
                    inputData.Type = "CLICK GAP";
                }
                else {
                    endTime = System.currentTimeMillis();
                    timeDifference = endTime - startTime;

                    if(timeDifference < 3000) {
                        Log.d(TAG, "CLICK GAP : " + timeDifference);

                        RawClickGapData rawCGData = new RawClickGapData();
                        rawCGData.TimeStamp = endTime;
                        rawCGData.RawGap = timeDifference;
                        CGList.add(rawCGData);

                        clickGapSum += timeDifference;
                        clickGapCounter++;
                    }
                    startTime = endTime;
                }
                touch_flag = true;
            }
            else    touch_flag = false;
        }
        else if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED && on_touch == 1) {
            if(clickGapCounter == 0)       return;

            inputData.ClickGap = clickGapSum / clickGapCounter;
            DataFeaturesRepo dataRepo = new DataFeaturesRepo(getApplicationContext());
            int index = dataRepo.insert(inputData);
            saveClickGapData(index);

            RawClickGapRepo CGRepo = new RawClickGapRepo(getApplicationContext());
            CGRepo.insert(CGList);

            clickGapSum = 0;
            clickGapCounter = 0;
        }
        if(nodeInfo == null)    return;
    }
    public void saveClickGapData(int index) {
        for(RawClickGapData data : CGList) {
            data.idx = index;
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected(){
        super.onServiceConnected();
        Log.i(TAG, "@@@@@@@@@@ on Service Connected @@@@@@@@@@");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        this.setServiceInfo(info);
    }

}
