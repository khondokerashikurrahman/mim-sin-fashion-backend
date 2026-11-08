package com.mimsinfashion.modules.media.api.dto;

import jakarta.validation.constraints.Size;

public record MediaMetadataRequest(
    @Size(max = 500, message = "Alt text cannot exceed 500 characters")
    String altText,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description
) {}
