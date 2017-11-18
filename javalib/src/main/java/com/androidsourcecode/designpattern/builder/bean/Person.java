package com.androidsourcecode.designpattern.builder.bean;

/**
 *  Builder模式
 * Created by hongshen on 2017/11/17 0017.
 */

public class Person {
    /**
     * 类的属性都是不可变的。所有的属性都添加了 final 修饰符，并且在构造方法中设置了值。并且，对外只提供getters方法。
     */
    //必选
    private final String id;

    private final String name;

    //非必选
    private final String address;

    private final int age;

    private final String hobby;

    private final String phone;

    /**
     * 构造方法是私有的。也就是说调用者不能直接创建 Person 对象。
     * @param builder
     */
    private Person(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.address = builder.address;
        this.age = builder.age;
        this.hobby = builder.hobby;
        this.phone = builder.phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getAge() {
        return age;
    }

    public String getHobby() {
        return hobby;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Builder的内部类构造方法中只接收必传的参数，并且该必传的参数适用了final修饰符。
     */
    public static class Builder{
        //必选
        private final String id;

        private final String name;

        //非必选
        private String address;

        private int age;

        private String hobby;

        private String phone;

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder hobby(String hobby) {
            this.hobby = hobby;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }


    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                ", hobby='" + hobby + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
