package com.example.loginregiste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.henu.entity.Group;

public class activity_group extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Group group = (Group)getIntent().getSerializableExtra("group");
        Toast.makeText(activity_group.this,group.getId()+","+group.getName()+","+group.getStartTime()
                +","+group.getCreator(),Toast.LENGTH_LONG).show();
    }
}
