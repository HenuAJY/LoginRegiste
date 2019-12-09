package com.example.creationclientdebug.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.creationclientdebug.activity.GroupInfoActivity;
import com.example.loginregiste.MainActivity;
import com.example.loginregiste.R;
import com.example.loginregiste.activity_create;
import com.example.loginregiste.activity_search;
import com.henu.entity.Group;
import com.henu.entity.User;
import com.henu.poxy.GroupServicePoxy;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.GroupService;
import com.henu.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements View.OnClickListener {

    private static GroupFragment instance;
    public Madapder madapder;
    public ExpandableListView expandableListView;
    public List<String> allList;
    public List<List<String>>list;
    public User user;
    public List<List<Group>> combGroup = new ArrayList<>();//群组对象的二维列表
    Button btn_search,btn_create;
    private boolean isFirstLoad = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    public static GroupFragment getInstance(){
        if(instance==null){
            instance = new GroupFragment();
        }
        return instance;
    }

    public GroupFragment() {

    }

    private Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        InitView(view);
        madapder=new Madapder();
        //自定义适配器
        expandableListView.setAdapter(madapder);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Group group = combGroup.get(groupPosition).get(childPosition);
            while(user==null);//如果user对象为null，一直等待，知道不为null；
            if(groupPosition==0){
                GroupInfoActivity.startActivity(getContext(),group,user,GroupInfoActivity.CREATED_GROUP);
            }else if(groupPosition==1){
                GroupInfoActivity.startActivity(getContext(),group,user,GroupInfoActivity.JOINED_GROUP);
            }
            return true;
        });
        loadGroupList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println(combGroup);
        //loadGroupList.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        //loadGroupList.interrupt();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 加载群组信息
     */
    private void loadGroupList(){
        new Thread(()-> {
            user = (User)getActivity().getIntent().getSerializableExtra("user");
            GroupService groupService = GroupServicePoxy.getInstance();
            List<Group> l1 = groupService.getCreatedGroup(user.getAccount());
            List<Group> l2 = groupService.getJoinedGroup(user.getAccount());
            mHandler.post(()->{
                initData(l1,l2);
            });
        }).start();
    }

    private void InitView(View view){
        expandableListView= view.findViewById(R.id.expandableListView);

        btn_create = view.findViewById(R.id.btn_create);
        btn_search = view.findViewById(R.id.btn_search);
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


    /**
     * 向列表中添加群组项
     * @param group 要添加的群组
     * @param i 群组类型，0===创建的群组；1===加入的群组
     */
    private void addGroup(Group group,int i){
        if(isFirstLoad){
            combGroup.add(new ArrayList<>());
            combGroup.add(new ArrayList<>());
            isFirstLoad = false;
        }
        combGroup.get(i).add(group);//更新数据实体
        list.get(i).add(group.getName());
        madapder.notifyDataSetChanged();
    }

    /**
     * 更新群组数据
     * @param group
     * @param i
     */
    public void updateList(Group group,int i){
        mHandler.post(()->{
            addGroup(group,i);
        });
    }

    private void initData(List<Group> gl1,List<Group> gl2){
        for (Group g:gl1){
            addGroup(g,0);
        }
        for (Group g:gl2){
            addGroup(g,1);
        }
    }



    //Button
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_search:
                activity_search.startActivity(getContext(),user);
                break;
            case R.id.btn_create:
                activity_create.startActivity(mContext,user.getPhone());
                break;
        }
    }


    class Madapder extends BaseExpandableListAdapter {

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
                convertView=View.inflate(getContext(),R.layout.expandablelistview_first, null);
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.expandablelistview_next,parent,false);
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
