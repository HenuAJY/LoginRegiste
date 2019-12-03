package com.example.loginregiste;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;
import com.henu.types.ReturnType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class activity_register extends AppCompatActivity implements View.OnClickListener {

    EditText userName, send_phone, confirm_code, password, checkpwd;
    Button btn_confirm, btn_send;
    public EventHandler eh; //事件接收器
    private TimeCount mTimeCount;//计时器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initEvent();
        init();
    }
    private void initEvent(){

        password = findViewById(R.id.passWord);
        checkpwd = findViewById(R.id.checkpwd);
        userName = findViewById(R.id.userName);
        send_phone = findViewById(R.id.send_phone);
        btn_confirm= findViewById(R.id.btn_confirm);
        confirm_code = findViewById(R.id.confirm_code);
        btn_send = findViewById(R.id.btn_send);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_send.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        mTimeCount = new TimeCount(60000, 1000);
    }
    private void init(){
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { //提交验证码成功

                        if(password.getText().toString().trim().equals(checkpwd.getText().toString().trim())){
                            register();
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity_register.this, "两次密码不一致！", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){ //获取验证码成功

                    } else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){ //返回支持发送验证码的国家列表

                    }
                } else{
                    ((Throwable)data).printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity_register.this, "请获取验证码！", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

    }

//Button点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                if(check_isValid()){
                    Send();
                }
                break;
            case R.id.btn_confirm:
                //check_isValid();
                //boolean b = check_isValid();
                //if(b==true)
                //{
                    check();
                //}
                break;
        }
    }

//发送验证码
    public void Send(){
        // SMSSDK.getSupportedCountries();//获取短信目前支持的国家列表
        if(!send_phone.getText().toString().trim().equals("")){
            if (checkTel(send_phone.getText().toString().trim())) {
                SMSSDK.getVerificationCode("+86",send_phone.getText().toString());//获取验证码
                mTimeCount.start();
            }else{
                Toast.makeText(activity_register.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(activity_register.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }

    }

//验证验证码
    public void check(){
        if (!send_phone.getText().toString().trim().equals("")) {
            if (checkTel(send_phone.getText().toString().trim())) {
                if (!confirm_code.getText().toString().trim().equals("")) {
                    SMSSDK.submitVerificationCode("+86",send_phone.getText().toString().trim(),confirm_code.getText().toString().trim());//提交验证
                }else{
                    Toast.makeText(activity_register.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(activity_register.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(activity_register.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }
    }

//判断注册界面输入是否合法
    public boolean check_isValid(){
        if (userName.getText().toString().equals("")) {
            Toast.makeText(activity_register.this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return  false;
        }else if (password.getText().toString().equals("")){
            Toast.makeText(activity_register.this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return  false;
        }else if (checkpwd.getText().toString().equals("")){
            Toast.makeText(activity_register.this, "请验证密码！", Toast.LENGTH_SHORT).show();
            return  false;
        }else
            return  true;
    }

    /**
     * 正则匹配手机号码
     * @param tel
     * @return
     */
    public boolean checkTel(String tel){
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            btn_send.setClickable(false);
            btn_send.setText(l/1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            btn_send.setClickable(true);
            btn_send.setText("获取验证码");
        }
    }

    //注册
    public void register(){
        UserService service = UserServicePoxy.getInstance();
        User user = new User();
        user.setAccount(userName.getText().toString().trim());
        user.setPassword(password.getText().toString().trim());
        user.setPhone(send_phone.getText().toString().trim());
        final String str = service.register(user);
        System.out.println(str);
        if(str.equals(ReturnType.USEREXISTS)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userName.setText("");
                    Toast.makeText(activity_register.this, "该账号名已被注册，请重新输入！", Toast.LENGTH_LONG).show();
                }
            });
        }else if(str.equals(ReturnType.PHONEEXITS)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    send_phone.setText("");
                    Toast.makeText(activity_register.this, "该手机号已被注册，请重新输入！", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity_register.this, "注册成功！", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(activity_register.this, activity_login.class);
                    startActivity(intent);

                }
            });
        }

    }
}

