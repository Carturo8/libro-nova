package com.libronova.controller;

import com.libronova.errors.*;
import com.libronova.model.Member;
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

    public Member createMember(Member member) {
        try {
            logger.info("Attempting to create a new member: {}", member.getFullName());
            Member createdMember = memberService.createMember(member);
            logger.info("Member '{}' created successfully with ID {} and number {}.",
                    createdMember.getFullName(), createdMember.getId(), createdMember.getMembershipNumber());
            return createdMember;

        } catch (ConflictException | BadRequestException e) {
            logger.warn("Member creation failed: {}", e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred during member creation.", e);
            return null;
        }
    }

    public Member getMemberById(Integer id) {
        try {
            logger.debug("Attempting to find member by ID: {}", id);
            return memberService.getMemberById(id);

        } catch (NotFoundException e) {
            logger.warn("Could not find member with ID {}: {}", id, e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching member with ID {}.", id, e);
            return null;
        }
    }

    public Member getMemberByMembershipNumber(String membershipNumber) {
        try {
            logger.debug("Attempting to find member by membership number: {}", membershipNumber);
            return memberService.getMemberByMembershipNumber(membershipNumber);

        } catch (NotFoundException | BadRequestException e) {
            logger.warn("Could not find member with number {}: {}", membershipNumber, e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching member with number {}.", membershipNumber, e);
            return null;
        }
    }

    public List<Member> getAllMembers() {
        try {
            logger.debug("Attempting to fetch all members.");
            return memberService.getAllMembers();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching all members.", e);
            return Collections.emptyList();
        }
    }

    public Member updateMember(Member member) {
        try {
            logger.info("Attempting to update member with ID: {}", member.getId());
            Member updatedMember = memberService.updateMember(member);
            logger.info("Member with ID {} updated successfully.", member.getId());
            return updatedMember;

        } catch (NotFoundException | ConflictException | BadRequestException e) {
            logger.warn("Member update failed for member ID {}: {}", member.getId(), e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred while updating member with ID {}.", member.getId(), e);
            return null;
        }
    }

    public boolean deleteMember(Integer id) {
        try {
            logger.info("Attempting to delete member with ID: {}", id);
            memberService.deleteMember(id);
            logger.info("Member with ID {} marked as inactive successfully.", id);
            return true;

        } catch (NotFoundException e) {
            logger.warn("Could not delete member with ID {}: {}", id, e.getMessage());
            return false;
        } catch (ServiceException e) {
            logger.error("A service error occurred while deleting member with ID {}.", id, e);
            return false;
        }
    }
}