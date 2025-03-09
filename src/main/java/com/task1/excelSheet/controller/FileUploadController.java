package com.task1.excelSheet.controller;


import com.task1.excelSheet.excpetions.FileFormatWrongException;
import com.task1.excelSheet.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Controller
public class FileUploadController {

    @Autowired
    private FileProcessingService fileProcessingService;

    // Show the upload form
    @GetMapping("/")
    public String showUploadForm() {
        return "uploadForm";
    }

    // Handle file upload and data processing
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "startRow", defaultValue = "0") Integer startRow,
                                   Model model) {

        try {
            List<List<String>> data = fileProcessingService.processFile(file, startRow);

            model.addAttribute("data", data);
            return "fileData";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "uploadForm";
        }
    }
}
