package com.example.user.mymaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText Rname,RPassowrd;
    private Button RRegister;
    private TextView test;
    private TextView LLoginLink;
    private final static String mUrl = "http://35.184.29.240:80/conn.php";
    private final static String mUrl_S = "http://35.184.29.240:80/get.php";
    private RequestQueue mQueue,mmQueue;
    boolean success = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Rname = (EditText) findViewById(R.id.RUsername);
        RPassowrd = (EditText) findViewById(R.id.RPasspword);
        RRegister = (Button) findViewById(R.id.RRegister);
        LLoginLink = (TextView) findViewById(R.id.LLoginLink);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mmQueue = Volley.newRequestQueue(getApplicationContext());


        RRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //確認輸入有無錯誤
                if(!(TextUtils.isEmpty(Rname.getText().toString()))&&!(TextUtils.isEmpty(RPassowrd.getText().toString())))
                    volley_JsonObjectRequestPOST();
                else
                    Toast.makeText(getApplicationContext(), "請輸入完整!", Toast.LENGTH_LONG).show();
            }
        });

        LLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳轉至註冊頁面
                Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(registerIntent);
            }
        });
    }

    private void volley_JsonObjectRequestPOST_Insert(){
        /**
         * 用map.put來增加JSON內資料
         *
         * */
        //之後可改為
        Map<String,String> map =new HashMap<String, String>();
        map.put("id",Rname.getText().toString());
        map.put("score",RPassowrd.getText().toString());
        map.put("name","Logintest");
        JSONObject data_send = new JSONObject(map);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, mUrl_S,data_send, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, "onResponse: "+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //og.d(TAG, "onErrorResponse: "+error.toString());
            }
        })  {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        postRequest.setTag("volley_JsonObjectRequestPost");
        mQueue.add(postRequest);
    }


    private void volley_JsonObjectRequestPOST(){
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, mUrl,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsondata = data.getJSONObject(i);
                        String id = jsondata.getString("id");
                        String name = jsondata.getString("name");
                        String score = jsondata.getString("score");
                        /**搜尋資料庫有無相同資料*/
                        if(id.equals(Rname.getText().toString())){
                            success = false;
                            break;
                        }
                    }
                    if(success){
                        Toast.makeText(getApplicationContext(), "註冊成功!", Toast.LENGTH_LONG).show();
                        /**若無便新增一位使用者，這邊暫時用新增一筆資料測試*/
                        volley_JsonObjectRequestPOST_Insert();
                        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(registerIntent);
                    }else {
                        Toast.makeText(getApplicationContext(), "帳號已被註冊!", Toast.LENGTH_LONG).show();
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
        mmQueue.add(getRequest);
    }
}
