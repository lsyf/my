package com.loushuiyifan;

import org.junit.Test;

import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class AppTests {


    @Test
    public void test() {
        List<Map> list = new ArrayList<>();

        Map map = new HashMap();

        for (int i = 1; i <= 2; i++) {
            map = new HashMap<>();
            map.put("id", i + "");
            list.add(map);
        }

        System.out.println(list);
    }

    @Test
    public void test2() {
        ArrayDeque<Integer> list = new ArrayDeque();
        TreeMap treeMap = new TreeMap();

        HashMap map = new HashMap();
        LinkedHashMap x = new LinkedHashMap();
        LinkedHashSet v = new LinkedHashSet();

        RandomAccessFile file = null;
        ConcurrentHashMap a = null;
        AtomicInteger b;

        LockSupport c;

        AbstractQueuedSynchronizer d;
        ReentrantLock e;
    }


}
