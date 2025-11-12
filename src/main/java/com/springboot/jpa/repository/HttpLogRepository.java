package com.springboot.jpa.repository;

import com.springboot.jpa.domain.HttpLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpLogRepository extends JpaRepository<HttpLog, Long> {
}
