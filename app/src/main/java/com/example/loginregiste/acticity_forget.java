package com.example.loginregiste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class acticity_forget extends AppCompatActivity implements View.OnClickListener {

    EditText ed_phone ;
    Button btn_next ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_forget);

        initEvent();

    }
    public void initEvent(){
        ed_phone = findViewById(R.id.phone);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_next:
                if(!ed_phone.getText().toString().trim().equals("")){
                    if(checkTel(ed_phone.getText().toString().trim())){
                        //页面跳转

                        Intent intent = new Intent(acticity_forget.this,activity_foget2.class);
                        //用Bundle携带数据
                        Bundle bundle=new Bundle();
                        //传递name参数为tinyphp
                        bundle.putString("phone", ed_phone.getText().toString());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        Toast.makeText(acticity_forget.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(acticity_forget.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //正则表达式判断手机号
    public boolean checkTel(String tel){
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }
}
