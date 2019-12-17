package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.loginregiste.R;
import com.henu.entity.Cmder;
import com.henu.entity.Group;

import java.util.ArrayList;
import java.util.List;

public class JoinGroupReplayActivity extends AppCompatActivity {

    public static void startActivity(Context c,Cmder cmder){
        Intent i = new Intent(c,JoinGroupReplayActivity.class);
        i.putExtra("cmder",cmder);
        c.startActivity(i);
    }

    private ListView lvJoinApply;

    private List<String> applyInfo =new ArrayList<>();

    private ArrayAdapter applyInfoAdapter;

    private Cmder c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_replay);

        initView();
        updateData();
    }

    private void initView(){
        lvJoinApply = findViewById(R.id.lv_join_apply);
        applyInfoAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,applyInfo);
        lvJoinApply.setAdapter(applyInfoAdapter);
    }

    public void updateData(){
        c = (Cmder) getIntent().getSerializableExtra("cmder");
        Group group = c.getGroup();
        updateView("申请群："+ group.getId());
        if (group.getName()!=null){
            updateView("申请结果：申请成功");
        }else{
            updateView("申请结果：申请失败");
        }
    }

    public void updateView(String item){
        applyInfo.add(item);
        applyInfoAdapter.notifyDataSetChanged();
    }
}
