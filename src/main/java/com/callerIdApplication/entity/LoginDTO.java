package com.callerIdApplication.entity;

public class LoginDTO {
    
    private String phoneNumber;
    private String password;
    
    // Constructores
    public LoginDTO() {
    }
    
    public LoginDTO(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    
    // Getters y Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
