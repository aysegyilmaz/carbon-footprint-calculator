package com.example.carbon_calculator.controller;

import com.example.carbon_calculator.model.CarbonRecord;
import com.example.carbon_calculator.services.CarbonService;
import com.example.carbon_calculator.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carbon")
@CrossOrigin(origins = "*") // Frontend (HTML) ile bağlanırken CORS hatası almamak için
public class CarbonController {

    @Autowired
    private CarbonService carbonService;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/save")
    public ResponseEntity<CarbonRecord> createRecord(@RequestBody CarbonRecord record) {
        CarbonRecord savedRecord = carbonService.saveCalculation(record);
        return ResponseEntity.ok(savedRecord);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary() {
        return ResponseEntity.ok(carbonService.getCarbonSummary());
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Double>> getDailySummary() {
        return ResponseEntity.ok(carbonService.getDailyCarbonSummary());
    }

    @GetMapping("/report/download")
    public ResponseEntity<InputStreamResource> downloadReport() {
        List<CarbonRecord> records = carbonService.getAllRecords();
        ByteArrayInputStream bis = pdfService.generateCarbonReport(records);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=carbon_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
