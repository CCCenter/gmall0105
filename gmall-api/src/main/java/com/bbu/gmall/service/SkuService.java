package com.bbu.gmall.service;

import com.bbu.gmall.beans.PmsSkuInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SkuService {

    String saveSkuInfo(PmsSkuInfo pmsSkuInfo);
    PmsSkuInfo getSkuById(String skuId);
    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);
}
