package com.bbu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bbu.gmall.beans.PmsSkuAttrValue;
import com.bbu.gmall.beans.PmsSkuImage;
import com.bbu.gmall.beans.PmsSkuInfo;
import com.bbu.gmall.beans.PmsSkuSaleAttrValue;
import com.bbu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.bbu.gmall.manage.mapper.PmsSkuImageMapper;
import com.bbu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.bbu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.bbu.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {


        List<PmsSkuAttrValue> SkuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        //默认图片
        if(StringUtils.isBlank(pmsSkuInfo.getSkuDefaultImg())){
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
    public PmsSkuInfo getSkuById(String skuId) {

        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        pmsSkuInfo.setSkuImageList(pmsSkuImageMapper.select(pmsSkuImage));

        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }
}
