package com.libronova.service.impl;

import com.libronova.dao.MemberDao;
import com.libronova.dao.UserDao;
import com.libronova.dao.impl.MemberDaoImpl;
import com.libronova.dao.impl.UserDaoImpl;
import com.libronova.errors.BadRequestException;
import com.libronova.errors.DataAccessException;
import com.libronova.errors.NotFoundException;
import com.libronova.errors.ServiceException;
import com.libronova.model.Member;
import com.libronova.model.User;
import com.libronova.service.MemberService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final UserDao userDao;

    public MemberServiceImpl() {
        this.memberDao = new MemberDaoImpl();
        this.userDao = new UserDaoImpl();
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
    public List<User> getAllMembers() {
        try {
            return userDao.findAllByRole("MEMBER");

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving all members", e);
        }
    }

    @Override
    public List<User> getAllActiveMembers() {
        try {
            List<User> allMembers = userDao.findAllByRole("MEMBER");
            return allMembers.stream()
                    .filter(User::isActive)
                    .collect(Collectors.toList());

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving active members", e);
        }
    }

    @Override
    public Member updateMemberDetails(Member member) {
        try {
            Member existingMember = memberDao.findById(member.getId());
            if (existingMember == null) {
                throw new NotFoundException("Member", member.getId());
            }

            // Only update fields that are part of the Member entity
            existingMember.setPhone(member.getPhone());
            existingMember.setStatus(member.getStatus());

            boolean updated = memberDao.update(existingMember);
            if (!updated) {
                throw new ServiceException("Failed to update member details");
            }

            return memberDao.findById(member.getId());

        } catch (DataAccessException e) {
            throw new ServiceException("Error updating member details", e);
        }
    }

    @Override
    public void deleteMember(Integer id) { // <-- IMPLEMENTACIÓN AÑADIDA
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
            List<Member> allMembers = memberDao.findAll();
            int maxNumber = 0;

            for (Member m : allMembers) {
                String num = m.getMembershipNumber();
                if (num != null && num.startsWith(prefix + year)) {
                    try {
                        String numberPart = num.substring((prefix + year + "-").length());
                        int number = Integer.parseInt(numberPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
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
}