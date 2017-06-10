package com.designpattern.strategy;

/**
 * 收银台
 * Created by Administrator on 2017/5/23 0023.
 */

public class CheckStand {

    private ActivityStrategy activityStrategty;

    public CheckStand(){
        this.activityStrategty = new DefualtStrategy();
    }

    public CheckStand(ActivityStrategy mActivityStrategty){
        this.activityStrategty = mActivityStrategty;
    }

    public void setActivityStrategty(ActivityStrategy mActivityStrategty){
        this.activityStrategty = mActivityStrategty;
    }

    public void  printBill(){
        System.out.println( "This Account Bill is : " + activityStrategty.getActivityPrice());
    }
}
