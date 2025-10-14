package com.libronova.controller;

import com.libronova.errors.*;
import com.libronova.model.Member;
import com.libronova.model.User;
import com.libronova.service.MemberService;
import com.libronova.service.impl.MemberServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final MemberService memberService;

    public MemberController() {
        this.memberService = new MemberServiceImpl();
    }

    public Member getMemberById(Integer id) {
        try {
            logger.debug("Attempting to find member by ID: {}", id);
            return memberService.getMemberById(id);

        } catch (NotFoundException | ServiceException e) {
            logger.warn("Could not find member with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public Member getMemberByMembershipNumber(String membershipNumber) {
        try {
            logger.debug("Attempting to find member by membership number: {}", membershipNumber);
            return memberService.getMemberByMembershipNumber(membershipNumber);

        } catch (NotFoundException | BadRequestException | ServiceException e) {
            logger.warn("Could not find member with number {}: {}", membershipNumber, e.getMessage());
            return null;
        }
    }

    public List<User> getAllMembers() {
        try {
            logger.debug("Attempting to fetch all members (as Users).");
            return memberService.getAllMembers();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching all members.", e);
            return Collections.emptyList();
        }
    }

    public List<User> getAllActiveMembers() {
        try {
            logger.debug("Attempting to fetch all active members (as Users).");
            return memberService.getAllActiveMembers();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching active members.", e);
            return Collections.emptyList();
        }
    }

    public Member updateMemberDetails(Member member) {
        try {
            logger.info("Attempting to update member details for ID: {}", member.getId());
            Member updatedMember = memberService.updateMemberDetails(member);
            logger.info("Member details for ID {} updated successfully.", member.getId());
            return updatedMember;

        } catch (NotFoundException | BadRequestException | ServiceException e) {
            logger.warn("Member details update failed for member ID {}: {}", member.getId(), e.getMessage());
            return null;
        }
    }

    public boolean deleteMember(Integer id) {
        try {
            logger.info("Attempting to delete member with ID: {}", id);
            memberService.deleteMember(id);
            logger.info("Member with ID {} deleted successfully.", id);
            return true;

        } catch (NotFoundException | ServiceException e) {
            logger.warn("Could not delete member with ID {}: {}", id, e.getMessage());
            return false;
        }
    }
}