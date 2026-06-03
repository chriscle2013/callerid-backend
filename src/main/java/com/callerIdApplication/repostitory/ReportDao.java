package com.callerIdApplication.repostitory;

import com.callerIdApplication.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportDao extends JpaRepository<Report, Long> {
    List<Report> findByPhoneNumber(String phoneNumber);
    List<Report> findBySpammerTrue();
}
