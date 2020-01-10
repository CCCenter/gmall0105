package com.bbu.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.bbu.gmall.annotations.LoginRequired;
import com.bbu.gmall.util.CookieUtil;
import com.bbu.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        //是否拦截
        if (methodAnnotation == null) {
            return true;
        }

        //更新token
        String token = "";
        //如果存在oldToken token = oldToken
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        //如果存在新token  更新 Token
        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }

        boolean loginSuccess = methodAnnotation.loginSuccess();//是否需要登录成功

        //调认证中心验证 认证中心为webController 需要http请求访问 写成web容易被外部程序访问 java代码发送http请求 httpClient
        String success;
        Map<String,String> successMap;

        String ip = request.getHeader("x-forwarded-for"); //nginx 转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }

        if(StringUtils.isNotBlank(token)){
            String successJson = HttpclientUtil.doGet("http://passport.gmall.com:8085/verify?token=" + token + "&currentIp=" + ip);
            successMap = JSON.parseObject(successJson, Map.class);
            success = successMap.get("status");

            //验证成功 写入memberId & nickname
            if("success".equals(success)){
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickname", successMap.get("nickname"));
                //覆盖cookie 中 token 值
                CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
                return true;
            }else{
                //token 错误
                //是否需要登陆
                if (loginSuccess) {
                    StringBuffer requestURL = request.getRequestURL();
                    response.sendRedirect("http://passport.gmall.com:8085/index?ReturnUrl=" + requestURL);
                    return false;
                }
            }
        }else {
            //token 空
            //是否需要登陆
            if (loginSuccess) {
                StringBuffer requestURL = request.getRequestURL();
                response.sendRedirect("http://passport.gmall.com:8085/index?ReturnUrl=" + requestURL);
                return false;
            }
        }
/**
 *                  token空      token 不空
 *  需要登陆        登陆      验证token 不正确 去登陆
 *
 *  不需要的登陆   不做       验证token 正确 写入 不正确 不做
 */
        return true;
    }
}
