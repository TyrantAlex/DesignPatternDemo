package com.androidsourcecode.designpattern.builder;

import com.androidsourcecode.designpattern.builder.bean.Person;

/**
 * Main方法
 * Created by hongshen on 2017/11/17 0017.
 */

public class BuilderMainMethod {
    public static void main(String[] args){
        Person person = new Person.Builder("430304", "Alex").age(12).hobby("Games").phone("18670942222").address("HuNan").build();
        System.out.println(person.toString());
    }
}
