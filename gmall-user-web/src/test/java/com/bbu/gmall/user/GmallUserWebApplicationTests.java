package com.bbu.gmall.user;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SpringBootTest
class GmallUserWebApplicationTests {

    @Test
    void contextLoads() {
        List<String> s = new ArrayList<>();
        s.add("a");
        s.add("b");
        s.add("c");
        s.add("d");
        s.remove("b");
        System.out.println(s.size());
    }

}
