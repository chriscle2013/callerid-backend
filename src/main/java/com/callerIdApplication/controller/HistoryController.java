package com.callerIdApplication.controller;

import com.callerIdApplication.entity.SearchHistory;
import com.callerIdApplication.repostitory.HistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryDao historyDao;

    // 📥 Endpoint actualizado para guardar llamadas o SMS en el historial
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addHistoryRecord(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userPhoneNumber = requestData.get("userPhoneNumber");
            String searchedNumber = requestData.get("searchedNumber");
            
            // --- NUEVA LÓGICA PARA SMS ---
            // Obtenemos el tipo (si no viene, por defecto es CALL) y el contenido del mensaje
            String type = requestData.getOrDefault("type", "CALL");
            String messageBody = requestData.get("messageBody");

            if (searchedNumber == null || searchedNumber.isEmpty()) {
                response.put("status", "error");
                response.put("message", "El número buscado es requerido");
                return ResponseEntity.badRequest().body(response);
            }

            if (userPhoneNumber == null || userPhoneNumber.isEmpty()) {
                userPhoneNumber = "Anonymous";
            }

            // Usamos el nuevo constructor de 4 parámetros definido en SearchHistory.java
            SearchHistory newRecord = new SearchHistory(userPhoneNumber, searchedNumber, type, messageBody);
            SearchHistory savedRecord = historyDao.save(newRecord);

            response.put("status", "success");
            response.put("message", "Registro agregado al historial");
            response.put("data", savedRecord);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al guardar el historial: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // 📤 Endpoint para obtener todo el historial de un usuario específico
    @GetMapping("/user/{phoneNumber}")
    public ResponseEntity<Map<String, Object>> getUserHistory(@PathVariable("phoneNumber") String phoneNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SearchHistory> userHistory = historyDao.findByUserPhoneNumberOrderBySearchDateDesc(phoneNumber);
            
            response.put("status", "success");
            response.put("count", userHistory.size());
            response.put("history", userHistory);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al obtener el historial: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
