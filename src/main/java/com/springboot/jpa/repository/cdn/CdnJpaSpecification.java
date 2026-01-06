package com.springboot.jpa.repository.cdn;

import com.springboot.cdn.enums.EDomain;
import com.springboot.cdn.enums.EEnv;
import com.springboot.cdn.service.cdn.dto.CdnSearchRequest;
import com.springboot.jpa.domain.Cdn;
import com.springboot.lib.enums.EStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Timestamp;

public interface CdnJpaSpecification extends JpaSpecificationExecutor<Cdn> {
    static Specification<Cdn> env(EEnv env) {
        return (root, query, cb) ->
                env == null ? cb.conjunction() :
                        cb.equal(root.get("env"), env);
    }

    static Specification<Cdn> status(EStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() :
                        cb.equal(root.get("status"), status);
    }

    static Specification<Cdn> group(String group) {
        return (root, query, cb) -> {
            if (group == null || group.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.upper(root.get("group")), "%" + group.toUpperCase() + "%", '\\');
        };
    }

    static Specification<Cdn> domain(EDomain domain) {
        return (root, query, cb) ->
                domain == null ? cb.conjunction() :
                        cb.equal(root.get("domain"), domain);
    }

    static Specification<Cdn> createdDateBetween(Timestamp from, Timestamp to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return cb.conjunction();
            }
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    static Specification<Cdn> lastModifiedDateBetween(Timestamp from, Timestamp to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return cb.conjunction();
            }
            if (from != null && to != null) {
                return cb.between(root.get("updatedAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("updatedAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("updatedAt"), to);
        };
    }

    static Specification<Cdn> build(CdnSearchRequest req) {
        if (req == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        Specification<Cdn> spec = null;

        if (req.getEnv() != null) {
            spec = env(req.getEnv());
        }
        if (req.getStatus() != null) {
            spec = spec == null ? status(req.getStatus()) : spec.and(status(req.getStatus()));
        }
        if (req.getGroup() != null) {
            spec = spec == null ? group(req.getGroup()) : spec.and(group(req.getGroup()));
        }
        if (req.getDomain() != null) {
            spec = spec == null ? domain(req.getDomain()) : spec.and(domain(req.getDomain()));
        }
        if (req.getCreatedFrom() != null || req.getCreatedTo() != null) {
            spec = spec == null ? createdDateBetween(req.getCreatedFrom(), req.getCreatedTo())
                    : spec.and(createdDateBetween(req.getCreatedFrom(), req.getCreatedTo()));
        }
        if (req.getLastModifiedFrom() != null || req.getLastModifiedTo() != null) {
            spec = spec == null ? lastModifiedDateBetween(req.getLastModifiedFrom(), req.getLastModifiedTo())
                    : spec.and(lastModifiedDateBetween(req.getLastModifiedFrom(), req.getLastModifiedTo()));
        }

        return spec;
    }
}
