package com.callerIdApplication.services;

import com.callerIdApplication.entity.CurrentUserSession;
import com.callerIdApplication.entity.LoginDTO;
import com.callerIdApplication.entity.User;
import com.callerIdApplication.exceptions.LoginException;
import com.callerIdApplication.repostitory.SessionDao;
import com.callerIdApplication.repostitory.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao cDao;

    @Autowired
    private SessionDao sDao;

    @Override
    public String logIntoAccount(LoginDTO dto) throws LoginException {
        User existingCustomer = cDao.findByphoneNumber(dto.getPhoneNumber());

        if (existingCustomer == null) {
            throw new LoginException("Por favor ingresa un número móvil válido");
        }

        // Validación estricta de la contraseña
        if (!existingCustomer.getPassword().equals(dto.getPassword())) {
            throw new LoginException("Contraseña incorrecta");
        }

        String uuid = existingCustomer.getUuid();

        // LÓGICA ESTABLE: Si el usuario NO tiene UUID en la tabla principal, se le genera uno permanente de 6 caracteres
        if (uuid == null || uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString().substring(0, 6);
            existingCustomer.setUuid(uuid);
            cDao.save(existingCustomer); // Guardado definitivo e inmutable en app_user
        }

        // Eliminar cualquier sesión activa previa en el sistema para evitar duplicados
        CurrentUserSession existingSession = sDao.findByUserId(existingCustomer.getUserId());
        if (existingSession != null) {
            sDao.delete(existingSession);
        }

        // Crear y guardar la sesión activa vinculada al UUID FIJO del usuario
        CurrentUserSession currentUserSession = new CurrentUserSession();
        currentUserSession.setUserId(existingCustomer.getUserId());
        currentUserSession.setUuid(uuid); // Reutiliza la misma llave estática
        currentUserSession.setLocalDateTime(LocalDateTime.now());

        sDao.save(currentUserSession);

        return uuid;
    }

    @Override
    public String logOutFromAccount(String key) throws LoginException {
        CurrentUserSession validStatusSession = sDao.findByUuid(key);

        if (validStatusSession == null) {
            throw new LoginException("Usuario no logueado con esta clave (Key)");
        }

        sDao.delete(validStatusSession);
        return "Sesión cerrada correctamente!";
    }
}
