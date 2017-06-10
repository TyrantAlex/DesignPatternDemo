package com.designpattern.simplefactory;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public abstract class Steam {

    public void buyGame(){

        System.out.println("Login Account...");

        System.out.println("Choose the game: " + chooseGame());

        System.out.println("buying...");
        System.out.println("Success! ");
    }

    abstract String chooseGame();
}
