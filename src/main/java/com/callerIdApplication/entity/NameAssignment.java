package com.callerIdApplication.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "name_assignments")
public class NameAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;   // El número al que le sugerimos nombre
    private String assignedName;  // El nombre sugerido
    private String assignedBy;    // El teléfono del usuario que sugirió

    @Temporal(TemporalType.TIMESTAMP)
    private Date assignmentDate;

    public NameAssignment() { this.assignmentDate = new Date(); }

    public NameAssignment(String phoneNumber, String assignedName, String assignedBy) {
        this.phoneNumber = phoneNumber;
        this.assignedName = assignedName;
        this.assignedBy = assignedBy;
        this.assignmentDate = new Date();
    }

    // Getters y Setters...
    public String getAssignedName() { return assignedName; }
    public void setAssignedName(String assignedName) { this.assignedName = assignedName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
