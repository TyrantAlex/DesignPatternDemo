package com.designpattern.strategy;

/**
 * 策略模式
 * Created by Administrator on 2017/5/23 0023.
 */

public class Alex {
    public static void main(String[] args){
        CheckStand checkStand = new CheckStand();
        checkStand.printBill();

        checkStand.setActivityStrategty(new ChristmasStrategy());
        checkStand.printBill();
    }
}
