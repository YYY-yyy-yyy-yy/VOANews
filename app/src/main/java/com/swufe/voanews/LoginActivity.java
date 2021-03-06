package com.swufe.voanews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText user;
    EditText password;
    private final  String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = findViewById(R.id.Login_user);
        password = findViewById(R.id.Login_password);
    }

    public void openRegister(View btn) {
        Intent config = new Intent(this, RegisterActivity.class);
        startActivity(config);
    }

    public void openMain(View btn) {
        String name =  user.getText().toString();
        String pass = password.getText().toString();
        if(name == ""||pass==""||name==null ||pass==null){
            Toast.makeText(LoginActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
        }else{
            //判断数据库中有没有相同的名称
            UserManager manager = new UserManager(this);
            if (!manager.queryUserByUserAndPassword(name,pass)) {
                Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("name",name);
                bundle.putString("pass",pass);
                Intent config = new Intent(this, ShowActivity.class);

                SettingFragment fragment = new SettingFragment(name, pass);
                startActivity(config);

            }
        }
    }
}
