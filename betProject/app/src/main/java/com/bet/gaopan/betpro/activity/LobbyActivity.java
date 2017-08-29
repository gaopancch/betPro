package com.bet.gaopan.betpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.utils.ConstantUtils;

public class LobbyActivity extends Activity {

    private TextView userName;
    private Button goInRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        userName=(TextView)findViewById(R.id.user_name_text);
        userName.setText(ConstantUtils.userName);
        goInRoomButton=(Button)findViewById(R.id.go_room_button);
        goInRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLobbyAcitivity();
            }
        });
    }

    private void goLobbyAcitivity(){
//        ConstantUtils.userName= mEmailView.getText().toString();
        Intent intent = new Intent();
        intent.setClass(LobbyActivity.this,RoomActivity.class);
        this.startActivity(intent);
    }
}
