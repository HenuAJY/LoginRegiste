package com.example.debug;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void Toast(Context c,String s){
        Toast.makeText(c,s,Toast.LENGTH_SHORT).show();
    }
}
