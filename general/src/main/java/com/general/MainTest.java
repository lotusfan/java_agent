package com.general;

import java.util.concurrent.TimeUnit;

/**
 * @title: MainTest
 * @description:
 * @author: zhangfan
 * @data: 2021年09月26日 19:36
 */
public class MainTest {

    public static void main(String[] args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        sub1();
    }

    public static void sub1() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Main2.sss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
