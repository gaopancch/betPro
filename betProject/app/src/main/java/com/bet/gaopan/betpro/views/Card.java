package com.bet.gaopan.betpro.views;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;

import static com.bet.gaopan.betpro.views.Card.HSDC_type.club;
import static com.bet.gaopan.betpro.views.Card.HSDC_type.diamond;
import static com.bet.gaopan.betpro.views.Card.HSDC_type.heart;
import static com.bet.gaopan.betpro.views.Card.HSDC_type.spade;

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
        initNumberText(context);
        if(value>13||value<1){
            value=0;
        }
        number=numbers[value];
        this.type=type;
        showCard();
    }

    public Card(Context context){
        super(context);
        initNumberText(context);
    }

    public void setValue(int value){
        number=numbers[value];
    }

    public void setValue(String value){
        number=value;
    }

    public String getValue(){
        return  number;
    }

    public void setType(HSDC_type type){
        this.type=type;
    }

    public void setType(String type){
        if(type.equals("heart")){
            this.type=heart;
        }else if(type.equals("spade")){
            this.type=spade;
        }else if(type.equals("diamond")){
            this.type=diamond;
        }else if(type.equals("club")){
            this.type=club;
        }
    }

    public String getType(){
        String typeStr="";
        switch (type){
            case heart:
                typeStr="heart";
                break;
            case spade:
                typeStr="spade";
                break;
            case diamond:
                typeStr="diamond";
                break;
            case club:
                typeStr="club";
                break;
        }
        return  typeStr;
    }

    private void initNumberText(Context context){
        numberText=new TextView(context);
        numberText.setTextSize(20);
        numberText.setTextColor(Color.WHITE);
        setOrientation(LinearLayout.VERTICAL);
        addView(numberText);
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
