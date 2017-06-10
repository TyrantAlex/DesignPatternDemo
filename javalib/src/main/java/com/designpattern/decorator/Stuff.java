package com.designpattern.decorator;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public abstract class Stuff implements Drink{

    private Drink originalDrink;

    public Stuff (Drink originalDrink){
        this.originalDrink = originalDrink;
    }

    @Override
    public String make() {
        return originalDrink.make() + " add a :" + stuffName();
    }

    abstract String stuffName();
}
