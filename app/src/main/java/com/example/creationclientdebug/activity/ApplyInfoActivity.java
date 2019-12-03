package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.debug.ToastUtil;
import com.example.loginregiste.R;
import com.henu.entity.Cmder;
import com.henu.entity.Group;
import com.henu.entity.User;
import com.henu.poxy.GroupServicePoxy;
import com.henu.service.GroupService;

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

    private TextView tApplyInfo,tGroupInfo;
    private Button bReject,bAgree;

    private Listener listener = new Listener();

    private GroupService groupService = GroupServicePoxy.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_info);

        tApplyInfo = findViewById(R.id.TApplyInfo);
        tGroupInfo = findViewById(R.id.TGroupInfo);

        bReject = findViewById(R.id.BRejectApply);
        bReject.setOnClickListener(listener);

        bAgree = findViewById(R.id.BAgree);
        bAgree.setOnClickListener(listener);

        init();

    }

    public void init(){
        applyer = data.getUser();
        group = data.getGroup();

        tApplyInfo.setText(applyer.toString());
        tGroupInfo.setText(group.toString());
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
