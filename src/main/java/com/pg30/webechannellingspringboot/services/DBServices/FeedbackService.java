package com.pg30.webechannellingspringboot.services.DBServices;

import com.pg30.webechannellingspringboot.database.repositories.FeedbackRepository;
import com.pg30.webechannellingspringboot.entities.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository repository;
    
    public List<Feedback> listAll() {
        return repository.findAll();
    }
    
    public void save(Feedback feedback) {
        repository.save(feedback);
    }
    
    public Feedback get(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
