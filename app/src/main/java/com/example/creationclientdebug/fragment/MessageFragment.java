package com.example.creationclientdebug.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.creationclientdebug.activity.ApplyInfoActivity;
import com.example.creationclientdebug.activity.SigninActivity;
import com.example.creationclientdebug.entity.Message;
import com.example.loginregiste.R;
import com.henu.entity.Cmder;
import com.henu.entity.Group;
import com.henu.entity.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private static MessageFragment instance;

    public static MessageFragment getInstance(){
        if(instance==null){
            instance = new MessageFragment();
        }
        return instance;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    public MessageFragment() {
        // Required empty public constructor
    }


    private ListView msgListView;
    private LinkedList<String> msgList = new LinkedList<>();
    private ArrayAdapter<String> msgAdapter;

    private LinkedList<Cmder> messages = new LinkedList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        msgListView = view.findViewById(R.id.list_msg);
        msgAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,msgList);
        msgListView.setAdapter(msgAdapter);
        msgListView.setOnItemClickListener(new ListItemClickListener());
        updateView();
        return view;
    }

    //更新消息列表
    public void updateMsg(Cmder cmder){
        messages.addFirst(cmder);
        String title;
        switch (cmder.getType()){
            case Cmder.GROUPAPPLY:
                title = "加群申请";
                break;
            case Cmder.GROUPRESPONSE:
                title = "加群回复";
                break;
            case Cmder.SIGNIN:
                title = "签到邀请";
                break;
                default:
                    title = "未知消息类型";
        }
        msgList.addFirst(title);
        updateView();
    }
    //更新视图
    public void updateView(){
        if(msgAdapter!=null){
            mHandler.post(()-> msgAdapter.notifyDataSetChanged());
        }
    }

    class ListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Cmder c = messages.get(i);
            switch (c.getType()){
                case Cmder.GROUPAPPLY://进入加群申请详细信息界面
                    ApplyInfoActivity.startActivity(getContext(),c);
                    break;
                case Cmder.GROUPRESPONSE://进入加群回复界面，显示群详细信息
                    Group g = c.getGroup();
                    System.out.println(g);
                    break;
                case Cmder.SIGNIN://进入签到邀请页面
                    SigninActivity.startActivity(getContext(),c.getSignin());
                    break;
            }
        }
    }
}
