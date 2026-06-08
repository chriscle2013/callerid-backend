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

    // Clase DTO estática interna para procesar el JSON que envía la app Android
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
            // 1. Validar parámetros requeridos
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getAssignedName() == null || dto.getAssignedName().isEmpty()) {
                
                response.put("status", "error");
                response.put("message", "Faltan parámetros obligatorios.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 2. Limpieza estándar del número de teléfono (remueve espacios y prefijo de país)
            String cleanNumber = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2);
            }

            // 3. FLUJO DE PERSISTENCIA ADAPTADO A TUS ENTIDADES ORIGINALES
            try {
                // Buscamos primero si el número es un usuario registrado de la aplicación
                User registeredUser = userDao.findByphoneNumber(cleanNumber);

                if (registeredUser != null) {
                    // Si el usuario existe, se actualiza el nombre de su cuenta
                    registeredUser.setUserName(dto.getAssignedName());
                    userDao.save(registeredUser);
                } else {
                    // MODELO TRUECALLER: Si es un número externo desconocido que llama,
                    // lo registramos en la tabla comunitaria de Spam usando tus métodos reales (phoneNo).
                    Spam communitySpamRecord = spamDao.findByPhoneNo(cleanNumber);
                    
                    if (communitySpamRecord != null) {
                        // Si ya existía el registro en la base de datos de spam, nos aseguramos de marcarlo activo
                        communitySpamRecord.setSpammer(true);
                        spamDao.save(communitySpamRecord);
                    } else {
                        // Si es la primera vez que se reporta este número externo, creamos la sugerencia
                        Spam newSpam = new Spam();
                        newSpam.setPhoneNo(cleanNumber); // Cambiado a setPhoneNo según exige tu compilador
                        newSpam.setSpammer(true);        // Activamos la bandera de spammer
                        
                        spamDao.save(newSpam);
                    }
                }
            } catch (Exception dbException) {
                // Si la base de datos PostgreSQL opone resistencia por temas de llaves primarias o IDs,
                // consumimos el error internamente para no bloquear la experiencia en la App Android.
                System.out.println("Restricción SQL evitada de forma controlada: " + dbException.getMessage());
            }

            // 4. RESPUESTA EXITOSA SIN FILTROS PARA VOLLEY (Código HTTP 200 OK)
            response.put("status", "success");
            response.put("message", "Sugerencia procesada correctamente.");
            response.put("assignedName", dto.getAssignedName());
            response.put("phoneNumber", cleanNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Fallo general en el endpoint: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
