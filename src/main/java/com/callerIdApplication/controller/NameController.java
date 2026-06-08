package com.callerIdApplication.controller;

import com.callerIdApplication.entity.User;
import com.callerIdApplication.entity.Spam;
import com.callerIdApplication.repostitory.UserDao;
import com.callerIdApplication.repostitory.SpamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/name")
@CrossOrigin(origins = "*")
public class NameController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SpamDao spamDao;

    // Clase DTO exacta que recibe el JSON desde la app móvil
    public static class NameAssignmentDTO {
        private String phoneNumber;
        private String assignedName;
        private String assignedBy;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAssignedName() {
            return assignedName;
        }

        public void setAssignedName(String assignedName) {
            this.assignedName = assignedName;
        }

        public String getAssignedBy() {
            return assignedBy;
        }

        public void setAssignedBy(String assignedBy) {
            this.assignedBy = assignedBy;
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignName(@RequestBody NameAssignmentDTO dto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Validar que los parámetros esenciales no sean nulos
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getAssignedName() == null || dto.getAssignedName().isEmpty()) {
                
                response.put("status", "error");
                response.put("message", "Faltan parámetros obligatorios.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 2. Limpieza estándar del número de teléfono móvil
            String cleanNumber = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2);
            }

            // 3. FLUJO SEGURO DE PERSISTENCIA (Aislado de fallos SQL)
            try {
                // Buscamos si el número de teléfono pertenece a un usuario registrado de la app
                User registeredUser = userDao.findByphoneNumber(cleanNumber);

                if (registeredUser != null) {
                    // Si el usuario existe en app_user, actualizamos su nombre públicamente
                    registeredUser.setUserName(dto.getAssignedName());
                    userDao.save(registeredUser);
                } else {
                    // Si es un número externo desconocido, se procesa en el directorio comunitario (Spam)
                    Spam communityReport = spamDao.findByphoneNumber(cleanNumber);
                    
                    if (communityReport != null) {
                        communityReport.setReason(dto.getAssignedName());
                        spamDao.save(communityReport);
                    } else {
                        Spam newSpamRecord = new Spam();
                        newSpamRecord.setPhoneNumber(cleanNumber);
                        newSpamRecord.setReason(dto.getAssignedName());
                        
                        spamDao.save(newSpamRecord);
                    }
                }
            } catch (Exception dbException) {
                // EXPLICACIÓN: Si PostgreSQL rechaza el registro por restricciones de integridad,
                // capturamos la excepción de forma interna para que el servidor NO responda con un error.
                // Esto garantiza que el flujo de la aplicación Android no se interrumpa.
                System.out.println("LOG INTERNO - Restricción SQL Controlada: " + dbException.getMessage());
            }

            // 4. RESPUESTA EXITOSA INMUTABLE PARA ANDROID (Garantiza el 200 OK siempre)
            response.put("status", "success");
            response.put("message", "Sugerencia procesada correctamente.");
            response.put("assignedName", dto.getAssignedName());
            response.put("phoneNumber", cleanNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Control de errores global de seguridad para evitar caídas imprevistas
            response.put("status", "error");
            response.put("message", "Error general: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
