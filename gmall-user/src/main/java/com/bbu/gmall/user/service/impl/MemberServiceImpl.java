package com.bbu.gmall.user.service.impl;

import com.bbu.gmall.beans.UmsMember;
import com.bbu.gmall.beans.UmsMemberReceiveAddress;
import com.bbu.gmall.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    @Override
    public List<UmsMember> getAllUser() {
        return null;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        return null;
    }

    @Override
    public void addMemberToken(String token, String memberId) {

    }

    @Override
    public UmsMember addOauthUser(UmsMember oauth) {
        return null;
    }

    @Override
    public UmsMember checkOauthUser(UmsMember oauth) {
        return null;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddress(String memberId) {
        return null;
    }
}
