package com.example.loginregiste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.creationclientdebug.activity.GroupInfoActivity;
import com.example.creationclientdebug.fragment.GroupFragment;
import com.example.debug.ToastUtil;
import com.henu.entity.Group;
import com.henu.poxy.GroupServicePoxy;
import com.henu.service.GroupService;

public class activity_create extends AppCompatActivity implements View.OnClickListener {

    public static void startActivity(Context context,String phoneNum){
        Intent intent = new Intent(context,activity_create.class);
        intent.putExtra("phone",phoneNum);
        context.startActivity(intent);
    }

    Button btn_confirm;
    EditText group_name;
    String phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        phoneNum = getIntent().getStringExtra("phone");
        InitData();
    }

    public void InitData(){
        btn_confirm = findViewById(R.id.btn_confirm);
        group_name = findViewById(R.id.group_name);
        btn_confirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                if(group_name.getText().toString().trim().equals("")){
                    Toast.makeText(activity_create.this,"群名称不可为空，请重新设置！",Toast.LENGTH_LONG).show();
                }else{
                    new Thread(() -> {
                        GroupService groupService = GroupServicePoxy.getInstance();
                        Group group = groupService.setGroup(phoneNum,group_name.getText().toString().trim());
                        if(group!=null){
                            runOnUiThread(()->{
                                ToastUtil.Toast(activity_create.this,"创建成功！");
                            });
                            GroupFragment.getInstance().updateList(group,0);
                            finish();
                        }else{
                            runOnUiThread(() -> {
                                Toast.makeText(activity_create.this,"创建失败，请重新设置！",Toast.LENGTH_LONG).show();
                            });
                        }
                    }).start();

                }
                break;
        }
    }

}
