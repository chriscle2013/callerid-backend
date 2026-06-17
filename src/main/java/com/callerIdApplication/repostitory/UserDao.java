package com.callerIdApplication.repostitory;

import com.callerIdApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    
    // Método estandarizado para mapear perfectamente con la variable 'phoneNumber' de la entidad
    User findByPhoneNumber(String phoneNumber);
    
    // Método alternativo de compatibilidad temporal por si alguna otra clase del proyecto lo invoca
    default User findByphoneNumber(String phoneNumber) {
        return findByPhoneNumber(phoneNumber);
    }
}
