package com.pg30.webechannellingspringboot.controllers;

import com.pg30.webechannellingspringboot.entities.Report;
import com.pg30.webechannellingspringboot.services.DBServices.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

@Controller
public class ReportController {
    
    @Autowired
    private ReportService service;
    
    private final String UPLOAD_DIR = "uploads/";

    Logger logger = Logger.getLogger("info");

    @GetMapping("/doctor/report")
    public String showUploadForm(Model model) {
        model.addAttribute("report", new Report());
        return "report_upload";
    }

    @PostMapping("/doctor/report/save")
    public String uploadReport(@Valid @ModelAttribute Report report,
                               BindingResult result,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            logger.info("Validation errors while uploading report: {}");
            return "report_upload";
        }

        if (file.isEmpty()) {
            logger.info("No file selected for report upload by doctor: {}");
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            return "redirect:/doctor/report";
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                logger.info("Created upload directory at {}");
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            report.setFilePath(UPLOAD_DIR + fileName);
            report.setFileName(file.getOriginalFilename());
            service.save(report);

            logger.info("Report uploaded successfully: {} by doctor: {}");
            redirectAttributes.addFlashAttribute("successMessage", "Report uploaded successfully!");
            return "redirect:/doctor/report?success";

        } catch (IOException e) {
            logger.info("Failed to upload report: {} by doctor: {}"     );
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload file: " + e.getMessage());
            return "redirect:/doctor/report";
        }
    }

    
    @GetMapping("/patient/reports")
    public String viewReports(Model model) {
        model.addAttribute("listReports", service.listAll());
        return "view_reports";
    }
    
    @GetMapping("/admin/reports")
    public String adminViewReports(Model model) {
        model.addAttribute("listReports", service.listAll());
        return "admin_reports";
    }
    
    @GetMapping("/report/download/{id}")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) {
        try {
            Report report = service.get(id);
            if (report == null || report.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(report.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "attachment; filename=\"" + report.getFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/admin/reports/delete/{id}")
    public String deleteReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Report report = service.get(id);
            if (report != null && report.getFilePath() != null) {
                // Delete file from filesystem
                Path filePath = Paths.get(report.getFilePath());
                Files.deleteIfExists(filePath);
            }
            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Report deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete report: " + e.getMessage());
        }
        return "redirect:/admin/reports";
    }
}
