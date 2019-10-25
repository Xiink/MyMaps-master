package com.example.user.mymaps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText TIET_Name, TIET_Pass;
    private TextInputLayout TIL_Name, TIL_Pass;
    private Button Llogin;
    private TextView LRegiserLink;;
    private final static String mUrl = "http://35.184.29.240:80/getusers.php";
    private RequestQueue mQueue;
    boolean success = false;
    private JSONArray data;
    private static final int RESULT_FROM_LOGIN = 65200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TIL_Name = (TextInputLayout)findViewById(R.id.TIL_login_Username);
        TIET_Name = (TextInputEditText)findViewById(R.id.TIET_login_Username);
        TIL_Pass = (TextInputLayout)findViewById(R.id.TIL_login_Password);
        TIET_Pass = (TextInputEditText)findViewById(R.id.TIET_login_Password);

        LRegiserLink = (TextView) findViewById(R.id.LRegisterHere);
        Llogin = (Button) findViewById(R.id.Llogin);
        mQueue = Volley.newRequestQueue(getApplicationContext());

        //取得記憶過的帳號
        SharedPreferences setting =
                getSharedPreferences("atm", MODE_PRIVATE);
        TIET_Name.setText(setting.getString("PREF_USERID", ""));

        LRegiserLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳轉至註冊頁面 -- 註冊按鈕
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        Llogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //確認輸入有無錯誤 -- 登入按鈕
                TIET_Name.clearFocus();
                TIET_Pass.clearFocus();
                if(!(TextUtils.isEmpty(TIET_Name.getText().toString()))&&!(TextUtils.isEmpty(TIET_Pass.getText().toString()))){
                    volley_JsonObjectRequestPOST();
                }
                if (TextUtils.isEmpty(TIET_Name.getText().toString())){
                    TIL_Name.setError("請輸入帳號");
                }
                if(TextUtils.isEmpty(TIET_Pass.getText().toString())){
                    TIL_Pass.setError("請輸入密碼");
                }
            }
        });
        //點擊帳號欄事件
        TIET_Name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TIL_Name.setErrorEnabled(false);
                return false;
            }
        });
        //點擊密碼欄事件
        TIET_Pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TIL_Pass.setErrorEnabled(false);
                return false;
            }
        });
    }

    private void volley_JsonObjectRequestPOST(){
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, mUrl,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsondata = data.getJSONObject(i);
                        String username = jsondata.getString("username");
                        String password = jsondata.getString("password");
                        /**進行帳號與密碼的比對*/
                        CheckUser(username,password);
                    }
                    VolleyLogin();
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

    private void CheckUser(String username,String password){
        if(username.equals(TIET_Name.getText().toString())&&password.equals(TIET_Pass.getText().toString())){
            success = true;
        }
    }

    private void VolleyLogin(){
        if(success){
            SharedPreferences setting =
                    getSharedPreferences("atm", MODE_PRIVATE);
            setting.edit()
                    .putString("PREF_USERID", TIET_Name.getText().toString())
                    .commit();
            Toast.makeText(getApplicationContext(), "登入成功!", Toast.LENGTH_LONG).show();
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putString("name",TIET_Name.getText().toString());
            bundle.putBoolean("LogInSuccess",true);
            intent.putExtras(bundle);
            LoginActivity.this.setResult(RESULT_FROM_LOGIN, intent);
            LoginActivity.this.finish();

        }else {
            TIL_Name.setError("帳號或密碼錯誤!");
            TIL_Pass.setError(" ");
        }
    }
}
