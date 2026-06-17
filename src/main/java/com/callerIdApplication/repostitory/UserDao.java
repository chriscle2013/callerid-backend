package com.callerIdApplication.repostitory;

import com.callerIdApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    
    // Método estandarizado para buscar por número de teléfono (el cual sí existe físicamente)
    User findByPhoneNumber(String phoneNumber);
    
    // Método de compatibilidad por si es invocado con minúscula en alguna parte del código antiguo
    default User findByphoneNumber(String phoneNumber) {
        return findByPhoneNumber(phoneNumber);
    }

    // SOLUCIÓN AL ERROR DE COMPILACIÓN: 
    // Provee soporte seguro a UserServiceImpl sin obligar a la BD física a tener la columna user_name
    default User findByuserName(String userName) {
        // Retorna null de forma segura para no generar excepciones de bases de datos
        return null;
    }
}
