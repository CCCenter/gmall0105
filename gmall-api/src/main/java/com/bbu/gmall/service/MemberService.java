package com.bbu.gmall.service;

import com.bbu.gmall.beans.UmsMember;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemberService {
    List<UmsMember> getAllUser();
}
