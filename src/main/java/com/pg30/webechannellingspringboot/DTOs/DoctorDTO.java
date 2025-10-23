package com.pg30.webechannellingspringboot.DTOs;

import java.math.BigDecimal;

public class DoctorDTO {

    private Long doctorId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private BigDecimal fee;

    public DoctorDTO() {}

    public DoctorDTO(Long doctorId, String fullName, String email, String phone, String specialization, BigDecimal fee) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.fee = fee;
    }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
