package com.example.user.mymaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GroupActivity extends AppCompatActivity {

    public String Username="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        for(int i=0;i<=20;i++)
            AddView(i);

        Bundle bundle = this.getIntent().getExtras();
        Username = bundle.getString("name");
    }

    protected void AddView(int i){
        LinearLayout Group = (LinearLayout) findViewById(R.id.Group_layout);
        RelativeLayout layout = new RelativeLayout(this);
        Button _group = new Button(this);

        LinearLayout.LayoutParams relativeLayout_parent_params
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams button_parent_params
                = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,300);

        button_parent_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        _group.setText("群組"+i);
        _group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(GroupActivity.this, MapsActivity_Test.class);
                registerIntent.putExtra("name", Username);
                GroupActivity.this.startActivity(registerIntent);
            }
        });

        layout.addView(_group,button_parent_params);
        Group.addView(layout,relativeLayout_parent_params);
    }
}
