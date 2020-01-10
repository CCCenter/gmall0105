package com.bbu.gmall.user.controller;



import com.bbu.gmall.beans.UmsMember;
import com.bbu.gmall.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UmsMemberController {

    @Autowired
    MemberService memberService;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public List getAllUser() {
        List<UmsMember> UmsMembers = memberService.getAllUser();
        return UmsMembers;
    }
}
