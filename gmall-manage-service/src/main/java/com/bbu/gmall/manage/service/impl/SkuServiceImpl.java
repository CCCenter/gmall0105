package com.bbu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.bbu.gmall.beans.PmsSkuAttrValue;
import com.bbu.gmall.beans.PmsSkuImage;
import com.bbu.gmall.beans.PmsSkuInfo;
import com.bbu.gmall.beans.PmsSkuSaleAttrValue;
import com.bbu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.bbu.gmall.manage.mapper.PmsSkuImageMapper;
import com.bbu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.bbu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.bbu.gmall.manage.util.RedisUtil;
import com.bbu.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {


        List<PmsSkuAttrValue> SkuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        //默认图片
        if (StringUtils.isBlank(pmsSkuInfo.getSkuDefaultImg())) {
            pmsSkuInfo.setSkuDefaultImg(skuImageList.get(0).getImgUrl());
        }
        //插入Spuid
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        //插入skuInfo
        int i = pmsSkuInfoMapper.insert(pmsSkuInfo);
        //返回主键
        String skuId = pmsSkuInfo.getId();

        //插入平台属性PmsSkuAttrValue 1
        for (PmsSkuAttrValue pmsSkuAttrValue : SkuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }

        //插入销售属性PmsSkuSaleAttrValue
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }

        //插入图片属性PmsSkuImage  1
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setProductImgId(skuId);
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        return "success";
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId, String ip) {
        PmsSkuInfo pmsSkuInfo;
        //连接缓存
        Jedis jedis = redisUtil.getJedis();
        //查询缓存
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);

        if (StringUtils.isNotBlank(skuJson)) {
            //在redis中查询到数据
            System.out.println(ip+"在redis中查询到数据");

            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);

        } else {
            //未命中 查询MySQL
            //设置分布式锁 设置过期时间

            System.out.println("ip为"+ip+"同学"+Thread.currentThread().getName()+"在redis中未查询到数据___申请分布式锁："+ "sku:" + skuId + ":lock");
            String token = UUID.randomUUID().toString();
            String ok = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10 * 1000);
            if ("OK".equals(ok)) {
                System.out.println("ip为"+ip+"同学"+Thread.currentThread().getName()+"申请成功 访问数据库："+ "sku:" + skuId + ":lock");
                //设置成功有权在10s 的 过期时间内访问数据库
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                pmsSkuInfo = getSkuByIdFromDb(skuId);

                if (pmsSkuInfo != null) {
                    //如果数据存在 MySQL查询结果存入redis
                    String s = jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                    System.out.println("存入redis" + s);
                } else {
                    //如果数据不存在 防止缓存穿透 设置 redis 三分钟之内 为空值
                    System.out.println("设置空值");
                    jedis.setex("sku:" + skuId + ":info", 60 * 3, "");
                }

                System.out.println("ip为"+ip+"同学"+Thread.currentThread().getName()+"释放分布式锁："+ "sku:" + skuId + ":lock");

                //操作完毕后释放分布锁
                String lockToken = jedis.get("sku:" + skuId + ":lock");
                if(token.equals(lockToken)) {
                    jedis.del("sku:" + skuId + ":lock");
                }

            } else {
                //设置失败 自旋（线程在一定时间内在重新访问本方法）
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ip为"+ip+"同学"+Thread.currentThread().getName()+ "申请失败 "+Thread.currentThread().getName()+"自旋");
                //getSkuById(skuId);直接调用 会开启新线程 导致当前线程失去控制
                return getSkuById(skuId, ip);
            }

        }
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId) {

        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        pmsSkuInfo.setSkuImageList(pmsSkuImageMapper.select(pmsSkuImage));

        return pmsSkuInfo;
    }
}
