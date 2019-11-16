package com.bbu.gmall.service;

import com.bbu.gmall.beans.PmsBaseAttrInfo;
import com.bbu.gmall.beans.PmsBaseAttrValue;
import com.bbu.gmall.beans.PmsBaseSaleAttr;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);
    List<PmsBaseAttrValue> attrValueList(String attrId);
    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);
    List<PmsBaseSaleAttr> baseSaleAttrList();
}
