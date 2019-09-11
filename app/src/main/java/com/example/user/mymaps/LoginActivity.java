package com.example.user.mymaps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

    private EditText Lname,LPassowrd;
    private Button Llogin;
    private TextView LRegiserLink;;
    private final static String mUrl = "http://35.184.29.240:80/conn.php";
    private RequestQueue mQueue;
    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Lname = (EditText) findViewById(R.id.LUsername);
        LPassowrd = (EditText) findViewById(R.id.LPassowrd);
        LRegiserLink = (TextView) findViewById(R.id.LRegisterHere);
        Llogin = (Button) findViewById(R.id.Llogin);
        mQueue = Volley.newRequestQueue(getApplicationContext());

        SharedPreferences setting =
                getSharedPreferences("atm", MODE_PRIVATE);
        Lname.setText(setting.getString("PREF_USERID", ""));

        LRegiserLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳轉至註冊頁面
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        Llogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //確認輸入有無錯誤
                if(!(TextUtils.isEmpty(Lname.getText().toString()))&&!(TextUtils.isEmpty(LPassowrd.getText().toString()))){
                    volley_JsonObjectRequestPOST();
                }else{
                    Toast.makeText(getApplicationContext(), "請完整填寫帳號及密碼", Toast.LENGTH_LONG).show();
                }
            }
        });
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
                        String score = jsondata.getString("score");
                        /**進行帳號與密碼的比對*/
                        if(id.equals(Lname.getText().toString())&&score.equals(LPassowrd.getText().toString())){
                            success = true;
                            break;
                        }
                    }
                    if(success){
                        SharedPreferences setting =
                                getSharedPreferences("atm", MODE_PRIVATE);
                        setting.edit()
                                .putString("PREF_USERID", Lname.getText().toString())
                                .commit();
                        Toast.makeText(getApplicationContext(), "登入成功!", Toast.LENGTH_LONG).show();
                        //成功便跳轉到主頁面
                        Intent registerIntent = new Intent(LoginActivity.this, MapsActivity_Test.class);
                        //將使用者帳號傳送到主頁面
                        registerIntent.putExtra("name", Lname.getText().toString());
                        LoginActivity.this.startActivity(registerIntent);
                    }else {
                        Toast.makeText(getApplicationContext(), "帳號或密碼錯誤!", Toast.LENGTH_LONG).show();
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
