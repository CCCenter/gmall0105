package com.bbu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bbu.gmall.beans.PmsBaseAttrInfo;
import com.bbu.gmall.beans.PmsBaseAttrValue;
import com.bbu.gmall.beans.PmsBaseSaleAttr;
import com.bbu.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.bbu.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.bbu.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.bbu.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        Example example = new Example(PmsBaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectByExample(example);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            pmsBaseAttrInfo.setAttrValueList(this.attrValueList(pmsBaseAttrInfo.getId()));
        }
        return pmsBaseAttrInfos;
    }

    @Override
    public List<PmsBaseAttrValue> attrValueList(String attrId) {
        Example example = new Example(PmsBaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId", attrId);
        return pmsBaseAttrValueMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();

        //插入操作
        if(StringUtils.isBlank(pmsBaseAttrInfo.getId())){
            //保存属性
            pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);

            //保存属性值
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        }else{
            //更新属性
            pmsBaseAttrInfoMapper.updateByPrimaryKeySelective(pmsBaseAttrInfo);

            //更新属性值
            PmsBaseAttrValue t = new PmsBaseAttrValue();
            t.setAttrId(pmsBaseAttrInfo.getId());
            pmsBaseAttrValueMapper.delete(t);
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
            }
        }
        return "success";
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueId(Set<String> valueIdSet) {
        String valueIdStr = StringUtils.join(valueIdSet, ",");
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectAttrValueListByValueId(valueIdStr);
        return pmsBaseAttrInfos;
    }


}
