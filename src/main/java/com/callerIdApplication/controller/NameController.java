package com.callerIdApplication.controller;

import com.callerIdApplication.entity.AssignedName;
import com.callerIdApplication.entity.Report;
import com.callerIdApplication.entity.Spam;
import com.callerIdApplication.repostitory.AssignedNameDao;
import com.callerIdApplication.repostitory.ReportDao;
import com.callerIdApplication.repostitory.SpamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/name")
public class NameController {

    @Autowired
    private AssignedNameDao assignedNameDao;
    
    @Autowired
    private SpamDao spamDao;
    
    @Autowired
    private ReportDao reportDao;

    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignName(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String phoneNumber = data.get("phoneNumber");
            String assignedName = data.get("assignedName");
            String assignedBy = data.get("assignedBy");
            
            // Buscar si ya existe un nombre para este número
            List<AssignedName> existing = assignedNameDao.findByPhoneNumber(phoneNumber);
            
            if (!existing.isEmpty()) {
                boolean found = false;
                for (AssignedName name : existing) {
                    if (name.getAssignedName().equalsIgnoreCase(assignedName)) {
                        name.incrementVote();
                        assignedNameDao.save(name);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    AssignedName newName = new AssignedName(phoneNumber, assignedName, assignedBy);
                    assignedNameDao.save(newName);
                }
            } else {
                AssignedName newName = new AssignedName(phoneNumber, assignedName, assignedBy);
                assignedNameDao.save(newName);
            }
            
            // ACTUALIZAR: También actualizar el nombre en la tabla Spam si existe
            List<Spam> spamList = spamDao.findBynumber(phoneNumber);
            if (spamList != null && !spamList.isEmpty()) {
                Spam spam = spamList.get(0);
                spam.setName(assignedName);
                spamDao.save(spam);
            }
            
            response.put("success", true);
            response.put("message", "Nombre asignado correctamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/get/{phoneNumber}")
    public ResponseEntity<Map<String, Object>> getName(@PathVariable String phoneNumber) {
        Map<String, Object> response = new HashMap<>();
        
        // PRIMERO: Verificar si está reportado como SPAM
        List<Spam> spamReports = spamDao.findBynumber(phoneNumber);
        boolean isSpammer = spamReports != null && !spamReports.isEmpty() && spamReports.get(0).getSpammer();
        
        if (isSpammer) {
            response.put("exists", true);
            response.put("name", "SPAM");
            response.put("isSpam", true);
            return ResponseEntity.ok(response);
        }
        
        // SEGUNDO: Buscar nombre asignado
        AssignedName bestName = assignedNameDao.findTopByPhoneNumberOrderByVoteCountDesc(phoneNumber);
        
        if (bestName != null) {
            response.put("exists", true);
            response.put("name", bestName.getAssignedName());
            response.put("votes", bestName.getVoteCount());
            response.put("isSpam", false);
        } else {
            response.put("exists", false);
        }
        
        return ResponseEntity.ok(response);
    }
}
