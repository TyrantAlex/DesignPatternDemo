
package com.designpattern.strategy;

/**
 * 双12活动算法
 * Created by Administrator on 2017/5/23 0023.
 */

public class DoubleTwelveDayStrategy implements ActivityStrategy{

    @Override
    public String getActivityPrice() {
        return "DoubleTwelve day all drinking 1 discount.";
    }

}
