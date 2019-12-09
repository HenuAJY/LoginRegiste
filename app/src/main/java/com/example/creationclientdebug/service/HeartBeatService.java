package com.example.creationclientdebug.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.creationclientdebug.receiver.HeartBeatReceiver;
import com.henu.entity.Cmder;
import com.henu.entity.Heartbeat;
import com.henu.entity.Signin;
import com.henu.poxy.HeartbeatPoxy;
import com.henu.service.HeartbeatService;

public class HeartBeatService extends Service {

    private static Intent heartBeatService;

    public static void startMe(Context context, String account){
        heartBeatService = new Intent(context,HeartBeatService.class);
        heartBeatService.putExtra("account",account);
        context.startService(heartBeatService);
    }

    public static void stopMe(Context context){
        if(heartBeatService!=null){
            context.stopService(heartBeatService);
            System.out.println("HeartBeatService:stopMe()");
        }
    }

    private boolean isStopHeartBeat = false;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final String account = intent.getStringExtra("account");
        new Thread(() -> {
            HeartbeatService heartbeatService = HeartbeatPoxy.getInstance();
            Heartbeat heartbeat = new Heartbeat();
            heartbeat.setAccount(account);
            long startTime = 0;
            int timeout = 10*200;//每两秒发送一次心跳
            while(!isStopHeartBeat){
                long currentTime = System.currentTimeMillis();
                if(currentTime - startTime>timeout){
                    heartbeat.setTime(currentTime);
                    Cmder cmder = heartbeatService.sendHeartbeat(heartbeat);
                    if(cmder!=null){
                        ansyCmder(cmder);//解析收到的消息
                    }
                    startTime = currentTime;
                }

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("停止服务");
        isStopHeartBeat = true;
    }

    private void ansyCmder(Cmder cmder){
        Intent i = new Intent(HeartBeatReceiver.ACTION_MSSAGE);
        i.putExtra("cmder",cmder);
        sendBroadcast(i);
    }
}
