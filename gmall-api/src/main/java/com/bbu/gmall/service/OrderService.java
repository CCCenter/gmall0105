package com.bbu.gmall.service;

import com.bbu.gmall.beans.OmsCartItem;
import com.bbu.gmall.beans.OmsOrder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface OrderService {
    String genTradeCode(String memberId);

    String checkTradeCode(String memberId, String tradeCode);

    BigDecimal getToTalAmount(String memberId);

    void saveOrder(OmsOrder omsOrder);
}
