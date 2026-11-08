package com.mimsinfashion.modules.content.api.dto;

import com.mimsinfashion.modules.content.domain.enums.LandingSectionType;

import java.time.Instant;

public record LandingSectionResponse(
    Long id,
    LandingSectionType sectionType,
    String sectionKey,
    String eyebrow,
    String title,
    String subtitle,
    String description,
    String imageUrl,
    String buttonText,
    String buttonUrl,
    boolean active,
    boolean published,
    int sortOrder,
    Instant createdAt,
    Instant updatedAt
) {
}
