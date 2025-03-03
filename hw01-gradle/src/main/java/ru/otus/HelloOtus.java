package ru.otus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Set;
import java.util.Map;


public class HelloOtus {

    public static void main(String[] args) {
        System.out.println("Hello, Otus!");


        List<String> list = Lists.newArrayList("one", "two", "three");
        Set<Integer> set = Sets.newHashSet(1, 2, 3, 4);
        Map<String, Integer> map = Maps.newHashMap();

        map.put("Peter", 25);
        map.put("Ann", 35);


        System.out.println("List: " + list);
        System.out.println("Set: " + set);
        System.out.println("Map: " + map);
    }

}