package com.bbu.gmall.service;

import com.bbu.gmall.beans.PmsProductImage;
import com.bbu.gmall.beans.PmsProductInfo;
import com.bbu.gmall.beans.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);
    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductImage> spuImageList(String spuId);
}
