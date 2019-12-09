package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.loginregiste.R;
import com.henu.entity.Group;
import com.henu.entity.Signin;
import com.henu.entity.User;
import com.henu.poxy.GroupServicePoxy;
import com.henu.service.GroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示本群群成员列表
 * 1、开一个线程，从服务器获取数据
 * 2、更新本地群成员数据
 * 3、更新列表数据
 * 4、更新视图
 */

public class MembersActivity extends AppCompatActivity {

    public static void startActivity(Context c, Group group){
        Intent i = new Intent(c,MembersActivity.class);
        i.putExtra("group",group);
        c.startActivity(i);
    }

    private Group group;

    private List<String> memberStr = new ArrayList<>();

    private List<User> membersL = new ArrayList<>();

    private ArrayAdapter<String> lAdapter;

    private ListView memberStrL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        lAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,memberStr);

        memberStrL = findViewById(R.id.lv_members);

        memberStrL.setAdapter(lAdapter);

        group = (Group)getIntent().getSerializableExtra("group");

        loadMembers(group.getId());
    }

    /**
     * 更新本地数据
     * @param groupId
     */
    private void loadMembers(int groupId){
        new Thread(()->{
            GroupService groupService = GroupServicePoxy.getInstance();
            membersL = groupService.getMembers(groupId);
            if(membersL.size()!=0){
                runOnUiThread(this::updateStrL);
            }
        }).start();
    }

    /**
     * 更新列表数据
     */
    private void updateStrL(){
        for(User user:membersL){
            if (user.getName()==null){
                memberStr.add("未命名");
            }else{
                memberStr.add(user.getName());
            }
        }
        System.out.println(memberStr);
        updateView();
    }

    /**
     * 更新视图
     */
    private void updateView(){
        lAdapter.notifyDataSetChanged();
    }
}
