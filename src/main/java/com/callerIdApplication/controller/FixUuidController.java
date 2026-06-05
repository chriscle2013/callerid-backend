package com.callerIdApplication.controller;

import com.callerIdApplication.entity.User;
import com.callerIdApplication.repostitory.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FixUuidController {

    @Autowired
    private UserDao userDao;

    @GetMapping("/fix-uuid/{phoneNumber}")
    public String fixUuid(@PathVariable String phoneNumber) {
        try {
            User user = userDao.findByphoneNumber(phoneNumber);
            if (user != null) {
                if (user.getUuid() == null || user.getUuid().isEmpty()) {
                    String newUuid = java.util.UUID.randomUUID().toString().substring(0, 8);
                    user.setUuid(newUuid);
                    userDao.save(user);
                    return "✅ UUID asignado: " + newUuid + " para el número " + phoneNumber;
                } else {
                    return "ℹ️ El usuario ya tiene UUID: " + user.getUuid();
                }
            }
            return "❌ Usuario no encontrado con número: " + phoneNumber;
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}
