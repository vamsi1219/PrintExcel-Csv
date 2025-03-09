package com.task1.excelSheet.service;

import com.task1.excelSheet.excpetions.FileFormatWrongException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileProcessingService {
    List<List<String>> processFile(MultipartFile file, int startRow) ;

}

