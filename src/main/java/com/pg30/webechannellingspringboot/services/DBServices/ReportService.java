package com.pg30.webechannellingspringboot.services.DBServices;

import com.pg30.webechannellingspringboot.database.repositories.ReportRepository;
import com.pg30.webechannellingspringboot.entities.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    
    @Autowired
    private ReportRepository repository;
    
    public List<Report> listAll() {
        return repository.findAll();
    }
    
    public void save(Report report) {
        repository.save(report);
    }
    
    public Report get(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    public List<Report> findByPatientName(String patientName) {
        return repository.findByPatientName(patientName);
    }
    
    public List<Report> findByDoctorName(String doctorName) {
        return repository.findByDoctorName(doctorName);
    }
}
