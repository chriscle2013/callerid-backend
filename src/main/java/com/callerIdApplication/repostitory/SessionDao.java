package com.callerIdApplication.repostitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.callerIdApplication.entity.CurrentUserSession;

@Repository
public interface SessionDao extends JpaRepository<CurrentUserSession, Integer> {
    
    // Buscar por UUID (para logout)
    CurrentUserSession findByUuid(String uuid);
    
    // Buscar por UserId (para login)
    CurrentUserSession findByUserId(Integer userId);
}
