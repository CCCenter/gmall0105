package com.bbu.gmall.gmallmanageservice.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bbu.gmall.beans.PmsBaseCatalog1;
import com.bbu.gmall.beans.PmsBaseCatalog2;
import com.bbu.gmall.beans.PmsBaseCatalog3;
import com.bbu.gmall.gmallmanageservice.mapper.PmsBaseCatalog1Mapper;
import com.bbu.gmall.gmallmanageservice.mapper.PmsBaseCatalog2Mapper;
import com.bbu.gmall.gmallmanageservice.mapper.PmsBaseCatalog3Mapper;
import com.bbu.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Mapper.selectAll();
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2ByCatalog1Id(String catalog1Id) {

        Example example = new Example(PmsBaseCatalog2.class);
        example.createCriteria().andEqualTo("catalog1Id",catalog1Id);

        return pmsBaseCatalog2Mapper.selectByExample(example);
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3ByCatalog2Id(String catalog2Id) {

        Example example = new Example(PmsBaseCatalog3.class);
        example.createCriteria().andEqualTo("catalog2Id",catalog2Id);

        return pmsBaseCatalog3Mapper.selectByExample(example);
    }
}
