package com.bbu.gmall.Service;

import com.bbu.gmall.Beans.UmsMember;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemberService {
    List<UmsMember> getAllUser();
}
