package com.bbu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.bbu.gmall.beans.UmsMember;
import com.bbu.gmall.passport.sourceType.PlatformPType;
import com.bbu.gmall.service.MemberService;
import com.bbu.gmall.util.HttpclientUtil;
import com.bbu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    MemberService memberService;

    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String token, String currentIp){
        Map<String, Object> map = new HashMap<>();
        //此时ip 为 拦截器转发的请求ip interceptor

        Map<String, Object> decode = JwtUtil.decode(token, "2019bbu0105", currentIp);
        if(decode != null){
            map.put("status", "success");
            map.put("memberId", decode.get("memberId"));
            map.put("nickname", decode.get("nickname"));
        }else {
            map.put("status", "fail");
        }

        return JSON.toJSONString(map);
    }

    @RequestMapping("/vlogin")
    public String vlogin(String code,HttpServletRequest request){
        //通过code 获取accessToken
        Map<String, Object> accessMap = getAccessToken(code);
        String accessToken = (String) accessMap.get("access_token");
        Long uid = Long.parseLong((String)accessMap.get("uid"));
        //通过accessToken 获取用户信息
        Map<String,Object> infoMap = getUserInfo(accessToken,uid);
        if(infoMap == null){
            return "redirect:http://search.gmall.com:8083/index";
        }
        UmsMember oauth = new UmsMember();
        //微博登陆
        oauth.setSourceType(PlatformPType.WEIBO.getCode());
        oauth.setSourceUid(uid);
        UmsMember OauthUserCheck = memberService.checkOauthUser(oauth);
        if (OauthUserCheck == null) {
            oauth.setNickname((String)infoMap.get("screen_name"));
            oauth.setCreateTime(new Date(System.currentTimeMillis()));
            oauth.setCity((String) infoMap.get("location"));
            oauth.setGender((String) infoMap.get("gender"));
            oauth = memberService.addOauthUser(oauth);
        }
        String token = getToken(request, oauth);
        return "redirect:http://search.gmall.com:8083/index?token=" + token;
    }

    private Map<String, Object> getUserInfo(String accessToken,Long uid) {
        String s1 = "https://api.weibo.com/2/users/show.json?access_token=" + accessToken + "&uid=" + uid;
        String s = HttpclientUtil.doGet(s1);
        Map<String, Object> map = JSON.parseObject(s, Map.class);
        return map;
    }

    private Map<String,Object> getAccessToken(String code) {
        String s = "https://api.weibo.com/oauth2/access_token";

        Map<String,String> map = new HashMap<>();
        map.put("client_id","1313097522");
        map.put("client_secret","b013921014b78e9dc8b4808174dade57");
        map.put("grant_type","authorization_code");
        map.put("code",code);
        map.put("redirect_uri","http://passport.gmall.com:8085/vlogin");

        String doPost = HttpclientUtil.doPost(s, map);
        Map<String,Object> map1 = JSON.parseObject(doPost, Map.class);
        return map1;
    }

    @RequestMapping("/index")
    public String index(String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "index";
    }

    @RequestMapping("/login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request){

        String token;
        //调用用户名密码
        UmsMember umsMemberLogin= memberService.login(umsMember);
        if(!(umsMemberLogin == null)){
            //成功登陆
            token = getToken(request, umsMemberLogin);
        }else{
            token = "fail";
        }
        return token;
    }

    private String getToken(HttpServletRequest request, UmsMember umsMemberLogin) {
        String token;
        String memberId = umsMemberLogin.getId();
        String nickname = umsMemberLogin.getNickname();
        Map<String,Object> memberMap = new HashMap<>();
        memberMap.put("memberId",memberId);
        memberMap.put("nickname",nickname);

        String ip = request.getHeader("x-forwarded-for"); //nginx 转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }
        //需要算法加密
        token = JwtUtil.encode("2019bbu0105", memberMap, ip);

        //token 写入redis
        memberService.addMemberToken(token,memberId);
        return token;
    }

}
