package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserAddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserAddressController {

    @Reference
    private UserAddressService userAddressService;

    @RequestMapping("getAdressByUserId")
    public List<UserAddress> getAddressByUserId(HttpServletRequest request) {
        String id = request.getParameter("userId");
        List<UserAddress> userAddresses = userAddressService.getAddressByUserId(id);
        return userAddresses;
    }

}
