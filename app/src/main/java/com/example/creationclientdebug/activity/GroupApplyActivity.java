package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.loginregiste.R;
import com.henu.entity.Cmder;
import com.henu.entity.Group;
import com.henu.entity.User;

import java.util.ArrayList;
import java.util.List;

public class GroupApplyActivity extends AppCompatActivity {

    public static void startActivity(Context context, List<Cmder> cmderList){
        Intent i = new Intent(context,GroupApplyActivity.class);
        context.startActivity(i);
        cmders = cmderList;
    }

    private static List<Cmder> cmders = new ArrayList<>();
    private List<String> applyStrList = new ArrayList<>();

    private ListView lapplyList;
    private ArrayAdapter<String> applyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_apply);

        applyAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,applyStrList);
        lapplyList = findViewById(R.id.LapplyList);
        lapplyList.setAdapter(applyAdapter);

        updateData();

        lapplyList.setOnItemClickListener((adapterView, view, i, l) -> {
            ApplyInfoActivity.startActivity(GroupApplyActivity.this,cmders.get(i));
        });
    }

    public void updateData(){
        for(Cmder cmder:cmders){
            User applyer = cmder.getUser();
            Group group = cmder.getGroup();
            applyStrList.add("申请人:"+applyer.getName()+",申请群:"+group.getName()+",状态:");
        }
        applyAdapter.notifyDataSetChanged();
    }
}
