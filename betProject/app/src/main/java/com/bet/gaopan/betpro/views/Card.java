package com.bet.gaopan.betpro.views;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;

/**
 * Created by letv on 2017/8/28.
 */

public class Card extends LinearLayout {
    public enum HSDC_type{
        heart,
        spade,
        diamond,
        club
    }
    /**
     * 红桃:H-Heart 桃心(象形),代表爱情.
     * 黑桃:S-Spade 橄榄叶(象形),代表和平.
     *方块:D-Diamond 钻石(形同意合),代表财富.
     *梅花:C-Club 三叶草(象形),代表幸运.*/
    private String[] numbers={"unknow","A","2","3","4","5","6","7","8","9","10","J","Q","K"};
    private String number;
    private HSDC_type type;
    private TextView numberText;

    public Card(Context context,int value,HSDC_type type) {
        super(context);
        numberText=new TextView(context);
        numberText.setTextSize(50);
        numberText.setTextColor(Color.WHITE);
        setOrientation(LinearLayout.VERTICAL);
        addView(numberText);
        if(value>13||value<1){
            value=0;
        }
        number=numbers[value];
        this.type=type;
        showCard();
    }

    private void setBackgroundWithType(){
        switch (type){
            case heart:
                setBackgroundResource(R.drawable.heart);
                break;
            case spade:
                setBackgroundResource(R.drawable.spade);
                break;
            case club:
                setBackgroundResource(R.drawable.club);
                break;
            case diamond:
                setBackgroundResource(R.drawable.diamond);
                break;
            default:
                break;
        }
    }

    public void hideCard(){
        numberText.setText("");
        setBackgroundResource(R.drawable.bet);
    }

    public void showCard(){
        numberText.setText(number);
        setBackgroundWithType();
    }

}
