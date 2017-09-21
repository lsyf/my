package com.loushuiyifan;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppTests {

    class A{
        int a = 1;
    }

    class B extends A {
        int a ;
    }


    @Test
    public void test() {
        Path path = Paths.get("d:/");
        path = path.resolve(Paths.get(""));
        System.out.println(path);
    }


}
