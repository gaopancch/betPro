package com.bet.gaopan.betpro.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bet.gaopan.betpro.activity.ServerActivity;

import java.util.ArrayList;

/**
 * Created by letv on 2017/8/28.
 */

public class Player extends LinearLayout {
    private String name;
    private ArrayList<Card> cards=new ArrayList<>();
    private LinearLayout subLayout;
    private Context context;

    public Player(Context context,String name) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        TextView nameText=new TextView(context);
        nameText.setText("玩家："+name);
        nameText.setTextSize(12);
        nameText.setTextColor(Color.BLACK);
        nameText.setPadding(20,0,20,0);
//        nameText.setMinWidth(350);
//        nameText.setMaxWidth(350);
//        nameText.setBackgroundColor(Color.CYAN);
        addView(nameText,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        //放置card的布局
        subLayout=new LinearLayout(context);
        subLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(subLayout);
        this.name=name;

    }

    public String getName(){
        return  name;
    }

    public ArrayList<Card> getCards(){
        return  cards;
    }

    public void addCard(Card card){
//        cards.add(card);
        subLayout.addView(card,new LinearLayout.LayoutParams(150,210));
//        subLayout.addView(new View(context),new LinearLayout.LayoutParams(10,280));
        cards.add(card);
    }

    public void clearCards(){
        subLayout.removeAllViews();
        cards.clear();
    }

    public void hideCards(){
        for(int i=0;i<cards.size();i++){
            cards.get(i).hideCard();
        }
    }

//    private int timeInterval=1;//1s
    public void showCards(){
        for(int i=0;i<cards.size();i++){
            cards.get(i).showCard();
        }

//        ServerActivity.mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cards.get(i).showCard();
//            }
//        },timeInterval*1000);
//        timeInterval++;
    }

}
