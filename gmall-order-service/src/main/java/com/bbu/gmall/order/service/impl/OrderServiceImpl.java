package com.bbu.gmall.order.service.impl;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.bbu.gmall.beans.OmsCartItem;
import com.bbu.gmall.beans.OmsOrder;
import com.bbu.gmall.beans.OmsOrderItem;
import com.bbu.gmall.manage.util.RedisUtil;
import com.bbu.gmall.order.mapper.OmsOrderItemMapper;
import com.bbu.gmall.order.mapper.OmsOrderMapper;
import com.bbu.gmall.service.CartService;
import com.bbu.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Reference
    CartService cartService;

    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    OmsOrderMapper omsOrderMapper;

    @Override
    public String genTradeCode(String memberId) {
        String tradeCode = UUID.randomUUID().toString();
        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            String key = "user:"+ memberId + ":tradeCode";
            jedis.setex(key, 60 * 3 ,tradeCode);
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return tradeCode;
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            String key = "user:"+ memberId + ":tradeCode";
            //lua 脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(key), Collections.singletonList(tradeCode));

            if (eval != null && eval != 0L ) {
                return "success";
            }else {
                return "fail";
            }
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public BigDecimal getToTalAmount(String memberId) {
        List<OmsCartItem> cartList = cartService.getCartList(memberId);
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : cartList) {
            if("1".equals(omsCartItem.getIsChecked())){
                totalAmount.add(omsCartItem.getTotalPrice());
            }
        }
        return totalAmount;
    }


    @Override
    public void saveOrder(OmsOrder omsOrder) {
        omsOrderMapper.insertSelective(omsOrder);
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(omsOrder.getId());
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
    }
}
