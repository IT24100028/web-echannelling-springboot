package com.pg30.webechannellingspringboot.database.repositories;

import com.pg30.webechannellingspringboot.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPatientName(String patientName);
    List<Report> findByDoctorName(String doctorName);
}
