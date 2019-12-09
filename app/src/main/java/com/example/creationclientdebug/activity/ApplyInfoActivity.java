package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.debug.ToastUtil;
import com.example.loginregiste.R;
import com.henu.entity.Cmder;
import com.henu.entity.Group;
import com.henu.entity.User;
import com.henu.poxy.GroupServicePoxy;
import com.henu.service.GroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * 加群申请的详细信息页
 */
public class ApplyInfoActivity extends AppCompatActivity {

    public static void startActivity(Context context, Cmder cmder){
        Intent i = new Intent(context,ApplyInfoActivity.class);
        context.startActivity(i);
        data = cmder;
    }

    private static Cmder data;

    private User applyer;
    private Group group;

    private TextView tUserAccount,tUserName,tUserUniversity,tUserCollege,tUserMayjor,tUserPhone,tUserEmail;
    private TextView tGroupId,tGroupName,tGroupOwner;
    private Button bReject,bAgree;

    private Listener listener = new Listener();

    /****************************/

    private ListView lvApplyInfo;

    private ArrayAdapter<String> lvAdapter;

    private List<String> applyInfo = new ArrayList<>();

    /****************************/

    private GroupService groupService = GroupServicePoxy.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_info);

        bReject = findViewById(R.id.BRejectApply);
        bReject.setOnClickListener(listener);

        bAgree = findViewById(R.id.BAgree);
        bAgree.setOnClickListener(listener);

        initView();
        initData();
    }

    public void initView(){
//        tUserAccount = findViewById(R.id.tv_user_account);
//        tUserName = findViewById(R.id.tv_user_name);
//        tUserUniversity = findViewById(R.id.tv_user_university);
//        tUserCollege = findViewById(R.id.tv_user_college);
//        tUserMayjor = findViewById(R.id.tv_user_mayjor);
//        tUserPhone = findViewById(R.id.tv_user_phone);
//        tUserEmail = findViewById(R.id.tv_user_email);

//        tGroupId = findViewById(R.id.tv_group_id);
//        tGroupName = findViewById(R.id.tv_group_name);
//        tGroupOwner = findViewById(R.id.tv_group_owner);

        lvApplyInfo = findViewById(R.id.lv_apply_info);
        lvAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,applyInfo);
        lvApplyInfo.setAdapter(lvAdapter);
    }

    public void initData(){
        applyer = data.getUser();
        group = data.getGroup();

//        tUserAccount.setText("账号:"+applyer.getAccount());
//        tUserName.setText("姓名:"+applyer.getName());
//        tUserUniversity.setText("学校:"+applyer.getUniversity());
//        tUserCollege.setText("学院:"+applyer.getCollege());
//        tUserMayjor.setText("专业:"+applyer.getMayjor());
//        tUserPhone.setText("手机号:"+applyer.getPhone());
//        tUserEmail.setText("邮箱:"+applyer.getEmail());
//
//        tGroupId.setText("群号:"+group.getId());
//        tGroupName.setText("群名称:"+group.getName());
//        tGroupOwner.setText("群主:"+group.getCreator());

        applyInfo.add("账号："+applyer.getAccount());
        applyInfo.add("姓名："+applyer.getName());
        applyInfo.add("学校："+applyer.getUniversity());
        applyInfo.add("学院："+applyer.getCollege());
        applyInfo.add("专业："+applyer.getMayjor());
        applyInfo.add("手机："+applyer.getPhone());
        if(applyer.getEmail()!=null)
            applyInfo.add("邮箱："+applyer.getEmail());

        applyInfo.add("群名："+group.getName());
        applyInfo.add("群号："+group.getId());
        applyInfo.add("群主："+group.getCreator());

        lvAdapter.notifyDataSetChanged();
    }

    class Listener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.BRejectApply:
                    new Thread(()->{
                        groupService.dealApply(applyer.getAccount(),group.getId(),false);
                    }).start();
                    break;
                case R.id.BAgree:
                    new Thread(()->{
                        groupService.dealApply(applyer.getAccount(),group.getId(),true);
                    }).start();
                    break;
            }
            ToastUtil.Toast(ApplyInfoActivity.this,"已处理！");
            finish();
        }
    }
}
