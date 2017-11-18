package com.androidsourcecode.designpattern.factory;

/**
 *  抽象工厂类
 * Created by hongshen on 2017/11/18 0018.
 */

public abstract class Factory {

    /**
     * 抽象工厂方法
     * 生成什么由子类去实现
     * @return 具体产品对象
     */
    public abstract Product createProduct();

    /**
     * 抽象工厂方法
     * 具体生产什么由子类实现
     * @param T 产品对象类型
     * @param <T> 具体的产品对象
     * @return
     */
    public abstract <T extends Product> T createProduct(Class<T> T);
}
