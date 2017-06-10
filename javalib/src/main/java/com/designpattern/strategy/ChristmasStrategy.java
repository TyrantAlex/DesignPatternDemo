package com.designpattern.strategy;

/**
 * 圣诞节活动算法
 * Created by Administrator on 2017/5/23 0023.
 */

public class ChristmasStrategy implements ActivityStrategy{

    @Override
    public String getActivityPrice() {
        return "Christmas day all drinking 8 discount.";
    }

}
