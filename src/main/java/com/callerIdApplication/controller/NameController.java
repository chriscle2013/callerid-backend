package com.callerIdApplication.controller;

import com.callerIdApplication.entity.User;
import com.callerIdApplication.repostitory.UserDao; // Asegúrate de que coincida con tu nombre (UserDao o cDao)
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

    // Clase DTO estática interna para mapear exactamente el JSON que envía Android
    public static class NameAssignmentDTO {
        private String phoneNumber;
        private String assignedName;
        private String assignedBy;

        // Métodos Getter y Setter obligatorios para que Spring procese el JSON
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
            // 1. Validar que los campos requeridos no vengan vacíos
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getAssignedName() == null || dto.getAssignedName().isEmpty()) {
                
                response.put("status", "error");
                response.put("message", "El número de teléfono y el nombre asignado son campos obligatorios.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 2. Limpiar el número recibido (eliminar espacios o caracteres extraños)
            String cleanNumber = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2); // Quitar prefijo de país si aplica
            }

            // 3. Buscar si el número ya existe en tu tabla de usuarios (app_user) o de directorio
            User targetUser = userDao.findByphoneNumber(cleanNumber);

            if (targetUser != null) {
                // Si el número ya existe, actualizamos su nombre con el sugerido por la comunidad
                targetUser.setUserName(dto.getAssignedName());
                userDao.save(targetUser);
            } else {
                // Si el número no existe en el sistema, creamos un registro nuevo básico para el directorio
                User newUser = new User();
                newUser.setPhoneNumber(cleanNumber);
                newUser.setUserName(dto.getAssignedName());
                newUser.setEmail("comunidad@callerid.com"); // Email por defecto
                newUser.setPassword("no_password_assigned"); // Contraseña segura por defecto
                newUser.setUuid(""); // Sin sesión activa
                userDao.save(newUser);
            }

            // 4. Responder con éxito a la aplicación Android en formato JSON
            response.put("status", "success");
            response.put("message", "Nombre asignado correctamente en la comunidad.");
            response.put("assignedName", dto.getAssignedName());
            response.put("phoneNumber", cleanNumber);
            
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error interno en el servidor: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
