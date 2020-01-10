package com.bbu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.bbu.gmall.beans.UmsMember;
import com.bbu.gmall.beans.UmsMemberReceiveAddress;
import com.bbu.gmall.manage.util.RedisUtil;
import com.bbu.gmall.service.MemberService;
import com.bbu.gmall.user.mapper.UmsMemberMapper;
import com.bbu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

//dubbo service 注解 RPC（Remote process call） 远程地址调用
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    UmsMemberMapper umsMemberMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = umsMemberMapper.selectAll();
        return umsMembers;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {

        UmsMember umsMemberLogin = null;
        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            if(jedis != null) {
                String memberInfo = jedis.get("member;" + umsMember.getUsername() + ";memberInfo");
                if(StringUtils.isNotBlank(memberInfo)){
                    umsMemberLogin = JSON.parseObject(memberInfo, UmsMember.class);
                    return umsMemberLogin;
                }
            }
            //数据库中查找
            List<UmsMember> umsMembers = umsMemberMapper.select(umsMember);
            if(umsMembers != null && umsMembers.size() != 0) {
                umsMemberLogin = umsMembers.get(0);
                jedis.setex("member:" + umsMemberLogin.getUsername() +":memberInfo", 60 * 60 * 2 , JSON.toJSONString(umsMemberLogin));
            }
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return umsMemberLogin;
    }

    /**
     * @param token
     * @param memberId
     * add token to redis by memberId
     */
    @Override
    public void addMemberToken(String token, String memberId) {
        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            if(jedis != null){
                jedis.setex("member:" + memberId + ":token",60*60*2,token);
            }
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    @Override
    public UmsMember addOauthUser(UmsMember oauth) {
        umsMemberMapper.insertSelective(oauth);
        return oauth;
    }

    @Override
    public UmsMember checkOauthUser(UmsMember oauth) {
        UmsMember umsMember = umsMemberMapper.selectOne(oauth);
        return umsMember;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddress(String memberId) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);
        for (UmsMemberReceiveAddress umsMemberReceiveAddress : umsMemberReceiveAddresses) {
            StringBuilder address = new StringBuilder();
            address.append(umsMemberReceiveAddress.getProvince()).append(umsMemberReceiveAddress.getCity()).append(umsMemberReceiveAddress.getDetailAddress());
            umsMemberReceiveAddress.setAddress(address.toString());
        }
        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddresById(String receiveAddressId) {
        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setId(receiveAddressId);
        UmsMemberReceiveAddress receiveAddress = umsMemberReceiveAddressMapper.selectOne(address);
        return receiveAddress;
    }
}
