package com.callerIdApplication.repostitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.callerIdApplication.entity.CurrentUserSession;

@Repository
public interface SessionDao extends JpaRepository<CurrentUserSession, Integer> {
    
    // Este método ya debería existir (para buscar por UUID)
    CurrentUserSession findByUuid(String uuid);
    
    // 👇 ESTE ES EL MÉTODO NUEVO QUE DEBES AGREGAR (si no existe)
    CurrentUserSession findByUserId(Integer userId);
}
