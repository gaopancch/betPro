package com.bet.gaopan.betpro.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.json.ParaCardJson;
import com.bet.gaopan.betpro.utils.ConstantUtils;
import com.bet.gaopan.betpro.utils.ToastUtils;
import com.bet.gaopan.betpro.views.CardBox;
import com.bet.gaopan.betpro.views.Player;
import com.bet.gaopan.betpro.views.XCLoadingImageView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.bet.gaopan.betpro.views.XCLoadingImageView.MaskOrientation.TopToBottom;

public class ServerActivity extends Activity {
    /**
     * {"result":"500","data":[{"name":"gaopan","cards":[{"value":"A","hsdc":"spade"},{"value":"3","hsdc":"diamond"},{"value":"5","hsdc":"club"}]},
     * {"name":"gaopan","cards":[{"value":"A","hsdc":"spade"},{"value":"3","hsdc":"diamond"},{"value":"5","hsdc":"club"}]}]}
     *
     * {"result":"499","data":[{"name":"","cards":[{"value":"","hsdc":""}]}]} //还没有发牌时候返回的数据
     *
     * {"result":"404","data":[{"name":"","cards":[{"value":"A","hsdc":"spade"}]]}  error
     * {"result":"1","data":[{"name":"","cards":[{"value":"","hsdc":""}]}]}   收到加入房间请求后返回给客户端数据
     * {"result":"0","data":[{"name":"","cards":[{"value":"","hsdc":""}]}]}   收到离开房间请求后返回给客户端数据
     * */
    private static TextView playerListTextView;//庄家 显示玩家列表
    private TextView ipTextView;//显示房间号（ip地址）
    private static ArrayList<String> currentPlayerList=new ArrayList<>();//当前玩家列表
    private Thread joinThread,leaveThread,getCardThread;//服务器端的三个线程
    private boolean joinThreadKeep=true,leaveThreadKeep=true,getCardsThreadKeep=true;
    private Button sendCards,showCardsButton;//发牌按钮
    private String joinRoomResponse="{\"result\":\"1\",\"data\":[{\"name\":\"\",\"cards\":[{\"value\":\"\",\"hsdc\":\"\"}]}]}";
    private String leaveRoomResponse="{\"result\":\"0\",\"data\":[{\"name\":\"\",\"cards\":[{\"value\":\"\",\"hsdc\":\"\"}]}]}";
    private String cardsContent;//每次庄家发牌后更新该字符串
    private String nullCardsContent="{\"result\":\"499\",\"data\":[{\"name\":\"\",\"cards\":[{\"value\":\"\",\"hsdc\":\"\"}]}]}";

    private boolean hasSendCards=false;

    private CardBox cardBox;
    private ArrayList<Player> players=new ArrayList<Player>();
    private LinearLayout parentLinearLayout;
//    private Player currentPlayer;
    private Context context;

    private MediaPlayer mediaPlayer;

//    private XCLoadingImageView xcLoadingImageView;
    private ProgressBar progressBar;


    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                playerListTextView.setText("");
                playerListTextView.append("玩家:");
                for (int i=0;i<currentPlayerList.size();i++){
                    playerListTextView.append("  " + currentPlayerList.get(i));
                }
                playerListTextView.append(" 已加入游戏");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        initViews();
        initFormationBeforePlay();
        sendCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasSendCards){
                    sendCards.setText("发牌");
//                    currentPlayer.clearCards();
                    parentLinearLayout.removeAllViews();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
//                    xcLoadingImageView.setVisibility(View.VISIBLE);
//                    xcLoadingImageView.startMaskAnim();
//                    xcLoadingImageView.setProgress(50);
                    mediaPlayer.start();//播放发牌音效
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            actionWhileSendCards();
                        }
                    },1500);
                }
                hasSendCards = !hasSendCards;
            }
        });
        cheatCode();
        mediaPlayer= MediaPlayer.create(ServerActivity.this, R.raw.sendcards);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
//                xcLoadingImageView.setVisibility(View.GONE);
//                progressView.stopAnimation();
//                progressView.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void cheatCode(){
        //作弊
        parentLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(ConstantUtils.userName.endsWith(" ")&&ConstantUtils.userName.startsWith(" ")){
                    for(int i=0;i<players.size();i++){
                        players.get(i).showCards();
                    }
                }
                return false;
            }
        });

        parentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConstantUtils.userName.endsWith(" ")&&ConstantUtils.userName.startsWith(" ")){
                    for(int i=0;i<players.size();i++){
                        if(!players.get(i).getName().equals(ConstantUtils.userName)) {
                            players.get(i).hideCards();
                        }
                    }
                }
            }
        });
    }

    private  Gson gson=new Gson();
    private void actionWhileSendCards(){
        sendCards.setText("洗牌");
        //洗牌
        cardBox.clearCardBox();
        //清除上次玩家
        players.clear();
        //把当前玩家全部加入
        for(int i=0;i<currentPlayerList.size();i++){
            Player player=new Player(context,currentPlayerList.get(i));
            players.add(player);
        }
        //发牌
        for(int i=0;i<players.size();i++){
            players.get(i).clearCards();
            players.get(i).addCard(cardBox.sendCard());
            players.get(i).addCard(cardBox.sendCard());
            players.get(i).addCard(cardBox.sendCard());
        }
        showCurrentPlayerCards();
    }

    private void showCurrentPlayerCards(){
        ParaCardJson paraCardJson=playersToParaCardJson();
        cardsContent=gson.toJson(paraCardJson);
        ParaCardJson.playerBean playerBean;
        //移除之前的所有玩家
        parentLinearLayout.removeAllViews();

        for(int i=0;i<players.size();i++){
            parentLinearLayout.addView(players.get(i));//把玩家加入界面
            players.get(i).hideCards();
        }
    }

    private ParaCardJson playersToParaCardJson() {
        ParaCardJson paraCardJson = new ParaCardJson();
        paraCardJson.setResult("500");
        ArrayList<ParaCardJson.playerBean> data = new ArrayList<>();
        ParaCardJson.playerBean playerBean;
        ArrayList<ParaCardJson.playerBean.CardsBean> cards;
        for (int i = 0; i < players.size(); i++) {
            playerBean = new ParaCardJson.playerBean();
            playerBean.setName(players.get(i).getName());
            cards = new ArrayList<>();
            ParaCardJson.playerBean.CardsBean cardsBean;
            for (int j = 0; j < players.get(i).getCards().size(); j++) {
                cardsBean = new ParaCardJson.playerBean.CardsBean();
                cardsBean.setValue(players.get(i).getCards().get(j).getValue());
                cardsBean.setHsdc(players.get(i).getCards().get(j).getType());
                cards.add(cardsBean);
            }
            playerBean.setCards(cards);
            data.add(playerBean);
        }
        paraCardJson.setData(data);
        return paraCardJson;
    }
    private void newGetCardsThread(){
        getCardThread=new GetCardsThread();
        getCardThread.start();
    }

    private void newJoinThread(){
        joinThread=new JoinRoomThread();
        joinThread.start();
    }

    private void leaveRoomThread(){
        leaveThread=new LeaveRoomThread();
        leaveThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        joinThreadKeep=false;
        getCardsThreadKeep=false;
        leaveThreadKeep=false;
    }

    private String getlocalip() {
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

     class JoinRoomThread extends Thread{
        @Override
        public void run() {
            OutputStream output;
            try {
                ServerSocket serverSocket = new ServerSocket(30000);
                while (joinThreadKeep) {
                    try {
                        Socket socket = serverSocket.accept();
                        Log.i("gaopan123","newJoinThread");
                        //获取输入信息
                        //向client发送消息
                        output = socket.getOutputStream();
                        output.write(joinRoomResponse.toString().getBytes("utf-8"));
                        output.flush();
                        socket.shutdownOutput();

                        BufferedReader bff = new BufferedReader(new InputStreamReader

                                (socket.getInputStream()));
                        //读取信息
                        String result = "";
                        String buffer = "";
                        while ((buffer = bff.readLine()) != null) {
                            result = result + buffer;
                        }
                        currentPlayerList.add(result.toString().substring(9));
                        mHandler.sendEmptyMessage(1);
                        output.close();
                        bff.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class LeaveRoomThread extends Thread{
        @Override
        public void run() {
            OutputStream output;
            try {
                ServerSocket serverSocket = new ServerSocket(30002);
                while (leaveThreadKeep) {
                    try {
                        Socket socket = serverSocket.accept();
                        Log.i("gaopan123","leaveRoomThread");
                        //获取输入信息
                        //向client发送消息
                        output = socket.getOutputStream();
                        output.write(leaveRoomResponse.getBytes("utf-8"));
                        output.flush();
                        socket.shutdownOutput();

                        BufferedReader bff = new BufferedReader(new InputStreamReader

                                (socket.getInputStream()));
                        //读取信息
                        String result = "";
                        String buffer = "";
                        while ((buffer = bff.readLine()) != null) {
                            result = result + buffer;
                        }
                        currentPlayerList.remove(result.toString().substring(10));
                        mHandler.sendEmptyMessage(1);
                        output.close();
                        bff.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class GetCardsThread extends Thread {
        @Override
        public void run(){
            OutputStream output;
            try {
                ServerSocket serverSocket = new ServerSocket(30001);
                while (getCardsThreadKeep) {
                    try {
                        Socket socket = serverSocket.accept();
                        Log.i("gaopan123","newGetCardsThread");
                        //获取输入信息
                        //向client发送消息
                        output = socket.getOutputStream();
                        if(hasSendCards) {
                            output.write(cardsContent.getBytes("utf-8"));
                        }else{
                            output.write(nullCardsContent.getBytes("utf-8"));
                        }
                        output.flush();
                        socket.shutdownOutput();

                        BufferedReader bff = new BufferedReader(new InputStreamReader

                                (socket.getInputStream()));
                        //读取信息
                        String result = "";
                        String buffer = "";
                        while ((buffer = bff.readLine()) != null) {
                            result = result + buffer;
                        }
                        output.close();
                        bff.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
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
            finish();
        }
    }

    private void initViews(){
        context=this;
        parentLinearLayout=(LinearLayout) findViewById(R.id.server_scrolayout);
        playerListTextView = (TextView) findViewById(R.id.serve_content);
        ipTextView = (TextView) findViewById(R.id.ip);
        sendCards=(Button)findViewById(R.id.send_cards);
//        xcLoadingImageView=(XCLoadingImageView)findViewById(R.id.xc_imageview);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar_server);
//        Resources resources = this.getResources();
//        Drawable drawable = resources.getDrawable(R.drawable.zhajinhua_cubic);
//        xcLoadingImageView.setImageDrawable(drawable);
//        xcLoadingImageView.setMaskOrientation(TopToBottom);
        showCardsButton=(Button)findViewById(R.id.show_cards_server);
        showCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShow=false;
                for (int i=0;i<players.size();i++) {
                    if(players.get(i).getName().equals(ConstantUtils.userName)){
                        players.get(i).showCards();
                        isShow=true;
                    }
                }
                if(!isShow){
                    ToastUtils.showMessage(getApplicationContext(),"未知错误，请重新登录并输入用户名");
                }
            }
        });
        String ip=getlocalip();
        if(TextUtils.isEmpty(ip)){
            ToastUtils.showMessage(this,"无法获取局域网ip，请确认连接wifi");
            finish();
        }
        ipTextView.setText("用户："+ConstantUtils.userName+"\n房间号:" + getlocalip()+"  \n请其他玩家输入房间号，加入该房间");
    }

    private void initFormationBeforePlay(){
        //找一副牌（不含大小王）
        cardBox=new CardBox(this);
        currentPlayerList.clear();
        currentPlayerList.add(ConstantUtils.userName);
        newJoinThread();
        newGetCardsThread();
        leaveRoomThread();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        exitBy2Click();
    }
}