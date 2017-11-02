package com.loushuiyifan;

import org.junit.Test;

import java.util.ArrayList;

public class AppTests {



    @Test
    public void test() {
        ArrayList<Integer> list = new ArrayList();
        for (int i=0;i<10;i++) {
            list.add(i);
        }
        list.toArray();
    }


}
