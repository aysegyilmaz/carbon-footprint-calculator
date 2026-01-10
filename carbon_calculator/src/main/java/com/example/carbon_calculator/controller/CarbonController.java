package com.example.carbon_calculator.controller;

import com.example.carbon_calculator.model.CarbonRecord;
import com.example.carbon_calculator.services.CarbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carbon")
@CrossOrigin(origins = "*") // Frontend (HTML) ile bağlanırken CORS hatası almamak için
public class CarbonController {

    @Autowired
    private CarbonService carbonService;

    // Veriyi kaydeden ve hesaplamayı başlatan uç nokta (POST)
    @PostMapping("/save")
    public ResponseEntity<CarbonRecord> createRecord(@RequestBody CarbonRecord record) {
        CarbonRecord savedRecord = carbonService.saveCalculation(record);
        return ResponseEntity.ok(savedRecord);
    }

    // Tüm kayıtları listeleyen uç nokta (GET)
    @GetMapping("/all")
    public ResponseEntity<List<CarbonRecord>> getAllRecords() {
        return ResponseEntity.ok(carbonService.getAllRecords());
    }
}