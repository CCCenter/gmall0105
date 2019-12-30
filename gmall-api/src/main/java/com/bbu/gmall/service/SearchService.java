package com.bbu.gmall.service;

import com.bbu.gmall.beans.PmsSearchParam;
import com.bbu.gmall.beans.PmsSearchSkuInfo;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
