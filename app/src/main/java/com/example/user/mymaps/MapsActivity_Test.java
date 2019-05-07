package com.example.user.mymaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


    private TextView mMyTextView;
    private TextView mChickTextView;
    private TextView textView;
    private Button button, button2,button3;
    private GoogleMap mMap;
    int num = 0, num2 = 0, num3 = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    MarkerOptions markerOptions1 = new MarkerOptions();
    MarkerOptions markerOptions2 = new MarkerOptions();
    Handler handler = new Handler();
    Handler handler2 = new Handler();

    LocationManager mLocationManager;

    Location mylocation;
    Criteria criteria;

    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

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


    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    private String send_text = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps__test);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMyTextView = (TextView) findViewById(R.id.my_text);
        mChickTextView = (TextView) findViewById(R.id.chick_text);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.buttontest);







        //開始導航
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(timerDirection);
                handler.postDelayed(timerDirection, 500);
            }
        });

        //暫停導航
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    findBT();
                    openBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //handler.removeCallbacksAndMessages(null);
                Dexter.withActivity(MapsActivity_Test.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                mRequestingLocationUpdates = true;
                                startLocationUpdates();
                                handler2.removeCallbacks(moveMap);
                                handler2.postDelayed(moveMap, 500);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    // open device settings when the permission is
                                    // denied permanently
                                   // openSettings();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                handler2.removeCallbacksAndMessages(null);
            }
        });

        init();
    }

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

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
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
        mChickTextView.setText("Maker, point=" + point);
        if (num == 0) {
            //建立標記並加入地圖中
            markerOptions1.position(new LatLng(point.latitude, point.longitude));
            markerOptions1.title("Destination!");
            markerOptions1.draggable(true);
            mMap.addMarker(markerOptions1);
            latLng2 = new LatLng(point.latitude, point.longitude);
            mChickTextView.setText("latitude:" + point.latitude + ",longitude:" + point.longitude);
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
                    //正則
                    howlong = howlong.replace("<b>", " ");
                    howlong = howlong.replace("</b>", " ");
                    howlong = howlong.replace("</div>", " ");
                    howlong = howlong.replace("<div style=" + '"' + "font-size:0.9em" + '"' + '>', " ");
                    textView.setText(howlong);
                    try {
                        sendData(howlong);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        //mChickTextView.setText("long pressed, point=" + point);
    }

    @Override
    public void onCameraIdle() {
        //mMyTextView.setText(mMap.getCameraPosition().toString());
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
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        if (num2 == 0) {
            latLng1 = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
            markerOptions2.position(new LatLng(latLng1.latitude, latLng1.longitude));
            markerOptions2.title("Origin!");
            markerOptions2.draggable(true);
            mMap.addMarker(markerOptions2);
            mMyTextView.setText("latitude:" + latLng1.latitude + ",longitude:" + latLng1.longitude);
            num2++;
        } else if (num == 0 && num2 != 0) {
            latLng1 = new LatLng(0, 0);
            mMap.clear();
            num2 = 0;
        } else if (num != 0 && num2 != 0) {
            mMap.clear();
            latLng1 = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
            markerOptions1.position(new LatLng(latLng2.latitude, latLng2.longitude));
            markerOptions1.title("Destination!");
            markerOptions1.draggable(true);
            markerOptions2.position(new LatLng(latLng1.latitude, latLng1.longitude));
            markerOptions2.title("Origin!");
            markerOptions2.draggable(true);
            mMap.addMarker(markerOptions1);
            mMap.addMarker(markerOptions2);
            mMyTextView.setText("latitude:" + latLng1.latitude + ",longitude:" + latLng1.longitude);
            num2++;
            String url = getRequestUrl(latLng1, latLng2);
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);
        }

        return false;
    }


    private Runnable timerDirection = new Runnable() {
        @Override
        public void run() {
            mMap.clear();
            latLng1 = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15.0f));
            markerOptions1.position(new LatLng(latLng3.latitude, latLng3.longitude));
            markerOptions1.title("Destination!");
            markerOptions1.draggable(true);
            markerOptions2.position(new LatLng(latLng1.latitude, latLng1.longitude));
            markerOptions2.title("Origin!");
            markerOptions2.draggable(true);
            mMap.addMarker(markerOptions1);
            mMyTextView.setText("latitude:" + latLng1.latitude + ",longitude:" + latLng1.longitude);
            String url = getRequestUrl(latLng1, latLng2);
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);
            handler.postDelayed(timerDirection, 500);
        }
    };

    private Runnable moveMap = new Runnable() {
        @Override
        public void run() {
            latLng1 = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15.0f));
        }
    };

    void updateMyLocation(Location mylocation){
        latLng1 = new LatLng(mylocation.getLatitude(),mylocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15.0f));
    }


    LocationListener myListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null) {
                mylocation = location;
                button.setText(location+"");
            }
            else
                button.setText("NULL");

            button.setText("TRUE");

            mLocationManager.removeUpdates(myListener);
            updateMyLocation(mylocation);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            button.setText("TRUE");
        }

        @Override
        public void onProviderEnabled(String provider) {
            button.setText("TRUE");
        }

        @Override
        public void onProviderDisabled(String provider) {
            button.setText("TRUE");
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
        String best = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(best, 1000, 10, myListener);
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }











    /**     搜尋並配對 ESP32test 設備*/
    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /**     確認有無藍芽功能*/
        if (mBluetoothAdapter == null) {
            //myLabel.setText("No bluetooth adapter available");
            Toast.makeText(this,"當前設備並無藍芽功能",Toast.LENGTH_LONG).show();
        }

        /**     確認當前有無開啟藍芽，若無則發送請求開啟藍芽*/
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        /**     有開啟藍芽，且有搜尋到指定設備則連接*/
        if (mBluetoothAdapter.isEnabled()){
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("ESP32test")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            Toast.makeText(this,"已連接藍芽設備",Toast.LENGTH_LONG).show();
//            myLabel.setText("Bluetooth Device Found");
        }
    }

    /**     設備連接*/
    void openBT() throws IOException{
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); /**        Standard SerialPortService ID*/
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
        } catch (Exception e) {
            Log.e(TAG, "openBT: Can't connect to the device", e);
        }

        beginListenForData();

//        myLabel.setText("Bluetooth Opened");
    }


    /**     資料監聽*/
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

    /**     傳送資料*/
    void sendData(String s) throws IOException {
        String msg = s;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
//        myLabel.setText("Data Sent");
    }

    /**     斷開設備*/
    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
//        myLabel.setText("Bluetooth Closed");
    }









}






