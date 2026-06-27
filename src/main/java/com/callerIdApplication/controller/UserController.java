package com.callerIdApplication.controller;

import com.callerIdApplication.entity.User;
import com.callerIdApplication.entity.Report;
import com.callerIdApplication.repostitory.UserDao;
import com.callerIdApplication.repostitory.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ReportDao reportDao;

    @PostMapping("/user/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String phoneNumber = payload.containsKey("phoneNumber") ? String.valueOf(payload.get("phoneNumber")) : null;
            String password = payload.containsKey("password") ? String.valueOf(payload.get("password")) : null;
            
            // Captura flexible de nombres para la App
            String userName = payload.containsKey("userName") ? String.valueOf(payload.get("userName")) : "Usuario Velo";
            if (payload.containsKey("name")) userName = String.valueOf(payload.get("name"));
            
            String email = payload.containsKey("email") ? String.valueOf(payload.get("email")) : "";
            String work = payload.containsKey("work") ? String.valueOf(payload.get("work")) : "";

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Falta número de teléfono");
                return ResponseEntity.status(400).body(response);
            }

            String cleanRegNumber = phoneNumber.replaceAll("[^0-9]", "");
            if (cleanRegNumber.length() == 12 && cleanRegNumber.startsWith("57")) {
                cleanRegNumber = cleanRegNumber.substring(2);
            }

            User existingUser = userDao.findByPhoneNumber(cleanRegNumber);
            User savedUser;

            if (existingUser != null) {
                if (password != null) existingUser.setPassword(password);
                existingUser.setUserName(userName);
                existingUser.setEmail(email);
                // Si agregaste el campo work en la entidad User:
                // existingUser.setWork(work); 
                savedUser = userDao.save(existingUser);
                response.put("message", "Perfil actualizado con éxito.");
            } else {
                User newUser = new User();
                newUser.setPhoneNumber(cleanRegNumber);
                newUser.setPassword(password != null ? password : "123456");
                newUser.setUserName(userName);
                newUser.setEmail(email);
                // newUser.setWork(work);

                long timeSeed = System.currentTimeMillis() % 899999L;
                newUser.setUserId((int) (100000 + timeSeed));

                savedUser = userDao.save(newUser);
                response.put("message", "Usuario registrado exitosamente.");
            }

            response.put("status", "success");
            response.put("uuid", savedUser.getUuid()); 
            response.put("userId", savedUser.getUserId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error en registro: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        try {
            String phoneNumber = credentials.get("phoneNumber");
            String password = credentials.get("password");

            if (phoneNumber == null || password == null) {
                response.put("status", "error");
                response.put("message", "Faltan parámetros");
                return ResponseEntity.status(400).body(response);
            }

            String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2);
            }

            User user = userDao.findByPhoneNumber(cleanNumber);

            if (user != null && user.getPassword().equals(password)) {
                response.put("status", "success");
                response.put("message", "Bienvenido a Velo");
                response.put("uuid", user.getUuid()); 
                
                // 🔥 ESTO ES LO QUE FALTABA: Enviar los datos para que la App los guarde
                response.put("userName", user.getUserName());
                response.put("email", user.getEmail());
                // response.put("work", user.getWork()); // Activa esto cuando añadas 'work' a User.java
                
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Credenciales inválidas");
                return ResponseEntity.status(401).body(response);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error en login: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/user/searchPerson/number={number}")
    public ResponseEntity<List<Map<String, Object>>> searchPerson(
            @PathVariable("number") String number,
            @RequestParam("key") String key) {
        
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, Object> responseMap = new HashMap<>();
        
        String cleanNumber = number.replaceAll("[^0-9]", "");
        if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
            cleanNumber = cleanNumber.substring(2);
        }
        
        boolean isSpammer = false;
        String resolvedName = "Unknown";
        
        try {
            User foundUser = userDao.findByPhoneNumber(cleanNumber);
            if (foundUser != null) {
                resolvedName = foundUser.getUserName(); // Mostrar el nombre REAL del usuario si está registrado
            }

            List<Report> reportList = reportDao.findByPhoneNumber(cleanNumber);
            if (reportList != null && !reportList.isEmpty()) {
                Report reportRecord = reportList.get(0);
                if (reportRecord != null) {
                    isSpammer = reportRecord.isSpammer(); 
                    if (isSpammer) {
                        resolvedName = (reportRecord.getCategory() != null) ? "Reporte: " + reportRecord.getCategory() : "SPAM";
                    }
                }
            }
            
            responseMap.put("number", cleanNumber);
            responseMap.put("spammer", isSpammer);
            responseMap.put("name", resolvedName);
            responseList.add(responseMap);
            return ResponseEntity.ok(responseList);
            
        } catch (Exception e) {
            responseMap.put("name", "Desconocido");
            responseList.add(responseMap);
            return ResponseEntity.status(500).body(responseList);
        }
    }
}
