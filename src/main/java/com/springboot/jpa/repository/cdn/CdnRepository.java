package com.springboot.jpa.repository.cdn;

import com.springboot.cdn.service.cdn.dto.CdnSearchRequest;
import com.springboot.jpa.domain.Cdn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdnRepository extends JpaRepository<Cdn, Long>, CdnJpaSpecification {
    default Page<Cdn> getFiles(CdnSearchRequest request, Pageable pageable) {
        Specification<Cdn> spec = CdnJpaSpecification.build(request);
        return findAll(spec, pageable);
    }
}