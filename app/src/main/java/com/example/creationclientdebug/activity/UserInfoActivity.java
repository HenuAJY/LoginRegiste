package com.example.creationclientdebug.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.loginregiste.R;
import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    public static void startActivity(Context c, String account){
        Intent i = new Intent(c,UserInfoActivity.class);
        i.putExtra("account",account);
        c.startActivity(i);
    }

    private User user;

    private String account;

    private ListView lvUserInfo;

    private Button btnChangeInfo;

    private View.OnClickListener btnListener = new MyBtnListener();

    private ArrayAdapter<String> lvAdapter;

    private List<String> userInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        account = getIntent().getStringExtra("account");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(()->{
            UserService userService = UserServicePoxy.getInstance();
            user = userService.queryByAccount(account);
            runOnUiThread(()->initial());
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userInfo.clear();
    }

    private void initial(){
        String name = "未设置";
        String univer = "未设置";
        String college = "未设置";
        String major = "未设置";
        String email = "未设置";
        if (user.getName()!=null){
            name = user.getName();
        }
        if (user.getUniversity()!=null){
            univer = user.getUniversity();
        }
        if(user.getCollege()!=null){
            college = user.getCollege();
        }
        if (user.getMayjor()!=null){
            major = user.getMayjor();
        }
        if(user.getEmail()!=null)
        {
            email = user.getEmail();
        }
        userInfo.add("姓名："+name);
        userInfo.add("学校："+univer);
        userInfo.add("学院："+college);
        userInfo.add("专业："+major);
        userInfo.add("邮箱："+email);

        lvAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,userInfo);
        initControl();
    }

    private void initControl(){
        lvUserInfo = findViewById(R.id.lv_user_info);
        lvUserInfo.setAdapter(lvAdapter);
        btnChangeInfo = findViewById(R.id.btn_change_info);
        btnChangeInfo.setOnClickListener(btnListener);
    }



    /**
     * 按钮监听器
     */
    class MyBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_change_info://点击了修改信息按钮
                    UserChangeInfoActivity.staticActivity(UserInfoActivity.this,user);
                    break;
            }
        }
    }
}
