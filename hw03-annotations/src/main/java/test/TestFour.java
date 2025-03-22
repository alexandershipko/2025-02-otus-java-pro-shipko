package test;

import annotation.After;
import annotation.Before;
import annotation.Test;


@SuppressWarnings("java:S106")
public class TestFour {

    @Before
    void init() {
        System.out.println("\nStart test TestFour");
    }

    @Test
    void test1() {
        System.out.println("test1");
    }

    @Test
    void test2() {
        System.out.println("test2");
    }

    @Test
    void test3() {
        System.out.println("test3");
    }

    @Test
    void test4() {
        System.out.println("test4");
    }

    @After
    void shutdown() {
        System.out.println("End test TestFour");
    }

}