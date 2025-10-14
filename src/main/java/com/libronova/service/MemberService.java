
package com.libronova.service;

import com.libronova.model.Member;

import java.util.List;

public interface MemberService {
    Member createMember(Member member);
    Member getMemberById(Integer id);
    Member getMemberByMembershipNumber(String membershipNumber);
    List<Member> getAllMembers();
    List<Member> getAllActiveMembers();
    Member updateMember(Member member);
    void deleteMember(Integer id);
    boolean isActive(Integer memberId);
    String generateMembershipNumber();
}