package com.libronova.service;

import com.libronova.model.Member;
import com.libronova.model.User;

import java.util.List;

public interface MemberService {
    Member getMemberById(Integer id);
    Member getMemberByMembershipNumber(String membershipNumber);
    List<User> getAllMembers();
    List<User> getAllActiveMembers();
    Member updateMemberDetails(Member member);
    void deleteMember(Integer id);
    boolean isActive(Integer memberId);
    String generateMembershipNumber();
}