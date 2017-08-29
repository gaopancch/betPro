package com.bet.gaopan.betpro.views;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by letv on 2017/8/28.
 */

public class Player extends LinearLayout {
    private String name;
    private ArrayList<Card> cards=new ArrayList<>();
    private LinearLayout subLayout;

    public Player(Context context,String name) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        TextView nameText=new TextView(context);
        nameText.setText(name);
        nameText.setTextSize(40);
        nameText.setTextColor(Color.RED);
        addView(nameText,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        //放置card的布局
        subLayout=new LinearLayout(context);
        subLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(subLayout);
        this.name=name;

    }

    public void addCard(Card card){
//        cards.add(card);
        subLayout.addView(card,new LinearLayout.LayoutParams(300,400));
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

    public void showCards(){
        for(int i=0;i<cards.size();i++){
            cards.get(i).showCard();
        }
    }

}
