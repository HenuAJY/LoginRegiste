package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.loginregiste.R;
import com.henu.entity.User;

public class SigninActivity extends AppCompatActivity {

    public static void startActivity(Context c, User user){
        Intent i = new Intent(c,SigninActivity.class);
        i.putExtra("user",user);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }
}
