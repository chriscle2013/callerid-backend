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

    public SearchHistory() {}

    public SearchHistory(String userPhoneNumber, String searchedNumber) {
        this.userPhoneNumber = userPhoneNumber;
        this.searchedNumber = searchedNumber;
        this.searchDate = new Date(); // Asigna la fecha y hora actual del servidor automáticamente
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserPhoneNumber() { return userPhoneNumber; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }

    public String getSearchedNumber() { return searchedNumber; }
    public void setSearchedNumber(String searchedNumber) { this.searchedNumber = searchedNumber; }

    public Date getSearchDate() { return searchDate; }
    public void setSearchDate(Date searchDate) { this.searchDate = searchDate; }
}
