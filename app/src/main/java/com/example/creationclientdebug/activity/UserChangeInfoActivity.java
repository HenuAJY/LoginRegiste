package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.debug.ToastUtil;
import com.example.loginregiste.R;
import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;

public class UserChangeInfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static void staticActivity(Context c, User u){
        Intent i = new Intent(c, UserChangeInfoActivity.class);
        i.putExtra("user",u);
        c.startActivity(i);
    }

    private String UserName,School,Collge,Major,E_mail;
    private EditText username,school,collge,major,e_mail;
    private Button submit,cancel;

    private User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change_info);
        Init();
    }

    public void Init(){
        username=findViewById(R.id.name);
        school=findViewById(R.id.school);
        collge=findViewById(R.id.collge);
        major=findViewById(R.id.major);
        e_mail=findViewById(R.id.e_mail);
        submit=findViewById(R.id.submit);
        cancel=findViewById(R.id.cancel);

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);

        u = (User) getIntent().getSerializableExtra("user");
    }

    @Override
    public void onClick(View v) {
        Data();
        switch (v.getId()){
            case R.id.submit:
                if(!(UserName.length()==0)){
                    if(!(School.length()==0)){
                        if(!(Collge.length()==0)){
                            if(!(Major.length()==0)){//满足输入条件
                                new Thread(()->{
                                    UserService userService = UserServicePoxy.getInstance();
                                    u.setName(UserName);
                                    u.setUniversity(School);
                                    u.setCollege(Collge);
                                    u.setMayjor(Major);
                                    try{
                                        userService.changeInfo(u);
                                        runOnUiThread(()->{
                                            ToastUtil.Toast(UserChangeInfoActivity.this,"保存成功");
                                            getIntent().putExtra("user",u);//更新Intent中的user数据
                                        });
                                    }catch (Exception e){
                                        runOnUiThread(()->{
                                            ToastUtil.Toast(UserChangeInfoActivity.this,"保存失败");
                                        });
                                    }
                                }).start();
                                finish();
                            }else{
                                ToastUtil.Toast(UserChangeInfoActivity.this,"专业不能为空！");
                            }
                        }else{
                            ToastUtil.Toast(UserChangeInfoActivity.this,"学院不能为空!");
                        }
                    }else{
                        ToastUtil.Toast(UserChangeInfoActivity.this,"学校不能为空!");
                    }
                }else{
                    ToastUtil.Toast(UserChangeInfoActivity.this,"姓名不能为空!");
                }
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }
    public void Data(){
        UserName=username.getText().toString().trim();
        School=school.getText().toString().trim();
        Collge=collge.getText().toString().trim();
        Major=major.getText().toString().trim();
        E_mail=e_mail.getText().toString().trim();
    }
}
