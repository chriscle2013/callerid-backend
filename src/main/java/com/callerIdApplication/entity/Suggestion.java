package com.callerIdApplication.entity;

import javax.persistence.*;

@Entity
@Table(name = "suggestion")
public class Suggestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String phoneNumber;
    private String suggestedName;
    private String suggestedBy;
    private boolean approved;
    
    public Suggestion() {}
    
    // Getters
    public Long getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSuggestedName() { return suggestedName; }
    public String getSuggestedBy() { return suggestedBy; }
    public boolean isApproved() { return approved; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setSuggestedName(String suggestedName) { this.suggestedName = suggestedName; }
    public void setSuggestedBy(String suggestedBy) { this.suggestedBy = suggestedBy; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
