package com.example.carbon_calculator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_records")
@Data
public class CarbonRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_name", nullable = false)
    private String activityName;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "carbon_amount")
    private Double carbonAmount;

    @Column(name = "record_date")
    private LocalDateTime recordDate = LocalDateTime.now();
}
