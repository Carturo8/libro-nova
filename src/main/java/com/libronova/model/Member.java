package com.libronova.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Member {
    private Integer id;
    private String membershipNumber;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private LocalDate registrationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Member() {
    }

    public Member(Integer id, String membershipNumber, String fullName, String email,
                  String phone, String status, LocalDate registrationDate,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.membershipNumber = membershipNumber;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.registrationDate = registrationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(String membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", membershipNumber='" + membershipNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}