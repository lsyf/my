package com.loushuiyifan;

import org.junit.Test;

import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class AppTests {


    @Test
    public void test() {
        Path path = Paths.get("D:/IDM");
        System.out.println(path.toString());
        System.out.println(path.getFileName().toString());

    }

    @Test
    public void test2() {
        ArrayDeque<Integer> list = new ArrayDeque();
        TreeMap treeMap = new TreeMap();

        HashMap map = new HashMap();
        LinkedHashMap x = new LinkedHashMap();
        LinkedHashSet v = new LinkedHashSet();

        RandomAccessFile file = null;
        ConcurrentHashMap  a = null;
        AtomicInteger b;

        LockSupport c ;

        AbstractQueuedSynchronizer d;
        ReentrantLock e;
    }


}
