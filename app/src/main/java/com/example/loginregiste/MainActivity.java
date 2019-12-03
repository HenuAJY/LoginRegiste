package com.example.loginregiste;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.creationclientdebug.activity.GroupInfoActivity;
import com.henu.entity.Group;
import com.henu.entity.User;
import com.henu.poxy.GroupServicePoxy;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.GroupService;
import com.henu.service.UserService;

public class MainActivity extends Activity implements View.OnClickListener {
    public MainActivity.Madapder madapder;
    public ExpandableListView expandableListView;
    public List<String> allList;
    public List<List<String>>list;
    public List<Group> listGroups1,listGroups2;

    public User user;
    public List<List<Group>> combGroup = new ArrayList<>();//群组对象的二维列表

    Button btn_search,btn_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();
        madapder=new Madapder();
        //自定义适配器
        expandableListView.setAdapter(madapder);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                String name = list.get(groupPosition).get(childPosition);
//                System.out.println(name);
//                Toast.makeText(MainActivity.this,name,Toast.LENGTH_SHORT).show();
                Group group = combGroup.get(groupPosition).get(childPosition);
                while(user==null);//如果user对象为null，一直等待，知道不为null；
                //GroupInfoActivity.startActivity(MainActivity.this,group,user);
                return true;
            }
        });

        //开一个线程来获取群组对象
        new Thread(new Runnable() {
            @Override
            public void run() {
                String phoneNum = getIntent().getStringExtra("phoneNum");
                System.out.println(phoneNum);
                UserService service = UserServicePoxy.getInstance();
                user = service.queryByPhone(phoneNum);

                GroupService groupService = GroupServicePoxy.getInstance();

                System.out.println(user.getAccount());
                listGroups1=groupService.getCreatedGroup(user.getAccount());
                listGroups2=groupService.getJoinedGroup(user.getAccount());

                /*****/
                combGroup.add(listGroups1);
                combGroup.add(listGroups2);
                /*****/

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(listGroups1);
                        initData();
                    }
                });
            }
        }).start();
    }
    private void InitView(){
        expandableListView=(ExpandableListView) findViewById(R.id.expandableListView);

        btn_create = findViewById(R.id.btn_create);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        btn_create.setOnClickListener(this);

        allList=new ArrayList<String>();
        list=new ArrayList<List<String>>();

        list.add(new ArrayList<>());
        list.add(new ArrayList<>());
//        list.get(0).add("组1项1");
//        list.get(1).add("组2项1");
        allList.add("   我发起的签到群");
        allList.add("   我加入的签到群");

    }
    private void initData(){
        for (Group listGroup1:listGroups1
        ){
            String str1=listGroup1.getName();
            list.get(0).add(str1);

        }
        for (Group listGroup2:listGroups2
        ) {
            String str2=listGroup2.getName();
            list.get(1).add(str2);
        }
        madapder.notifyDataSetChanged();
    }

    //Button
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_search:
                Intent i1 = new Intent(MainActivity.this,activity_search.class);
                startActivity(i1);

                break;
            case R.id.btn_create:
                Intent i2 = new Intent(MainActivity.this,activity_create.class);
                startActivity(i2);
                break;
        }
    }
    class Madapder extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return allList.size();

        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return list.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return allList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return list.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupView groupView;
            if(convertView==null){
                groupView=new GroupView();
                //获取一级列表的布局
                convertView=View.inflate(MainActivity.this,R.layout.expandablelistview_first, null);
                //复用控件
                groupView.name=(TextView) convertView.findViewById(R.id.one_name);
                //绑定
                convertView.setTag(groupView);
            }else {
                groupView = (GroupView) convertView.getTag();
            }
            //给控件设置值
            groupView.name.setText(allList.get(groupPosition));
//            expandableListView.collapseGroup(groupPosition);
//            expandableListView.expandGroup(groupPosition);
            return convertView;
        }
        @Override
        public View getChildView(int groupPosition,int childPosition,boolean isLastChild,View convertView,ViewGroup parent)
        {
            ViewHolder Holder;
            if(convertView==null){
                Holder=new ViewHolder();
                //获取二级列表的布局
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.expandablelistview_next,parent,false);
                //convertView=View.inflate(MainActivity.this,R.layout.expandablelistview_next, null);
                //复用控件
                Holder.text_name=(TextView) convertView.findViewById(R.id.tow_name);
                //绑定
                convertView.setTag(Holder);

            }else {
                Holder = (ViewHolder) convertView.getTag();
            }
            //给控件设置值
            Holder.text_name.setText(list.get(groupPosition).get(childPosition));
            //必须重新伸缩之后才能更新数据
            expandableListView.collapseGroup(groupPosition);
            expandableListView.expandGroup(groupPosition);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }
    }
    class ViewHolder{
        TextView text_name;
    }
    class GroupView{
        TextView name;
    }
}



