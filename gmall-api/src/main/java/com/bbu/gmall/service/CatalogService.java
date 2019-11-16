package com.bbu.gmall.service;

import com.bbu.gmall.beans.PmsBaseCatalog1;
import com.bbu.gmall.beans.PmsBaseCatalog2;
import com.bbu.gmall.beans.PmsBaseCatalog3;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();
    List<PmsBaseCatalog2> getCatalog2ByCatalog1Id(String catalog1Id);
    List<PmsBaseCatalog3> getCatalog3ByCatalog2Id(String catalog2Id);
}
