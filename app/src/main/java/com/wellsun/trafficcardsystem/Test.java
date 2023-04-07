package com.wellsun.trafficcardsystem;

/**
 * date     : 2023-03-20
 * author   : ZhaoZheng
 * describe :
 */
public class Test {

    public static void main(String[] args){
    String str ="123456";
        StringBuilder sb = new StringBuilder(str);

        StringBuilder ab = sb.replace(2, 4, "ab");
        System.out.println(ab.toString());
    }

}
