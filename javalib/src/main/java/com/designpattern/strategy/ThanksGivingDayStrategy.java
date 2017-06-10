package com.designpattern.strategy;

/**
 * 感恩节活动算法
 * Created by Administrator on 2017/5/23 0023.
 */

public class ThanksGivingDayStrategy implements ActivityStrategy{

    @Override
    public String getActivityPrice() {
        return "ThanksGivingDay all drinking 5 discount.";
    }

}
