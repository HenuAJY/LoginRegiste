package com.example.creationclientdebug.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.loginregiste.R;
import com.henu.entity.Cmder;
import com.henu.entity.Group;

public class DeleteGroupApplyActivity extends AppCompatActivity {

    public static void startActivity(Context context, Cmder cmder){
        Intent intent = new Intent(context,DeleteGroupApplyActivity.class);
        intent.putExtra("cmder",cmder);
        context.startActivity(intent);
    }

    private Cmder c;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group_apply);

        tv =findViewById(R.id.tv_reply);

        c = (Cmder) getIntent().getSerializableExtra("cmder");

        Group group = c.getGroup();

        String msg = "您好，您加入的群："+group.getName()+"，已经被群主解散！";

        tv.setText(msg);
    }
}
