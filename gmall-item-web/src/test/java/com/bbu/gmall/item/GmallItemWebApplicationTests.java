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

    @Test
    void contextLoads() {
        for (long i = 10000; i < 99999; i++){
            long x = i * i;
            if(hasEquale(i,x)){
                System.out.println(i+","+x);
            }
        }
    }

    private Boolean hasEquale(long i, long x) {
        boolean b = true;
        String iStr = String.valueOf(i);
        String xStr = String.valueOf(x);
        char[] iCh = iStr.toCharArray();
        char[] xCh = xStr.toCharArray();

        for(int j = 0; j < iCh.length; j ++){
            for (int z = 0 ; z < xCh.length; z ++){
                if(iCh[j] == xCh[z]){
                    b = false;
                    return b;
                }
            }
        }
        return b;
    }

}
