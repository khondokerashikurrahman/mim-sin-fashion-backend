package com.mimsinfashion.modules.content.api.mapper;

import com.mimsinfashion.modules.content.api.dto.LandingSectionResponse;
import com.mimsinfashion.modules.content.domain.entity.LandingSection;
import org.springframework.stereotype.Component;

@Component
public class LandingSectionMapper {

    public LandingSectionResponse toResponse(LandingSection section) {
        return new LandingSectionResponse(
            section.getId(),
            section.getSectionType(),
            section.getSectionKey(),
            section.getEyebrow(),
            section.getTitle(),
            section.getSubtitle(),
            section.getDescription(),
            section.getImageUrl(),
            section.getButtonText(),
            section.getButtonUrl(),
            section.isActive(),
            section.isPublished(),
            section.getSortOrder(),
            section.getCreatedAt(),
            section.getUpdatedAt()
        );
    }
}
