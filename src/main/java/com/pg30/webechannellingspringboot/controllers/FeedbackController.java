package com.pg30.webechannellingspringboot.controllers;

import com.pg30.webechannellingspringboot.entities.Feedback;
import com.pg30.webechannellingspringboot.services.DBServices.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
public class FeedbackController {
    
    @Autowired
    private FeedbackService service;
    
    @GetMapping("/feedback")
    public String showForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback_form";
    }
    
    @PostMapping("/feedback/save")
    public String saveFeedback(@Valid @ModelAttribute Feedback feedback, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "feedback_form";
        }
        service.save(feedback);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your feedback!");
        return "redirect:/feedback?success";
    }
    
    @GetMapping("/admin/feedback")
    public String viewAllFeedback(Model model) {
        model.addAttribute("listFeedback", service.listAll());
        return "admin_feedback";
    }
    
    @GetMapping("/admin/feedback/edit/{id}")
    public String editFeedback(@PathVariable Long id, Model model) {
        Feedback feedback = service.get(id);
        model.addAttribute("feedback", feedback);
        return "edit_feedback";
    }
    
    @PostMapping("/admin/feedback/update")
    public String updateFeedback(@Valid @ModelAttribute Feedback feedback, 
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "edit_feedback";
        }
        service.save(feedback);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback updated successfully!");
        return "redirect:/admin/feedback";
    }
    
    @GetMapping("/admin/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted successfully!");
        return "redirect:/admin/feedback";
    }
}
