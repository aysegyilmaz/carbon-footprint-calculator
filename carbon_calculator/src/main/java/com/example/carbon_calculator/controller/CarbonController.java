package com.example.carbon_calculator.controller;

import com.example.carbon_calculator.model.CarbonRecord;
import com.example.carbon_calculator.services.CarbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carbon")
@CrossOrigin(origins = "*") // Frontend (HTML) ile bağlanırken CORS hatası almamak için
public class CarbonController {

    @Autowired
    private CarbonService carbonService;

    @PostMapping("/save")
    public ResponseEntity<CarbonRecord> createRecord(@RequestBody CarbonRecord record) {
        CarbonRecord savedRecord = carbonService.saveCalculation(record);
        return ResponseEntity.ok(savedRecord);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary() {
        return ResponseEntity.ok(carbonService.getCarbonSummary());
    }

}
