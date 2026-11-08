package com.mimsinfashion.modules.content.infrastructure.persistence;

import com.mimsinfashion.modules.content.domain.entity.LandingSection;
import com.mimsinfashion.modules.content.domain.enums.LandingSectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LandingSectionJpaRepository
        extends JpaRepository<LandingSection, Long> {

    List<LandingSection> findAllByOrderBySortOrderAsc();

    List<LandingSection> findByActiveTrueAndPublishedTrueOrderBySortOrderAsc();

    Optional<LandingSection> findBySectionKey(String sectionKey);

    boolean existsBySectionKey(String sectionKey);

    List<LandingSection> findBySectionTypeAndActiveTrueAndPublishedTrueOrderBySortOrderAsc(
        LandingSectionType sectionType
    );
}
