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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;


public class MapsActivity_Test extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private TextView textView,textViewAll;
    private Button button, button2,button3;
    private GoogleMap mMap;
    private Switch switch1;
    int num = 0, num2 = 0, num3 = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    MarkerOptions markerOptions1 = new MarkerOptions();
    MarkerOptions markerOptions2 = new MarkerOptions();
    Handler handler = new Handler();
    Handler handler2 = new Handler();


    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

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

    //-------------BT------------
    private int REQUEST_ENABLE_BT = 1;
    final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); /**        Standard SerialPortService ID*/
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
    boolean btnopen = false;
    //-------------BT------------



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
        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.buttontest);
        button.setEnabled(false);
        textViewAll.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        switch1 = (Switch) findViewById(R.id.switch1);

        init();
        mRequestingLocationUpdates = true;

        /**請求取得位置權限*/
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        /**成功開始呼叫位置更新函式*/
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(key==false) {
                    button.setText("暫停導航");
                    handler.removeCallbacks(timerDirection);
                    handler.postDelayed(timerDirection, 500);
                    key = true;
                }else if(key==true){
                    button.setText("開始導航");
                    handler.removeCallbacksAndMessages(null);
                    handler2.removeCallbacksAndMessages(null);
                    mRequestingLocationUpdates = false;
                    stopLocationUpdates();
                    key = false;
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
              if(btnopen) {
                  handler2.removeCallbacks(moveMap);
                  handler2.postDelayed(moveMap, 500);
              }

            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switch1.isChecked()){
                    button.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                }else{
                    button.setVisibility(View.INVISIBLE);
                    button2.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
    /**位置設定*/
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
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
    }

    /**開始Location更新*/
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                        btnopen = true;
                        button.setEnabled(true);

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
    /**停止Location的更新*/
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
                    if(key!=true) {
                        if (num == 0) {
                //建立標記並加入地圖中
                markerOptions1.position(new LatLng(point.latitude, point.longitude));
                markerOptions1.title("Destination!");
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
            }
        }else{
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
                    //正則
                    howlong = howlong.replace("<b>", " ");
                    howlong = howlong.replace("</b>", " ");
                    howlong = howlong.replace("</div>", " ");
                    howlong = howlong.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");
                    howlong = howlong.replace("<b>", " ");
                    howlong = howlong.replace("</b>", " ");
                    howlong = howlong.replace("</div>", " ");
                    howlong = howlong.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");

                    AllMessage = AllMessage.replace("<b>", " ");
                    AllMessage = AllMessage.replace("</b>", " ");
                    AllMessage = AllMessage.replace("</div>", " ");
                    AllMessage = AllMessage.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");
                    AllMessage = AllMessage.replace("<b>", " ");
                    AllMessage = AllMessage.replace("</b>", " ");
                    AllMessage = AllMessage.replace("</div>", " ");
                    AllMessage = AllMessage.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");

                    Turn = Turn.replace("<b>", " ");
                    Turn = Turn.replace("</b>", " ");
                    Turn = Turn.replace("</div>", " ");
                    Turn = Turn.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");
                    Turn = Turn.replace("<b>", " ");
                    Turn = Turn.replace("</b>", " ");
                    Turn = Turn.replace("</div>", " ");
                    Turn = Turn.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");

                    switch (howlong.charAt(1)) {
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
                                case 'h':
                                    send_text += "H";
                                    break;

                                case 'T':
                                    send_text = "T";
                                    switch (send_text.charAt(6)) {
                                        case 'r':
                                        case 'R':
                                            send_text += "R";
                                            break;
                                        case 'l':
                                        case 'L':
                                            send_text += "L";
                                            break;
                                    }
                            }
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
                                case 'h':
                                    send_turn += "H";
                                    break;
                            }
                            break;
                        case  'M':
                            switch (Turn.charAt(9)){
                                case 'U':
                                    send_turn = "TU";
                            }
                            break;
                    }
                    km_text = Km;
                    all_text = AllMessage;
                    points.add(new LatLng(lat, lon));
                }
                //繪製路線
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.GREEN);
                polylineOptions.geodesic(true);
            }
            send_text+=km_text;
            send_turn+=km_text;

            if (mmSocket.isConnected()){
                try {
                    sendData(send_turn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.e(TAG, "onPostExecute: SEND_DATA: " + send_turn);
            textView.setText(send_turn);
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
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
      if(textViewAll.getVisibility()==View.VISIBLE)
          textViewAll.setVisibility(View.INVISIBLE);
      else
          textViewAll.setVisibility(View.VISIBLE);
        return false;
    }


    private Runnable timerDirection = new Runnable() {
        @Override
        public void run() {
            mMap.clear();
            latLng1 = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            markerOptions1.position(new LatLng(latLng3.latitude, latLng3.longitude));
            markerOptions1.title("Destination!");
            markerOptions1.draggable(true);
            mMap.addMarker(markerOptions1);
            String url = getRequestUrl(latLng1, latLng2);
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);
            handler.postDelayed(timerDirection, 1500);
        }
    };

    private Runnable moveMap = new Runnable() {
        @Override
        public void run() {
            latLng1 = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15.0f));
        }
    };


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    /*---------------------------------------------BT------------------------------------------------*/

    //尋找配對
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (device.getName().equals("ESP32test")) {
                    mBluetoothAdapter.cancelDiscovery();
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        device.createBond();
                    }
                }
            }
        }
    };


    private void enableMyBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /**
         * 確認有無藍芽功能
         */
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
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
    }


    /**
     * 搜尋 ESP32test 設備
     */
    void findBT() {
        /**
         * 有開啟藍芽，且有搜尋到指定設備則連接
         */
        //藍芽關閉狀態
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
        //藍芽開啟狀態
        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("ESP32test")) {
                        mmDevice = device;
                        Toast.makeText(this, "已配對過", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            //沒連線過開啟搜尋功能去尋找配對
            if (mmDevice == null) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d(TAG, "Canceling discovery.");
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
                }
                if (!mBluetoothAdapter.isDiscovering()) {

                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
                }
            }
        }
    }

    /**
     * 設備連接
     */
    void openBT() throws IOException {
        if (mmDevice != null) {
            try {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                beginListenForData();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "設備尚未開啟", Toast.LENGTH_LONG).show();
                mmSocket.close();
                Log.d(TAG, "doInBackground: SocketClose!");
                Log.e(TAG, "openBT: Can't connect to the device", e);
            }
        } else {
            Log.d(TAG, "openBT: Not already");
            findBT();
        }
    }


    /**
     * 資料監聽
     */
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

    /**
     * 傳送資料
     */
    void sendData(String s) throws IOException {
        String msg = s;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
//        myLabel.setText("Data Sent");
    }

    /**
     * 斷開設備
     */
    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
//        myLabel.setText("Bluetooth Closed");
    }

    /*---------------------------------------------BT------------------------------------------------*/

}





