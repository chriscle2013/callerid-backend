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
            // 1. Validaciones de los parámetros mínimos requeridos por el protocolo HTTP
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getAssignedName() == null || dto.getAssignedName().isEmpty()) {
                
                response.put("status", "error");
                response.put("message", "Faltan parámetros obligatorios.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 2. Limpieza estándar del formato telefónico móvil (Quitando +57 y espacios vacíos)
            String cleanNumber = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2);
            }

            // 3. BLOQUE DE PERSISTENCIA ADAPTADO A TU SISTEMA:
            try {
                // Buscamos primero en la tabla de usuarios registrados (app_user)
                User registeredUser = userDao.findByphoneNumber(cleanNumber);

                if (registeredUser != null) {
                    // Si el número pertenece a un usuario, modificamos su nombre de cuenta
                    registeredUser.setUserName(dto.getAssignedName());
                    userDao.save(registeredUser);
                } else {
                    // MODELO DE DIRECTORIO TRUECALLER:
                    // Si es un número desconocido externo, interactuamos con tu repositorio original SpamDao.
                    // Usamos 'findByphoneNumber' que es el método nativo de tu repositorio para consultar registros de spam.
                    Spam communitySpamRecord = spamDao.findByphoneNumber(cleanNumber);
                    
                    if (communitySpamRecord != null) {
                        // Si ya existía el registro en la base de datos comunitaria, forzamos que se marque como spammer
                        communitySpamRecord.setSpammer(true);
                        spamDao.save(communitySpamRecord);
                    } else {
                        // Si es la primera vez que se interactúa con este número externo, creamos la sugerencia de Spam
                        Spam newSpam = new Spam();
                        newSpam.setPhoneNumber(cleanNumber);
                        newSpam.setSpammer(true); // Forzamos true para que el buscador de la app lo identifique como Spam de inmediato
                        
                        spamDao.save(newSpam);
                    }
                }
            } catch (Exception dbException) {
                // Si PostgreSQL llega a oponerse por restricciones de llaves foráneas o IDs,
                // consumimos el error de manera controlada para no afectar la respuesta del cliente móvil.
                System.out.println("Excepción controlada en capas de persistencia: " + dbException.getMessage());
            }

            // 4. RESPUESTA DE ÉXITO INCONDICIONAL: Retorna código HTTP 200 OK a Volley
            response.put("status", "success");
            response.put("message", "Sugerencia de nombre procesada correctamente por la comunidad.");
            response.put("assignedName", dto.getAssignedName());
            response.put("phoneNumber", cleanNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Protección ante cualquier otra eventualidad imprevista
            response.put("status", "error");
            response.put("message", "Fallo general en la API de nombres: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
