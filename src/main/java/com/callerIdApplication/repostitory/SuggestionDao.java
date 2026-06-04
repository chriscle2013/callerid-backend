package com.callerIdApplication.repostitory;

import com.callerIdApplication.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SuggestionDao extends JpaRepository<Suggestion, Long> {
    List<Suggestion> findByApprovedFalse();
    List<Suggestion> findByPhoneNumber(String phoneNumber);
}
