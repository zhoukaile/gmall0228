package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.ItemService;
import com.atguigu.gmall.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {
    @Reference
    private ItemService itemService;


    @Reference
    private ListService listService;

    @RequestMapping("/{skuId}.html")
    // 该控制器需要登录
    //@LoginRequire(autoRedirect = true)
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, Model model){

        //根据skuID查找skuInfo的信息
       SkuInfo skuInfo =  itemService.getSkuInfo(skuId);
       model.addAttribute("skuInfo",skuInfo);

        List<SpuSaleAttr> spuSaleAttrs = itemService.selectSpuSaleAttrListCheckBySku(skuInfo);
        model.addAttribute("spuSaleAttrs",spuSaleAttrs);


        //调用热度排名
        listService.incrHotScore(skuId);


        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = itemService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        // 先声明一个字符串
        String valueIdsKey = "";
        // 需要定一个map集合
        HashMap<String, String> map = new HashMap<>();
        // 循环拼接
        for (int i = 0; i <skuSaleAttrValueListBySpu.size() ; i++) {
            // 取得第一个值
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
            // 什么时候加|
            if (valueIdsKey.length()>0){
                valueIdsKey+="|";
            }
            valueIdsKey+=skuSaleAttrValue.getSaleAttrValueId();

            // 什么时候停止拼接
            if ((i+1)==skuSaleAttrValueListBySpu.size()|| !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i+1).getSkuId())){
                map.put(valueIdsKey,skuSaleAttrValue.getSkuId());
                valueIdsKey="";
            }
        }
        // 将map 转换成json字符串
        String valueJson = JSON.toJSONString(map);

        System.out.println("valueJson:="+valueJson);
        model.addAttribute("valuesSkuJson",valueJson);

        //System.out.println(skuId);
        return "item";
    }
}
