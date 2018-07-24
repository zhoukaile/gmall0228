package com.atguigu.gmall.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.config.CookieUtil;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.util.List;

@Controller
public class CartController {
    @Reference
    private CartService cartService;


    @Autowired
    private CartCookieHandler cartCookieHandler;


    @Reference
    private ItemService itemService;

    /**
     * 添加购物车然后去向成功页面
     * @param request
     * @param response
     * @return
     */
   @RequestMapping(value = "addToCart",method = RequestMethod.POST)
   @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response,Model model){

       // 获取userId，skuId，skuNum
      String skuId = request.getParameter("skuId");

      String skuNum = request.getParameter("skuNum");
       //在页面里面放入的一个session域
      String userId = (String) request.getAttribute("userId");
       // 判断用户是否登录
       if(userId !=null){
           // 走数据库
           cartService.addToCart(skuId,userId,Integer.parseInt(skuNum));
       }else{
           // 走cookie
           cartCookieHandler.addToCart(request,response,skuId,userId,Integer.parseInt(skuNum));

       }
       // 获取skuInfo 信息 后台存储什么，是根据前台需要！
      SkuInfo skuInfo =  itemService.getSkuInfo(skuId);
       // 存储skuInfo 对象，
       model.addAttribute("skuInfo",skuInfo);
       model.addAttribute("skuNum",skuNum);

       return "success";
   }

    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response,Model model){
        // 判断是否登录
        String userId = (String) request.getAttribute("userId");
        // 取得cookie中所有的cartInfo 数据
        List<CartInfo> cartListFromCookie = cartCookieHandler.getCartList(request);
        List<CartInfo> cartList = null;
        if (userId!=null){
            if (cartListFromCookie!=null && cartListFromCookie.size()>0){
                // 合并购物车，cookie-->db。 根据skuId 相同的就合并，合并完之后，返回一个集合
                cartList = cartService.mergeToCartList(cartListFromCookie, userId);
                // cookie删除掉。
                cartCookieHandler.deleteCartCookie(request,response);
            }else{
                // 走的数据库！
                cartList = cartService.getCartList(userId);
            }

            // 将集合保存给前台使用
            model.addAttribute("cartList",cartList);
        }else{
            // 没有登录，cookie中取得
            List<CartInfo> cookieHandlerCartList = cartCookieHandler.getCartList(request);
            model.addAttribute("cartList",cookieHandlerCartList);
        }
        return "cartList";
    }

    /**
     * 点击复选框时,将选中的商品放到redis中让将redis中的状态改成1
     */
        @RequestMapping(value = "checkCart",method = RequestMethod.POST)
        @LoginRequire(autoRedirect = false)
        @ResponseBody
        public void checkCart(HttpServletRequest request,HttpServletResponse response){
               String userId = (String) request.getAttribute("userId");
               String isChecked = (String) request.getAttribute("isChecked");
               String skuId = (String) request.getAttribute("skuId");
               if(userId != null){
                   //登录 了 ，从redis中取数据将选中的商品放入到新建的一个redis中
                   cartService.checkCart(userId,skuId,isChecked);
               }else {
                   //没有登录  从cookie中取然后将状态改成1
                   cartCookieHandler.checkCart(request,response,skuId,isChecked);
               }

        }

    /**
     * 点击去结账时，要将cooki里的数据跟redis结算
     * @return
     */
    @RequestMapping("toTrade")
    @LoginRequire(autoRedirect = true)
    public String toTrade(HttpServletRequest request,HttpServletResponse response){
        //获得userIid
        String userId = (String) request.getAttribute("userId");

        //获取cookie中的数据
        List<CartInfo> cartInfoList = cartCookieHandler.getCartList(request);
        //先判断集合是否为空
       if(cartInfoList !=null && cartInfoList.size() >0){
           //合并redis中
           cartService.mergeToCartList(cartInfoList,userId);

           //合并完了将cookie中的数据删除掉
           cartCookieHandler.deleteCartCookie(request,response);

       }

        return "redirect://order.gmall.com/trade";
    }
}
