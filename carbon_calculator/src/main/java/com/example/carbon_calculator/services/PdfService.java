package com.example.carbon_calculator.services;

import com.example.carbon_calculator.model.CarbonRecord;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public ByteArrayInputStream generateCarbonReport(List<CarbonRecord> records) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("QuickCarbon Kişisel Rapor").setFontSize(20).setBold());
            document.add(new Paragraph("Toplam Karbon Ayak İzi Kayıtlarınız:"));

            // Tablo Oluşturma (3 Sütun: Aktivite, Süre, Miktar)
            Table table = new Table(3);
            table.addCell("Aktivite");
            table.addCell("Süre (dk)");
            table.addCell("Karbon (kg)");

            double total = 0;
            for (CarbonRecord record : records) {
                table.addCell(record.getActivityName());
                table.addCell(String.valueOf(record.getDurationMinutes()));
                table.addCell(String.format("%.2f", record.getCarbonAmount()));
                total += record.getCarbonAmount();
            }

            document.add(table);
            document.add(new Paragraph("\nToplam Salınım: " + String.format("%.2f", total) + " kg"));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}