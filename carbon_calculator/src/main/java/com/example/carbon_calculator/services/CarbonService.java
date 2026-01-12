package com.example.carbon_calculator.services;

import com.example.carbon_calculator.model.CarbonRecord;
import com.example.carbon_calculator.repository.CarbonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarbonService {

    @Autowired
    private CarbonRepository repository;

    public CarbonRecord saveCalculation(CarbonRecord record) {
        // Hesaplama Katsayıları (Gram CO2 / Dakika)
        double factor = switch (record.getActivityName()) {
            case "Video İzleme" -> 2.6;
            case "E-posta" -> 0.3;
            case "Online Toplantı" -> 3.2;
            case "Sosyal Medya" -> 1.5;
            default -> 1.0;
        };

        double totalCarbon = record.getDurationMinutes() * factor;
        record.setCarbonAmount(totalCarbon);


        return repository.save(record);
    }

    public List<CarbonRecord> getAllRecords() {
        return repository.findAll();
    }

    public Map<String, Double> getCarbonSummary() {

        List<CarbonRecord> records = getAllRecords();

        Map<String, Double> summary = new HashMap<>();
        double total = 0.0;

        for (CarbonRecord record : records) {
            summary.merge(
                    record.getActivityName(),
                    record.getCarbonAmount(),
                    Double::sum
            );
            total += record.getCarbonAmount();
        }

        summary.put("TOTAL", total);
        return summary;
    }


}
