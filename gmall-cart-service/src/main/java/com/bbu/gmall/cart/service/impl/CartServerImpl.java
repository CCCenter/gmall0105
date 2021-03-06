package com.bbu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.bbu.gmall.beans.OmsCartItem;
import com.bbu.gmall.cart.mapper.OmsCartItemMapper;
import com.bbu.gmall.manage.util.RedisUtil;
import com.bbu.gmall.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServerImpl implements CartService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem omsCartItemFromDb = omsCartItemMapper.selectOne(omsCartItem);
        return omsCartItemFromDb;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        if (StringUtils.isNotBlank(omsCartItem.getMemberId())) {
            omsCartItemMapper.insertSelective(omsCartItem);
        }
        flushCartCache(omsCartItem.getMemberId());
    }

    @Override
    public void updateCartBySkuId(OmsCartItem omsCartItem) {
        if (StringUtils.isNotBlank(omsCartItem.getProductSkuId())) {

            Example example = new Example(OmsCartItem.class);
            example.createCriteria().andEqualTo("memberId", omsCartItem.getMemberId())
                    .andEqualTo("productSkuId", omsCartItem.getProductSkuId());
            omsCartItemMapper.updateByExampleSelective(omsCartItem, example);
        }
        flushCartCache(omsCartItem.getMemberId());
    }

    @Override
    public List<OmsCartItem> flushCartCache(String memberId) {
        List<OmsCartItem> omsCartItems = null;
        Jedis jedis = null;
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotBlank(memberId)) {
            //同步到redis
            try {
                OmsCartItem omsCartItem = new OmsCartItem();
                omsCartItem.setMemberId(memberId);
                omsCartItems = omsCartItemMapper.select(omsCartItem);
                jedis = redisUtil.getJedis();
                for (OmsCartItem cartItem : omsCartItems) {
                    cartItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                    map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
                }
                jedis.del("user:" + memberId + ":cart");
                jedis.hmset("user:" + memberId + ":cart", map);

            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }

        }
        return omsCartItems;
    }

    @Override
    public List<OmsCartItem> getCartList(String memberId) {
        Jedis jedis = null;
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        try {
            jedis = redisUtil.getJedis();
            String key = "user:" + memberId + ":cart";
            List<String> hvals = jedis.hvals(key);
            if (hvals == null) {
                for (String hval : hvals) {
                    OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                    omsCartItemList.add(omsCartItem);
                }
            } else {
                omsCartItemList = flushCartCache(memberId);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            String message = e.getMessage();
//            logService.addErrLog(message);
            return null;
        } finally {
            jedis.close();
        }
        return omsCartItemList;
    }

    @Override
    public OmsCartItem getCartItem(OmsCartItem omsCartItem) {
        OmsCartItem omsCartItemFromDB = omsCartItemMapper.selectByPrimaryKey(omsCartItem.getId());
        return omsCartItemFromDB;
    }
}
