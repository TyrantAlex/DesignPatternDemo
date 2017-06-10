package com.designpattern.strategy;

/**
 * 默认算法
 * Created by Administrator on 2017/5/23 0023.
 */

public class DefualtStrategy implements ActivityStrategy{

    @Override
    public String getActivityPrice() {
        return "No Activity.";
    }

}
