package com.callerIdApplication.controller;

import com.callerIdApplication.entity.Suggestion;
import com.callerIdApplication.repostitory.SuggestionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/suggestion")
public class SuggestionController {

    @Autowired
    private SuggestionDao suggestionDao;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addSuggestion(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String phoneNumber = data.get("phoneNumber");
            String suggestedName = data.get("suggestedName");
            String suggestedBy = data.get("suggestedBy");
            
            if (phoneNumber == null || suggestedName == null) {
                response.put("success", false);
                response.put("message", "Número y nombre son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Suggestion suggestion = new Suggestion();
            suggestion.setPhoneNumber(phoneNumber);
            suggestion.setSuggestedName(suggestedName);
            suggestion.setSuggestedBy(suggestedBy != null ? suggestedBy : "usuario_app");
            suggestion.setApproved(false);
            
            suggestionDao.save(suggestion);
            
            response.put("success", true);
            response.put("message", "Sugerencia enviada. Gracias por contribuir!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/pending")
    public List<Suggestion> getPendingSuggestions() {
        return suggestionDao.findByApprovedFalse();
    }
    
    @PostMapping("/approve/{id}")
    public ResponseEntity<Map<String, Object>> approveSuggestion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Suggestion suggestion = suggestionDao.findById(id).orElse(null);
            if (suggestion != null) {
                suggestion.setApproved(true);
                suggestionDao.save(suggestion);
                response.put("success", true);
                response.put("message", "Sugerencia aprobada");
            } else {
                response.put("success", false);
                response.put("message", "Sugerencia no encontrada");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
