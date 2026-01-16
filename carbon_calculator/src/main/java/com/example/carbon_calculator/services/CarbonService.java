package com.example.carbon_calculator.services;

import com.example.carbon_calculator.model.CarbonRecord;
import com.example.carbon_calculator.repository.CarbonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarbonService {

    @Autowired
    private CarbonRepository repository;

    /**
     * Hesaplama: GRAM cinsinden
     */
    public CarbonRecord saveCalculation(CarbonRecord record) {

        double totalCarbonGram = switch (record.getActivityName()) {

            case "Video İzleme" ->
                    record.getDurationMinutes() * 2.6; // g/dk

            case "Online Toplantı" ->
                    record.getDurationMinutes() * 3.2; // g/dk

            case "Sosyal Medya" ->
                    record.getDurationMinutes() * 1.5; // g/dk

            case "E-posta" ->
                    record.getDurationMinutes() * 0.25; // g/dk ✅

            default -> 0.0;
        };

        record.setCarbonAmount(totalCarbonGram); // GRAM
        return repository.save(record);
    }

    public List<CarbonRecord> getAllRecords() {
        return repository.findAll();
    }

    /**
     * TÜM ZAMANLAR – KG
     */
    public Map<String, Double> getCarbonSummary() {

        List<CarbonRecord> records = getAllRecords();

        Map<String, Double> summaryKg = new HashMap<>();
        double totalGram = 0.0;

        for (CarbonRecord record : records) {
            summaryKg.merge(
                    record.getActivityName(),
                    record.getCarbonAmount() / 1000,
                    Double::sum
            );
            totalGram += record.getCarbonAmount();
        }

        summaryKg.put("TOTAL", totalGram / 1000);
        return summaryKg;
    }

    /**
     * GÜNLÜK – KG
     */
    public Map<String, Double> getDailyCarbonSummary() {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<CarbonRecord> records =
                repository.findByRecordDateBetween(startOfDay, endOfDay);

        Map<String, Double> summaryKg = new HashMap<>();
        double totalGram = 0.0;

        for (CarbonRecord record : records) {
            summaryKg.merge(
                    record.getActivityName(),
                    record.getCarbonAmount() / 1000,
                    Double::sum
            );
            totalGram += record.getCarbonAmount();
        }

        summaryKg.put("TOTAL", totalGram / 1000);
        return summaryKg;
    }
}




