package com.callerIdApplication.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userPhoneNumber; // Quién hizo la consulta
    private String searchedNumber;   // El número consultado
    private LocalDateTime searchDate;
    private String type;            // "CALL" o "SMS"
    
    @Column(columnDefinition = "TEXT")
    private String messageBody;     // Contenido del SMS si aplica

    public History() {
        this.searchDate = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getUserPhoneNumber() { return userPhoneNumber; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }
    public String getSearchedNumber() { return searchedNumber; }
    public void setSearchedNumber(String searchedNumber) { this.searchedNumber = searchedNumber; }
    public LocalDateTime getSearchDate() { return searchDate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessageBody() { return messageBody; }
    public void setMessageBody(String messageBody) { this.messageBody = messageBody; }
}
