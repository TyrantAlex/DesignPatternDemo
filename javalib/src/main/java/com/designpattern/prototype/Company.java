package com.designpattern.prototype;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/24 0024.
 */

public class Company implements Cloneable {

    private ArrayList<String> drinks = new ArrayList<>();

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addDrink(String drink){
        drinks.add(drink);
    }

    @Override
    protected Company clone(){
        Company company = null;
        try{
            company = (Company) super.clone();

            company.drinks = (ArrayList<String>) this.drinks.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return company;
    }

    @Override
    public String toString() {
        return "{" +
                "name: '" + getName() + '\'' +
                ", drinkname: " + drinks  + '\'' +
                '}';
    }
}
