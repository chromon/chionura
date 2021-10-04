package com.chionura.demo;

public class ServiceDemo {

    public String print() {
        System.out.println("-"+"hehe");
        return "haha";
    }

    public String print(String arg0, Integer arg1) {
        System.out.println("=" + arg0 + "-" + arg1);
        return arg0 + "-" + arg1;
    }
}