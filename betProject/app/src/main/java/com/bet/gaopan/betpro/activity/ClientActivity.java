package com.bet.gaopan.betpro.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

    private Button joinRoomButton;
    private Button getCardsFromServer,showCardButton;
    private EditText inputIP;//输入的ip地址（房间号）
    private ClientState state=ClientState.idle;
    private ArrayList<Player> playerList=new ArrayList<>();
    private Player currentPlayer;
    private LinearLayout parentScrollLayout;
    private Context context;
    private InputMethodManager imm;
    private TextView clientTextView;
    private ProgressBar progressBar;

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 1) {
                String serverResponse=(String)msg.obj;
                parseUserData(serverResponse);
            }else if(msg.what == 303){
                Toast.makeText(getApplicationContext(),"服务器连接失败！请检查网络是否打开并确认房间号是否正确！",Toast.LENGTH_SHORT).show();
            }else if(msg.what == 304){
                Toast.makeText(getApplicationContext(),"连接失败！请确认房间号是否输入正确！",Toast.LENGTH_SHORT).show();
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
        parentScrollLayout =(LinearLayout) findViewById(R.id.parent_scroll_client) ;
//        result = (TextView) findViewById(R.id.result);
        joinRoomButton = (Button) findViewById(R.id.send);
        getCardsFromServer=(Button)findViewById(R.id.get_cards_from_server);
        showCardButton=(Button)findViewById(R.id.show_cards_client);
        inputIP = (EditText) findViewById(R.id.input);
        clientTextView=(TextView)findViewById(R.id.client_content);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar_clien);
        context=this;
        currentPlayer=new Player(this,ConstantUtils.userName);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        showCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShow=false;
                for (int i=0;i<playerList.size();i++) {
                    if(playerList.get(i).getName().equals(ConstantUtils.userName)){
                        playerList.get(i).showCards();
                        isShow=true;
                    }
                }
                if(!isShow){
                    ToastUtils.showMessage(getApplicationContext(),"未知错误，请重新登录并输入用户名");
                }
            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                result.append("client:" + "join room!" + "\n");
                //启动线程 向服务器发送和接收信息
                if(!TextUtils.isEmpty(inputIP.getText().toString())){
                    progressBar.setVisibility(View.VISIBLE);
                    new MyThread("join_room"+ ConstantUtils.userName,30000).start();
                }else{
                    ToastUtils.showMessage(getApplicationContext(),"请输入正确的房间号");
                }
            }
        });

        getCardsFromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                result.append("client:" + "get_cards!" + "\n");
                progressBar.setVisibility(View.VISIBLE);
                playerList.clear();
                currentPlayer.clearCards();
                parentScrollLayout.removeView(currentPlayer);
                new MyThread("get_cards"+ ConstantUtils.userName,30001).start();
            }
        });

        if(!TextUtils.isEmpty(ConstantUtils.lastRoomId)){
            inputIP.setText(ConstantUtils.lastRoomId);
        }
        cheatCode();
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
            myHandler.sendEmptyMessage(303);
            Log.i("gaopan123","连接超时");
        } catch (IOException e) {
            myHandler.sendEmptyMessage(304);
            Log.i("gaopan123","未知错误");
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
                    myHandler.sendEmptyMessageDelayed(305,500);
                    hasJoinRoom();
                }else if(paraCardJson.getResult().equals("500")){//获取牌型成功
//                    getCardsFromServer.setText(strContent);
                    parentScrollLayout.removeAllViews();
                    ParaCardJson.playerBean playerBean;
                    for (int i=0;i<paraCardJson.getData().size();i++) {
                        playerBean= paraCardJson.getData().get(i);//返回的第i个玩家 数据（名字，牌型）
                        String name=playerBean.getName();
                        Player player=new Player(context,name);
                        playerList.add(player);
                        parentScrollLayout.addView(player);//把玩家加入界面

                        ParaCardJson.playerBean.CardsBean cardsBean;
                        for (int j=0;j<playerBean.getCards().size();j++) {
                            cardsBean = playerBean.getCards().get(j);//一根牌
                            cardsBean.getHsdc();
                            cardsBean.getValue();
                            Card card=new Card(context);
                            card.setValue(cardsBean.getValue());
                            card.setType(cardsBean.getHsdc());
                            player.addCard(card);
                        }
                        player.hideCards();
                    }
                    showCPlayerList();
                    myHandler.sendEmptyMessageDelayed(305,500);
                }else if(paraCardJson.getResult().equals("499")){ //还没有发牌
//                    parentScrollLayout.removeView(currentPlayer);
                    Toast.makeText(getApplicationContext(),"庄家尚未发牌，请等待",Toast.LENGTH_SHORT).show();
                }
                myHandler.sendEmptyMessageDelayed(305,500);
                return PARASE_RESULT_OK;
            } catch (Exception e) {
                e.printStackTrace();
                myHandler.sendEmptyMessageDelayed(305,500);
                Toast.makeText(getApplicationContext(),"PARASE_RESULT_EXCEPTION",Toast.LENGTH_SHORT).show();
                return PARASE_RESULT_EXCEPTION;
            }
        }
        myHandler.sendEmptyMessageDelayed(305,500);
        Toast.makeText(getApplicationContext(),"PARASE_RESULT_NULL",Toast.LENGTH_SHORT).show();
        return PARASE_RESULT_NULL;
    }

    private void showCPlayerList(){
        StringBuffer playerListStr=new StringBuffer();
        playerListStr.append("当前玩家：");
        for(int i=0;i<playerList.size();i++){
            playerListStr.append(" "+playerList.get(i).getName());
        }
        clientTextView.setVisibility(View.VISIBLE);
        clientTextView.setText(playerListStr);
    }

    private void hasJoinRoom(){
        joinRoomButton.setClickable(false);
        joinRoomButton.setText(ConstantUtils.userName+":已经加入房间");
        PreferenceUtil.putString(ConstantUtils.LAST_ROOM_ID,inputIP.getText().toString(),getApplicationContext());
        inputIP.setVisibility(View.GONE);
        state=ClientState.inRoom;
        imm.hideSoftInputFromWindow(inputIP.getWindowToken(), 0);
    }

    /**未做客户端所存在的状态*/
    enum ClientState{
        idle,
        inRoom,
        outRoom
    }

    private void cheatCode(){
        //作弊
        parentScrollLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(ConstantUtils.userName.endsWith(" ")&&ConstantUtils.userName.startsWith(" ")){
                    for(int i=0;i<playerList.size();i++){
                        playerList.get(i).showCards();
                    }
                }
                return false;
            }
        });

        parentScrollLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConstantUtils.userName.endsWith(" ")&&ConstantUtils.userName.startsWith(" ")){
                    for(int i=0;i<playerList.size();i++){
                        if(!playerList.get(i).getName().equals(ConstantUtils.userName)) {
                            playerList.get(i).hideCards();
                        }
                    }
                }
            }
        });
    }
}
