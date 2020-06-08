package com.swufe.voanews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



    }

    public void openRegister(View btn){
        Intent config = new Intent(this, RegisterActivity.class);
        startActivity(config);
    }

    public void openMain(View btn){
        Intent config = new Intent(this, MainActivity.class);
        startActivity(config);
    }
}
