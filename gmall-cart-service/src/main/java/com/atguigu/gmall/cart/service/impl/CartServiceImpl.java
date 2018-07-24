package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.constant.CartConst;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ItemService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    //相当于是老师代码里的manageService
    @Reference
    private ItemService itemService;
    @Autowired
    private RedisUtil redisUtil;
    
    /**
     * 根据商品Id和用户id来增加购物车
     * @param skuId
     * @param userId
     * @param skuNum
     */
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        //查看购物车DB有没有该商品
        CartInfo cartInfoQuery = new CartInfo();
        cartInfoQuery.setSkuId(skuId);
        cartInfoQuery.setUserId(userId);
        //根据购物车信息在购物车里看看是否有该商品
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfoQuery);
        if(cartInfoExist!=null){
            //购物车里有该商品,将购物车里的商品数量加一
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            //更新数据库
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
        }else {
            //没有创建购物车 创建购物车
            //根据skuid来查找商品
          SkuInfo skuInfo =  itemService.getSkuInfo(skuId);

          //创建购物车的一个对象
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);    //商品id
            cartInfo.setUserId(userId);     //用户id  根据商品id和用户id可以确定一个购物车的意见商品
            cartInfo.setSkuNum(skuNum);   //商品的个数
            cartInfo.setSkuName(skuInfo.getSkuName());
            // 实施价格
            cartInfo.setSkuPrice(skuInfo.getPrice());
            // 添加购物车时候的价格
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            //将数据插入数据库
            cartInfoMapper.insertSelective(cartInfo);
            //将插入的数据标记为数据库以及存在的数据
            cartInfoExist = cartInfo;

        }
        // 想办法将购物车数据放到redis中
        // hset(key,field,value) :key = （user:userId:cart）
        //构造购物车里面的key
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;

        //获取redis对象
        Jedis jedis = redisUtil.getJedis();
        //保存数据  将购物车已经存在的购物车里的商品转换成字符串的
       String cartJson =  JSON.toJSONString(cartInfoExist);

       //将数据放入redis中
        jedis.hset(userCartKey,skuId,cartJson);
        // 细节的地方！user:userId+:info
        //拼接用户的信息的key
        String userInfoKey =  CartConst.USER_KEY_PREFIX+userId+CartConst.USERINFOKEY_SUFFIX;
        //返回key值的剩余过期的时间
        Long ttl = jedis.ttl(userInfoKey);
        //将这个用户的时间再延长
        jedis.expire(userCartKey,ttl.intValue());
        jedis.close();


    }

    //合并购物车
    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId) {
        // 根据数据库里的商品信息的商品价格和购物车里的商品价格查找得到购物车信息的集合
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCarPrice(userId);
        //遍历cookie中的购物车信息的集合
        for (CartInfo cartInfoCK : cartListFromCookie) {
            // 有相同的，没有相同[insert]
            boolean isMatch = false;
            for (CartInfo infoDB : cartInfoListDB) {
                // 如果skuId 相同，则说明是同一个商品，则数据要增加
                if (cartInfoCK.getSkuId().equals(infoDB.getSkuId())){
                    infoDB.setSkuNum(cartInfoCK.getSkuNum()+infoDB.getSkuNum());
                    cartInfoMapper.updateByPrimaryKeySelective(infoDB);
                    isMatch=true;
                }
            }
            // 插入信息
            if (!isMatch){
                // userId 赋值
                cartInfoCK.setUserId(userId);
                //将cookie中的购物车信息插入到数据库中
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }
        // loadCartCache ： 根据userId 先查数据库，在放缓存
        List<CartInfo> cartInfoList = loadCartCache(userId);
        //放入redis中的数据 跟cookie中的数据的ischeck进行匹配 如果匹配上了 更改redis中的数据并且放到新的redis中
        for (CartInfo cartInfo : cartInfoList) {
            //遍历cookie中的数据
            for (CartInfo info : cartListFromCookie) {
                if(cartInfo.getSkuId().equals(info.getSkuId())){
                    if("1".equals(info.getSkuId())){
                        //将redis中的状态更改成1
                        cartInfo.setIsChecked("1");
                        //修改redis中数据的状态
                        checkCart(userId,info.getSkuId(),info.getIsChecked());
                    }
                }
            }

        }

        return cartInfoList;
    }
    // 根据用户id查询购物车
    @Override
    public List<CartInfo> getCartList(String userId) {
        // 看缓存，数据库！   缓存key
        String userCartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        // 创建redis对象 hset(key,field,value); hget(key,field) hget(userCartKey , skuId);
        Jedis jedis = redisUtil.getJedis();
        // redis=hash --- java=list;
        List<String> cartJsons = jedis.hvals(userCartKey);
        // 循环
        // 准备一个新的集合：
        if (cartJsons != null && !"".equals(cartJsons)) {
            List<CartInfo> cartInfoList = new ArrayList<>();
            for (String cartJson : cartJsons) {
                // 将对象转换成cartInfo对象
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            // hash 有顺序么？you  根据id进行排序 time 外部比较器
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        } else {
            // 走数据库，验价过程！ sku_Info 中的price ，car_Info cartprice ,将数据库中的数据放入缓存
            List<CartInfo> cartInfoList = loadCartCache(userId);

            return cartInfoList;
        }
    }



    private List<CartInfo> loadCartCache(String userId) {
        // 在mapper中写个方法，
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCarPrice(userId);
        //  判断集合
        if (cartInfoList!=null && cartInfoList.size()>0){
            // 准备放入redis
            Jedis jedis = redisUtil.getJedis();
            // 对数据进行转换 hset(key,field,value);
            String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
            // field，value === 正好对应上我们的map.put(field,value) jedis.hmset(userCartKey,map);
            Map<String,String> map = new HashMap<>(cartInfoList.size());
            for (CartInfo cartInfo : cartInfoList) {
                // 将cartInfo 转换成对象
                String cartJson  = JSON.toJSONString(cartInfo);
                map.put(cartInfo.getSkuId(),cartJson);
            }
            // 往redis 中添加数据
            jedis.hmset(userCartKey,map);
            jedis.close();
        }
        return cartInfoList;
    }
    //更改redis中商品的状态
    @Override
    public void checkCart(String userId, String skuId, String isChecked) {
        //拼接redis中的key
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;

        //获取redis工具
        Jedis jedis = redisUtil.getJedis();

        //找到redis中对应的key的值
        //这里为什么要用hget  而不用hval(这个是一个集合)   因为点击复选只选中一个，选中一个时就将该数据更改redis中的数据
        String cartInfoJson = jedis.hget(userCartKey,skuId);

        //将json串转换成一个对象
       CartInfo cartInfo =  JSON.parseObject(cartInfoJson,CartInfo.class);

       //修改里面的属性值
       cartInfo.setIsChecked(isChecked);

       //再将对象转换成一个json串
        cartInfoJson =  JSON.toJSONString(cartInfo);

       //修改完在放入到redis中
        jedis.hset(userCartKey,skuId,cartInfoJson);

       //将选中的商品放入到一个新的rids中
        String cartIscheckedKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CHECKED_KEY_SUFFIX;

        //当isChecked为1(选中状态)时
        if("1".equals(isChecked)){
            //这将商品放入到新建的一个redis中
             jedis.hset(cartIscheckedKey,skuId,cartInfoJson);
        }else{
            //不为1(没有选中)从redi删除
            jedis.hdel(cartIscheckedKey,skuId);

        }
        //关闭jedis
        jedis.close();
    }

    /**
     * 根据userid区redis中查找被勾选的商品
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartCheckList(String userId) {
        //拼接redis中的key
        String userCheckedKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CHECKED_KEY_SUFFIX;
        //创建redis的对象
        Jedis jedis = redisUtil.getJedis();
        //因为一个key对应的是多个value所有返回用list  返回的是一个字符串的一个集合
         List<String> cartCheckedList =  jedis.hvals(userCheckedKey);
         //新建一个购物车信息的一个集合
         List<CartInfo> cartInfoList = new ArrayList<CartInfo>();
        //遍历集合
        for (String cartJson : cartCheckedList) {
            //将json串转换成对象
           CartInfo cartInfo =  JSON.parseObject(cartJson,CartInfo.class);
           //将转成的一个一个得对象放入到购物车集合中
           cartInfoList.add(cartInfo);
        }
        return cartInfoList;
    }
}
