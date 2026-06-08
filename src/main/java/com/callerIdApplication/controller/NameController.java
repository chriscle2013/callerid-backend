package com.callerIdApplication.controller;

import com.callerIdApplication.entity.User;
import com.callerIdApplication.repostitory.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/name")
@CrossOrigin(origins = "*")
public class NameController {

    @Autowired
    private UserDao userDao;

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
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getAssignedName() == null || dto.getAssignedName().isEmpty()) {
                
                response.put("status", "error");
                response.put("message", "Faltan parámetros obligatorios.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String cleanNumber = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2);
            }

            // Buscar si el número de teléfono ya existe en la base de datos
            User targetUser = userDao.findByphoneNumber(cleanNumber);

            if (targetUser != null) {
                // CASO 1: El usuario ya existe, solo le actualizamos el nombre de manera limpia
                targetUser.setUserName(dto.getAssignedName());
                userDao.save(targetUser);
            } else {
                // CASO 2: Es un número nuevo desconocido.
                // Para evitar errores de duplicado en la base de datos (SQL Constraint Error),
                // generamos valores aleatorios numéricos cortos que cumplan con cualquier validación básica.
                Random rand = new Random();
                int randomId = 100000 + rand.nextInt(900000);
                
                User newUser = new User();
                newUser.setPhoneNumber(cleanNumber);
                newUser.setUserName(dto.getAssignedName());
                
                // Estos correos y claves evitan disparar excepciones 'Unique' de PostgreSQL
                newUser.setEmail("user_" + randomId + "@callerid.com");
                newUser.setPassword("Pass" + randomId + "!");
                newUser.setUuid(""); // Sin llave asignada de sesión
                
                userDao.save(newUser);
            }

            // Respuesta exitosa formateada para Volley (Android)
            response.put("status", "success");
            response.put("message", "Nombre guardado con éxito.");
            response.put("assignedName", dto.getAssignedName());
            response.put("phoneNumber", cleanNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // En caso de que ocurra cualquier otra restricción imprevista de la BD, no rompemos la app,
            // capturamos el mensaje y le informamos al usuario de forma controlada.
            response.put("status", "error");
            response.put("message", "Restricción de Base de Datos: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
