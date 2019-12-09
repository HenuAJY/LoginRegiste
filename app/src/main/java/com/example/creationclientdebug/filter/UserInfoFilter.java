package com.example.creationclientdebug.filter;

import android.content.Context;

import com.example.creationclientdebug.activity.UserChangeInfoActivity;
import com.example.debug.ToastUtil;
import com.henu.entity.User;
import com.henu.poxy.UserServicePoxy;
import com.henu.service.UserService;

public class UserInfoFilter {
    private User user;
    public boolean doFilter(Context c, User u){
        Thread t = new Thread(()->{
            UserService service = UserServicePoxy.getInstance();
            user = service.queryByAccount(u.getAccount());
        });
        t.start();
        while (user==null);
        if (u.getName()==null){
            ToastUtil.Toast(c,"个人信息不完整，请先补充！");
            UserChangeInfoActivity.staticActivity(c,u);
            return false;
        }
        return true;
    }
}
