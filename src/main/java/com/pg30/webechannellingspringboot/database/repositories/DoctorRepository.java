package com.pg30.webechannellingspringboot.database.repositories;

import com.pg30.webechannellingspringboot.entities.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {

    @Query("SELECT d FROM DoctorEntity d JOIN d.user u " +
            "WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DoctorEntity> searchDoctors(@Param("keyword") String keyword);
}
