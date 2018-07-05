package com.atguigu.gmall.usermanage.service.impl;


import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import com.atguigu.gmall.usermanage.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserInfo> findLikeUserInfo() {
        // 创建一个Example 对象
        Example example = new Example(UserInfo.class);
        example.createCriteria().andLike("loginName","%a%");
        // 调用方法
        List<UserInfo> userInfoList = userInfoMapper.selectByExample(example);
        return userInfoList;
    }

    @Override
    public void addUserInfo(UserInfo userInfo) {
        // insert() 全部插入
        userInfoMapper.insertSelective(userInfo);
        // userInfoMapper.insertSelective(userInfo); 选择性的插入
    }

    @Override
    public void upd(UserInfo userInfo) {
    //   userInfoMapper.updateByPrimaryKey(userInfo);、
        // 选择性
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
    }

    @Override
    public void upd1(UserInfo userInfo) {
        // 根据名称修改
        Example example = new Example(UserInfo.class);
        // 创建sql语句的查询体
        // update user_info set xxx where loginName=? 该?是从外界传递过来。
        example.createCriteria().andEqualTo("loginName",userInfo.getLoginName());
        userInfoMapper.updateByExampleSelective(userInfo,example);
    }

    @Override
    public void del(UserInfo userInfo) {

        userInfoMapper.deleteByPrimaryKey(userInfo);
    }

}
