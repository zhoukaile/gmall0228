package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuInfo;

import java.util.List;

public interface SpuInfoService {

    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);
    //查询baseSaleAtrr表
    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);
}
