package com.callerIdApplication.controller;

import com.callerIdApplication.entity.User;
import com.callerIdApplication.entity.CurrentUserSession;
import com.callerIdApplication.repostitory.UserDao;
import com.callerIdApplication.repostitory.SessionDao;
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
    private SessionDao sessionDao;

    // Clase contenedora (DTO) idéntica al formato JSON enviado por la app Android
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
            // 1. Validar campos requeridos enviados por el cliente móvil
            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getAssignedName() == null || dto.getAssignedName().isEmpty()) {
                
                response.put("status", "error");
                response.put("message", "Faltan parámetros: phoneNumber o assignedName");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 2. Homologar y limpiar el número telefónico recibido
            String cleanNumber = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanNumber.length() == 12 && cleanNumber.startsWith("57")) {
                cleanNumber = cleanNumber.substring(2);
            }

            // 3. Buscar si el registro ya existe en el sistema
            User targetUser = userDao.findByphoneNumber(cleanNumber);

            if (targetUser != null) {
                // Si el número ya existe, actualizamos su nombre en la base de datos
                targetUser.setUserName(dto.getAssignedName());
                userDao.save(targetUser);
            } else {
                // Si el número es completamente nuevo, creamos un registro seguro en la base de datos
                User newUser = new User();
                newUser.setPhoneNumber(cleanNumber);
                newUser.setUserName(dto.getAssignedName());
                newUser.setEmail(cleanNumber + "@callerid-comunidad.local"); // Correo dinámico autogenerado válido
                newUser.setPassword("Pass_Community_" + cleanNumber); // Contraseña por defecto válida para evitar fallos de persistencia
                newUser.setUuid(""); // Sin sesión inicializada
                userDao.save(newUser);
            }

            // 4. Retornar respuesta exitosa estructurada en formato JSON
            response.put("status", "success");
            response.put("message", "Nombre asignado exitosamente en el sistema comunitario.");
            response.put("assignedName", dto.getAssignedName());
            response.put("phoneNumber", cleanNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Fallo crítico en el servidor: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
