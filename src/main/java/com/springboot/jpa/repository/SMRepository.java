package com.springboot.jpa.repository;

import com.springboot.jpa.domain.SM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SMRepository extends JpaRepository<SM, Long> {
    Optional<SM> findByTemplateIdAndMasterIdOrderByIdDesc(String id, long masterId);
}
