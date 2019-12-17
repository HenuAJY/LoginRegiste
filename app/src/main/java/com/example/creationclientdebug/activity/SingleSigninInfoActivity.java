package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.loginregiste.R;
import com.henu.entity.Signin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SingleSigninInfoActivity extends AppCompatActivity {

    public static void startActivity(Context c,Signin signin){
        Intent i = new Intent(c,SingleSigninInfoActivity.class);
        i.putExtra("signin",signin);
        c.startActivity(i);
    }

    private Signin signin;

    private ListView lvSignin;

    private ArrayAdapter<String> lvAdapter;

    private List<String> signinStr = new ArrayList<>();

    private Button btnViewOnMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_signin_info);
        loadData();
    }

    private void loadData(){
        signin = (Signin)getIntent().getSerializableExtra("signin");
        signinStr.add("群："+signin.getGroupid());
        signinStr.add("发起人："+signin.getOriginator());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日-hh时mm分");
        signinStr.add("发起时间："+sdf.format(new Date(signin.getTime())));

        signinStr.add("发起人经度："+signin.getLongtitude());
        signinStr.add("发起人纬度："+signin.getLatitude());
        signinStr.add("签到范围："+signin.getRegion());
        signinStr.add("签到经度："+signin.getRlongitude());
        signinStr.add("签到纬度："+signin.getRlatitude());
        signinStr.add("签到结果："+signin.isResult());
        loadView();
    }

    private void loadView(){
        lvSignin = findViewById(R.id.lv_signins);
        lvAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,signinStr);
        lvSignin.setAdapter(lvAdapter);
        btnViewOnMap = findViewById(R.id.btn_view_on_map);
        btnViewOnMap.setOnClickListener(view -> SigninActivity.startActivity(SingleSigninInfoActivity.this,signin,SigninActivity.LOCATIONVIEWER));
    }
}
