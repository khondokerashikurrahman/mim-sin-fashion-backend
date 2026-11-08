package com.mimsinfashion.modules.access.infrastructure.persistence;

import com.mimsinfashion.modules.access.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
    boolean existsByCode(String code);
}
