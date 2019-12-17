package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.creationclientdebug.utils.DataUtil;
import com.example.creationclientdebug.utils.ExcelUtil;
import com.example.creationclientdebug.utils.ShareUtil;
import com.example.debug.ToastUtil;
import com.example.loginregiste.R;
import com.henu.entity.Signin;
import com.henu.poxy.SigninServicePoxy;
import com.henu.service.SigninService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SigninRecodeActivity extends AppCompatActivity {

    public static void startActivity(Context c,int userId,int groupId){
        Intent i = new Intent(c,SigninRecodeActivity.class);
        i.putExtra("userid",userId);
        i.putExtra("groupid",groupId);
        c.startActivity(i);
    }

    private int userId;

    private int groupId;

    private List<Signin> signins = new ArrayList<>();

    private ListView lvSignin;

    private ArrayAdapter<String> lvAdapter;

    private List<String> signinStr = new ArrayList<>();

    private MyLvItemListener lvListener = new MyLvItemListener();

    private Button btnExport;

    SigninService signinService = SigninServicePoxy.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_recode);

        userId = getIntent().getIntExtra("userid",0);
        groupId = getIntent().getIntExtra("groupid",0);
        lvAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,signinStr);
        lvSignin = findViewById(R.id.lv_signins);
        lvSignin.setAdapter(lvAdapter);
        lvSignin.setOnItemClickListener(lvListener);
        loadSignins();

        btnExport = findViewById(R.id.btn_export_exl);
        btnExport.setOnClickListener(view -> {
            new Thread(()->{
                List<Signin> signins = signinService.getSigninByReceiverAndGroupId(userId,groupId);
                //runOnUiThread(()->writeExcel(signins));
                ShareUtil shareUtil = new ShareUtil();
                String tableName = "群-"+groupId+"-人-"+userId+"-签到记录.xls";
                runOnUiThread(()->{
                    if (signins.size()==0){
                        ToastUtil.Toast(SigninRecodeActivity.this,"无签到记录");
                    }else{
                        shareUtil.shareExcel(signins,SigninRecodeActivity.this,tableName);
                    }
                });
            }).start();
        });
    }

    private void loadSignins(){
        new Thread(()->{
            signins = signinService.getSigninByReceiverAndGroupId(userId,groupId);
            if(signins.size()!=0){
                runOnUiThread(this::updateListView);
            }
        }).start();
    }

    private void updateListView(){
        for (Signin signin:signins){
            signinStr.add("签到发起日期：" + DataUtil.getDate(signin.getTime()));
        }
        lvAdapter.notifyDataSetChanged();
    }

//    private void writeExcel(List<Signin> signins){
//        exportExcel(signins);
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fileName)));
//        shareIntent.setType("application/vnd.ms-excel");
//        startActivity(Intent.createChooser(shareIntent,"分享到："));
//    }
//
//    /**********************************************/
//
//    /**
//     * 导出excel
//     * @param view
//     */
//
//    File file;
//    String fileName;
//    String[] title = {"签到发起人","发起时间","发起人经度","发起人纬度","签到地理范围","签到人","签到人经度","签到人纬度","签到结果"};
//    ArrayList<ArrayList<String>> recordList;
//    public void exportExcel(List<Signin> signins) {
//        String tableName = "群-"+groupId+"-签到记录.xls";
//        file = new File(getSDPath() + "/Record");
//        makeDir(file);
//        ExcelUtil.initExcel(file.toString() + "/"+tableName, title);
//        fileName = getSDPath() + "/Record/"+tableName;
//        ExcelUtil.writeObjListToExcel(getRecordData(signins), fileName, this);
//    }
//
//    /**
//     * 将数据集合 转化成ArrayList<ArrayList<String>>
//     * @return
//     */
//    private  ArrayList<ArrayList<String>> getRecordData(List<Signin> signins) {
//        recordList = new ArrayList<>();
//        for (Signin s:signins){
//            ArrayList<String> items = new ArrayList<>();
//            items.add(s.getOriginator()+"");
//            items.add(DataUtil.getDate(s.getTime()));
//            items.add(s.getLongtitude()+"");
//            items.add(s.getLatitude()+"");
//            items.add(s.getRegion()+"");
//            items.add(s.getReceiver()+"");
//            items.add(s.getRlongitude()+"");
//            items.add(s.getRlatitude()+"");
//            items.add(s.isResult()+"");
//            recordList.add(items);
//        }
//        return recordList;
//    }
//
//    private  String getSDPath() {
//        File sdDir = null;
//        boolean sdCardExist = Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED);
//        if (sdCardExist) {
//            sdDir = Environment.getExternalStorageDirectory();
//        }
//        String dir = sdDir.toString();
//        return dir;
//    }
//
//    public  void makeDir(File dir) {
//        if (!dir.getParentFile().exists()) {
//            makeDir(dir.getParentFile());
//        }
//        dir.mkdir();
//    }
//
//    /*****************************************************************/

    class MyLvItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SingleSigninInfoActivity.startActivity(SigninRecodeActivity.this,signins.get(i));
        }
    }
}
