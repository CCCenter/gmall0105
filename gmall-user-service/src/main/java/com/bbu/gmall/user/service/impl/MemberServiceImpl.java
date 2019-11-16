package com.bbu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bbu.gmall.beans.UmsMember;
import com.bbu.gmall.service.MemberService;
import com.bbu.gmall.user.mapper.UmsMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//dubbo service 注解 RPC（Remote process call） 远程地址调用
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    UmsMemberMapper umsMemberMapper;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = umsMemberMapper.selectAll();
        return umsMembers;
    }
}
