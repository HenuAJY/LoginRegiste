package com.example.creationclientdebug.activity;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.creationclientdebug.datatool.DataUpdateTool;
import com.example.creationclientdebug.entity.Message;
import com.example.creationclientdebug.fragment.GroupFragment;
import com.example.creationclientdebug.fragment.MessageFragment;
import com.example.creationclientdebug.fragment.MyInfoFragment;
import com.example.creationclientdebug.receiver.HeartBeatReceiver;
import com.example.creationclientdebug.service.HeartBeatService;
import com.example.loginregiste.R;
import com.henu.entity.Cmder;
import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;

public class MainApp extends AppCompatActivity {

    public static void startActivity(Context context,User user){
        Intent intent =  new Intent(context,MainApp.class);
        intent.putExtra("user",user);
        context.startActivity(intent);
    }

    private User user;
    private BottomNavigationView bottomNavigationView;

    private MessageFragment messagePage;
    private GroupFragment groupPage;
    private MyInfoFragment myInfoPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        initData();
        bottomNavigationView = findViewById(R.id.app_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.tab_messages:
                    showNextFragment(messagePage);

                    break;
                case R.id.tab_groups:
                    showNextFragment(groupPage);
                    break;
                case R.id.tab_my_info:
                    showNextFragment(myInfoPage);
                    break;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.tab_groups);

        registerReceiver();
        startHeartBeatService();
    }

    public void initData(){
        messagePage = MessageFragment.getInstance();
        groupPage = GroupFragment.getInstance();
        myInfoPage = MyInfoFragment.getInstance();
        user = (User)getIntent().getSerializableExtra("user");
//        user = new User();
//        user.setAccount("121181");//*****************************************************调试用，免去登录
    }

    public void showNextFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.app_frame,fragment).commit();
    }

    HeartBeatReceiver receiver;
    public void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(HeartBeatReceiver.ACTION_MSSAGE);
        receiver = new HeartBeatReceiver();

        DataUpdateTool updateData = new UpdateData();
        receiver.bindTool(updateData);
        registerReceiver(receiver,filter);
    }

    public void startHeartBeatService(){
        HeartBeatService.startMe(MainApp.this,user.getAccount());
    }

    public void addBadge(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(()->{
            UserService userService = UserServicePoxy.getInstance();
            User u = userService.queryByAccount(user.getAccount());
            runOnUiThread(()->{
                getIntent().putExtra("user",u);
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        HeartBeatService.stopMe(this);
        super.onDestroy();
    }

    class UpdateData implements DataUpdateTool{

        @Override
        public void update(Cmder cmder) {
            messagePage.updateMsg(cmder);
        }
    }
}
