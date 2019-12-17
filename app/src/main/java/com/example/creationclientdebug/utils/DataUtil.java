package com.example.creationclientdebug.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
    public static String getDate(long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分");
        String s = sdf.format(date);
        return s;
    }
}
