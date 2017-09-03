package com.bet.gaopan.betpro.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.utils.ActivityUtils;
import com.bet.gaopan.betpro.utils.ConstantUtils;
import com.bet.gaopan.betpro.utils.PreferenceUtil;
import com.bet.gaopan.betpro.utils.ToastUtils;
import com.bet.gaopan.betpro.views.ExplosionField;

import java.util.Timer;
import java.util.TimerTask;

public class LobbyActivity extends Activity {

    private TextView userName;
    private Button createGameRoom;
    private Button joinInGameRoom;
    private TextView lastRoomText;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        userName=(TextView)findViewById(R.id.user_name_text);
//        View view=LayoutInflater.from(LobbyActivity.this).inflate(R.layout.back_bar_layout,null);
        backButton=(ImageButton) findViewById(R.id.back_button);
//        ((TextView)view.findViewById(R.id.bar_text)).setText("aaaaa");

        createGameRoom =(Button)findViewById(R.id.create_room_button);
//        createGameRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityUtils.goToActivity(LobbyActivity.this,ServerActivity.class);
//            }
//        });

        joinInGameRoom=(Button)findViewById(R.id.join_room_button);
//        joinInGameRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConstantUtils.lastRoomId="";
//                ActivityUtils.goToActivity(LobbyActivity.this,ClientActivity.class);
//            }
//        });
        ConstantUtils.lastRoomId=PreferenceUtil.getString(ConstantUtils.LAST_ROOM_ID,"",getApplicationContext());
        lastRoomText=(TextView)findViewById(R.id.last_room);
//        lastRoomText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityUtils.goToActivity(LobbyActivity.this,ClientActivity.class);
//            }
//        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.goToActivity(LobbyActivity.this,LoginActivity.class);
                finish();
            }
        });

        ExplosionField explosionField = new ExplosionField(this);

        explosionField.addListener((LinearLayout)findViewById(R.id.three_button_linear));
        explosionField.setDoAfterExplosiedListener(new ExplosionField.DoAfterExplosiedListener() {
            @Override
            public void doAfterExploside(View view) {
                if(view.getId()==R.id.create_room_button) {
                    ActivityUtils.goToActivity(LobbyActivity.this, ServerActivity.class);
                }else if(view.getId()==R.id.join_room_button){
                    ConstantUtils.lastRoomId="";
                    ActivityUtils.goToActivity(LobbyActivity.this,ClientActivity.class);
                }else if(view.getId()==R.id.last_room){
                    ActivityUtils.goToActivity(LobbyActivity.this,ClientActivity.class);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConstantUtils.lastRoomId=PreferenceUtil.getString(ConstantUtils.LAST_ROOM_ID,"",getApplicationContext());
        if(lastRoomText!=null&&!TextUtils.isEmpty(ConstantUtils.lastRoomId)) {
            lastRoomText.setVisibility(View.VISIBLE);
            lastRoomText.setText("加入房间："+ConstantUtils.lastRoomId);
        }else{
            lastRoomText.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(PreferenceUtil.getString(ConstantUtils.USER_NAME,"",this))){
            ConstantUtils.userName=PreferenceUtil.getString(ConstantUtils.USER_NAME,"",this);
        }
        userName.setText("欢迎 "+ConstantUtils.userName);
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtils.showMessage(this, "再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        exitBy2Click();
    }
}
