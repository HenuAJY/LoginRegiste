package com.example.creationclientdebug.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.henu.entity.Signin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShareUtil {
    public String shareExcel(List<Signin> signins, Context c,String tableName){
        exportExcel(signins,c,tableName);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fileName)));
        shareIntent.setType("application/vnd.ms-excel");
        c.startActivity(Intent.createChooser(shareIntent,"分享到："));
        return fileName;
    }

    /**********************************************/

    /**
     * 导出excel
     * @param view
     */

    File file;
    String fileName;
    String[] title = {"签到发起人","发起时间","发起人经度","发起人纬度","签到地理范围","签到人","签到人经度","签到人纬度","签到结果"};
    ArrayList<ArrayList<String>> recordList;
    private void exportExcel(List<Signin> signins,Context c,String tableName) {
        file = new File(getSDPath() + "/Record");
        makeDir(file);
        ExcelUtil.initExcel(file.toString() + "/"+tableName, title);
        fileName = getSDPath() + "/Record/"+tableName;
        ExcelUtil.writeObjListToExcel(getRecordData(signins), fileName, c);
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     * @return
     */
    private  ArrayList<ArrayList<String>> getRecordData(List<Signin> signins) {
        recordList = new ArrayList<>();
        for (Signin s:signins){
            ArrayList<String> items = new ArrayList<>();
            items.add(s.getOriginator()+"");
            items.add(DataUtil.getDate(s.getTime()));
            items.add(s.getLongtitude()+"");
            items.add(s.getLatitude()+"");
            items.add(s.getRegion()+"");
            items.add(s.getReceiver()+"");
            items.add(s.getRlongitude()+"");
            items.add(s.getRlatitude()+"");
            items.add(s.isResult()+"");
            recordList.add(items);
        }
        return recordList;
    }

    private  String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    private  void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }
}
