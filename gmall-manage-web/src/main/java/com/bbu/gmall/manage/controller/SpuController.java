package com.bbu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.beans.PmsProductImage;
import com.bbu.gmall.beans.PmsProductInfo;
import com.bbu.gmall.beans.PmsProductSaleAttr;
import com.bbu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @RequestMapping("/spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(@RequestParam(name = "catalog3Id") String catalog3Id) {
        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);
        return pmsProductInfos;
    }

    @RequestMapping("/fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam(name = "file") MultipartFile multipartFile) {
        //将文件上传到分布式文件系统用

        //将图片的存储路径返还给页面
        String imgUrl = "https://img13.360buyimg.com/n1/s450x450_jfs/t1/40122/36/5988/643761/5ccfe287Ed9324a6a/42c5e50fe2f32804.png";
        return imgUrl;
    }

    @RequestMapping("/spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(@RequestParam(name = "spuId") String spuId) {
        List<PmsProductSaleAttr> pmsProductInfos = spuService.spuSaleAttrList(spuId);
        return pmsProductInfos;
    }

    @RequestMapping("/spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(@RequestParam(name = "spuId") String spuId) {
        List<PmsProductImage> pmsProductImages = spuService.spuImageList(spuId);
        return pmsProductImages;
    }

    @RequestMapping("/saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        return "success";
    }
}
