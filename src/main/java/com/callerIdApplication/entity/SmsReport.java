package com.callerIdApplication.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sms_reports")
public class SmsReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;     // Remitente del SMS
    @Column(columnDefinition = "TEXT")
    private String messageBody;     // Texto del mensaje
    private String category;        // Ej: Phishing, Cobranzas
    private String reportedBy;      // Usuario que reportó
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportDate;

    private boolean confirmed = true; // Control administrativo

    public SmsReport() { this.reportDate = new Date(); }

    public SmsReport(String phoneNumber, String messageBody, String category, String reportedBy) {
        this.phoneNumber = phoneNumber;
        this.messageBody = messageBody;
        this.category = category;
        this.reportedBy = reportedBy;
        this.reportDate = new Date();
    }

    // Getters y Setters...
    public Long getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getMessageBody() { return messageBody; }
    public void setMessageBody(String messageBody) { this.messageBody = messageBody; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}
