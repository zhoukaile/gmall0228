package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;

import java.util.List;

public interface ItemService {

    //根基skuID查找skuInfo信息
    SkuInfo getSkuInfo(String skuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    // 根据spuId 拼接属性值
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);




}
