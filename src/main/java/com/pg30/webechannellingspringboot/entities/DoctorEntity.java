package com.pg30.webechannellingspringboot.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Doctors")
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "specialization", nullable = false, length = 50)
    private String specialization;

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;


    public DoctorEntity() {}

    public DoctorEntity(UserEntity user, String specialization, BigDecimal fee) {
        this.user = user;
        this.specialization = specialization;
        this.fee = fee;
    }


    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
