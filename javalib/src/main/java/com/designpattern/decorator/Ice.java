package com.designpattern.decorator;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public class Ice extends Stuff {

    public Ice (Drink originalDrink){
        super(originalDrink);
    }

    @Override
    String stuffName() {
        return "Ice";
    }
}
