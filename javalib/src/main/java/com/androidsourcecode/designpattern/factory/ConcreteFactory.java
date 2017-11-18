package com.androidsourcecode.designpattern.factory;

/**
 *  具体工厂类
 * Created by hongshen on 2017/11/18 0018.
 */

public class ConcreteFactory extends Factory {

    @Override
    public Product createProduct() {
        return new ConcreteProductA();
    }

    @Override
    public <T extends Product> T createProduct(Class<T> clz) {
        Product product = null;
        try {
            product = (Product) Class.forName(clz.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) product;
    }
}
