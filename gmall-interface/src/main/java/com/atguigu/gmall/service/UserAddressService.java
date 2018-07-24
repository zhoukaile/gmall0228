package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;

import java.util.List;

public interface UserAddressService {
    //根据id得到用的地址
    List<UserAddress> getAddressByUserId(String id);
}
