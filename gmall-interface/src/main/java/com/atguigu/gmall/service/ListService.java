package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

public interface ListService {

    //将数据库里的数据放到es中
     void saveSkuInfo(SkuLsInfo skuLsInfo);

    /**
     * 根据搜索的属性来查找商品  然后返回一个商品的列表集合
     * @param skuLsParams 商品的 属性
     * @return
     */

    SkuLsResult search(SkuLsParams skuLsParams);

    /**
     * 热度排名
     * @param skuId
     */
    void incrHotScore(String skuId);
}
