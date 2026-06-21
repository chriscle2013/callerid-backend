package com.callerIdApplication.services;

import com.callerIdApplication.model.PhoneNumber;
import com.callerIdApplication.model.SmsSpamReport;
import com.callerIdApplication.repostitory.PhoneNumberRepository;
import com.callerIdApplication.repostitory.SmsSpamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Autowired
    private SmsSpamRepository smsSpamRepository;

    // --- Métodos para Números ---
    public PhoneNumber updateSpamStatus(Long id, boolean isSpam) {
        PhoneNumber number = phoneNumberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Número no encontrado"));
        number.setSpam(isSpam);
        return phoneNumberRepository.save(number);
    }

    // --- Métodos para SMS Spam ---
    public List<SmsSpamReport> getAllSmsReports() {
        return smsSpamRepository.findAll();
    }

    public SmsSpamReport updateSmsStatus(Long id, String status) {
        SmsSpamReport report = smsSpamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        report.setStatus(status);
        return smsSpamRepository.save(report);
    }
}
