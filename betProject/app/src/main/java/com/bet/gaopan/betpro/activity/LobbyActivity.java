package com.bet.gaopan.betpro.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.utils.ConstantUtils;

public class LobbyActivity extends Activity {

    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        userName=(TextView)findViewById(R.id.user_name_text);
        userName.setText(ConstantUtils.userName);
    }
}
