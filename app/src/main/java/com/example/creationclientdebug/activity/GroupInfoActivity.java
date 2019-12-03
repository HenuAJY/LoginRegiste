package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.debug.ToastUtil;
import com.example.loginregiste.R;
import com.henu.entity.Group;
import com.henu.entity.User;
import com.henu.poxy.GroupServicePoxy;
import com.henu.service.GroupService;

public class GroupInfoActivity extends AppCompatActivity {

    public static final int CREATED_GROUP = 590;
    public static final int JOINED_GROUP = 591;
    public static final int SEARCHED_GROUP = 592;

    public static void startActivity(Context context, Group group, User user,int signal){
        Intent intent = new Intent(context,GroupInfoActivity.class);
        intent.putExtra("group",group);
        intent.putExtra("user",user);
        intent.putExtra("signal",signal);
        System.out.println("GroupInfoActivity_signal:"+signal);
        context.startActivity(intent);
    }

    private Group group;
    private User user;

    private TextView tGroupInfo;

    private Button bSendApply;

    private LinearLayout lineCreatedPan;

    private Button bLaunchSignin;

    private Button bViewMember;

    private Button bViewSigninInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        group = (Group)getIntent().getSerializableExtra("group");
        user = (User)getIntent().getSerializableExtra("user");
        initControl();
        showGroupInfo();
        updateView();
    }

    private void initControl(){
        tGroupInfo = findViewById(R.id.TGroupInfo);
        bSendApply = findViewById(R.id.BSendApply);
        bSendApply.setOnClickListener(view -> {
            new Thread(()->{
                GroupService service = GroupServicePoxy.getInstance();
                service.sendJoinGroup(user.getAccount(),group.getId());
            }).start();
            ToastUtil.Toast(this,"已发送申请，请耐心等候！");
            finish();
        });

        lineCreatedPan = findViewById(R.id.layer_created);
        bLaunchSignin = findViewById(R.id.btn_launch_signin);
        bViewMember = findViewById(R.id.btn_view_member);
        bViewSigninInfo = findViewById(R.id.btn_view_my_signin_info);
    }

    private void showGroupInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("群名称：").append(group.getName());
        sb.append("群号：").append(group.getId());
        sb.append("群主：").append(group.getCreator());
        tGroupInfo.setText(sb.toString());
    }

    private void updateView(){
        int signal = getIntent().getIntExtra("signal",0);
        switch (signal){
            case CREATED_GROUP://查看的是已经创建的群
                //显示“发起签到按钮”
                lineCreatedPan.setVisibility(View.VISIBLE);
                break;
            case JOINED_GROUP://查看的是已经加入的群
                bViewSigninInfo.setVisibility(View.VISIBLE);
                break;
            case SEARCHED_GROUP://查看的是搜索的群
                bSendApply.setVisibility(View.VISIBLE);
                break;
        }
    }
}
