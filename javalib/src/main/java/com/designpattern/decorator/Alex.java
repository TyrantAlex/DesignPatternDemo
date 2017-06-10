package com.designpattern.decorator;

/**
 * 装饰者模式
 * Created by Administrator on 2017/5/23 0023.
 */

public class Alex {
    public static void main(String[] args){
        Coke coke = new Coke();
        String make = coke.make();
        System.out.println(make);

        Ice iceCoke = new Ice(new Coke());
        String makeIce = iceCoke.make();
        System.out.println(makeIce);

        Drink iceSugarCoke = new Ice(new Sugar(new Coke()));
        String makeIceSugarCoke = iceSugarCoke.make();
        System.out.println(makeIceSugarCoke);
    }
}
