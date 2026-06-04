package com.callerIdApplication.entity;

import javax.persistence.*;

@Entity
@Table(name = "assigned_name")
public class AssignedName {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String phoneNumber;
    private String assignedName;
    private String assignedBy;
    private int voteCount;
    
    public AssignedName() {}
    
    public AssignedName(String phoneNumber, String assignedName, String assignedBy) {
        this.phoneNumber = phoneNumber;
        this.assignedName = assignedName;
        this.assignedBy = assignedBy;
        this.voteCount = 1;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAssignedName() { return assignedName; }
    public String getAssignedBy() { return assignedBy; }
    public int getVoteCount() { return voteCount; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAssignedName(String assignedName) { this.assignedName = assignedName; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    
    public void incrementVote() { this.voteCount++; }
}
