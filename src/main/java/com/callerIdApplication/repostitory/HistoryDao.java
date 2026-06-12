package com.callerIdApplication.repostitory;

import com.callerIdApplication.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoryDao extends JpaRepository<SearchHistory, Long> {
    
    // Devuelve el historial de un usuario específico ordenado del más reciente al más antiguo
    List<SearchHistory> findByUserPhoneNumberOrderBySearchDateDesc(String userPhoneNumber);
}
