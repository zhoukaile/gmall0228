package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.config.CookieUtil;
import com.atguigu.gmall.service.ItemService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartCookieHandler {
    // 定义购物车名称
    private String cookieCartName = "CART";
    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE=7*24*3600;

   @Reference
    private ItemService itemService;


    public void  addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, Integer skuNum){
        // 判断cookie中是否存在，先获取数据
        String cartJson  = CookieUtil.getCookieValue(request, cookieCartName, true);
        List<CartInfo> cartInfoList = new ArrayList<>();
        // 设置一个boolean 类型的变量
        boolean ifExist=false;
        if (cartJson!=null){
            // cartJson转换成对象 ,坑！购物车中应该会有很多条数据，所以该处应该是集合
            cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
            for (CartInfo cartInfo : cartInfoList) {
                // 判断cookie中的数据跟添加的数据是否一致
                if (cartInfo.getSkuId().equals(skuId)){
                    // 对数量进行更新
                    cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
                    // 对价格的处理
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    ifExist=true;
                }
            }
        }
        // 商品id(skuId)不一样的时候添加到cookie中根本就没有cartInfo的时候也要添加cookie中，没有该商品则添加
        if (!ifExist){
            // 根据当前的skuId查找数据，放到cartInfo 中
            SkuInfo skuInfo = itemService.getSkuInfo(skuId);
            CartInfo cartInfo=new CartInfo();

            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());

            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            // 将每个商品都放入集合中。
            cartInfoList.add(cartInfo);
        }
        // 先将集合转换成字符串
        String newCartJson  = JSON.toJSONString(cartInfoList);
        // 将cartInfo 信息放到cookie中 ，
        CookieUtil.setCookie(request,response,cookieCartName,newCartJson,COOKIE_CART_MAXAGE,true);
    }

    // 取得cookie中的信息
    public List<CartInfo> getCartList(HttpServletRequest request){
        // 调用工具类
        String cartJson  = CookieUtil.getCookieValue(request, cookieCartName, true);
        // 将字符串转换成对象
        List<CartInfo> cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
        return cartInfoList;
    }

    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,cookieCartName);
    }



    /**
     * 去结算时，没有登录状态下，数据放入在cookie中 将选中的商品的状态更改为
     * @param skuId     商品id
     * @param isChecked     商品被选中的状态
     */
    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        //获取cookie中的对象集合
       List<CartInfo> cartInfoList =  getCartList(request);
        //遍历
        for (CartInfo cartInfo : cartInfoList) {
            //先判断一下商品是否存在
            if(cartInfo.getSkuId().equals(skuId)){
                //根据cartInfo中的状态
                cartInfo.setIsChecked(isChecked);
            }
        }
        //将对象集合转成json串
       String carInfoJsonList =  JSON.toJSONString(cartInfoList);
        //再将最新的商品信息放入到cookie中
        CookieUtil.setCookie(request,response,cookieCartName,carInfoJsonList,COOKIE_CART_MAXAGE,true);
    }
}
