package com.bbu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.beans.PmsBaseAttrInfo;
import com.bbu.gmall.beans.PmsBaseAttrValue;
import com.bbu.gmall.beans.PmsBaseSaleAttr;
import com.bbu.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {

    @Reference
    AttrService attrService;

    @RequestMapping("/attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(@RequestParam(name = "catalog3Id") String catalog3Id){
        return attrService.attrInfoList(catalog3Id);
    }

    @RequestMapping("/saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        String success = attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }

    @RequestMapping("/getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> attrValueList(@RequestParam(name = "attrId") String attrId){
        return attrService.attrValueList(attrId);
    }

    @RequestMapping("/baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        return attrService.baseSaleAttrList();
    }

}
