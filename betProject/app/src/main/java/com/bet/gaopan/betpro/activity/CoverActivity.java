package com.bet.gaopan.betpro.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bet.gaopan.betpro.R;

public class CoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);

                }catch (Exception e){

                }
                goLoginActivity();
            }
        }).start();
    }

    private void goLoginActivity(){
        Intent intent = new Intent();
        intent.setClass(CoverActivity.this,LoginActivity.class);
        this.startActivity(intent);
    }
}
