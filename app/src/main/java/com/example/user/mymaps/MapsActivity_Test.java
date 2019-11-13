package com.example.user.mymaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MapsActivity_Test extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private TextView textView, textViewAll;
    private Button closeBTbtn, refreshBTbtn;
    private GoogleMap mMap;
    private Switch switch1;
    private ListView listView;
    private LinearLayout Member;
    private LinearLayout BTLinear;
    private ScrollView Scroll_menber;
    private ScrollView Scroll_BT;
    private ConstraintLayout BTlayout;
    int num = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    ArrayList<Marker> Allmarker = new ArrayList<Marker>(); //儲存所有標記
    MarkerOptions markerOptions1 = new MarkerOptions();
    MarkerOptions markerOptions2 = new MarkerOptions();
    Marker mPerth;
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();


    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 100;

    private static final int REQUEST_CHECK_SETTINGS = 500;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    private static final String TAG = MapsActivity_Test.class.getSimpleName();
    boolean key = false;
    boolean canDirection = false;
    ArrayList<Polyline> poly1 = new ArrayList<Polyline>();
    ArrayList<Polyline> poly2 = new ArrayList<Polyline>();
    private Boolean changePoly = true;
    private Boolean canCheck = false;
    private Boolean searchEnd = false;
    //-------------BT------------
    private static final int REQUEST_ENABLE_BT = 62512;
    final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    /**
     * Standard SerialPortService ID
     */
    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    private String send_text = null;
    private String all_text = null;
    private String send_turn = null;
    private String km_text = null;
    private String onlyone = null;
    boolean btnopen = false;

    private String BTDeviceName = null;
    //-------------BT------------

    //-------------PHP-----------
    private final static String mUrl = "http://35.184.29.240:80/conn.php";
    private RequestQueue mQueue;
    public String Username = "";
    public String result = "";  //使用者名稱
    private JSONArray data;
    //-------------PHP-----------

    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private Toolbar toolbar;
    private static final int REQUEST_LOGIN = 65502;
    private static final int REQUEST_GROUP = 65501;
    private static final int RESULT_FROM_LOGIN = 65200;      //確認是否由登入頁面回傳
    private static final int RESULT_FROM_GROUP = 65300;      //確認是否由群組頁面回傳
    private static boolean LogInSuccess = false;  //開啟選擇群組按鈕
    private static boolean openGroup = false;    //開啟群組功能

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps__test);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        textView = (TextView) findViewById(R.id.textView);
        textViewAll = (TextView) findViewById(R.id.textViewAll);
        textViewAll.setVisibility(View.INVISIBLE);
        textViewAll.setMovementMethod(ScrollingMovementMethod.getInstance());
        init();

        //接收使用者帳號
        Bundle bundle = this.getIntent().getExtras();
        //Username = bundle.getString("name");
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestingLocationUpdates = true;

        /*---------------------------------------------Layout------------------------------------------------*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigation = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar4);
        Scroll_menber = (ScrollView) findViewById(R.id.scrollView2);
        Scroll_BT = (ScrollView) findViewById(R.id.scrollView3);
        BTlayout = (ConstraintLayout) findViewById(R.id.BTconstraint);
        Member = (LinearLayout) findViewById(R.id.Member_layout);  //成員列表
        BTLinear = (LinearLayout) findViewById(R.id.BT_layout);  //藍芽列表
        closeBTbtn = (Button) findViewById(R.id.closeBT);
        refreshBTbtn = (Button) findViewById(R.id.refreshBT);
        Scroll_menber.setVisibility(View.INVISIBLE);
        Scroll_BT.setBackgroundColor(Color.WHITE);
        BTlayout.setVisibility(View.INVISIBLE);
        BTlayout.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //關閉藍芽
        closeBTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BT_IsConnected()){
                    try {
                        closeBT();
                        Toast.makeText(getApplicationContext(), "已與設備斷線", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "尚未連接設備", Toast.LENGTH_LONG).show();
                }
            }
        });

        refreshBTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTLinear.removeAllViews();
                BTDeviceName = null;
                searchEnd = false;
                AddBT("已配對過裝置", "", false);
                BTlayout.setVisibility(View.VISIBLE);
                if (!BT_IsConnected()) {
                    if (mBluetoothAdapter.isEnabled()) {
                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                        if (pairedDevices.size() > 0) {
                            for (BluetoothDevice device : pairedDevices) {
                                AddBT(device.getName(), device.getAddress(), true);
                            }
                        }
                    }
                    AddBT("搜尋到的設備", "", false);
                    SearchDevice();
                } else {
                    try {
                        closeBT();
                        Toast.makeText(getApplicationContext(), "已與設備斷線", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //--------------------選單
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 收起選單
                drawerLayout.closeDrawer(GravityCompat.START);
                // 取得選項
                int number = item.getItemId();
                // 依照id判斷點了哪個項目並做相應事件
                switch (number) {
                    //menu BT連線
                    case R.id.action_BTconnect:
                        BTLinear.removeAllViews();
                        BTDeviceName = null;
                        searchEnd = false;
                        closeBTbtn.setEnabled(false);
                        refreshBTbtn.setEnabled(false);
                        if (BTlayout.getVisibility() == View.VISIBLE) {
                            item.setTitle("開啟藍芽設備清單");
                            BTlayout.setVisibility(View.INVISIBLE);
                            //Scroll_BT.setVisibility(View.INVISIBLE);
                        } else {
                            AddBT("已配對過裝置", "", false);
                            item.setTitle("關閉藍芽設備清單");
                            BTlayout.setVisibility(View.VISIBLE);
                            if (!BT_IsConnected()) {
                                if (mBluetoothAdapter.isEnabled()) {
                                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                                    if (pairedDevices.size() > 0) {
                                        for (BluetoothDevice device : pairedDevices) {
                                            AddBT(device.getName(), device.getAddress(), true);
                                        }
                                    }
                                }
                                AddBT("搜尋到的設備", "", false);
                                SearchDevice();
                            } else {
                                try {
                                    closeBT();
                                    Toast.makeText(getApplicationContext(), "已與設備斷線", Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    //menu Map定位
                    case R.id.action_Start:
                        //findBT();
                        //startLocationUpdates();
                        if (btnopen) {
                            handler2.removeCallbacks(moveMap);
                            handler2.postDelayed(moveMap, 500);
                        }
                        break;
                    //menu 開始導航
                    case R.id.action_Direction:
                        if (canDirection == false) {
                            Toast.makeText(getApplicationContext(), "請先建立路線", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        if (key == false) {
                            item.setTitle("暫停導航");
                            key = true;
                        } else if (key == true) {
                            item.setTitle("開始導航");
                            handler2.removeCallbacksAndMessages(null);
                            key = false;
                        }
                        break;
                    //menu 導航資訊
                    case R.id.action_information:
                        if (textViewAll.getVisibility() == View.VISIBLE) {
                            item.setTitle("開啟路線資訊");
                            textViewAll.setVisibility(View.INVISIBLE);
                            ;
                        } else {
                            item.setTitle("關閉路線資訊");
                            textViewAll.setVisibility(View.VISIBLE);
                        }
                        break;
                    //menu 登入
                    case R.id.action_login:
                        if (!LogInSuccess) {
                            Intent intent_login = new Intent(MapsActivity_Test.this, LoginActivity.class);
                            startActivityForResult(intent_login, REQUEST_LOGIN); //REQ_FROM_A(識別碼)
                        } else {
                            TextView logout = navigation.getHeaderView(0).findViewById(R.id.headertext);
                            logout.setText("尚未登入");
                            LogInSuccess = false;
                            item.setTitle("登入");
                        }

                        break;
                    //menu 選擇群組
                    case R.id.action_Chose:
                        if (!LogInSuccess) {
                            Toast.makeText(getApplicationContext(), "請先進行登入!", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        Intent intent_chose = new Intent(MapsActivity_Test.this, GroupActivity.class);
                        intent_chose.putExtra("name", result);
                        startActivityForResult(intent_chose, REQUEST_GROUP);
                        break;

                    case R.id.action_member:
                        AddMember("GA","123",24,120);
                        if (Scroll_menber.getVisibility() == View.VISIBLE) {
                            item.setTitle("開啟成員清單");
                            Scroll_menber.setVisibility(View.INVISIBLE);
                        } else {
                            item.setTitle("關閉成員清單");
                            Scroll_menber.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return false;
            }
        });

        //---------選單
        /*---------------------------------------------Layout------------------------------------------------*/
    }


    /**
     * 位置設定
     */
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            /**只要位置有變動就會進來執行*/
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                latLng1 = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                //按下導航開始畫線
                if (key == true) {
                    timerDirection();
                    Toast.makeText(getApplicationContext(), latLng1.latitude + "" + latLng1.longitude, Toast.LENGTH_SHORT).show();
                }
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        enableMyBluetooth();
    }

    /**
     * 開始Location更新
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        Toast.makeText(getApplicationContext(), "開始準備位置更新", Toast.LENGTH_SHORT).show();
                        btnopen = true;
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MapsActivity_Test.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(MapsActivity_Test.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * 停止Location的更新
     */
    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    //當google地圖準備好自動執行
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        enableMyLocation();

        mMap.setOnMyLocationButtonClickListener(this);
    }

    LatLng latLng1, latLng2, latLng3;

    //點擊地圖
    @Override
    public void onMapClick(LatLng point) {
        canDirection = true;
        if (!canCheck)
            return;
        if (key != true) {
            if (num == 0) {
                //建立標記並加入地圖中
                markerOptions1.position(new LatLng(point.latitude, point.longitude));
                markerOptions1.title("目的地");
                markerOptions1.draggable(true);
                mMap.addMarker(markerOptions1);
                latLng2 = new LatLng(point.latitude, point.longitude);
                //備份座標點
                latLng3 = latLng2;
                String url = getRequestUrl(latLng1, latLng2);
                //串接導航API
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
                num++;
            } else if (num != 0) {
                //初始化
                latLng2 = new LatLng(0, 0);
                latLng3 = new LatLng(0, 0);
                num = 0;
                mMap.clear();
                textView.setText("");
                canDirection = false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "請先暫停導航在選擇目的地", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //製作URL
        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + "key=AIzaSyDlAaBb42EL4x41PTTz_uVyq6uiNxhRSBY";
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map
            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    //印出導航資訊
                    String howlong = point.get("howlong");
                    String AllMessage = point.get("all");
                    String Turn = point.get("Turn");
                    String Km = point.get("Km");
                    String only = point.get("onlyone");
                    //正則
                    AllMessage = AllMessage.replaceAll("(?:<b>|</b>|</div>|<b>|</b>|</div>|/<wbr/>)", " ");
                    AllMessage = AllMessage.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");

                    Turn = Turn.replaceAll("(?:<b>|</b>|</div>|<b>|</b>|</div>|/<wbr/>)", " ");
                    Turn = Turn.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");

                    howlong = howlong.replaceAll("(?:<b>|</b>|</div>|<b>|</b>|</div>|/<wbr/>)", " ");
                    howlong = howlong.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");

                    switch (howlong.charAt(1)) {
                        case 'T':
                            send_text = "T";
                            switch (howlong.charAt(7)) {
                                case 'r':
                                case 'R':
                                    send_text += "R";
                                    break;
                                case 'l':
                                case 'L':
                                    send_text += "L";
                                    break;
                            }
                            break;
                        case 'S':
                            send_text = "T";
                            switch (howlong.charAt(9)) {
                                case 'r':
                                case 'R':
                                    send_text += "R";
                                    break;
                                case 'l':
                                case 'L':
                                    send_text += "L";
                                    break;
                            }
                            break;
                        case 'H':
                            send_text = "H";
                            switch (howlong.charAt(7)) {
                                case 'n':
                                    send_text += "N";
                                    break;
                                case 's':
                                    send_text += "S";
                                    break;
                                case 'w':
                                    send_text += "W";
                                    break;
                                case 'e':
                                    send_text += "E";
                                    break;
                            }
                            break;
                        case 'M':
                            switch (howlong.charAt(9)) {
                                case 'U':
                                    send_text = "TU";
                            }
                            break;
                    }

                    switch (Turn.charAt(1)) {
                        case 'T':
                            send_turn = "T";
                            switch (Turn.charAt(7)) {
                                case 'r':
                                case 'R':
                                    send_turn += "R";
                                    break;
                                case 'l':
                                case 'L':
                                    send_turn += "L";
                                    break;
                            }
                            break;
                        case 'S':
                            send_turn = "T";
                            switch (Turn.charAt(9)) {
                                case 'r':
                                case 'R':
                                    send_turn += "R";
                                    break;
                                case 'l':
                                case 'L':
                                    send_turn += "L";
                                    break;
                            }
                            break;
                        case 'H':
                            send_turn = "H";
                            switch (Turn.charAt(7)) {
                                case 'n':
                                    send_turn += "N";
                                    break;
                                case 's':
                                    send_turn += "S";
                                    break;
                                case 'w':
                                    send_turn += "W";
                                    break;
                                case 'e':
                                    send_turn += "E";
                                    break;
                            }
                            break;
                        case 'M':
                            switch (Turn.charAt(9)) {
                                case 'U':
                                    send_turn = "TU";
                            }
                            break;
                    }
                    km_text = Km;
                    all_text = AllMessage;
                    onlyone = only;
                    points.add(new LatLng(lat, lon));
                }
                //防止上個路線資訊殘留
                if (poly1 != null && poly2 != null) {
                    for (Polyline line : poly1) {
                        line.remove();
                    }
                    for (Polyline line : poly2) {
                        line.remove();
                    }

                }
                //繪製路線
                poly1.add(mMap.addPolyline(new PolylineOptions().addAll(points).width(15).color(Color.GREEN).geodesic(true)));
                poly2.add(mMap.addPolyline(new PolylineOptions().addAll(points).width(15).color(Color.GREEN).geodesic(true)));
            }

            String sendBT = send_turn;

            switch (send_turn) {
                case "TR":
                    send_turn = "右轉";
                    break;
                case "TL":
                    send_turn = "左轉";
                    break;
                case "HN":
                    send_turn = "朝北";
                    break;
                case "HS":
                    send_turn = "朝南";
                    break;
                case "HW":
                    send_turn = "朝西";
                    break;
                case "HE":
                    send_turn = "朝東";
                    break;
            }

            switch (send_text) {
                case "TR":
                    send_text = "右轉";
                    break;
                case "TL":
                    send_text = "左轉";
                    break;
                case "HN":
                    send_text = "朝北";
                    break;
                case "HS":
                    send_text = "朝南";
                    break;
                case "HW":
                    send_text = "朝西";
                    break;
                case "HE":
                    send_text = "朝東";
                    break;
            }
            //send_text += km_text;
            //send_turn += km_text;

            if (onlyone == "true") {
                textView.setText(send_text + "走" + km_text + "公尺");
                sendBT = "F" + km_text;
            } else {
                textView.setText(send_text + "走" + km_text + "公尺後向" + send_turn);
                sendBT += km_text;
            }
            if (BT_IsConnected()) {
                try {
                    sendData(sendBT);
                    Log.e(TAG, "onPostExecute: SEND_DATA: " + send_turn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            textViewAll.setText(all_text);

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
    }

    @Override
    public void onCameraIdle() {
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /**請求取得位置權限*/
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        //當找位置權限認可時
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            mRequestingLocationUpdates = true;
                            /**成功開始呼叫位置更新函式*/
                            startLocationUpdates();
                            if (mMap != null) {
                                // Access to the location has been granted to the app.
                                //
                                if (ActivityCompat.checkSelfPermission(MapsActivity_Test.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity_Test.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                mMap.setMyLocationEnabled(true);
                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getApplicationContext(), "請允許位置權限", Toast.LENGTH_LONG).show();
                            //MapsActivity_Test.this.finish();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        } else if (mMap != null) {
            startLocationUpdates();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void timerDirection() {
        ClearMarkers();
        if (changePoly) {
            for (Polyline line : poly1) {
                line.remove();
            }
            poly1.clear();
            changePoly = false;
        } else {
            for (Polyline line : poly2) {
                line.remove();
            }
            poly2.clear();
            changePoly = true;
        }

        markerOptions1.position(new LatLng(latLng3.latitude, latLng3.longitude));
        markerOptions1.title("目的地!");
        markerOptions1.draggable(true);
        mMap.addMarker(markerOptions1);
        String url = getRequestUrl(latLng1, latLng2);
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

    private Runnable moveMap = new Runnable() {
        @Override
        public void run() {
            if (mCurrentLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15.0f));
                Toast.makeText(getApplicationContext(), "定位完成", Toast.LENGTH_SHORT).show();
                canCheck = true;
            } else {
                Toast.makeText(getApplicationContext(), "定位尚未準備完成請稍後", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        unregisterReceiver(mBroadcastReceiver);

        if (mmSocket != null) {
            if (mmSocket.isConnected()) {
                try {
                    closeBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 確認Activity狀態
     *
     * @param requestCode Activity請求
     * @param resultCode  Activity回傳狀態
     * @param data        回傳資料(可為空值)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //當收到藍芽有開
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Result_OK");
                Toast.makeText(getApplicationContext(), "藍芽已開啟", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "為了連接設備請開啟藍芽", Toast.LENGTH_LONG).show();
            }
        }


        /**接收使用者資料*/
        if (requestCode == REQUEST_LOGIN) //確認是否從登入頁面回傳
        {
            if (resultCode == RESULT_FROM_LOGIN) //確認所要執行的動作
            {
                result = data.getExtras().getString("name"); //接收使用者名稱，為了給Group用
                LogInSuccess = data.getExtras().getBoolean("LogInSuccess"); //開啟群組按鈕

                View header = navigation.getHeaderView(0);
                TextView headertext = (TextView) header.findViewById(R.id.headertext);
                headertext.setText(result);
                navigation.getMenu().findItem(R.id.action_login).setTitle("登出");
            }
        }

        if (requestCode == REQUEST_GROUP) //確認是否從群組回傳
        {
            /**開啟群組功能*/
            if (resultCode == RESULT_FROM_GROUP) //確認所要執行的動作
            {
                openGroup = data.getExtras().getBoolean("openGroup");  //開啟群組功能
                Member.removeAllViews();
                volley_JsonObjectRequestPOST();
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    /*---------------------------------------------HttpPOST------------------------------------------------*/
    double longitude = 0;  //經度
    double latitude = 0; //緯度

    private void volley_JsonObjectRequestPOST() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, mUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    data = response.getJSONArray("data");
                    if (openGroup) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsondata = data.getJSONObject(i);
                            String group = jsondata.getString("Gname");
                            String user = jsondata.getString("username");
                            String _longitude = jsondata.getString("longitude");
                            String _latitude = jsondata.getString("latitude");
                            try {
                                longitude = Double.valueOf(_longitude);
                            } catch (Exception e) {
                                e.toString();
                            }
                            try {
                                latitude = Double.valueOf(_latitude);
                            } catch (Exception e) {
                                e.toString();
                            }

                            /**只取出使用者以外的資料加入地圖標記中*/
                            AddMember(group,user,(float)longitude,(float) latitude);
                            AddUser(user);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mQueue.add(getRequest);
    }

    private void AddUser(String user) {
        if (!(user.equals(result))) {
            markerOptions2.position(new LatLng(longitude, latitude));
            markerOptions2.title("Destination!");
            markerOptions2.draggable(true);
            mMap.addMarker(markerOptions2);
            mPerth = mMap.addMarker(markerOptions2);
            Allmarker.add(mPerth);
        }
    }

    private void ClearMarkers() {
        for (int i = 0; i < Allmarker.size(); i++)
            Allmarker.get(i).remove();
    }
    /*---------------------------------------------HttpPOST------------------------------------------------*/


    /*---------------------------------------------Layout------------------------------------------------*/

    /**
     * 加進群組成員
     */
    protected void AddMember(final String group,final String name, final float longitude, final float latitude) {
        RelativeLayout layout = new RelativeLayout(this);
        final TextView text_group = new TextView(this);
        final TextView text_name = new TextView(this);
        final TextView text_longlat = new TextView(this);
        text_group.setText(group);
        text_group.setAutoSizeTextTypeUniformWithConfiguration(6,25,1, TypedValue.COMPLEX_UNIT_DIP);
        text_group.setId(R.id.main_group);
        text_name.setText(name);
        text_name.setAutoSizeTextTypeUniformWithConfiguration(6,25,1, TypedValue.COMPLEX_UNIT_DIP);
        text_name.setId(R.id.main_name);
        text_longlat.setText("long:" + longitude + " lat:" + latitude);
        text_longlat.setAutoSizeTextTypeUniformWithConfiguration(6,25,1, TypedValue.COMPLEX_UNIT_DIP);
        text_longlat.setId(R.id.main_longlat);

        LinearLayout.LayoutParams relativeLayout_parent_params
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams text_parent_group
                = new RelativeLayout.LayoutParams(200, 150);

        RelativeLayout.LayoutParams text_parent_name
                = new RelativeLayout.LayoutParams(200, 150);

        RelativeLayout.LayoutParams text_parent_latlong
                = new RelativeLayout.LayoutParams(800, 150);


        text_parent_group.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text_parent_group.addRule(RelativeLayout.CENTER_VERTICAL);
        text_parent_group.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        text_parent_name.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text_parent_name.addRule(RelativeLayout.CENTER_VERTICAL);
        text_parent_name.addRule(RelativeLayout.RIGHT_OF,R.id.main_group);

        text_parent_latlong.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        text_parent_latlong.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text_parent_latlong.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.addView(text_group, text_parent_group);
        layout.addView(text_name, text_parent_name);
        layout.addView(text_longlat, text_parent_latlong);

        //點擊成員事件
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "點擊成功", Toast.LENGTH_SHORT).show();
            }
        });

        Member.addView(layout, relativeLayout_parent_params);
    }

    /**
     * 加進藍芽設備
     */
    protected void AddBT(final String name, final String MAC, final Boolean canclick) {
        RelativeLayout layout = new RelativeLayout(this);
        final TextView text_name = new TextView(this);
        //final TextView text_longitude = new TextView(this);
        if (canclick) {
            text_name.setText(name + "\n" + MAC);
        } else {
            text_name.setText(name);
            text_name.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
        }


        LinearLayout.LayoutParams relativeLayout_parent_params
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);

        RelativeLayout.LayoutParams text_parent_name
                = new RelativeLayout.LayoutParams(1200, RelativeLayout.LayoutParams.MATCH_PARENT);

        text_name.setTextSize(20);
        text_name.setPadding(20, 0, 0, 0);

        text_parent_name.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text_parent_name.addRule(RelativeLayout.CENTER_VERTICAL);
        text_parent_name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        text_parent_name.setMargins(0, 15, 0, 15);


        layout.addView(text_name, text_parent_name);

        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        //點擊成員事件
        if (canclick) {
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BTDeviceName = null; //初始化
                    try {
                        alert.setTitle("確定要與 " + name + " 進行匹配??")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    //當OK被按下時的動作
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!searchEnd) {
                                            Toast.makeText(getApplicationContext(), "請等候搜尋完成", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        BTDeviceName = name;
                                        if (!BT_IsConnected())
                                            SearchDevice();
                                        else {
                                            try {
                                                closeBT();
                                                Toast.makeText(getApplicationContext(), "已與設備斷線", Toast.LENGTH_LONG).show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                })
                                //取消按鈕
                                .setNegativeButton("cancel", null)
                                .create().show();   //建立浮動對話窗並顯示
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                    }
                    // Toast.makeText(getApplicationContext(), BTDeviceName, Toast.LENGTH_SHORT).show();
                }
            });
        }
        BTLinear.addView(layout, relativeLayout_parent_params);
    }

    /*---------------------------------------------Layout------------------------------------------------*/

    /*---------------------------------------------BT------------------------------------------------*/

    /**
     * 順序 => 有無支援藍芽 => 有無開啟藍芽 => 先搜尋是否有相對應的藍芽裝置存在 => 如果存在判斷是否配對過(當停止搜尋) => 連線
     */

    //確認是否有無支援，並且確認有無開啟(1)
    private void enableMyBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("抱歉!")
                    .setMessage("此裝置並不支援藍芽功能")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "GOGO", Toast.LENGTH_SHORT).show();
                            System.exit(0);
                        }
                    })
                    .show();
        }
        /**
         *已經確認有藍芽設備且沒有開啟藍芽時，會發送請求開啟藍芽
         */
        else if (!mBluetoothAdapter.isEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
    }

    //搜尋裝置(2)
    private void SearchDevice() {
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "請開藍芽，謝謝", Toast.LENGTH_LONG).show();
        } else {
            //沒連線過開啟搜尋功能去尋找配對
            if (mBluetoothAdapter.isDiscovering()) {
                Log.d(TAG, "Canceling discovery.");
                mBluetoothAdapter.cancelDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter();
                discoverDevicesIntent.addAction(mmDevice.ACTION_FOUND);
                discoverDevicesIntent.addAction(mmDevice.ACTION_BOND_STATE_CHANGED);
                discoverDevicesIntent.addAction(mBluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                discoverDevicesIntent.addAction(mBluetoothAdapter.ACTION_DISCOVERY_STARTED);
                registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
                mBluetoothAdapter.startDiscovery();
            }
            if (!mBluetoothAdapter.isDiscovering()) {
                Log.d(TAG, "SearchDevice: ");
                IntentFilter discoverDevicesIntent = new IntentFilter();
                discoverDevicesIntent.addAction(mmDevice.ACTION_FOUND);
                discoverDevicesIntent.addAction(mmDevice.ACTION_BOND_STATE_CHANGED);
                discoverDevicesIntent.addAction(mBluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                discoverDevicesIntent.addAction(mBluetoothAdapter.ACTION_DISCOVERY_STARTED);
                registerReceiver(mBroadcastReceiver, discoverDevicesIntent);        //送給廣播狀態設定
                mBluetoothAdapter.startDiscovery();
            }
        }
    }

    //廣播(3)
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        private boolean _IS_FOUND_DEVICE = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // When discovery finds a device
            Log.d(TAG, "onReceive: " + action);
            //當搜尋結束
            if (mBluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                navigation.getMenu().findItem(R.id.action_BTconnect).setEnabled(true);
                if (BTDeviceName != null) {
                    //有找到
                    if (_IS_FOUND_DEVICE) {
                        //Toast.makeText(getApplicationContext(), "Found Device", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onReceive: Device is founded");
                        BTlayout.setVisibility(View.INVISIBLE);
                        navigation.getMenu().findItem(R.id.action_BTconnect).setTitle("開啟藍芽設備清單");
                    }
                    //沒找到
                    else {
                        Toast.makeText(getApplicationContext(), "無法與設備連線", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onReceive: Device is not found");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "搜尋完畢", Toast.LENGTH_LONG).show();
                    //搜尋完畢才去做配對
                    searchEnd = true;
                    closeBTbtn.setEnabled(true);
                    refreshBTbtn.setEnabled(true);
                }
            }

            if (mBluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                navigation.getMenu().findItem(R.id.action_BTconnect).setEnabled(false);
                if (BTDeviceName != null) {
                    Toast.makeText(getApplicationContext(), "與" + BTDeviceName + "配對中...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "搜尋中...", Toast.LENGTH_LONG).show();
                }
            }
            //當設備找到
            if (mmDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(mmDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                Log.i(TAG, "onReceive: Device:" + device.getName() + " MAC:" + device.getAddress());
                //決定一開始是搜尋並顯示設備清單還是搜尋並配對指定設備
                if (BTDeviceName != null) {
                    //找到設備名叫ESP32test的裝置
                    if (device.getName() != null && device.getName().equals(BTDeviceName)) {
                        _IS_FOUND_DEVICE = true;
                        //沒做過配對時去進行配對
                        if (device.getBondState() != mmDevice.BOND_BONDED) {
                            device.createBond();
                            Toast.makeText(getApplicationContext(), "配對完請重新連線", Toast.LENGTH_LONG).show();
                            //device.createBond();
                            Log.d(TAG, "onReceive: Bound");
                        }
                        //有配對過，準備連線
                        else if (device.getBondState() == mmDevice.BOND_BONDED) {
                            Log.d(TAG, "onReceive: ReadyTOConnect");
                            mmDevice = device;
                            new openBT().execute();
                            Toast.makeText(getApplicationContext(), "找到設備並以連接", Toast.LENGTH_LONG).show();
                            if (mmDevice == null)
                                Toast.makeText(getApplicationContext(), "配對失敗", Toast.LENGTH_SHORT).show();
                        }
                        //已找到須關閉藍芽尋找功能，因為尋找是很耗資源的動作
                        mBluetoothAdapter.cancelDiscovery();
                    }
                } else {
                    //搜尋並加入清單
                        if (device.getName() != null) {
                            AddBT(device.getName(), device.getAddress(), true);
                    }else
                            mBluetoothAdapter.cancelDiscovery();
                }
            }

            if (mmDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                //Toast.makeText(getApplicationContext(),"已配對，請重新連線",Toast.LENGTH_LONG).show();
            }
        }
    };

    //設備連線(4)
    class openBT extends AsyncTask<Void, Void, Void> {
        boolean BTOK = true;

        //Task預先動作
        @Override
        protected void onPreExecute() {
            if (mmDevice != null) {
                try {
                    mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);        //SerialSocket
                    mmOutputStream = mmSocket.getOutputStream();
                    mmInputStream = mmSocket.getInputStream();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "設備尚未開啟", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "openBT: Can't connect to the device", e);
                }
            }
        }

        //Task背景執行
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                mmSocket.connect();
            } catch (IOException e) {
                BTOK = false;
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "onPostExecute: ");
            if (!BTOK)
                Toast.makeText(getApplicationContext(), "設備尚未開啟", Toast.LENGTH_LONG).show();
            //跑完藍芽才去打開導航按鈕
            canDirection = true;
            //連線完成才開始接收資料
            beginListenForData();
            //navigation.getMenu().findItem(R.id.action_BTconnect).setTitle("藍芽斷線");
        }
    }

    //資料監聽----背景線呈
    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "UTF-8");                      /**     傳送編碼*/
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
//                                            myLabel.setText(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }


    //傳送資料
    void sendData(String s) throws IOException {
        String msg = s;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
    }


    //斷開設備
    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

    //socket是否存在或是連線
    private boolean BT_IsConnected() {
        if (mmSocket == null || !mmSocket.isConnected())
            return false;
        else
            return true;
    }

    /*---------------------------------------------BT------------------------------------------------*/
}