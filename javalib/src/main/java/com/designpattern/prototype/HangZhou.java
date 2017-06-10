package com.designpattern.prototype;

/**
 * Created by Administrator on 2017/5/24 0024.
 */

public class HangZhou {

    public static void main(String[] args) {

        Company company = new GuangGu();
        System.out.println("GuangGu :" + company);

        Company hangzhouCompany = company.clone();
        hangzhouCompany.setName("HangZhou Company");
        hangzhouCompany.addDrink("XueBi");
        System.out.println("Hangzhou : " + hangzhouCompany);
        System.out.println("GuangGu :" + company);
    }
}
