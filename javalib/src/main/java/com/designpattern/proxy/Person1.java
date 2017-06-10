package com.designpattern.proxy;

/**
 * Created by Administrator on 2017/5/24 0024.
 */

public class Person1 implements Person{

    @Override
    public void signing(int price) {
        System.out.println("A have: " + price + "The price of each box is traded.");
    }
}
