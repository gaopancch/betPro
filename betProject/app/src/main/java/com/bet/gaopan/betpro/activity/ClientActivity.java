package com.bet.gaopan.betpro.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.json.ParaCardJson;
import com.bet.gaopan.betpro.utils.ConstantUtils;
import com.bet.gaopan.betpro.utils.PreferenceUtil;
import com.bet.gaopan.betpro.utils.ToastUtils;
import com.bet.gaopan.betpro.views.Card;
import com.bet.gaopan.betpro.views.Player;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ClientActivity extends Activity {

    private TextView result;
    private Button joinRoomButton;
    private Button getCardsFromServer;
    private EditText inputIP;//输入的ip地址（房间号）
    private ClientState state=ClientState.idle;
    private ArrayList<String> playerList=new ArrayList<>();
    private Player currentPlayer;
    private LinearLayout parentLinearLayout;
    private Context context;
//    private Card card;

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                result.append("server:" + msg.obj + "\n");
                String serverResponse=(String)msg.obj;
                parseUserData(serverResponse);
            }
        }

    };

    @Override
    public void onBackPressed() {
        exitBy2Click();
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        parentLinearLayout=(LinearLayout)findViewById(R.id.parent_linearlayout_client) ;
        result = (TextView) findViewById(R.id.result);
        joinRoomButton = (Button) findViewById(R.id.send);
        getCardsFromServer=(Button)findViewById(R.id.get_cards_from_server);
        inputIP = (EditText) findViewById(R.id.input);
        context=this;
        currentPlayer=new Player(this,ConstantUtils.userName);
//        card=new Card(this);


        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                result.append("client:" + "join room!" + "\n");
                //启动线程 向服务器发送和接收信息
                new MyThread("join_room"+ ConstantUtils.userName,30000).start();
            }
        });

        getCardsFromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                result.append("client:" + "get_cards!" + "\n");
                playerList.clear();
                new MyThread("get_cards"+ ConstantUtils.userName,30001).start();
            }
        });

        if(!TextUtils.isEmpty(ConstantUtils.lastRoomId)){
            inputIP.setText(ConstantUtils.lastRoomId);
        }

    }

class MyThread extends Thread {

    public String content;
    public int port=30000;

    public MyThread(String str,int port) {
        content = str;
        this.port=port;
    }

    @Override
    public void run() {
        //定义消息
        Message msg = new Message();
        msg.what = 1;
        try {
            Log.i("gaopan123","MyThread");
            //连接服务器 并设置连接超时为5秒
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(inputIP.getText().toString(), port), 1000);

            //获取输入输出流
            OutputStream ou = socket.getOutputStream();
            //获取输出输出流
            BufferedReader bff = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            //向服务器发送信息
            ou.write(content.getBytes("utf-8"));
            ou.flush();
            Log.i("gaopan123","MyThread1");
            //读取发来服务器信息
            String result = "";
            String buffer = "";
            while ((buffer = bff.readLine()) != null) {
                result = result + buffer;
            }
            Log.i("gaopan123","MyThread2");
            msg.obj = result.toString();
            //发送消息 修改UI线程中的组件
            myHandler.sendMessage(msg);
            //关闭各种输入输出流
            bff.close();
            ou.close();
            socket.close();
        } catch (SocketTimeoutException aa) {
            //连接超时 在UI界面显示消息
            Log.i("gaopan123","请检查网络是否打开");
            Toast.makeText(getApplicationContext(),"服务器连接失败！请检查网络是否打开",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.i("gaopan123","未知错误");
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"服务器连接失败！未知错误",Toast.LENGTH_SHORT).show();
        }
    }
}

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtils.showMessage(this, "再按一次返回");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            if(state==ClientState.inRoom) {
                new MyThread("leave_room" + ConstantUtils.userName, 30002).start();
                state=ClientState.outRoom;
            }
            finish();
        }
    }

    int PARASE_RESULT_OK=0;
    int PARASE_RESULT_NULL=1;
    int PARASE_RESULT_EXCEPTION=2;
    //strContent为服务器返回的jason数据
    private int parseUserData(String strContent) {
        if (!TextUtils.isEmpty(strContent)) {
            try {
                Gson mgson = new Gson();
                ParaCardJson paraCardJson = mgson.fromJson(
                        strContent, ParaCardJson.class);
                if(paraCardJson.getResult().equals("0")){//离开房间
                    ;
                }else if(paraCardJson.getResult().equals("1")){//加入房间
                    hasJoinRoom();
                }else if(paraCardJson.getResult().equals("500")){//获取牌型成功
//                    getCardsFromServer.setText(strContent);

                    ParaCardJson.DataBean playerBean;
                    for (int i=0;i<paraCardJson.getData().size();i++) {
                        playerBean= paraCardJson.getData().get(i);//返回的第i个玩家 数据（名字，牌型）
                        String name=playerBean.getName();
                        playerList.add(name);
                        if(name.equals(ConstantUtils.userName)){//如果是自己的牌就获取，如果是其他玩家的忽略
                            result.setVisibility(View.GONE);
                            parentLinearLayout.removeView(currentPlayer);
                            parentLinearLayout.addView(currentPlayer);//把玩家加入界面
                            ParaCardJson.DataBean.CardsBean cardsBean;
                            for (int j=0;j<playerBean.getCards().size();j++) {
                                cardsBean = playerBean.getCards().get(j);//一根牌
                                cardsBean.getHsdc();
                                cardsBean.getValue();
                                Card card=new Card(context);
                                card.setValue(cardsBean.getValue());
                                card.setType(cardsBean.getHsdc());
                                currentPlayer.addCard(card);
                            }
                        }
                        currentPlayer.showCards();
                    }

                }else if(paraCardJson.getResult().equals("499")){ //还没有发牌
                    Toast.makeText(getApplicationContext(),"庄家尚未发牌，请等待",Toast.LENGTH_SHORT).show();
                }

                return PARASE_RESULT_OK;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"PARASE_RESULT_EXCEPTION",Toast.LENGTH_SHORT).show();
                return PARASE_RESULT_EXCEPTION;
            }
        }
        Toast.makeText(getApplicationContext(),"PARASE_RESULT_NULL",Toast.LENGTH_SHORT).show();
        return PARASE_RESULT_NULL;
    }

    private void hasJoinRoom(){
        joinRoomButton.setClickable(false);
        joinRoomButton.setText(ConstantUtils.userName+":已经加入房间，等待庄家发牌");
        PreferenceUtil.putString(ConstantUtils.LAST_ROOM_ID,inputIP.getText().toString(),getApplicationContext());
        inputIP.setVisibility(View.GONE);
        state=ClientState.inRoom;
    }

    /**未做客户端所存在的状态*/
    enum ClientState{
        idle,
        inRoom,
        outRoom
    }
}
