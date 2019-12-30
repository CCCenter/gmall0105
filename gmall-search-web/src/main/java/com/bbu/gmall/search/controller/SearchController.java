package com.bbu.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.beans.*;
import com.bbu.gmall.service.AttrService;
import com.bbu.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    @RequestMapping("/list.html")
    public String list(PmsSearchParam pmsSearchParam, Model model) {
        //
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = searchService.list(pmsSearchParam);
        model.addAttribute("skuLsInfoList", pmsSearchSkuInfoList);

        //抽取检索结果包含的平台属性集合
        Set<String> valueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
            List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            if (pmsSkuAttrValueList != null) {
                for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValueList) {
                    String valueId = pmsSkuAttrValue.getValueId();
                    valueIdSet.add(valueId);
                }
            }
        }
        String urlParam = getUrlParam(pmsSearchParam);
        //根据平台属性集合获取属性列表
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListByValueId(valueIdSet);
        //对平台属性进一步处理 去掉当前valueId所在的属性组
        List<String> delValueIds = pmsSearchParam.getValueId();

        //制作面包屑
        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
        //如果删除的valueId 不为空
        if(delValueIds != null){

            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();

            while (iterator.hasNext()) {
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                //获得
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();

                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                    String valueId = pmsBaseAttrValue.getId();
                    for (String delValueId : delValueIds) {
                        if(delValueId.equals(valueId)){
                            //获得要删除的valueId 此属性为面包屑属性

                            //创建面包屑
                            PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                            //设置面包屑id
                            pmsSearchCrumb.setValueId(valueId);
                            //设置面包屑名称
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            //设置面包屑URL 创建一个创建一个searchParam

                            PmsSearchParam pmsSearchCrumbParam = new PmsSearchParam();
                            //设置searchParam属性 只有valueId列表不同
                            BeanUtils.copyProperties(pmsSearchParam, pmsSearchCrumbParam);
                            //创建一个属性列表
                            List<String> valueIds = new ArrayList<>();
                            //添加内容
                            valueIds.addAll(delValueIds);
                            //找到要删除的valueId
                            Iterator<String> ValueIdIterator = valueIds.iterator();
                            //去除本身id在要删除的delValueIds中
                            while(ValueIdIterator.hasNext()){
                                String next = ValueIdIterator.next();
                                if(delValueId.equals(next)){
                                    ValueIdIterator.remove();
                                }
                            }
                            //处理后的valueIds 添加到pmsSearchCrumbParam中 用getUrlParam();获取URL
                            pmsSearchCrumbParam.setValueId(valueIds);
                            String crumbUrlParam = getUrlParam(pmsSearchCrumbParam);
                            //添加打面包屑中
                            pmsSearchCrumb.setUrlParam(crumbUrlParam);
                            //添加入面包屑列表
                            pmsSearchCrumbs.add(pmsSearchCrumb);

                            //删除平台属性
                            iterator.remove();
                        }
                    }
                }
            }
        }
        String keyword = pmsSearchParam.getKeyword();

        model.addAttribute("attrValueSelectedList", pmsSearchCrumbs);
        model.addAttribute("attrList", pmsBaseAttrInfos);
        model.addAttribute("urlParam", urlParam);
        model.addAttribute("keyword", keyword);

        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        StringBuilder urlParam = new StringBuilder();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        if(StringUtils.isNotBlank(keyword)){
            urlParam.append("keyword=" + keyword);
        }
        if(StringUtils.isNotBlank(catalog3Id)){
            urlParam.append("catalog3Id=" + catalog3Id);
        }
        List<String> valueIds = pmsSearchParam.getValueId();
        if (valueIds != null) {
            for (String valueId : valueIds) {
                urlParam.append("&valueId=" + valueId);
            }
        }
        return urlParam.toString();
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
