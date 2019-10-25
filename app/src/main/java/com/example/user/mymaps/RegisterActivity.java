package com.example.user.mymaps;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText TIET_Name, TIET_Pass;
    private TextInputLayout TIL_Name, TIL_Pass;
    private Button RRegister;
    private TextView test;
    private TextView LLoginLink;
    private final static String mUrl = "http://35.184.29.240:80/getusers.php";      //確認帳號是否存在
    private final static String mUrl_S = "http://35.184.29.240:80/Register.php";    //註冊
    private RequestQueue mQueue;
    boolean ACCOUNT_IS_EXIST = false;
    private String TAG = "ONActivity_RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RRegister = (Button) findViewById(R.id.RRegister);
        LLoginLink = (TextView) findViewById(R.id.LLoginLink);
        mQueue = Volley.newRequestQueue(getApplicationContext());

        TIL_Name = (TextInputLayout) findViewById(R.id.TIL_register_Username);
        TIET_Name = (TextInputEditText) findViewById(R.id.TIET_register_Username);
        TIL_Pass = (TextInputLayout) findViewById(R.id.TIL_register_Password);
        TIET_Pass = (TextInputEditText) findViewById(R.id.TIET_register_Password);

        TIET_Name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ACCOUNT_IS_EXIST)
                    TIL_Name.setErrorEnabled(false);
                return false;
            }
        });

        //註冊按鈕
        RRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TIET_Name.clearFocus();
                TIET_Pass.clearFocus();
                //重置判斷
                ACCOUNT_IS_EXIST = false;
                //確認帳號或密碼是否沒輸入
                if (!(TextUtils.isEmpty(TIET_Name.getText().toString())) && !(TextUtils.isEmpty(TIET_Pass.getText().toString())))
                    volley_JsonObjectRequestPOST();
                else
                    Toast.makeText(getApplicationContext(), "請輸入完整!", Toast.LENGTH_LONG).show();
            }
        });

        //轉跳Link
        LLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳轉至註冊頁面
                Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(registerIntent);
            }
        });
    }

    //註冊Volley
    private void volley_JsonObjectRequestPOST_Insert() {
        /**
         * 用map.put來增加JSON內資料
         *
         * */
        //之後可改為
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", TIET_Name.getText().toString());
        map.put("password", TIET_Pass.getText().toString());
        map.put("online", "0");
        JSONObject data_send = new JSONObject(map);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, mUrl_S, data_send, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ",error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        postRequest.setTag("volley_JsonObjectRequestPost");
        mQueue.add(postRequest);
    }


    //比對Volley
    private void volley_JsonObjectRequestPOST() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, mUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsondata = data.getJSONObject(i);
                        String Username = jsondata.getString("username");
                        //搜尋資料庫有無相同帳號，有則變true
                        if (TIET_Name.getText().toString().equals(Username)) {
                            ACCOUNT_IS_EXIST = true;
                            break;
                        }
                    }
                    //如果帳號不存在
                    if (!ACCOUNT_IS_EXIST) {
                        Toast.makeText(getApplicationContext(), "註冊成功!", Toast.LENGTH_LONG).show();
                        /**若無便新增一位使用者，這邊暫時用新增一筆資料測試*/
                        volley_JsonObjectRequestPOST_Insert();
                        //Activity轉跳
                        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(registerIntent);
                        RegisterActivity.this.finish();     //結束Activity
                    } else {
                        //Toast.makeText(getApplicationContext(), "帳號已被註冊!", Toast.LENGTH_LONG).show();
                        TIL_Name.setError("此帳號已被註冊");
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
}
