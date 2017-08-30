package com.bet.gaopan.betpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bet.gaopan.betpro.R;
import com.bet.gaopan.betpro.json.ParaCardJson;
import com.bet.gaopan.betpro.utils.ActivityUtils;
import com.bet.gaopan.betpro.utils.ConstantUtils;
import com.bet.gaopan.betpro.views.Card;
import com.bet.gaopan.betpro.views.CardBox;
import com.bet.gaopan.betpro.views.Player;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RoomActivity extends Activity {

    private LinearLayout linearLayoutSub;
    private Button distribute,hideCardsButton;
    private CardBox cardBox;
    private ArrayList<Player> players=new ArrayList<Player>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        linearLayoutSub=(LinearLayout)findViewById(R.id.linear_sub);
        distribute=(Button)findViewById(R.id.distribute);
        hideCardsButton=(Button)findViewById(R.id.hideCards);
        //找一副牌（不含大小王）
        cardBox=new CardBox(this);

        //找玩家
        final Player player1=new Player(this,ConstantUtils.userName);
        final Player player2=new Player(this,"computer1");
        final Player player3=new Player(this,"computer2");
        players.add(player1);
        players.add(player2) ;
        players.add(player3);

        //玩家落座
        for(int i=0;i<players.size();i++){
        linearLayoutSub.addView(players.get(i));
        }


        distribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //洗牌
                cardBox.clearCardBox();
                //发牌
                for(int i=0;i<players.size();i++){

                    players.get(i).clearCards();
                    players.get(i).addCard(cardBox.sendCard());
                    players.get(i).addCard(cardBox.sendCard());
                    players.get(i).addCard(cardBox.sendCard());
                }
//                ActivityUtils.goToActivity(RoomActivity.this,ClientActivity.class);
//                playersToParaCardJson();
                Gson gson=new Gson();
                Log.i("gaopan123","gson.toJson(playersToParaCardJson())=="+gson.toJson(playersToParaCardJson()));
            }
        });

        hideCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                players.get(0).hideCards();
//                ActivityUtils.goToActivity(RoomActivity.this,ServerActivity.class);

            }
        });
       // {"data":[{"cards":[{"hsdc":"diamond","value":"9"},{"hsdc":"club","value":"7"},{"hsdc":"heart","value":"2"}],"name":"bnk"},{"cards":[{"hsdc":"diamond","value":"3"},{"hsdc":"spade","value":"6"},{"hsdc":"diamond","value":"2"}],"name":"computer1"},{"cards":[{"hsdc":"club","value":"4"},{"hsdc":"club","value":"3"},{"hsdc":"heart","value":"6"}],"name":"computer2"}],"result":"500"}

    }

    private ParaCardJson playersToParaCardJson(){
        ParaCardJson paraCardJson=new ParaCardJson();
        paraCardJson.setResult("500");
        ArrayList<ParaCardJson.playerBean> data=new ArrayList<>();
        ParaCardJson.playerBean playerBean;
        ArrayList<ParaCardJson.playerBean.CardsBean> cards;
        for (int i=0;i<players.size();i++){
            playerBean=new ParaCardJson.playerBean();
            playerBean.setName(players.get(i).getName());
            cards=new ArrayList<>();
            ParaCardJson.playerBean.CardsBean cardsBean;
            for (int j=0;j<players.get(i).getCards().size();j++){
                cardsBean=new ParaCardJson.playerBean.CardsBean();
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

}
