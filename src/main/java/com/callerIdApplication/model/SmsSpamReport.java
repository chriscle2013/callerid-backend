package com.velo.model;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_spam_reports")
@Data
public class SmsSpamReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderNumber;
    
    @Column(columnDefinition = "TEXT")
    private String messageContent;

    private String status;

    private LocalDateTime reportedAt;

    @PrePersist
    protected void onReport() {
        this.reportedAt = LocalDateTime.now();
        this.status = "PENDING";
    }
}
