package com.callerIdApplication.controller;

import com.callerIdApplication.entity.SmsReport;
import com.callerIdApplication.repostitory.SmsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsDao smsDao;

    @PostMapping("/report")
    public ResponseEntity<?> reportSms(@RequestBody SmsReport report, @RequestParam String key) {
        if (key == null || key.isEmpty()) {
            return ResponseEntity.status(401).body("Acceso denegado");
        }
        try {
            SmsReport saved = smsDao.save(report);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar SMS: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkSms(@RequestParam String number) {
        var reports = smsDao.findByPhoneNumber(number);
        Map<String, Object> response = new HashMap<>();
        response.put("isDangerous", !reports.isEmpty());
        response.put("totalReports", reports.size());
        return ResponseEntity.ok(response);
    }
}
