package com.mimsinfashion.modules.content.api.dto;

import com.mimsinfashion.modules.content.domain.enums.LandingSectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LandingSectionRequest(

    @NotNull
    LandingSectionType sectionType,

    @NotBlank
    @Size(max = 100)
    String sectionKey,

    @NotBlank
    @Size(max = 250)
    String title,

    @Size(max = 200)
    String eyebrow,

    @Size(max = 250)
    String subtitle,

    @Size(max = 3000)
    String description,

    @Size(max = 1000)
    String imageUrl,

    @Size(max = 100)
    String buttonText,

    @Size(max = 1000)
    String buttonUrl,

    boolean active,

    boolean published,

    int sortOrder
) {
}
