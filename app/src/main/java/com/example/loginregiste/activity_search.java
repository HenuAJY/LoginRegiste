package com.example.loginregiste;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.creationclientdebug.activity.GroupInfoActivity;
import com.example.debug.ToastUtil;
import com.henu.entity.Group;
import com.henu.entity.User;
import com.henu.poxy.GroupServicePoxy;
import com.henu.service.GroupService;

import java.util.ArrayList;
import java.util.List;

public class activity_search extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    public static void startActivity(Context c,User u){
        Intent i = new Intent(c,activity_search.class);
        i.putExtra("user",u);
        c.startActivity(i);
    }

    Button btn_cancle;
    EditText edit_searchFor;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ListView groupListView;

    private List<Group> groups = new ArrayList<>();

    private List<String> groupsStr = new ArrayList<>();

    private ArrayAdapter<String> groupListViewAdapter;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        InitData();
    }

    private void InitData() {
        user = (User)getIntent().getSerializableExtra("user");
        btn_cancle=findViewById(R.id.btn_cancle);
        edit_searchFor=findViewById(R.id.edit_searchFor);
        btn_cancle.setOnClickListener(this);
        edit_searchFor.addTextChangedListener(this);

        groupListView = findViewById(R.id.search_group_list);

        groupListViewAdapter = new ArrayAdapter<String>(activity_search.this,android.R.layout.simple_list_item_1,groupsStr);

        groupListView.setAdapter(groupListViewAdapter);

        groupListView.setOnItemClickListener((adapterView, view, i, l) -> {
            GroupInfoActivity.startActivity(this,groups.get(i),user,GroupInfoActivity.SEARCHED_GROUP);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancle:
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        new Thread(()->{
            GroupService service = GroupServicePoxy.getInstance();
            String key = edit_searchFor.getText().toString();
            System.out.println(key);
            List<Group> groups = service.searchGroup(key);
            mHandler.post(()->{
                updateListView(groups);
            });
        }).start();
    }

    public void updateListView(List<Group> groups){
        System.out.println(groups);
        this.groups = groups;
        groupsStr.clear();
        for (Group group:groups){
            String name = group.getName();
            groupsStr.add(name);
        }
        groupListViewAdapter.notifyDataSetChanged();
    }
}
