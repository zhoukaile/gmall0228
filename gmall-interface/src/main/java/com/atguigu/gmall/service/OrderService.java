package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.enums.ProcessStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {
    //保存订单信息  返回一个订单编号
    String saveOrder(OrderInfo orderInfo);
    //获得流水单号
    String getTradeNo(String userId);
    //检查流水号
    boolean checkTradeCode(String tradeNo,String userId);
    //删除redis中的流水单号
    void delTradeNo(String userId);

    //检查库存
    boolean checkStock(String skuId,Integer skuNum);
    //根据orderId找到订单信息
    OrderInfo getOrderInfo(String orderId);
    //支付成功修改订单的状态
    void updateOrderStatus(String orderId, ProcessStatus paid);

    //发消息给库存系统，将订单的信息放入到消息中发给库存
    void sendOrderStatus(String orderId);
    //查找那些过期没有付款的订单
    List<OrderInfo> getExpiredOrderList();
    //关闭过期未支付的订单
    void exeExpiredOrder(OrderInfo orderInfo);
    //将一个订单信息转换成一个map类型
    Map initWareOrder(OrderInfo orderInfo);
    //获得子订单的集合
    List<OrderInfo> orderSplit(String orderId, String wareSkuMap);
}
