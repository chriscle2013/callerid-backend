package com.callerIdApplication.controller;

import com.callerIdApplication.model.SmsReport;
import com.callerIdApplication.repostitory.SmsSpamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsSpamRepository smsSpamRepository;

    @PostMapping("/report")
    public ResponseEntity<?> reportSms(@RequestBody SmsReport report, @RequestParam String key) {
        // Validar llave de seguridad (UUID) similar a tus otros controladores
        if (key == null || key.isEmpty()) {
            return ResponseEntity.status(401).body("Llave de seguridad requerida");
        }

        try {
            SmsReport saved = smsRepository.save(report);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Mensaje fraudulentos registrado con éxito");
            response.put("id", saved.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkSms(@RequestParam String number) {
        // Verifica si ese remitente ya tiene reportes de SMS
        var reports = smsRepository.findByPhoneNumber(number);
        Map<String, Object> response = new HashMap<>();
        response.put("isDangerous", !reports.isEmpty());
        response.put("totalReports", reports.size());
        return ResponseEntity.ok(response);
    }
}
