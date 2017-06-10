package com.designpattern.proxy;

/**
 * Created by Administrator on 2017/5/24 0024.
 */

public class Alex {

    public static void main(String[] args){
        Proxy proxy = new Proxy(new Person1());

        proxy.signing(120);

        proxy.signing(100);

        proxy.signing(99);
    }
}
