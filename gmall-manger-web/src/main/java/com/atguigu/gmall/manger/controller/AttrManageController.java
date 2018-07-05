package com.atguigu.gmall.manger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AttrManageController {



    @RequestMapping("attrListPage")
    public String attrListPage(){
        return "attrListPage";
    }



}
