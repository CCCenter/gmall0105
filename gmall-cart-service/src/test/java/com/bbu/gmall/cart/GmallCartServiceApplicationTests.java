package com.bbu.gmall.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.beans.OmsCartItem;
import com.bbu.gmall.cart.mapper.OmsCartItemMapper;
import com.bbu.gmall.cart.service.impl.CartServerImpl;
import com.bbu.gmall.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallCartServiceApplicationTests {

    CartServerImpl cartService = new CartServerImpl();

    @Autowired
    OmsCartItemMapper omsCartItemMapper;
    @Test
    void contextLoads() {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductName("测试专用机器");

        int insert = omsCartItemMapper.insert(omsCartItem);

        System.out.println(insert);
    }

}
