package com.bet.gaopan.betpro.views;

import android.content.Context;

import com.bet.gaopan.betpro.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by letv on 2017/8/28.
 */

public class CardBox {

    /**这是一副牌，*/
    private ArrayList<Card> cards=new ArrayList<Card>();
//    private ArrayList<Integer> cardHaveBeenSend=new ArrayList<Integer>();
    private Random random;
    private int numberCardRemain=52;
    private Context context;

    public void  clearCardBox(){
        cards.clear();
        numberCardRemain=52;
        fillAllCards();
    }

    public CardBox(Context context){
        this.context=context;
        fillAllCards();
    }

    private void fillAllCards(){
        for(int i=1;i<14; i++){
            random=new Random();
            cards.add(new Card(context,i, Card.HSDC_type.diamond));
            cards.add(new Card(context,i, Card.HSDC_type.club));
            cards.add(new Card(context,i, Card.HSDC_type.spade));
            cards.add(new Card(context,i, Card.HSDC_type.heart));
        }
    }

    //向外输出一个不会重复的牌
    public Card sendCard(){
       int n=random.nextInt(numberCardRemain--);
        Card card=cards.get(n);
        cards.remove(card);

//       cardHaveBeenSend.add(n);//标记这个牌已经不在牌堆

        return card;
    }

}
