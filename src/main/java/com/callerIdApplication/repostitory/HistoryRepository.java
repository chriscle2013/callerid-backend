package com.callerIdApplication.repository;

import com.callerIdApplication.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    // Buscar historial de un usuario específico ordenado por fecha reciente
    List<History> findByUserPhoneNumberOrderBySearchDateDesc(String userPhoneNumber);
}
