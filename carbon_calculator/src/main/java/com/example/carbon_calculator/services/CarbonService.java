package com.example.carbon_calculator.services;

import com.example.carbon_calculator.model.CarbonRecord;
import com.example.carbon_calculator.repository.CarbonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarbonService {

    @Autowired
    private CarbonRepository repository;

    // Karbon ayak izini hesaplayan ve kaydeden metod
    public CarbonRecord saveCalculation(CarbonRecord record) {
        // Hesaplama Katsayıları (Gram CO2 / Dakika)
        double factor = switch (record.getActivityName()) {
            case "Video İzleme" -> 2.6;   // HD yayın ortalaması
            case "E-posta" -> 0.3;        // Ekli/eksiz ortalama
            case "Online Toplantı" -> 3.2; // Kamera açık toplantı
            case "Sosyal Medya" -> 1.5;   // Kaydırma (scrolling)
            default -> 1.0;
        };

        // Hesaplama: Dakika * Katsayı
        double totalCarbon = record.getDurationMinutes() * factor;
        record.setCarbonAmount(totalCarbon);

        // Veritabanına kaydet
        return repository.save(record);
    }

    // Tüm geçmiş kayıtları getiren metod
    public List<CarbonRecord> getAllRecords() {
        return repository.findAll();
    }
}
