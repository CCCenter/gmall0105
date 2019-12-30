package com.bbu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.bbu.gmall.annotations.LoginRequired;
import com.bbu.gmall.beans.OmsCartItem;
import com.bbu.gmall.beans.PmsSkuInfo;
import com.bbu.gmall.service.CartService;
import com.bbu.gmall.service.SkuService;
import com.bbu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

    @RequestMapping("/toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade(String isChecked, String skuId,Model model) {
        return "toTradeTest";
    }

    @RequestMapping("/checkCart")
    @LoginRequired(loginSuccess = false)
    public String checkCart(String isChecked, String skuId,Model model) {
        String memberId = "1";
        OmsCartItem omsCartItem = new OmsCartItem();

        omsCartItem.setIsChecked(isChecked);
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
            cartService.updateCartBySkuId(omsCartItem);

        List<OmsCartItem> cartList = cartService.getCartList(memberId);

        BigDecimal totalAmount = getTotalAmount(cartList);
        model.addAttribute("totalAmount",totalAmount);
        model.addAttribute("cartList",cartList);
        cartService.flushCartCache(memberId);
        return "cartListInner";
    }

    @RequestMapping("/cartList")
    @LoginRequired(loginSuccess = false)
    public String cartList(Model model, HttpServletRequest request) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();

        String memberId = "1";
        if (StringUtils.isNotBlank(memberId)) {
            //查询数据库
            omsCartItems = cartService.getCartList(memberId);
        } else {
            //查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal price = omsCartItem.getPrice();
            BigDecimal bigDecimaQuantity = new BigDecimal(omsCartItem.getQuantity());
            omsCartItem.setTotalPrice(price.multiply(bigDecimaQuantity));
        }
        BigDecimal totalAmount = getTotalAmount(omsCartItems);

        model.addAttribute("totalAmount",totalAmount);
        model.addAttribute("cartList", omsCartItems);
        return "cartList";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            if("1".equals(omsCartItem.getIsChecked())){
                totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
            }
        }
        return totalAmount;
    }

    @RequestMapping("/addToCart")
    @LoginRequired(loginSuccess = false)
    public String addToCart(String skuId, Integer quantity, HttpServletRequest request, HttpServletResponse response) {

        List<OmsCartItem> omsCartItems;

        //调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId, "127.0.0.1");
        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setTotalPrice(skuInfo.getPrice().multiply(new BigDecimal(quantity)));
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(quantity);

        //判断用户是否登陆
        String memberId = "1";

        if (StringUtils.isBlank(memberId)) {
            //没有登陆
            //取出当前cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            if (StringUtils.isBlank(cartListCookie)) {
                //cookie 没值
                omsCartItems.add(omsCartItem);
            } else {
                //有值判断当前数据是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist) {
                    //购物车存在该数据 更新购物车添加数量
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity() + omsCartItem.getQuantity());
                            omsCartItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                        }
                    }
                } else {
                    //购物车不存在该数据 添加当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }
            //覆盖(更新)cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        } else {
            //用户已经登陆登陆
            //db中查询购物车
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId, skuId);

            if (omsCartItemFromDb == null) {
                //如果不存在 插入该商品进数据库
                omsCartItem.setMemberId(memberId);
                cartService.addCart(omsCartItem);
            } else {
                //存在 更新购物车
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity() + omsCartItem.getQuantity());
                cartService.updateCartBySkuId(omsCartItemFromDb);
            }
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }

    public boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();
            if (productSkuId.equals(omsCartItem.getProductSkuId())) {
                b = true;
            }
        }
        return b;
    }
}
