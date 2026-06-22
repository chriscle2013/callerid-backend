package com.callerIdApplication.repostitory;

import com.callerIdApplication.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoryDao extends JpaRepository<SearchHistory, Long> {
    
    // Buscar historial de un usuario específico (ya lo tenías)
    List<SearchHistory> findByUserPhoneNumberOrderBySearchDateDesc(String userPhoneNumber);

    // NUEVO: Buscar TODO el historial de la app ordenado por lo más reciente
    List<SearchHistory> findAllByOrderBySearchDateDesc();
}
