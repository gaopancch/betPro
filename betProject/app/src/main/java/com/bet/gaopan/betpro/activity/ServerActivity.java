package com.bet.gaopan.betpro.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.utils.ConstantUtils;
import com.bet.gaopan.betpro.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private static TextView client_content;
    private TextView ip;
    private String serverIp = "";
    private static ArrayList<String> currentPlayerList=new ArrayList<>();
    private Thread joinThread,leaveThread,getCardThread;
    private boolean joinThreadKeep=true,leaveThreadKeep=true,getCardsThreadKeep=true;
    private Button sendCards;
    private String joinRoomResponse="{\"result\":\"1\",\"data\":[{\"name\":\"\",\"cards\":[{\"value\":\"\",\"hsdc\":\"\"}]}]}";
    private String leaveRoomResponse="{\"result\":\"0\",\"data\":[{\"name\":\"\",\"cards\":[{\"value\":\"\",\"hsdc\":\"\"}]}]}";
    private String cardsContent="{\"result\":\"500\",\"data\":[" +
            "{\"name\":\"gaopan\",\"cards\":[" +
            "{\"value\":\"A\",\"hsdc\":\"spade\"}," +
            "{\"value\":\"3\",\"hsdc\":\"diamond\"}," +
            "{\"value\":\"5\",\"hsdc\":\"club\"}]}," +

            "{\"name\":\"gaopan1\",\"cards\":[" +
            "{\"value\":\"A\",\"hsdc\":\"heart\"}," +
            "{\"value\":\"6\",\"hsdc\":\"diamond\"}," +
            "{\"value\":\"8\",\"hsdc\":\"club\"}]}]}";

    private String nullCardsContent="{\"result\":\"499\",\"data\":[{\"name\":\"\",\"cards\":[{\"value\":\"\",\"hsdc\":\"\"}]}]}";

    private boolean hasSendCards=false;


    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                client_content.setText("");
                for (int i=0;i<currentPlayerList.size();i++){
                    client_content.append("玩家:" + currentPlayerList.get(i)+ "  已加入\n");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        client_content = (TextView) findViewById(R.id.client_content);
        ip = (TextView) findViewById(R.id.ip);
        sendCards=(Button)findViewById(R.id.send_cards);
        serverIp = getlocalip();
        ip.setText("用户："+ConstantUtils.userName+"\n房间号:" + serverIp+"  \n请其他玩家输入房间号，加入该房间");
        currentPlayerList.clear();
        currentPlayerList.add(ConstantUtils.userName);
        newJoinThread();
        newGetCardsThread();
        leaveRoomThread();
        sendCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasSendCards=!hasSendCards;
            }
        });
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        exitBy2Click();
    }
}