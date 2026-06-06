package com.callerIdApplication.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 👈 Asegura la persistencia en la BD

import com.callerIdApplication.entity.CurrentUserSession;
import com.callerIdApplication.entity.LoginDTO;
import com.callerIdApplication.entity.User;
import com.callerIdApplication.exceptions.LoginException;
import com.callerIdApplication.repostitory.SessionDao;
import com.callerIdApplication.repostitory.UserDao;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao cDao;

    @Autowired
    private SessionDao sDao;

    @Override
    @Transactional // 👈 Aplica commits automáticos en PostgreSQL al finalizar el método
    public String logIntoAccount(LoginDTO dto) throws LoginException {
        User existingCustomer = cDao.findByphoneNumber(dto.getPhoneNumber());

        if (existingCustomer == null) {
            throw new LoginException("Please Enter a valid mobile number");
        }
        
        if (!existingCustomer.getPassword().equals(dto.getPassword())) {
            throw new LoginException("Please Enter a valid password");
        }

        // Recuperamos el UUID actual del usuario en la base de datos
        String uuid = existingCustomer.getUuid();
        
        // Si el UUID está vacío, es null o tiene espacios en blanco, generamos uno ESTÁTICO
        if (uuid == null || uuid.trim().isEmpty()) {
            uuid = java.util.UUID.randomUUID().toString().substring(0, 8); // Longitud estándar de 8 caracteres
            existingCustomer.setUuid(uuid);
            cDao.saveAndFlush(existingCustomer); // 👈 Sincronización inmediata con PostgreSQL
        }

        // Gestión rigurosa de la sesión actual
        CurrentUserSession existingSession = sDao.findByUserId(existingCustomer.getUserId());
        if (existingSession != null) {
            sDao.delete(existingSession);
            sDao.flush(); // Validamos que se borre la sesión vieja antes de crear la nueva
        }

        CurrentUserSession currentUserSession = new CurrentUserSession();
        currentUserSession.setUserId(existingCustomer.getUserId());
        currentUserSession.setLocalDateTime(LocalDateTime.now());
        currentUserSession.setUuid(uuid); // Se asigna el UUID estático a la sesión activa
        
        sDao.save(currentUserSession);

        return uuid; // Retornamos el UUID que ahora quedará fijo
    }

    @Override
    @Transactional
    public String logOutFromAccount(String key) throws LoginException {
        CurrentUserSession validCustomerSession = sDao.findByUuid(key);
        if (validCustomerSession == null) {
            throw new LoginException("User Not Logged In with this number");
        }
        sDao.delete(validCustomerSession);
        return "Logged Out !";
    }
}
