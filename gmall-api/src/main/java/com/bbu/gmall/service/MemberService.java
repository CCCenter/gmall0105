package com.bbu.gmall.service;

import com.bbu.gmall.beans.UmsMember;
import com.bbu.gmall.beans.UmsMemberReceiveAddress;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemberService {
    List<UmsMember> getAllUser();

    UmsMember login(UmsMember umsMember);

    void addMemberToken(String token, String memberId);

    UmsMember addOauthUser(UmsMember oauth);

    UmsMember checkOauthUser(UmsMember oauth);

    List<UmsMemberReceiveAddress> getReceiveAddress(String memberId);

    UmsMemberReceiveAddress getReceiveAddresById(String receiveAddressId);
}
