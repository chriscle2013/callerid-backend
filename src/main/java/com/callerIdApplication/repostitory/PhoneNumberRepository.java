package com.callerIdApplication.repostitory;

import com.velo.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    // Permite buscar por el número de teléfono
    Optional<PhoneNumber> findByNumber(String number);
}
