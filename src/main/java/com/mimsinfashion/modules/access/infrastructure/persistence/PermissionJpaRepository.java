package com.mimsinfashion.modules.access.infrastructure.persistence;

import com.mimsinfashion.modules.access.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
    boolean existsByCode(String code);
}
