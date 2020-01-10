package com.bbu.gmall.order.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bbu.gmall.annotations.LoginRequired;
import com.bbu.gmall.beans.OmsCartItem;
import com.bbu.gmall.beans.OmsOrder;
import com.bbu.gmall.beans.OmsOrderItem;
import com.bbu.gmall.beans.UmsMemberReceiveAddress;
import com.bbu.gmall.service.CartService;
import com.bbu.gmall.service.MemberService;
import com.bbu.gmall.service.OrderService;
import com.bbu.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    CartService cartService;

    @Reference
    MemberService memberService;
    @Reference
    SkuService skuService;

    @Reference
    OrderService orderService;

    @RequestMapping("/submitOrder")
    @LoginRequired(loginSuccess = true)
    public String submitOrder(String deliveryAddressId,String tradeCode,String orderComment, HttpServletRequest request, Model model) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        //调用支付
        String check = orderService.checkTradeCode(memberId, tradeCode);
        if ("success".equals(check)) {
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            omsOrder.setDiscountAmount(null);
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setNote(orderComment);

            String outTradeNo = "gmall";
            outTradeNo = outTradeNo + System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo + sdf.format(new Date());

            omsOrder.setOrderSn(outTradeNo);//外部订单号
            omsOrder.setTotalAmount(orderService.getToTalAmount(memberId));
            omsOrder.setOrderType(1);

            //订单用户信息
            UmsMemberReceiveAddress receiveAddress = memberService.getReceiveAddresById(deliveryAddressId);
            omsOrder.setReceiverCity(receiveAddress.getCity());
            omsOrder.setReceiverRegion(receiveAddress.getRegion());
            omsOrder.setReceiverDetailAddress(receiveAddress.getDetailAddress());
            omsOrder.setReceiverName(receiveAddress.getName());
            omsOrder.setReceiverPhone(receiveAddress.getPhoneNumber());
            omsOrder.setReceiverProvince(receiveAddress.getProvince());
            omsOrder.setReceiverPostCode(receiveAddress.getPostCode());

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,1);
            Date time = c.getTime();
            omsOrder.setReceiveTime(time);

            omsOrder.setSourceType(0);
            omsOrder.setStatus(0);

            List<OmsCartItem> cartList = cartService.getCartList(memberId);
            for (OmsCartItem omsCartItem : cartList) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    //获取订单信息
                    OmsOrderItem omsOrderItem = new OmsOrderItem();

                    //验价格
                    boolean b = skuService.checkPrice(omsCartItem);
                    if(!b) {
                        return "tradeFail";
                    }
                    //验库存
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());

                    omsOrderItem.setOrderSn(outTradeNo);
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuCode("1111111111");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库的sku");
                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItems(omsOrderItems);
            orderService.saveOrder(omsOrder);
            return "";
        }else {
            return "tradeFail";
        }

    }

    @RequestMapping("/toTrade")
    @LoginRequired(loginSuccess = true)
    public String toTrade(HttpServletRequest request, Model model) {

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        BigDecimal totalAmount = new BigDecimal("0");
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = memberService.getReceiveAddress(memberId);
        List<OmsCartItem> cartList = cartService.getCartList(memberId);
        List<OmsOrderItem> orderDetailList = new ArrayList<>();
        for (OmsCartItem omsCartItem : cartList) {
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
                orderDetailList.add(omsOrderItem);
            }
        }
        String tradeCode = orderService.genTradeCode(memberId);

        model.addAttribute("orderDetailList", cartList);
        model.addAttribute("userAddressList", umsMemberReceiveAddresses);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("tradeCode", tradeCode);

        return "trade";
    }

}