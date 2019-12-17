package com.example.creationclientdebug.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.creationclientdebug.activity.GroupInfoActivity;
import com.example.debug.ToastUtil;
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
    public List<List<Group>> combGroup;//群组对象的二维列表
    Button btn_search,btn_create;
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

    private MyExpandShortClick shortClickListener = new MyExpandShortClick();

    private GroupService groupService = GroupServicePoxy.getInstance();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView()");
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        InitView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnResume()");
        initData();
        loadGroupList();

    }

    @Override
    public void onPause() {
        super.onPause();
        clearData();
    }

    private void initData(){
        combGroup = new ArrayList<>();
        list = new ArrayList<>();
        list.add(new ArrayList<>());
        list.add(new ArrayList<>());
        madapder=new Madapder();
        //自定义适配器
        expandableListView.setAdapter(madapder);
        expandableListView.setOnChildClickListener(shortClickListener);
    }

    private void clearData(){
        combGroup = null;
        list = null;
        madapder = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /********************************************************/
    int longClickGroupId;
    int longClickChildId;

    String deleteMenu = "删除";
    String quitMenu = "退出";

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        longClickGroupId = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        longClickChildId = ExpandableListView.getPackedPositionChild(info.packedPosition);
        if(type == 1 && longClickGroupId == 0){
            menu.add(deleteMenu);
        }else if(type == 1 && longClickGroupId == 1){
            menu.add(quitMenu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (deleteMenu.equals(title)){
            System.out.println("点击了删除");
            new Thread(()->{
                int groupId = this.longClickGroupId;
                int childId = this.longClickChildId;
                boolean res = groupService.deleteById(combGroup.get(groupId).get(childId).getId());//进行删除分组操作的结果
                mHandler.post(()->{
                    if (res){//删除成功，更新视图
                        clearData();
                        initData();
                        loadGroupList();
                    }else{//删除失败
                        ToastUtil.Toast(getContext(),"删除失败");
                    }
                });
            }).start();
        }else if (quitMenu.equals(title)){
            System.out.println("点击了提出");
            new Thread(()->{
                int groupId = this.longClickGroupId;
                int childId = this.longClickChildId;
                boolean res = groupService.quitByUserIdAndGroupId(user.getId(),combGroup.get(groupId).get(childId).getId());//进行退出分组操作的结果
                mHandler.post(()->{
                    if (res){//退出成功，更新视图
                        clearData();
                        initData();
                        loadGroupList();
                    }else{//退出失败
                        ToastUtil.Toast(getContext(),"退出失败");
                    }
                });
            }).start();
        }
        return super.onContextItemSelected(item);
    }

    /********************************************************/

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
                expandableListView.expandGroup(0);
                expandableListView.expandGroup(1);
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
//        list.get(0).add("组1项1");
//        list.get(1).add("组2项1");
        allList.add("   我发起的签到群");
        allList.add("   我加入的签到群");
        registerForContextMenu(expandableListView);
    }


    /**
     * 向列表中添加群组项
     * @param group 要添加的群组
     * @param i 群组类型，0===创建的群组；1===加入的群组
     */
    private void addGroup(Group group,int i){
        combGroup.add(new ArrayList<>());
        combGroup.add(new ArrayList<>());
        combGroup.get(i).add(group);//更新数据实体
        list.get(i).add(group.getName());
        madapder.notifyDataSetChanged();
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


    class MyExpandShortClick implements ExpandableListView.OnChildClickListener{

        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

            Group group = combGroup.get(i).get(i1);
            System.out.println("点击了组："+i+",此组长度为："+combGroup.get(i).size()+"，遍历如下：");
            for (Group g:combGroup.get(i)){
                System.out.println(g);
            }

            System.out.println("ExpandList---groupPosition:"+i+",childPosition:"+i1);
            //System.out.println("组别："+groupPosition+",子项："+childPosition);
            if(i==0){
                GroupInfoActivity.startActivity(getContext(),group,user,GroupInfoActivity.CREATED_GROUP);
            }else if(i==1){
                GroupInfoActivity.startActivity(getContext(),group,user,GroupInfoActivity.JOINED_GROUP);
            }
            return true;
        }
    }

    class MyExpandLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            return false;
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
