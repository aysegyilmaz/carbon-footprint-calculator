package com.example.carbon_calculator.services;

import com.example.carbon_calculator.model.CarbonRecord;
import com.example.carbon_calculator.repository.CarbonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarbonServiceTest {
    @Mock
    private CarbonRepository repository;

    @InjectMocks
    private CarbonService carbonService;

    @Test
    void shouldCalculateCarbonSummaryCorrectly() {

        // GIVEN (mock data)
        CarbonRecord video = new CarbonRecord();
        video.setActivityName("Video İzleme");
        video.setCarbonAmount(5.0);

        CarbonRecord email = new CarbonRecord();
        email.setActivityName("E-posta");
        email.setCarbonAmount(1.0);

        CarbonRecord meeting = new CarbonRecord();
        meeting.setActivityName("Online Toplantı");
        meeting.setCarbonAmount(4.0);

        when(repository.findAll())
                .thenReturn(List.of(video, email, meeting));

        // WHEN
        Map<String, Double> summary = carbonService.getCarbonSummary();

        // THEN
        assertEquals(10.0, summary.get("TOTAL"));
        assertEquals(5.0, summary.get("Video İzleme"));
        assertEquals(1.0, summary.get("E-posta"));
        assertEquals(4.0, summary.get("Online Toplantı"));
    }

}