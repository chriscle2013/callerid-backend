package com.velo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_spam_reports")
@Data
public class SmsSpamReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderNumber; // Número que envió el SMS
    
    @Column(columnDefinition = "TEXT")
    private String messageContent; // Contenido del mensaje reportado

    private String status; // Ej: "PENDING", "APPROVED", "REJECTED"

    private LocalDateTime reportedAt;

    @PrePersist
    protected void onReport() {
        this.reportedAt = LocalDateTime.now();
        this.status = "PENDING";
    }
}
