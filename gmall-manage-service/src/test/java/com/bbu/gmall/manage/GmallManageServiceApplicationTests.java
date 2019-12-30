package com.bbu.gmall.manage;

import com.bbu.gmall.manage.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

   @Autowired
    RedisUtil redisUtil;

    @Test
    public void contextLoads() {

        Jedis jedis = redisUtil.getJedis();
        String skuId = "123";
        jedis.set("sku:" + skuId + ":info", "1");
        System.out.println(jedis.get("hello"));
    }

}
