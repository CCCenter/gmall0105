package com.bbu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.bbu.gmall.beans.PmsProductSaleAttr;
import com.bbu.gmall.beans.PmsSkuInfo;
import com.bbu.gmall.beans.PmsSkuSaleAttrValue;
import com.bbu.gmall.service.SkuService;
import com.bbu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;

    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable(name = "skuId") String skuId, Model model) {

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);
        //sku对象
        model.addAttribute("skuInfo",pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs =  spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), pmsSkuInfo.getId());
        model.addAttribute("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);
        //查询当前sku的spu的其他sku的集合的hash表

        Map<String, String> skuSaleAttrHash = new HashMap<>();

        List<PmsSkuInfo> skuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        for (PmsSkuInfo skuInfo : skuInfos) {
            String k = "";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";
            }
            skuSaleAttrHash.put(k,v);
        }
        String skuSaleAttrHashJSONStr = JSON.toJSONString(skuSaleAttrHash);
        model.addAttribute("skuSaleAttrHashJSONStr",skuSaleAttrHashJSONStr);
        return "item";
    }

    @RequestMapping("index")
    public String index(Model model) {
        model.addAttribute("hi","hello");
        return "index";
    }
}
