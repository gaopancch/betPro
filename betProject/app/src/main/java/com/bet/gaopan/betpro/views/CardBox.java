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
    private int numberCardRemain=52;//当前牌盒中的牌数目
    private Context context;

    //洗牌
    public void  clearCardBox(){
        cards.clear();
        numberCardRemain=52;
        fillAllCards();
    }

    public CardBox(Context context){
        this.context=context;
        fillAllCards();
    }

    //初始化，把所有牌加入牌盒
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
        Card card=cards.get(n);//从牌盒中取出来一个用于输出，
        cards.remove(card);//把牌盒中的拿出牌去掉

//       cardHaveBeenSend.add(n);//标记这个牌已经不在牌堆

        return card;
    }

}
