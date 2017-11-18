package com.androidsourcecode.designpattern.factory;

/**
 * 具体产品A
 * Created by hongshen on 2017/11/18 0018.
 */

public class ConcreteProductA extends Product {

    @Override
    public void create() {
        System.out.println("Concrete Product A");
    }
}
