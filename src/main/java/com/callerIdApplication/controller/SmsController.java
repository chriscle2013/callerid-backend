package com.callerIdApplication.controller;

// Ajustamos la importación para usar la carpeta con el error tipográfico 'repostitory'
import com.callerIdApplication.repostitory.SmsSpamRepository;
// Importamos la entidad correcta (SmsSpamReport, no SmsReport)
import com.callerIdApplication.model.SmsSpamReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // IMPORTACIÓN CLAVE QUE FALTABA
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsSpamRepository smsSpamRepository;

    @PostMapping("/report")
    public ResponseEntity<?> reportSms(@RequestBody SmsSpamReport report, @RequestParam String key) {
        if (key == null || key.isEmpty()) {
            return ResponseEntity.status(401).body("Llave de seguridad requerida");
        }

        try {
            // Usamos smsSpamRepository en lugar de smsRepository
            SmsSpamReport saved = smsSpamRepository.save(report);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Mensaje fraudulento registrado con éxito");
            response.put("id", saved.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkSms(@RequestParam String number) {
        // Asegúrate de que en SmsSpamRepository exista el método findBySenderNumber
        var reports = smsSpamRepository.findByStatus("PENDING"); 
        Map<String, Object> response = new HashMap<>();
        response.put("isDangerous", !reports.isEmpty());
        response.put("totalReports", reports.size());
        return ResponseEntity.ok(response);
    }
}
