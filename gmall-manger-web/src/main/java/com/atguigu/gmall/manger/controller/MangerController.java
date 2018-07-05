package com.atguigu.gmall.manger.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MangerController {

    @Reference
    private ManageService manageService;

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    /***
     * 获得一级分类
     *
     */
    @RequestMapping(value = "getCatalog1",method = RequestMethod.POST)
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }

    /***
     * 获得二级分类
     *
     */
    @RequestMapping(value="getCatalog2",method = RequestMethod.POST)
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(String catalog1Id ){
        return manageService.getCatalog2(catalog1Id);
    }



    /***
     * 获得三级分类
     *
     */
    @RequestMapping(value = "getCatalog3",method =RequestMethod.POST)
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        return manageService.getCatalog3(catalog2Id);

    }


    /***
     * 获得属性列表
     *
     */
    @RequestMapping(value = "attrInfoList",method = RequestMethod.GET)
    @ResponseBody
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){

        return  manageService.getAttrList(catalog3Id);
    }
    /**
     * 增加属性值
     */
    @RequestMapping(value = "saveAttrInfo",method = RequestMethod.POST)
    @ResponseBody
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);

    }
    /**
     * 编辑
     */
    @RequestMapping(value = "getAttrValueList",method = RequestMethod.POST)
    @ResponseBody
    public List<BaseAttrValue> getAttrValueList(String attrId){
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);
        return attrInfo.getAttrValueList();
    }


}
