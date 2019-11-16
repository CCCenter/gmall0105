package com.bbu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.beans.PmsBaseCatalog1;
import com.bbu.gmall.beans.PmsBaseCatalog2;
import com.bbu.gmall.beans.PmsBaseCatalog3;
import com.bbu.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
//@CrossOrigin(origins = "http://127.0.0.1:8888")
@CrossOrigin
public class CatalogController {

    @Reference
    CatalogService catalogService;

    @RequestMapping("/getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1() {
        return catalogService.getCatalog1();
    }

    @RequestMapping("/getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(@RequestParam(name = "catalog1Id") String catalog1Id) {
        return catalogService.getCatalog2ByCatalog1Id(catalog1Id);
    }

    @RequestMapping("/getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(@RequestParam(name = "catalog2Id") String catalog2Id) {
        return  catalogService.getCatalog3ByCatalog2Id(catalog2Id);
    }
}
