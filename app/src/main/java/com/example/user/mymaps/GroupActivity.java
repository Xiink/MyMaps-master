package com.example.user.mymaps;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import butterknife.internal.Utils;

public class GroupActivity extends AppCompatActivity {

    public String Username = "";  //使用者名稱
    private LinearLayout Group;
    private String TAG = "ONActivity_GroupActivity";

    private final static String GetGroup_Url = "http://35.184.29.240:80/SearchGroup.php";
    private final static String GetGroup_AddUser = ""; //將使用者加入群組
    private RequestQueue mQueue;

    private static final int RESULT_FROM_GROUP = 65300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Group = (LinearLayout) findViewById(R.id.Group_layout); //scrollView內的Group物件
        mQueue = Volley.newRequestQueue(getApplicationContext());
        // Bundle bundle = this.getIntent().getExtras();
        //Username = bundle.getString("name");
        Intent intent = this.getIntent();
        Username = intent.getStringExtra("name"); //從intent內拿取UserName
        Toast.makeText(getApplicationContext(), "使用者" + Username, Toast.LENGTH_LONG).show();
        initToolBar();
        GetGroup group = new GetGroup();
    }


    /**
     * 建立ToolBar
     */
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//這句程式碼使啟用Activity回退功能，並顯示Toolbar上的左側回退圖示
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        /**搜尋功能*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //傳進搜尋欄的字串
            public boolean onQueryTextSubmit(String query) {
                //Log.i(TAG,query);
                Toast.makeText(GroupActivity.this, query, Toast.LENGTH_SHORT).show();
                Group.removeAllViews();
                GetGroup group = new GetGroup(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    /**
     * 返回按鈕功能
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            GroupActivity.this.finish();    //結束Activity
        }
        return true;
    }


    /**
     * @param
     */
    protected void Addbtn() {
        //Layout層設定
        RelativeLayout layout = new RelativeLayout(this);

        Button btn_add = new Button(this);
        btn_add.setText("新建群組");

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "新增", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout.LayoutParams relativeLayout_parent_params
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams button
                = new RelativeLayout.LayoutParams(400, 200);

        button.addRule(RelativeLayout.CENTER_HORIZONTAL);

        layout.addView(btn_add, button);
        /**加入Group*/
        Group.addView(layout, relativeLayout_parent_params);
    }

    protected void AddView(final String name, final String password, final Integer Locked, final Integer color,final Integer max,final  Integer now) {     //TODO : 輸入參數應為密碼 群組名 及是否有密碼
        //Layout層設定
        RelativeLayout layout = new RelativeLayout(this);
        //Button設定
        Button btn_group = new Button(this);
        btn_group.setId(R.id.join_btn);
        //TextView設定
        final TextView text_group = new TextView(this);
        final TextView text_member = new TextView(this);
        text_member.setId(R.id.text_member);
        text_group.setId(R.id.text_group);
        //text顯示名稱
        text_group.setText(name);
        text_group.setGravity(Gravity.CENTER_VERTICAL);
        text_member.setText(now+"/"+max);
        text_member.setGravity(Gravity.CENTER_VERTICAL);
        //text字體大小
        text_group.setTextSize(25);
        text_member.setAutoSizeTextTypeUniformWithConfiguration(6,25,1, TypedValue.COMPLEX_UNIT_DIP);
        //text只能一行
        text_group.setMaxLines(1);
        //text只顯示到末端其餘以...顯示
        text_group.setEllipsize(TextUtils.TruncateAt.END);
        //text_group.setBackgroundColor(Color.WHITE);
        //text_group.setPadding(150, 0, 0, 0);
        //ImageView設定
        ImageView img_group = new ImageView(this);
        img_group.setId(R.id.img_lock);

        if (Locked.equals(1))
            img_group.setImageResource(getResources().getIdentifier("lockicon", "drawable", getPackageName()));
        if (color % 2 == 0) {
            layout.setBackgroundColor(Color.LTGRAY);
        } else {
            layout.setBackgroundColor(Color.GRAY);
        }

        LinearLayout.LayoutParams relativeLayout_parent_params
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        //介面設計:長寬
        RelativeLayout.LayoutParams button_parent_params
                = new RelativeLayout.LayoutParams(300, 200);

        RelativeLayout.LayoutParams text_parent_params
                = new RelativeLayout.LayoutParams(400, 150);

        RelativeLayout.LayoutParams text_parent_params2
                = new RelativeLayout.LayoutParams(200, 150);

        RelativeLayout.LayoutParams img_parent_params
                = new RelativeLayout.LayoutParams(150, 150);


        img_parent_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        img_parent_params.addRule(RelativeLayout.CENTER_VERTICAL);
        img_parent_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        text_parent_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text_parent_params.addRule(RelativeLayout.CENTER_VERTICAL);
        text_parent_params.addRule(RelativeLayout.RIGHT_OF,R.id.img_lock);
        text_parent_params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text_parent_params2.addRule(RelativeLayout.CENTER_VERTICAL);
        text_parent_params2.addRule(RelativeLayout.LEFT_OF,R.id.join_btn);
        button_parent_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        button_parent_params.addRule(RelativeLayout.CENTER_HORIZONTAL);


        //登入按鈕設計
        //輸入密碼對話框
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //進入群組按鈕名稱
        btn_group.setText("加入");
        //按鈕事件
        btn_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                View view;
                //動態載入layout
                LayoutInflater inflater = LayoutInflater.from(GroupActivity.this);
                //輸入欄位
                final View Password_view = inflater.inflate(R.layout.password_layout, null);
                try {
                    if (Locked.equals(1)) {
                        message = "請輸入密碼";
                        view = Password_view;
                    } else {
                        message = null;
                        view = null;
                    }
                    //浮動對話框，確認密碼用
                    alert.setTitle("確定要加入 " + name + " ?")
                            .setMessage(message)
                            .setView(view)
                            //確認按鈕
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                //當OK被按下時的動作
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) (Password_view.findViewById(R.id.password));
                                    if (!Locked.equals(1) || editText.getText().toString().equals(password)) {
                                        //將使用者加入群組
                                        AddUser addUser = new AddUser(Username,"true");
                                        Intent intent = getIntent();
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("openGroup", true);   //回傳值設定
                                        bundle.putString("Groupname",name);
                                        intent.putExtras(bundle);
                                        GroupActivity.this.setResult(RESULT_FROM_GROUP, intent);    //Activity回傳Result
                                        GroupActivity.this.finish();    //結束Activity
                                    } else {
                                        Toast.makeText(getApplicationContext(), "密碼錯誤!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            //取消按鈕
                            .setNegativeButton("cancel", null)
                            .create().show();   //建立浮動對話窗並顯示
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        layout.addView(img_group, img_parent_params);
        layout.addView(text_group, text_parent_params);
        layout.addView(btn_group, button_parent_params);
        layout.addView(text_member,text_parent_params2);

        /**加入Group*/
        Group.addView(layout, relativeLayout_parent_params);
    }


    /**
     * Using  google volley to do HttpRequest.
     */
    private class GetGroup {
        JSONArray data;
        String GroupName, GroupPassword;
        int Locked,Nowmem,Maxmem;

        //建構子，拿到全群組
        public GetGroup() {
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, GetGroup_Url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        data = response.getJSONArray("data");
                        Log.i(TAG, "onResponse: Get Group JSON" + data);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject JSONdata = data.getJSONObject(i);
                            GroupName = JSONdata.getString("Gname");
                            GroupPassword = JSONdata.getString("password");
                            Locked = JSONdata.getInt("Locked");
                            Nowmem = JSONdata.getInt("now_mem");
                            Maxmem = JSONdata.getInt("max_mem");
                            //if(i==0)
                            //   Addbtn();
                            AddView(GroupName, GroupPassword, Locked, i,Maxmem,Nowmem);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: Volley error : ", error);
                }
            });
            mQueue.add(getRequest);
        }

        //重構(搜尋功能)
        public GetGroup(final String name) {
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, GetGroup_Url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        data = response.getJSONArray("data");
                        Log.i(TAG, "onResponse: Get Group JSON" + data);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject JSONdata = data.getJSONObject(i);
                            GroupName = JSONdata.getString("Gname");
                            GroupPassword = JSONdata.getString("password");
                            Locked = JSONdata.getInt("Locked");
                            Nowmem = JSONdata.getInt("now_mem");
                            Maxmem = JSONdata.getInt("max_mem");
                            //if(i==0)
                            //    Addbtn();
                            if (GroupName.indexOf(name) != -1)
                                AddView(GroupName, GroupPassword, Locked, i,Maxmem,Nowmem);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: Volley error : ", error);
                }
            });
            mQueue.add(getRequest);
        }
    }

    private class AddUser {
        //建構子，加入使用者
        public AddUser(String Username,String isjoin) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("Username",Username );
            map.put("addmember", isjoin);
            JSONObject data_send = new JSONObject(map);

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, GetGroup_AddUser, data_send, new Response.Listener<JSONObject>() {
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
            mQueue.add(postRequest);
        }
    }

}
