package com.callerIdApplication.repostitory;

import com.callerIdApplication.entity.AssignedName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignedNameDao extends JpaRepository<AssignedName, Long> {
    List<AssignedName> findByPhoneNumber(String phoneNumber);
    AssignedName findTopByPhoneNumberOrderByVoteCountDesc(String phoneNumber);
}
