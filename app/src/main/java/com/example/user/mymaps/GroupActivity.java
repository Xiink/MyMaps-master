package com.example.user.mymaps;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GroupActivity extends AppCompatActivity {

    public String Username="";  //使用者名稱
    private LinearLayout Group;
    String name="";  //群組名稱
    private static final int RESULT_FROM_GROUP = 65300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Group = (LinearLayout) findViewById(R.id.Group_layout);

        for(int i=0;i<=20;i++)
            AddView(i);
        // Bundle bundle = this.getIntent().getExtras();
        //Username = bundle.getString("name");
        Intent intent = this.getIntent();
        Username = intent.getStringExtra("name");
        Toast.makeText(getApplicationContext(), "使用者"+Username, Toast.LENGTH_LONG).show();
        initToolBar();
    }


    /**建立ToolBar*/
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            //傳進搜尋欄的字串
            public boolean onQueryTextSubmit(String query) {
                //Log.i(TAG,query);
                Toast.makeText(GroupActivity.this,query,Toast.LENGTH_SHORT).show();
                Group.removeAllViews();
                int count = Integer.parseInt(query);
                for(int i=0;i<count;i++)
                    AddView(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    /**返回按鈕功能*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            //Log.i(TAG,"back");
            Toast.makeText(this,"back",Toast.LENGTH_SHORT).show();

        }
        return true;
    }


    protected void AddView(int i){
        RelativeLayout layout = new RelativeLayout(this);
        Button btn_group = new Button(this);
        final TextView text_group = new TextView(this);
        ImageView img_group = new ImageView(this);

        LinearLayout.LayoutParams relativeLayout_parent_params
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        /**介面設計*/
        RelativeLayout.LayoutParams button_parent_params
                = new RelativeLayout.LayoutParams(400,500);

        RelativeLayout.LayoutParams text_parent_params
                = new RelativeLayout.LayoutParams(500,150);

        RelativeLayout.LayoutParams img_parent_params
                = new RelativeLayout.LayoutParams(150,150);

        button_parent_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        text_parent_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        img_parent_params.addRule(RelativeLayout.CENTER_VERTICAL);

        /**群組按鈕設計*/
        final AlertDialog.Builder alert  = new AlertDialog.Builder(this);
        btn_group.setText("群組"+i);
        btn_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(GroupActivity.this);
                final View Password_view = inflater.inflate(R.layout.password_layout, null);
                try {
                        alert.setTitle("請輸入密碼")
                            .setView(Password_view)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳轉
                                    EditText editText = (EditText) (Password_view.findViewById(R.id.password));
                                    if (editText.getText().toString().equals("9999")) {
                                        //Intent registerIntent = new Intent(GroupActivity.this, MapsActivity_Test.class);
                                        //registerIntent.putExtra("name", Username);
                                        //GroupActivity.this.startActivity(registerIntent);
                                        Intent intent = getIntent();
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("openGroup",true);
                                        intent.putExtras(bundle);
                                        GroupActivity.this.setResult(RESULT_FROM_GROUP, intent);
                                        GroupActivity.this.finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "密碼錯誤!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).setNegativeButton("cancel", null).create().show();
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        name="";
        name = "BCVBCVBCBCVBCBVCVBCVBCVBCVBCVBCVBCVBCVBC";
        //名稱大於6個簡化
        if(name.length()>6)
            text_group.setText(name.substring(0,4)+"....");
        text_group.setTextSize(25);
        text_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tot=Toast.makeText(GroupActivity.this,name,Toast.LENGTH_LONG);
                tot.show();
            }
        });

        /**有無密碼鎖*/
        int resID = getResources().getIdentifier("lockicon", "drawable",getPackageName());
        img_group.setImageResource(resID);

        layout.addView(btn_group,button_parent_params);
        layout.addView(text_group,text_parent_params);
        layout.addView(img_group,img_parent_params);

        /**加入Group*/
        Group.addView(layout,relativeLayout_parent_params);
    }

}
