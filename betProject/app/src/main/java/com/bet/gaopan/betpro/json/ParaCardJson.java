package com.bet.gaopan.betpro.json;

import java.util.List;

/**
 * Created by letv on 2017/8/29.
 */

public class ParaCardJson {

    /**
     * result : 0
     * data : [{"name":"gaopan","cards":[{"value":"A","hsdc":"spade"},{"value":"3","hsdc":"diamond"},{"value":"5","hsdc":"club"}]},{"name":"gaopan","cards":[{"value":"A","hsdc":"spade"},{"value":"3","hsdc":"diamond"},{"value":"5","hsdc":"club"}]}]
     */

    private String result;
    private List<DataBean> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : gaopan
         * cards : [{"value":"A","hsdc":"spade"},{"value":"3","hsdc":"diamond"},{"value":"5","hsdc":"club"}]
         */

        private String name;
        private List<CardsBean> cards;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CardsBean> getCards() {
            return cards;
        }

        public void setCards(List<CardsBean> cards) {
            this.cards = cards;
        }

        public static class CardsBean {
            /**
             * value : A
             * hsdc : spade
             */

            private String value;
            private String hsdc;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getHsdc() {
                return hsdc;
            }

            public void setHsdc(String hsdc) {
                this.hsdc = hsdc;
            }
        }
    }
}
