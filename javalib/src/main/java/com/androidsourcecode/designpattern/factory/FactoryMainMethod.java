package com.androidsourcecode.designpattern.factory;

/**
 * Created by hongshen on 2017/11/18 0018.
 */

public class FactoryMainMethod {

    public static void main(String[] args) {
        Factory factory = new ConcreteFactory();
        Product product = factory.createProduct();
        product.create();

        ConcreteProductB productB = factory.createProduct(ConcreteProductB.class);
        productB.create();
    }
}
