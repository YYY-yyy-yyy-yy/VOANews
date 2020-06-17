package com.swufe.voanews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity{
    private final  String TAG = "RegisterActivity";
    EditText user;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user = findViewById(R.id.Register_user);
        password = findViewById(R.id.Register_password);

    }

    public void onClick(View btn){
        String name = user.getText().toString();
        String pass = password.getText().toString();
        //判断数据库中有没有相同的名称
        UserManager manager=new UserManager(this);
        List<UserItem> userList = new ArrayList<UserItem>();
        manager.add(new UserItem("manager","manager"));
        if(manager.queryUserByName(name)){
            Toast.makeText(RegisterActivity.this,"该用户名已存在，请重新输入",Toast.LENGTH_SHORT).show();
        }else{
            manager.add(new UserItem(name,pass));
            Log.i(TAG,"onClick:写入数据完毕");
            SettingFragment fragment = new SettingFragment(name, pass);
            Intent config = new Intent(this, ShowActivity.class);
            startActivity(config);
        }
    }
}
