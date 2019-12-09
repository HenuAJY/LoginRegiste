package com.example.loginregiste;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.creationclientdebug.activity.MainApp;
import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;
import com.henu.types.ReturnType;


public class activity_login extends AppCompatActivity implements View.OnClickListener {

    public static void startActivity(Context c){
        Intent i = new Intent(c,activity_login.class);
        c.startActivity(i);
    }

    Button btn_registe,btn_forget,btn_Login;
    EditText phoneNum, passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitEvent();
    }

    public void InitEvent(){
        btn_registe = findViewById(R.id.btn_registe);
        btn_forget = findViewById(R.id.btn_forget);
        btn_Login = findViewById(R.id.btn_Login);
        btn_forget.setOnClickListener(this);
        btn_registe.setOnClickListener(this);
        btn_Login.setOnClickListener(this);
        phoneNum = findViewById(R.id.phoneNum);
        passWord = findViewById(R.id.passWord);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_registe:
                Intent i1 = new Intent(activity_login.this, activity_register.class);
                startActivity(i1);
                break;
            case R.id.btn_forget:
                Intent i2 = new Intent(activity_login.this, acticity_forget.class);
                startActivity(i2);
                break;
            case R.id.btn_Login:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Login();
                    }
                }).start();

                break;
        }
    }
//Login
    public void Login(){

        UserService service = UserServicePoxy.getInstance();
        User user = new User();
        user.setPhone(phoneNum.getText().toString().trim());
        user.setPassword(passWord.getText().toString().trim());
        final String str = service.login(user);

        if(ReturnType.USERNOTFOUND.equals(str)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity_login.this, "该用户不存在!", Toast.LENGTH_LONG).show();
                }
            });
        }else if(ReturnType.SERVERERR.equals(str)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity_login.this, "服务器异常，请稍后重试!", Toast.LENGTH_LONG).show();
                }
            });
        }else if(ReturnType.ERRORPSD.equals(str)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity_login.this, "密码错误!", Toast.LENGTH_LONG).show();
                }
            });
        }else if(ReturnType.RIGHTPSD.equals(str)){
//            Intent i3 = new Intent(activity_login.this, MainActivity.class);
//            i3.putExtra("phoneNum",phoneNum.getText().toString().trim());
//            startActivity(i3);
            User u = service.queryByPhone(user.getPhone());
            MainApp.startActivity(this,u);
            finish();
        }
    }
}
