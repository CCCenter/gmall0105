package com.bbu.gmall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.beans.UmsMember;
import com.bbu.gmall.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UmsMemberController {
    //   远程注入  -- -  协议代理
    @Reference
    MemberService memberService;

    @RequestMapping("index")
    @ResponseBody
    public String index() {
        return "hello world";
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser() {
        List<UmsMember> UmsMembers = memberService.getAllUser();
        return UmsMembers;
    }
}
