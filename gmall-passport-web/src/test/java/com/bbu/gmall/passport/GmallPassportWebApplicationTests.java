package com.bbu.gmall.passport;

import com.alibaba.fastjson.JSON;
import com.bbu.gmall.util.HttpclientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GmallPassportWebApplicationTests {

    @Test
    void contextLoads() {
        String s = "https://api.weibo.com/oauth2/access_token";

        Map<String,String> map = new HashMap<>();
        map.put("client_id","1313097522");
        map.put("client_secret","b013921014b78e9dc8b4808174dade57");
        map.put("grant_type","authorization_code");
        map.put("code","95f65b45a67e32ff3863446217dab8b8");
        map.put("redirect_uri","http://passport.gmall.com:8085/vlogin");

        String doPost = HttpclientUtil.doPost(s, map);
        System.out.println(doPost);
    }
    @Test
    void contextLoads1() {
        String s = "https://api.weibo.com/oauth2/get_token_info";

        Map<String,String> map = new HashMap<>();
        map.put("access_token","2.00Zo8VCISecr7Ba4c6c51956_5rgOB");


        String doPost = HttpclientUtil.doPost(s, map);
        Map map1 = JSON.parseObject(doPost, Map.class);
        System.out.println(map1);
    }
    @Test
    void contextLoads2() {
//        String s = "https://api.weibo.com/2/users/show.json";
//
//        Map<String,String> map = new HashMap<>();
//        map.put("access_token","2.00vHQnQHSecr7Bb1ed036f4901Rfyt");
//        map.put("uid","6661091267");
//        map.put("client_id","1313097522");
//
//
//        String doPost = HttpclientUtil.doPost(s, map);

        String s1 = "https://api.weibo.com/2/users/show.json?access_token=2.00Zo8VCISecr7Ba4c6c51956_5rgOB&uid=7366110419";
        String s = HttpclientUtil.doGet(s1);
        Map map1 = JSON.parseObject(s, Map.class);
        System.out.println(map1);
    }

}
