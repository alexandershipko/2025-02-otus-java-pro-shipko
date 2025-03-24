package test;

import annotation.After;
import annotation.Before;
import annotation.Test;


@SuppressWarnings({"java:S112", "java:S106"})
public class TestOne {

    @Before
    void init() {
        System.out.println("\nStart test TestOne");
    }

    @Test
    void test1() {
        System.out.println("test1");
        throw new RuntimeException("Error in test1");
    }

    @Test
    void test2() {
        System.out.println("test2");
    }

    @Test
    void test3() {
        System.out.println("test3");
    }

    @After
    void shutdown() {
        System.out.println("End test TestOne");
    }

}