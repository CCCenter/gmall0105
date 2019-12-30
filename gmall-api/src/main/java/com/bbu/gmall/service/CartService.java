package com.bbu.gmall.service;

import com.bbu.gmall.beans.OmsCartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCartBySkuId(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);

    List<OmsCartItem> getCartList(String memberId);
}
