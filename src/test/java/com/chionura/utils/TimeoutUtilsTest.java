package com.chionura.utils;

import org.junit.Test;

import java.util.Arrays;

public class TimeoutUtilsTest {

    public String test() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "haha";
    }

    @Test
    public void testTimeout() {
        System.out.println(TimeoutUtils.process(() -> test(), 2));
    }
}
