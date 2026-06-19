package com.callerIdApplication.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_reports")
public class SmsReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String phoneNumber;
    @Column(columnDefinition = "TEXT")
    private String messageBody;
    private String category;
    private String reportedBy;
    private LocalDateTime reportDate = LocalDateTime.now();
    
    // Getters y Setters...
}
