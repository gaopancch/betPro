package com.bet.gaopan.betpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.utils.ActivityUtils;
import com.bet.gaopan.betpro.utils.ConstantUtils;
import com.bet.gaopan.betpro.utils.PreferenceUtil;

public class CoverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_cover);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1500);

                }catch (Exception e){

                }
                String name= PreferenceUtil.getString(ConstantUtils.USER_NAME,"",getApplicationContext());
                if(!TextUtils.isEmpty(name)){
                    ActivityUtils.goToActivity(CoverActivity.this,LobbyActivity.class);
                }else {
                    ActivityUtils.goToActivity(CoverActivity.this, LoginActivity.class);
                }
                finish();
            }
        }).start();

    }

}
