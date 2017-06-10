package com.designpattern.proxy;

/**
 * Created by Administrator on 2017/5/24 0024.
 */

public class Proxy implements Person {

    private Person person;

    public Proxy(Person person){
        this.person = person;
    }

    @Override
    public void signing(int price) {
        System.out.println("The other offer : " + price);

        if (price < 100){
             this.person.signing(price);
        }else{
            negotiate(price);
        }
    }

    private void negotiate(int price) {
        System.out.println("Do not accept the offer, asking for price cuts " + (price - 80));
    }
}
