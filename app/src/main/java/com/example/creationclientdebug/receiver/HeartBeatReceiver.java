package com.example.creationclientdebug.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.creationclientdebug.datatool.DataUpdateTool;
import com.henu.entity.Cmder;

import java.util.HashMap;
import java.util.Map;

/**
 * 此接收器用于处理加群请求
 */
public class HeartBeatReceiver extends BroadcastReceiver {
    public static final String ACTION_MSSAGE = "android.intent.action.Message";

    private DataUpdateTool tool;

    @Override
    public void onReceive(Context context, Intent intent) {
        Cmder cmder = (Cmder)intent.getSerializableExtra("cmder");
        if(tool!=null){
            tool.update(cmder);
        }

    }

    public void bindTool(DataUpdateTool tool){
        this.tool = tool;
    }
}