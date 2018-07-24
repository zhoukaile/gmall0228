package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {

    //增加购物车
   void  addToCart(String skuId,String userId,Integer skuNum);

    //根据skuId 相同的就合并，合并完之后，返回一个集合
    List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId);
    // 根据用户id查询购物车
    List<CartInfo> getCartList(String userId);
    //更改选中的商品的的状态，然后放入到一个新的redis中
    void checkCart(String userId, String skuId, String isChecked);
    //根据用户id查找购物车中被勾选的商品
    List<CartInfo> getCartCheckList(String userId);
}
