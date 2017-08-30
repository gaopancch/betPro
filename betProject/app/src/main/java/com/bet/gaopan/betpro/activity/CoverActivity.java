package com.bet.gaopan.betpro.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.utils.ActivityUtils;

public class CoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);

                }catch (Exception e){

                }
                ActivityUtils.goToActivity(CoverActivity.this,LoginActivity.class);
                finish();
            }
        }).start();

    }

}
