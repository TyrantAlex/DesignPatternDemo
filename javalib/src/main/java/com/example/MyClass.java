package com.example;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyClass {
    public static void main(String[] args){
        test();
    }

    public static void test() {
//        String dateFormat = "dd day MM month yyyy year";
        String dateFormat = "dd MM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        System.out.println(sdf.format(date));
    }
}
