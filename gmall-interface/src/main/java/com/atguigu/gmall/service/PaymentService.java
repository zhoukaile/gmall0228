package com.atguigu.gmall.service;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.bean.PaymentInfo;

public interface PaymentService {
    //保存支付的信息
    void savePaymentInfo(PaymentInfo paymentInfo);
    //修改支付的状态
    void updatePaymentInfo(PaymentInfo paymentInfo, String out_trade_no);
    //得到支付订单的信息
    PaymentInfo getpaymentInfo(PaymentInfo paymentInfo);
    //发送结果消息给activemq
    void sendPaymentResult(PaymentInfo paymentInfo,String result);
    //根据第三方的编号out_trade_no查找支付订单
     boolean checkPayment(PaymentInfo paymentInfoQuery) throws AlipayApiException;
     //开启延迟队列
    void sendDelayPaymentResult(String outTradeNo,int delaySec,int checkCount);
    //关闭支付的状态
    void closePayment(String id);
}
