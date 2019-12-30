package com.bbu.gmall.item;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.beans.OmsCartItem;
import com.bbu.gmall.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class GmallItemWebApplicationTests {
    @Autowired
    CartService cartService;
    @Test
    void contextLoads() {

    }

}
