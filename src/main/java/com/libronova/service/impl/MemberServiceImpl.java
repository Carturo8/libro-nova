package com.libronova.service.impl;

import com.libronova.dao.MemberDao;
import com.libronova.dao.impl.MemberDaoImpl;
import com.libronova.errors.*;
import com.libronova.model.Member;
import com.libronova.service.MemberService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;

    public MemberServiceImpl() {
        this.memberDao = new MemberDaoImpl();
    }

    @Override
    public Member createMember(Member member) {
        try {
            // Validate required fields
            validateMemberData(member);

            // Validate email uniqueness
            Member existingMemberByEmail = memberDao.findByEmail(member.getEmail());
            if (existingMemberByEmail != null) {
                throw new ConflictException("Member", "email", member.getEmail());
            }

            // Generate membership number if isn't provided
            if (member.getMembershipNumber() == null || member.getMembershipNumber().trim().isEmpty()) {
                member.setMembershipNumber(generateMembershipNumber());
            }

            // Validate membership number uniqueness
            Member existingMemberByNumber = memberDao.findByMembershipNumber(member.getMembershipNumber());
            if (existingMemberByNumber != null) {
                throw new ConflictException("Member", "membership number", member.getMembershipNumber());
            }

            // Set default values
            if (member.getStatus() == null || member.getStatus().trim().isEmpty()) {
                member.setStatus("ACTIVE");
            }

            if (member.getRegistrationDate() == null) {
                member.setRegistrationDate(LocalDate.now());
            }

            return memberDao.create(member);

        } catch (DataAccessException e) {
            throw new ServiceException("Error creating member", e);
        }
    }

    @Override
    public Member getMemberById(Integer id) {

        try {
            Member member = memberDao.findById(id);
            if (member == null) {
                throw new NotFoundException("Member", id);
            }

            return member;

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving member", e);
        }
    }

    @Override
    public Member getMemberByMembershipNumber(String membershipNumber) {

        try {
            if (membershipNumber == null || membershipNumber.trim().isEmpty()) {
                throw new BadRequestException("Membership number cannot be empty");
            }

            Member member = memberDao.findByMembershipNumber(membershipNumber);
            if (member == null) {
                throw new NotFoundException("Member with membership number " + membershipNumber + " not found");
            }

            return member;

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving member by membership number", e);
        }
    }

    @Override
    public List<Member> getAllMembers() {

        try {
            return memberDao.findAll();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving all members", e);
        }
    }

    @Override
    public List<Member> getAllActiveMembers() {

        try {
            return memberDao.findAllActive();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving active members", e);
        }
    }

    @Override
    public Member updateMember(Member member) {

        try {
            // Validate member exists
            Member existingMember = memberDao.findById(member.getId());
            if (existingMember == null) {
                throw new NotFoundException("Member", member.getId());
            }

            // Validate required fields
            validateMemberData(member);

            // Validate email uniqueness (excluding current member)
            Member memberWithSameEmail = memberDao.findByEmail(member.getEmail());
            if (memberWithSameEmail != null && !memberWithSameEmail.getId().equals(member.getId())) {
                throw new ConflictException("Member", "email", member.getEmail());
            }

            // Validate membership number uniqueness (excluding current member)
            Member memberWithSameNumber = memberDao.findByMembershipNumber(member.getMembershipNumber());
            if (memberWithSameNumber != null && !memberWithSameNumber.getId().equals(member.getId())) {
                throw new ConflictException("Member", "membership number", member.getMembershipNumber());
            }

            boolean updated = memberDao.update(member);
            if (!updated) {
                throw new ServiceException("Failed to update member");
            }

            return memberDao.findById(member.getId());

        } catch (DataAccessException e) {
            throw new ServiceException("Error updating member", e);
        }
    }

    @Override
    public void deleteMember(Integer id) {

        try {
            Member member = memberDao.findById(id);
            if (member == null) {
                throw new NotFoundException("Member", id);
            }

            boolean deleted = memberDao.delete(id);
            if (!deleted) {
                throw new ServiceException("Failed to delete member");
            }

        } catch (DataAccessException e) {
            throw new ServiceException("Error deleting member", e);
        }
    }

    @Override
    public boolean isActive(Integer memberId) {

        try {
            Member member = memberDao.findById(memberId);
            if (member == null) {
                throw new NotFoundException("Member", memberId);
            }

            return "ACTIVE".equalsIgnoreCase(member.getStatus());

        } catch (DataAccessException e) {
            throw new ServiceException("Error checking member status", e);
        }
    }

    @Override
    public String generateMembershipNumber() {
        String prefix = "MEM-";
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));

        try {
            // Get all members and find the highest number
            List<Member> allMembers = memberDao.findAll();
            int maxNumber = 0;

            for (Member member : allMembers) {
                String membershipNumber = member.getMembershipNumber();
                if (membershipNumber != null && membershipNumber.startsWith(prefix + year)) {
                    String numberPart = membershipNumber.substring((prefix + year + "-").length());
                    try {
                        int number = Integer.parseInt(numberPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid formats
                    }
                }
            }

            int nextNumber = maxNumber + 1;
            return String.format("%s%s-%03d", prefix, year, nextNumber);

        } catch (DataAccessException e) {
            throw new ServiceException("Error generating membership number", e);
        }
    }

    private void validateMemberData(Member member) {
        if (member.getFullName() == null || member.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name", "cannot be empty");
        }

        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email", "cannot be empty");
        }

        // Basic email validation
        if (!member.getEmail().contains("@")) {
            throw new BadRequestException("Email", "invalid format");
        }

        if (member.getStatus() != null && !member.getStatus().trim().isEmpty()) {
            String status = member.getStatus().toUpperCase();
            if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) {
                throw new BadRequestException("Status", "must be ACTIVE or INACTIVE");
            }
        }
    }
}