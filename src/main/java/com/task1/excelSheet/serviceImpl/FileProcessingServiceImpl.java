package com.task1.excelSheet.serviceImpl;



import com.opencsv.CSVReader;
import com.task1.excelSheet.service.FileProcessingService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileProcessingServiceImpl implements FileProcessingService {

    private final FileProcessingHelper helper = new FileProcessingHelper();

    public List<List<String>> processFile(MultipartFile file, int startRow) {

        try {
            List<List<String>> data = null;
            String fileName = file.getOriginalFilename();

            if(!(fileName.endsWith(".csv") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls")))
                throw new FileNotFoundException("File format is wrong, only .csv, .xlsx, .xls");

            if (fileName.endsWith(".csv")) {
                data = helper.processCSV(file, startRow);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                data = helper.processExcel(file, startRow);
            }

            return data;
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}

class FileProcessingHelper {

    public List<List<String>> processCSV(MultipartFile file, int startRow) throws IOException, CsvValidationException {

        List<List<String>> data = new ArrayList<>();
        int totalRows = 0;

        try (InputStream inputStream = file.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] line;
            List<String> firstRowData = null;

            // Read and process the CSV
            while ((line = csvReader.readNext()) != null) {
                if (totalRows == 0) {
                    // Always add the first row (header)
                    firstRowData = new ArrayList<>(Arrays.asList(line));
                } else if (totalRows >= startRow) {
                    // Add rows starting from startRow
                    data.add(new ArrayList<>(Arrays.asList(line)));
                }
                totalRows++;
            }

            // Add the first row (header) to the data at position 0
            if (firstRowData != null) {
                data.add(0, firstRowData);  // Ensure that the header is at the 0th index
            }
        }

        // Check if startRow exceeds the total number of rows
        if (totalRows <= startRow) {
            System.out.println("Warning: The file has only " + totalRows + " rows. startRow " + startRow + " is too high.");
            throw new RuntimeException("Warning: The file has only " + totalRows + " rows. startRow " + startRow + " is too high.");
        }

        return data;
    }

    public List<List<String>> processExcel(MultipartFile file, int startRow) throws IOException {

        List<List<String>> data = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int currentRow = 0;

            // Always add the first row (header row) to the data
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row firstRow = sheet.getRow(0);
                List<String> firstRowData = new ArrayList<>();
                for (Cell cell : firstRow) {
                    firstRowData.add(cell.toString());
                }
                data.add(firstRowData);
            }


            // Process rows starting from startRow
            for (Row row : sheet) {
                if (currentRow >= startRow) {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        rowData.add(cell.toString());
                    }
                    data.add(rowData);  // Add the rows starting from startRow
                }
                currentRow++;
            }

            // Check if startRow exceeds the total number of rows
            if (currentRow <= startRow) {
                System.out.println("Warning: The file has only " + currentRow + " rows. startRow " + startRow + " is too high.");
                throw new RuntimeException("Warning: The file has only " + currentRow + " rows. startRow " + startRow + " is too high.");
            }
        }
        return data;
    }

}