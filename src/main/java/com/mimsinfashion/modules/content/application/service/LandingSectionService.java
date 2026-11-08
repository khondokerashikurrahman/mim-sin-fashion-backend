package com.mimsinfashion.modules.content.application.service;

import com.mimsinfashion.common.error.ResourceNotFoundException;
import com.mimsinfashion.modules.content.api.dto.LandingSectionRequest;
import com.mimsinfashion.modules.content.api.dto.LandingSectionResponse;
import com.mimsinfashion.modules.content.api.mapper.LandingSectionMapper;
import com.mimsinfashion.modules.content.domain.entity.LandingSection;
import com.mimsinfashion.modules.content.infrastructure.persistence.LandingSectionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LandingSectionService {

    private final LandingSectionJpaRepository repository;
    private final LandingSectionMapper mapper;

    public LandingSectionService(
            LandingSectionJpaRepository repository,
            LandingSectionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<LandingSectionResponse> getAllSections() {
        return repository.findAllByOrderBySortOrderAsc()
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<LandingSectionResponse> getPublishedSections() {
        return repository.findByActiveTrueAndPublishedTrueOrderBySortOrderAsc()
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public LandingSectionResponse getSectionById(Long id) {
        return mapper.toResponse(findSection(id));
    }

    public LandingSectionResponse createSection(LandingSectionRequest request) {
        String sectionKey = normalizeKey(request.sectionKey());

        if (repository.existsBySectionKey(sectionKey)) {
            throw new IllegalArgumentException(
                "Landing section key already exists: " + sectionKey
            );
        }

        LandingSection section = LandingSection.create(
            request.sectionType(),
            sectionKey,
            request.title()
        );

        applyRequest(section, request);

        return mapper.toResponse(repository.save(section));
    }

    public LandingSectionResponse updateSection(
            Long id,
            LandingSectionRequest request) {

        LandingSection section = findSection(id);
        String sectionKey = normalizeKey(request.sectionKey());

        repository.findBySectionKey(sectionKey)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new IllegalArgumentException(
                    "Landing section key already exists: " + sectionKey
                );
            });

        applyRequest(section, request);

        return mapper.toResponse(repository.save(section));
    }

    public void deleteSection(Long id) {
        LandingSection section = findSection(id);
        repository.delete(section);
    }

    public LandingSectionResponse updatePublication(
            Long id,
            boolean published) {

        LandingSection section = findSection(id);
        section.setPublished(published);

        return mapper.toResponse(repository.save(section));
    }

    private LandingSection findSection(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Landing section not found with id: " + id
            ));
    }

    private void applyRequest(
            LandingSection section,
            LandingSectionRequest request) {

        section.setSectionType(request.sectionType());
        section.setSectionKey(request.sectionKey());
        section.setTitle(request.title());
        section.setEyebrow(request.eyebrow());
        section.setSubtitle(request.subtitle());
        section.setDescription(request.description());
        section.setImageUrl(request.imageUrl());
        section.setButtonText(request.buttonText());
        section.setButtonUrl(request.buttonUrl());
        section.setActive(request.active());
        section.setPublished(request.published());
        section.setSortOrder(request.sortOrder());
    }

    private String normalizeKey(String sectionKey) {
        return sectionKey.trim().toLowerCase();
    }
}
