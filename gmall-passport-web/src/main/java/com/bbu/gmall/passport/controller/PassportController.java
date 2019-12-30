package com.bbu.gmall.passport.controller;

import com.bbu.gmall.beans.UmsMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PassportController {

    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String token, Model model){

        return "success";
    }

    @RequestMapping("/index")
    public String index(String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "index";
    }

    @RequestMapping("/login")
    @ResponseBody
    public String login(UmsMember umsMember){
        //调用用户名密码

        return "token";
    }

}
