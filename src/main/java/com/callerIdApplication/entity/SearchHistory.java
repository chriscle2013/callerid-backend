package com.callerIdApplication.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userPhoneNumber; // Quién hizo la búsqueda/recibió la llamada
    private String searchedNumber;  // El número telefónico que fue consultado
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date searchDate;

    // --- NUEVOS CAMPOS PARA SMS ---
    private String type;            // Guardará "CALL" o "SMS"
    
    @Column(columnDefinition = "TEXT")
    private String messageBody;     // Guardará el contenido del mensaje de texto

    public SearchHistory() {}

    // Constructor actualizado para soportar todos los datos
    public SearchHistory(String userPhoneNumber, String searchedNumber, String type, String messageBody) {
        this.userPhoneNumber = userPhoneNumber;
        this.searchedNumber = searchedNumber;
        this.type = type;
        this.messageBody = messageBody;
        this.searchDate = new Date(); 
    }

    // Getters y Setters existentes
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserPhoneNumber() { return userPhoneNumber; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }

    public String getSearchedNumber() { return searchedNumber; }
    public void setSearchedNumber(String searchedNumber) { this.searchedNumber = searchedNumber; }

    public Date getSearchDate() { return searchDate; }
    public void setSearchDate(Date searchDate) { this.searchDate = searchDate; }

    // --- NUEVOS GETTERS Y SETTERS ---
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessageBody() { return messageBody; }
    public void setMessageBody(String messageBody) { this.messageBody = messageBody; }
}
