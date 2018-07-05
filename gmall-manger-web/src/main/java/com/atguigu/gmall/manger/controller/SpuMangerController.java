package com.atguigu.gmall.manger.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.SpuInfoService;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class SpuMangerController {

    @Reference
    private SpuInfoService spuInfoService;

    @RequestMapping("spuListPage")
    public String spuListPage(){
        return "spuListPage";
    }

    /**
     * 根据3及分类查询supinfo的信息
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> spuList(String catalog3Id){

        SpuInfo spuInfo = new SpuInfo();


        //将spuinfo类的擦taligId赋值
        spuInfo.setCatalog3Id(catalog3Id);

        List<SpuInfo>   spuInfoList= spuInfoService.getSpuInfoList(spuInfo);

        return spuInfoList;

    }



}
