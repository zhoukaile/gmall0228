package com.atguigu.gmall.manger.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.manger.mapper.SpuInfoMapper;
import com.atguigu.gmall.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class SpuInfoServiceImpl implements SpuInfoService {

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }
}
