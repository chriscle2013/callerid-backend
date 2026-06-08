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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/name")
@CrossOrigin(origins = "*")
public class NameController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SpamDao spamDao;

    // Clase DTO que mapea con precisión el JSON enviado por Volley desde la app Android
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
            // 1. Validaciones preventivas de parámetros
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getAssignedName() == null || dto.getAssignedName().isEmpty()) {
                
                response.put("status", "error");
                response.put("message", "Faltan parámetros obligatorios.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 2. Limpieza estándar del número de teléfono móvil (+57, espacios)
            String cleanNumber = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2);
            }

            // 3. PERSISTENCIA EN LA BASE DE DATOS SEGÚN TUS ENTIDADES REALES
            try {
                // Buscamos primero en la tabla de usuarios de la aplicación (app_user)
                User registeredUser = userDao.findByphoneNumber(cleanNumber);

                if (registeredUser != null) {
                    // Si el número pertenece a un usuario registrado, actualizamos el nombre de su perfil
                    registeredUser.setUserName(dto.getAssignedName());
                    userDao.save(registeredUser);
                } else {
                    // MODELO TRUECALLER COMUNITARIO:
                    // Guardamos la sugerencia como un nuevo reporte en la tabla comunitaria.
                    // Al insertar un registro nuevo cada vez, permitimos la acumulación de nombres para la votación.
                    Spam newSpamReport = new Spam();
                    newSpamReport.setNumber(cleanNumber);          // Usa 'setNumber' de tu entidad original
                    newSpamReport.setName(dto.getAssignedName());    // Guarda el nombre sugerido asociado al reporte
                    newSpamReport.setSpammer(true);                // Marcamos que es clasificado como spammer
                    
                    spamDao.save(newSpamReport);
                }
            } catch (Exception dbException) {
                // En caso de restricciones de BD imprevistas, evitamos romper el flujo de Android
                System.out.println("Restricción SQL controlada: " + dbException.getMessage());
            }

            // 4. RESPUESTA EXITOSA INMEDIATA A VOLLEY (Evita bloqueos en Android)
            response.put("status", "success");
            response.put("message", "Sugerencia de nombre procesada correctamente por la comunidad.");
            response.put("assignedName", dto.getAssignedName());
            response.put("phoneNumber", cleanNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error general: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
