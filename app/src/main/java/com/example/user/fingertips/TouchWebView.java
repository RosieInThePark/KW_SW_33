package com.example.user.fingertips;

import android.Manifest;
import android.content.SharedPreferences;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.user.fingertips.db.DBHelper;
import com.example.user.fingertips.db.DataFeaturesRepo;
import com.example.user.fingertips.db.InputDataFeatures;
import com.example.user.fingertips.db.RawClickGapData;
import com.example.user.fingertips.db.RawPointData;
import com.example.user.fingertips.db.RawPointRepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.webkit.WebSettings.LOAD_NO_CACHE;

public class TouchWebView extends AppCompatActivity implements View.OnTouchListener, android.os.Handler.Callback {
    public static final String TAG = "POCKY";
    private WebView mWebView;
    private WebSettings settings;
    private String current_Url;
    public RecordService me;

    private SharedPreferences user_name, URL;
    private SharedPreferences.Editor editor;
    long startPositionTime, lastPositionTime, timeDifference = 0;
    long startPositionTime2, lastPositionTime2, timeDifference2 = 0;
    float contact_size = 0;

    boolean flag_second_finger = false;
    boolean flag_first_up = false;
    boolean flag_second_up = false;
    boolean flag_th = false;
    boolean flag_th2 = false;
    float posX1_start = 0, posX1_last = 0, posY1_start = 0, posY1_last = 0;
    float posX2_start = 0, posX2_last = 0, posY2_start = 0, posY2_last = 0;

    static final int NONE = 0, ZOOM = 1, SWIPE = 2;
    int mode = NONE;

    private String DB_Url;
    SwipeLengthSpeedCurvature Swipe;
    String DATABASE_NAME;
    String CSV_NAME;
    ArrayList <InputDataFeatures> list = null;
    boolean printColumnName = true;
    private EditText edit_Url;
    private Button go;
    private ImageButton logout;

    DBHelper dbHelper;
    InputDataFeatures inputData;

    private String path;
    private String currentTime;
    private String Traing_name;
    private String Test_name;
    private String result;
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_touch_web_view);
        Swipe = new SwipeLengthSpeedCurvature();
        user_name = getSharedPreferences("already", Context.MODE_PRIVATE);
        DATABASE_NAME = user_name.getString("name", "NONE");
        CSV_NAME = DATABASE_NAME;
        DATABASE_NAME += ".db";

        Log.d(TAG, "GIT TEST");

        mWebView = (WebView) findViewById(R.id.webView);
        settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);

        mWebView.setScrollBarStyle(mWebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.loadUrl("http://www.naver.com/");
        mWebView.setWebViewClient(new WebViewClientClass());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setOnTouchListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        URL = getApplicationContext().getSharedPreferences("url", Context.MODE_PRIVATE);
        editor = URL.edit();
        editor.putString("Here", mWebView.getUrl());
        editor.apply();
        current_Url = mWebView.getUrl();
        me = new RecordService();
        me.onServiceConnected();
        edit_Url = (EditText) findViewById(R.id.url);
        edit_Url.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String urlString = edit_Url.getText().toString();
                    if (urlString.startsWith("http") != true)
                        urlString = "https://" + urlString;
                    mWebView.loadUrl(urlString);

                    if (!current_Url.equals(mWebView.getUrl())) {
                        current_Url = mWebView.getUrl();
                        editor.putString("Here", mWebView.getUrl());
                        editor.apply();
                    }
                    edit_Url.setText(urlString);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_Url.getWindowToken(), 0);
                    return true;
                }

                return false;
            }
        });

        go = (Button) findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = edit_Url.getText().toString();

                if (urlString.startsWith("http") != true)
                    urlString = "https://" + urlString;
                mWebView.loadUrl(urlString);

                if (!current_Url.equals(mWebView.getUrl())) {
                    current_Url = mWebView.getUrl();
                    editor.putString("Here", mWebView.getUrl());
                    editor.apply();
                }
                edit_Url.setText(urlString);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_Url.getWindowToken(), 0);
            }
        });

        logout = (ImageButton) findViewById(R.id.log);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu p = new PopupMenu(TouchWebView.this, v); //menu
                getMenuInflater().inflate(R.menu.menu, p.getMenu());
                // 이벤트 처리
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    Intent go_intent;

                    @Override
                    public boolean onMenuItemClick(MenuItem item) { //메뉴아이템 클릭시
                        switch (item.getItemId()) {
                            case R.id.logout:
                                go_intent = new Intent(TouchWebView.this, MainActivity.class);
                                startActivity(go_intent);
                                return true;
                            case R.id.end_test:
                                ExportDB();
                                Traing_name = path+"/gogo.csv";
                                Test_name = path+"/"+CSV_NAME+currentTime+".csv";
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@path : "+Test_name);

                                int up_TP = 0, up_TN = 0, up_FP = 0, up_FN = 0;
                                int first = 0, first2 = 0;
                                double[][] train_data = new double[2000][7];
                                double[][] test_data = new double[2000][7];

                                double train_count = 0;
                                double test_count = 0;

                                double[][] for_train = new double[2000][7];
                                double[][] for_test = new double[2000][7];
                                int train_offset = 0;
                                int test_offset = 0;

                                int[] train_label = new int[2000];
                                int[] test_label = new int[2000];

                                int z=0, j=0;
                                try {
                                    //@@@@@@@@@@@@@@@@train
                                    File csvfile = new File(Traing_name);
                                    String line = "";
                                    String cvsSplit = ",";
                                    int row =0, i;
                                    BufferedReader br = new BufferedReader(new FileReader(csvfile));
                                    while ((line = br.readLine()) != null) {
                                        if(first==0){
                                            first++;
                                            continue;
                                        }
                                        // -1 옵션은 마지막 "," 이후 빈 공백도 읽기 위한 옵션
                                        String[] token = line.split(cvsSplit);
                                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@line!! : "+line);
                                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@token!! : "+token);
                                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@18!! : "+Double.parseDouble(token[18].split("\"")[1]));
                                        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@17!! : "+token[3].split("\"")[1]);

                                        if(token[3].split("\"")[1].equals("SWIPE UP")){
                                            train_data[row][0] = Double.parseDouble(token[4].split("\"")[1]);
                                            train_data[row][1] = Double.parseDouble(token[5].split("\"")[1]);
                                            train_data[row][2] = Double.parseDouble(token[7].split("\"")[1]);
                                            train_data[row][3] = Double.parseDouble(token[8].split("\"")[1]);
                                            train_data[row][4] = Double.parseDouble(token[16].split("\"")[1]);
                                            train_data[row][5] = Double.parseDouble(token[17].split("\"")[1]);
                                            train_data[row][6] = Double.parseDouble(token[18].split("\"")[1]);
                                            row++;
                                            System.out.println("train_data[row][6]");
                                            Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22!! : "+Double.parseDouble(token[18].split("\"")[1]));
                                        }
                                        // CSV에서 읽어 배열에 옮긴 자료 확인하기 위한 출력
                                       // for(i=0;i<6;i++)    System.out.print(train_data[row][i] + ",");
                                        //System.out.println("");
                                    }
                                    br.close();
                                    //@@@@@@train data complete
                                    Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@p!! : "+train_data);

                                    train_count = row;
                                    for(i=0;i<train_count;i++){
                                        if(i<(train_count/5)*4){
                                            for(j=0;j<7;j++){
                                                for_train[train_offset][j] = train_data[i][j];
                                            }
                                            train_label[train_offset] = 1;
                                            train_offset++;
                                        }
                                        else{
                                            for(j=0;j<7;j++){
                                                for_test[test_offset][j] = train_data[i][j];
                                            }
                                            test_label[test_offset] = 1;
                                            test_offset++;
                                        }
                                    }

                                    //@@@@@@@@@test data
                                    File test_csvfile = new File(Test_name);
                                    row =0;
                                    BufferedReader test_br = new BufferedReader(new FileReader(test_csvfile));
                                    while ((line = test_br.readLine()) != null) { //@@@@@@@@@@@@@@@@train
                                        // -1 옵션은 마지막 "," 이후 빈 공백도 읽기 위한 옵션
                                        if(first2==0){
                                            first2++;
                                            continue;
                                        }
                                        String[] token = line.split(",");
                                        if(token[3].split("\"")[1].equals("SWIPE UP")){
                                            test_data[row][0] = Double.parseDouble(token[4].split("\"")[1]);
                                            test_data[row][1] = Double.parseDouble(token[5].split("\"")[1]);
                                            test_data[row][2] = Double.parseDouble(token[7].split("\"")[1]);
                                            test_data[row][3] = Double.parseDouble(token[8].split("\"")[1]);
                                            test_data[row][4] = Double.parseDouble(token[16].split("\"")[1]);
                                            test_data[row][5] = Double.parseDouble(token[17].split("\"")[1]);
                                            test_data[row][6] = Double.parseDouble(token[18].split("\"")[1]);
                                            row++;
                                        }
                                        // CSV에서 읽어 배열에 옮긴 자료 확인하기 위한 출력
                                        // for(i=0;i<6;i++)    System.out.print(train_data[row][i] + ",");
                                        //System.out.println("");
                                    }
                                    test_br.close();

                                    test_count = row;

                                    for(i=0;i<test_count;i++){
                                        if(i<(test_count/5)*4){
                                            for(j=0;j<7;j++){
                                                for_train[train_offset][j] = test_data[i][j];
                                            }
                                            train_label[train_offset] = 2;
                                            train_offset++;
                                        }
                                        else{
                                            for(j=0;j<7;j++){
                                                for_test[test_offset][j] = test_data[i][j];
                                            }
                                            test_label[test_offset] = 2;
                                            test_offset++;
                                        }
                                    }
                                    // 데이터 분리 트레이닝=80% 테스트=20%

                                    for_train = nomalization(for_train);
                                    for_test = nomalization(for_test);
                                    //nomalization 완료
                                    int index = 0;
                                    for(j=0;j<test_offset;j++){
                                        index = euclidian(for_train,for_test[j]);
                                        if(test_label[j]==1 && train_label[index]==1) up_TP++;
                                        else if(test_label[j]==1 && train_label[index]==2) up_FP++;
                                        else if(test_label[j]==2 && train_label[index]==1) up_FN++;
                                        else if(test_label[j]==2 && train_label[index]==2) up_TN++;
                                    }

                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@6 : "+up_TP+" "+up_FN+" "+up_FP+" "+up_TN);
                                double under = (double)up_TP+up_FN;

                                double TPR = (double)up_TP/under;
                                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@67 : "+TPR);

                                if(TPR*100 <70){
                                    result = "인증 성공!";
                                }
                                else
                                    result = "인증 실패!" ;

                                openOptionsDialog();
                                return true;
                        }
                        return false;
                    }
                });
                p.show();
            }
        });


        AccessibilityManager acceMng = (AccessibilityManager) getSystemService(Service.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> list = acceMng.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);

        for (int i = 0; i < list.size(); i++) {
            AccessibilityServiceInfo info = list.get(i);
            if (info.getResolveInfo().serviceInfo.packageName.equals(getApplication().getPackageName()))
                return;
        }

        Toast.makeText(getApplicationContext(), "접근성 권한 설정", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 1000);
    }
    private int euclidian(double[][]train, double[]test){
        int index_num = 0;
        int i,j;
        double temp = 0;
        double distance = 0;
        for(i=0;i<train.length;i++){
            temp = Math.sqrt( Math.pow(train[i][0]-test[0],2) + Math.pow(train[i][1]-test[1],2) + Math.pow(train[i][2]-test[2],2) + Math.pow(train[i][3]-test[3],2) + Math.pow(train[i][4]-test[4],2)
                    + Math.pow(train[i][5]-test[5],2) + Math.pow(train[i][6]-test[6],2) );
            if(i==0) distance = temp;
            else if ( distance > temp){
                distance = temp;
                index_num = i;
            }
        }
        return index_num;
    }
    private double[] mean(double[][]data){
        double[] ret = new double[7];
        int i;

        for(i=0;i<data.length;i++){
            ret[0] += data[i][0];
            ret[1] += data[i][1];
            ret[2] += data[i][2];
            ret[3] += data[i][3];
            ret[4] += data[i][4];
            ret[5] += data[i][5];
            ret[6] += data[i][6];
        }
        ret[0] = ret[0]/data.length;
        ret[1] = ret[1]/data.length;
        ret[2] = ret[2]/data.length;
        ret[3] = ret[3]/data.length;
        ret[4] = ret[4]/data.length;
        ret[5] = ret[5]/data.length;
        ret[6] = ret[6]/data.length;
        return ret;
    }
    private double[] std(double[][]data){
        double[] sum = new double[7];
        double[] result = new double[7];
        double diff;
        double[] meanValue = mean(data);
        for (int i = 0; i < data.length; i++) {
            for(int j=0;j<7;j++){
                diff = data[i][j] - meanValue[j];
                sum[j] += diff * diff;
            }
        }
        for(int k=0;k<7;k++){
            result[k] = Math.sqrt(sum[k] / (double)(data.length - 1));
        }
        return result;
    }

    private double[][] nomalization(double[][]data){
        double[][] result = new double[2000][7];
        double[] mean_set = mean(data);
        double[] std_set = std(data);
        for (int i = 0; i < data.length; i++) {
            for(int j=0; j<7;j++){
                result[i][j] = (data[i][j]-mean_set[j])/std_set[j];
            }
        }
        return result;
    }


    private void openOptionsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("테스트종료")
                .setMessage(result)
                .setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                            }
                        }).show();
    }


    public void AppPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "READ/WRITE EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        } else
            Log.d(TAG, "csvFILE READ/WRITE PERMISSION");
    }
    public int InsertDataFeature() {
        DataFeaturesRepo dataRepo = new DataFeaturesRepo(getApplicationContext());

        int idx = dataRepo.insert(inputData);

        return idx;
    }
    public void InsertRawData(String type) {
        RawPointRepo rawRepo = new RawPointRepo(getApplicationContext());

        if(type.equals("SWIPE")) {
            rawRepo.insert(Swipe.Points1);
            Swipe.Points1.clear();
        }
        else if(type.equals("ZOOM")){
            rawRepo.insert(Swipe.Points1);
            rawRepo.insert(Swipe.Points2);
            Swipe.Points1.clear();
            Swipe.Points2.clear();
        }
    }
    @Override
    public boolean onKeyDown(int Keycode, KeyEvent event) {
        if ((Keycode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        if ((Keycode == KeyEvent.KEYCODE_BACK) && (!mWebView.canGoBack())) {
            Toast.makeText(this, "버튼 클릭 이벤트", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setTitle("프로그램 종료")
                    .setMessage("프로그램을 종료하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "EXIT");
                            //insertListToDB();
                            ExportDB();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton("아니오", null).show();
        }
        return super.onKeyDown(Keycode, event);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int pointer_cnt = motionEvent.getPointerCount();
        if (pointer_cnt >= 2)
            pointer_cnt = 2;

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = SWIPE;

                /*Get Touch Down Point X, Y, Contact Size and Time in Millis.*/
                startPositionTime = System.currentTimeMillis();
                posX1_start = motionEvent.getX();
                posY1_start = motionEvent.getY();
                contact_size = motionEvent.getSize();

                /*Save Data in 'InputDataFeatures' object*/
                inputData = new InputDataFeatures();
                inputData.TimeStamp = startPositionTime;
                inputData.WebName = DB_Url;
                inputData.DownPosX1 = posX1_start;
                inputData.DownPosY1 = posY1_start;
                inputData.DownConSize1 = contact_size;

                Log.d(TAG, "Time Stamp :" + inputData.TimeStamp);
                Log.d(TAG, "webName :" + DB_Url);
                Log.d(TAG, "First Touch Location Down :" + posX1_start + "/" + posY1_start);
                Log.d(TAG, "First Touch Down Size :" + contact_size);
                break;
            case MotionEvent.ACTION_UP:
                flag_first_up = true;
                if(motionEvent.getPointerId(0)==0) { //SWIPE or CLICK
                    lastPositionTime = System.currentTimeMillis();
                    timeDifference = lastPositionTime - startPositionTime;
                    posX1_last = motionEvent.getX((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_MASK);
                    posY1_last = motionEvent.getY((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_MASK);
                    contact_size = motionEvent.getSize((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_MASK);

                    inputData.UpPosX1 = posX1_last;
                    inputData.UpPosY1 = posY1_last;
                    inputData.UpConSize1 = contact_size;

                    Log.d(TAG, "First Touch Location Up :" + posX1_last + "/" + posY1_last);
                    Log.d(TAG, "First Touch Down Size : " + contact_size);
                    /*
                    * Calculate the distance from the start point to the last point.
                    * If the result is larger than 50, it is regarded as Swipe
                    */
                    float SwipeDetectValue = (float) Swipe.calLength(posX1_start, posY1_start, posX1_last, posY1_last);

                    if (SwipeDetectValue > 50 && !flag_second_finger && mode == SWIPE) {
                        Swipe.setPoints1((int)System.currentTimeMillis(), posX1_last, posY1_last);
                        float length = (float) Swipe.getLength1();

                        float speed = length / timeDifference;
                        float curvature = Swipe.calCurvatures1();
                        String type = null;

                        if(Math.abs(posX1_start-posX1_last) < Math.abs(posY1_start-posY1_last)) { //Swipe Vertical
                            if(posY1_start<posY1_last) { //Swipe Down
                                inputData.Type = "SWIPE DOWN";
                                type = "SWIPE DOWN";
                                Log.d(TAG, "mode : SWIPE DOWN");
                            }
                            else { //Swipe Up
                                inputData.Type = "SWIPE UP";
                                type = "SWIPE UP";
                                Log.d(TAG, "mode : SWIPE UP");
                            }
                        }
                        else { //Swipe Horizontal
                            if(posX1_start < posX1_last) { //Swipe Right
                                inputData.Type = "SWIPE RIGHT";
                                type = "SWIPE RIGHT";
                                Log.d(TAG, "mode : SWIPE RIGHT");
                            }
                            else { //Swipe Left
                                inputData.Type = "SWIPE LEFT";
                                type = "SWIPE LEFT";
                                Log.d(TAG, "mode : SWIPE LEFT");
                            }
                        }
                        inputData.SwipeLength = length;
                        inputData.SwipeSpeed = speed;
                        inputData.SwipeCurvature = curvature;

                        Log.d(TAG, "SWIPE LENGTH" + length);
                        Log.d(TAG, "SWIPE SPEED" + speed);
                        Log.d(TAG, "SWIPE CURVATURE" + curvature);

                        int idx = InsertDataFeature();
                        Swipe.saveRawData1(idx, type);
                        InsertRawData("SWIPE");
                        flag_first_up = false;
                    } else if (mode != ZOOM) { //MODE IS CLICK
                        Log.d(TAG, "mode : CLICK");
                        inputData.Type = "CLICK";

                        InsertDataFeature();
                        flag_first_up = false;
                    }
                }
                else if(motionEvent.getPointerId(0)==1) {
                    /*
                    * This case is about ZOOM case.
                    * Second Down Finger is Up after the First Down Finger is up
                    */
                    lastPositionTime2 = System.currentTimeMillis();
                    posX2_last = motionEvent.getX((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                    posY2_last = motionEvent.getY((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                    contact_size = motionEvent.getSize((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);

                    inputData.UpPosX2 = posX2_last;
                    inputData.UpPosY2 = posY2_last;
                    inputData.UpConSize2 = contact_size;

                    Log.d(TAG, "Second Touch Location Up : " + posX2_last + "/"+posY2_last);
                    Log.d(TAG, "Second Touch Up Contact Size : " + contact_size);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointer_cnt==1) {
                    if(motionEvent.getPointerId(0)==0) {
                        Swipe.setPoints1(System.currentTimeMillis(), motionEvent.getX(0), motionEvent.getY(0));
                    }
                    else if(motionEvent.getPointerId(0)==1) {
                        Swipe.setPoints2(System.currentTimeMillis(), motionEvent.getX(0), motionEvent.getY(0));
                    }
                }
                else if(pointer_cnt==2) {
                    Swipe.setPoints1(System.currentTimeMillis(), motionEvent.getX(0), motionEvent.getY(0));
                    Swipe.setPoints2(System.currentTimeMillis(), motionEvent.getX(1), motionEvent.getY(1));
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                startPositionTime2 = System.currentTimeMillis();
                flag_second_finger = true;
                posX2_start = motionEvent.getX(1);
                posY2_start = motionEvent.getY(1);
                contact_size = motionEvent.getSize(1);

                inputData.DownPosX2 = posX2_start;
                inputData.DownPosY2 = posY2_start;
                inputData.DownConSize2 = contact_size;

                Log.d(TAG, "Second Touch Location Down : " + posX2_start + "/"+posY2_start);
                Log.d(TAG, "Second Touch Down Contact Size : " + contact_size);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                flag_second_up = true;
                flag_second_finger = false;

                int pointerIdx = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                if(pointerIdx == 0) {
                    posX1_last = motionEvent.getX((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_MASK);
                    posY1_last = motionEvent.getY((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_MASK);
                    contact_size = motionEvent.getSize((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_MASK);

                    inputData.UpPosX1 = posX1_last;
                    inputData.UpPosY1 = posY1_last;
                    inputData.UpConSize1 = contact_size;

                    Log.d(TAG, "First Touch Location Up :" + posX1_last + "/" + posY1_last);
                    Log.d(TAG, "First Touch Down Size : " + contact_size);
                }
                else if(pointerIdx == 1) {
                    lastPositionTime2 = System.currentTimeMillis();
                    posX2_last = motionEvent.getX((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                    posY2_last = motionEvent.getY((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                    contact_size = motionEvent.getSize((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);

                    inputData.UpPosX2 = posX2_last;
                    inputData.UpPosY2 = posY2_last;
                    inputData.UpConSize2 = contact_size;

                    Log.d(TAG, "Second Touch Location Up : " + posX2_last + "/"+posY2_last);
                    Log.d(TAG, "Second Touch Up Contact Size : " + contact_size);
                }
                break;
            default:
                break;
        }

        if (flag_first_up && flag_second_up && !flag_th) {
            timeDifference2 = lastPositionTime2 - startPositionTime2;
            float oldDist = (float) Swipe.calLength(posX1_start, posY1_start, posX2_start, posY2_start);
            float newDist = (float) Swipe.calLength(posX1_last, posY1_last, posX2_last, posY2_last);

            String type = null;
            if (Math.abs(oldDist - newDist) > 100) {
                if (oldDist - newDist > 40) {
                    inputData.Type = "ZOOM OUT";
                    type = "ZOOM OUT";
                    Log.d(TAG, "ZOOM OUT");
                } else {
                    inputData.Type = "ZOOM IN";
                    type = "ZOOM IN";
                    Log.d(TAG, "ZOOM IN");
                }
                float length1 = (float)Swipe.getLength1();
                float speed1 = length1 / timeDifference;
                float length2 = (float)Swipe.getLength2();
                float speed2 = length2 / timeDifference2;
                float curvature1 = Swipe.calCurvatures1();
                Log.d(TAG, "ZOOM TEST");
                float curvature2 = Swipe.calCurvatures2();

                inputData.TimeStamp = startPositionTime;
                inputData.ZoomLength1 = length1;
                inputData.ZoomLength2 = length2;
                inputData.ZoomSpeed1 = speed1;
                inputData.ZoomSpeed2 = speed2;
                inputData.ZoomCurvature1 = curvature1;
                inputData.ZoomCurvature2 = curvature2;

                Log.d(TAG, "Zoom Length : " + length1 + " / " + length2);
                Log.d(TAG, "Zoom Speed : " + speed1 + " / " + speed2);
                Log.d(TAG, "Zoom Curvature : " + curvature1 + " / " + curvature2);

                int idx = InsertDataFeature();
                Swipe.saveRawData1(idx, type+"1");
                Swipe.saveRawData2(idx, type+"2");
                InsertRawData("ZOOM");
            }
            flag_first_up = false;
            flag_second_up = false;
        }
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
Log.d(TAG, "GIT TEST");
        /*Create Database & Table when Application started*/
        //dbHelper = new DBHelper(getApplicationContext());
        //dbHelper.getDBHelper(getApplicationContext()); //open database
        dbHelper = DBHelper.getDBHelper(getApplicationContext());

        AppPermission();
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        //insertListToDB();
        ExportDB();
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void ExportDB(){
        currentTime = getCurrentTime();
        AppPermission();
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/KW_FingerTip";
        File exportDir = new File(path);
        if(!exportDir.exists()) exportDir.mkdir();
        File file = new File(exportDir,CSV_NAME+currentTime+".csv");
        if(file.exists()) printColumnName = false;
        File rawFile = new File(exportDir,CSV_NAME+"_RAW_POINT_"+currentTime+".csv");
        File cgFile = new File(exportDir, CSV_NAME+"_RAW_CG_"+currentTime+".csv");

        SQLiteDatabase db = dbHelper.openDB();

        try{
            Log.d(TAG,"Export DATA FEATURES");
            file.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file,true));
            Cursor cursor = db.rawQuery("SELECT * FROM "+InputDataFeatures.TABLE_NAME, null);
            if(printColumnName) csvWriter.writeNext(cursor.getColumnNames());

            while(cursor.moveToNext()){
                String str[] = {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15),
                cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21),cursor.getString(22),cursor.getString(23),cursor.getString(24)
                        ,cursor.getString(25)};
                csvWriter.writeNext(str);
            }
            csvWriter.close();
            cursor.close();

        }
        catch(Exception ex){
            Log.e(TAG,ex.getMessage(),ex);
        }

        try{
            Log.d(TAG,"Export RAW DATA");
            rawFile.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(rawFile,true));
            Cursor cursor = db.rawQuery("SELECT * FROM "+ RawPointData.TABLE_NAME, null);
            if(printColumnName) csvWriter.writeNext(cursor.getColumnNames());

            while(cursor.moveToNext()){
                String str[] = {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)};
                csvWriter.writeNext(str);
            }
            csvWriter.close();
            cursor.close();

        }
        catch(Exception ex){
            Log.e(TAG,ex.getMessage(),ex);
        }

        try{
            Log.d(TAG,"Export RAW DATA");
            cgFile.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(cgFile,true));
            Cursor cursor = db.rawQuery("SELECT * FROM "+ RawClickGapData.TABLE_NAME, null);
            if(printColumnName) csvWriter.writeNext(cursor.getColumnNames());

            while(cursor.moveToNext()){
                String str[] = {cursor.getString(0), cursor.getString(1), cursor.getString(2)};
                csvWriter.writeNext(str);
            }
            csvWriter.close();
            cursor.close();

        }
        catch(Exception ex){
            Log.e(TAG,ex.getMessage(),ex);
        }

        dbHelper.onUpgrade(db,1,2);
    }
    public String getCurrentTime() {
        Long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        return mFormat.format(mDate);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    private class WebViewClientClass extends WebViewClient {
        @TargetApi(21)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (!current_Url.equals(url)) { //주소바뀜
                editor.putString("Here", url);
                editor.apply();
                DB_Url = Swipe.changeURL(url);
                Log.d(TAG, "DB_URL : " + DB_Url);
            }

            edit_Url.setText(url);
            if (request.isRedirect()) {
                view.loadUrl(url);
                Log.d(TAG, "WEB URL : " + mWebView.getUrl());
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "URL : " + url);
            editor.putString("Here", url);
            editor.apply();
            DB_Url = Swipe.changeURL(url);
            Log.d(TAG, "DB_URL : " + DB_Url);
            edit_Url.setText(url);
            current_Url = url;
            //insertListToDB();
        }
    }
}

