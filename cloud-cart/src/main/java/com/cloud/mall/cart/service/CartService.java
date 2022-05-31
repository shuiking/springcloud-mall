package com.cloud.mall.cart.service;

import com.cloud.mall.cart.vo.CartItemVo;
import com.cloud.mall.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    //将商品添加到购物车
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    //获取购物车的某个购物项
    CartItemVo getCartItem(Long skuId);

    //获取整个购物车
    CartVo getCart() throws ExecutionException, InterruptedException;

    //清空购物车
    void clearCartInfo(String cartKey);

    //勾选购物项
    void checkItem(Long skuId, Integer checked);

    //修改购物项的数量
    void changeItemCount(Long skuId, Integer num);

    //删除购物项
    void deleteIdCartInfo(Integer skuId);

    List<CartItemVo> getUserCartItems();
}
