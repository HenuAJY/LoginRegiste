package com.example.loginregiste;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;
import com.henu.types.ReturnType;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class activity_foget2 extends AppCompatActivity implements View.OnClickListener {
    String phoneNum;
    EditText ed1,ed2;
    Button btn_send,btn_confirm;
    TextView tv1;
    public EventHandler eh; //事件接收器
    private TimeCount mTimeCount;//计时器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foget2);

        InitEvent();
        init();

        Bundle bundle = this.getIntent().getExtras();
        phoneNum = bundle.getString("phone");

        tv1.setText("验证码将发送到"+phoneNum);
    }

    public void InitEvent(){
        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        btn_send = findViewById(R.id.btn_send);
        btn_confirm = findViewById(R.id.btn_confirm);
        tv1 = findViewById(R.id.tv1);
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
                        modify();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){ //获取验证码成功

                    } else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){ //返回支持发送验证码的国家列表

                    }
                } else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_send:
//                SMSSDK.getSupportedCountries();//获取短信目前支持的国家列表
                        SMSSDK.getVerificationCode("+86",phoneNum.toString());//获取验证码
                        mTimeCount.start();
                break;
            case R.id.btn_confirm:
                if (!ed1.getText().toString().trim().equals("")) {
                    SMSSDK.submitVerificationCode("+86",phoneNum.trim(),ed1.getText().toString().trim());//提交验证
                }else{
                    Toast.makeText(activity_foget2.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
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

//修改modify
    public void modify(){
        UserService service = UserServicePoxy.getInstance();
        User user = new User();
        String str = service.changePSDByPhone(phoneNum,ed2.getText().toString());
        if(str.equals(ReturnType.USERNOTFOUND)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity_foget2.this, "该手机号不存在！", Toast.LENGTH_LONG).show();
                }
            });
        }else if(str.equals(ReturnType.SERVERERR)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity_foget2.this, "服务器异常，修改密码失败！", Toast.LENGTH_LONG).show();
                }
            });
        }else if(str.equals(ReturnType.OPTSUCCESS)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity_foget2.this, "密码修改成功！", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(activity_foget2.this, activity_login.class);
                    startActivity(intent);
                }
            });
        }
    }
}
