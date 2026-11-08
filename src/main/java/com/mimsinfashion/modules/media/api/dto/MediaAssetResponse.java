package com.mimsinfashion.modules.media.api.dto;

import com.mimsinfashion.modules.media.domain.entity.MediaAsset;

import java.time.Instant;

public record MediaAssetResponse(
    Long id,
    String originalFilename,
    String imageUrl,
    String mimeType,
    long fileSize,
    Integer imageWidth,
    Integer imageHeight,
    String altText,
    String description,
    Long uploadedBy,
    Instant createdAt
) {
    public static MediaAssetResponse from(MediaAsset asset) {
        return new MediaAssetResponse(
            asset.getId(),
            asset.getOriginalFilename(),
            asset.getImageUrl(),
            asset.getMimeType(),
            asset.getFileSize(),
            asset.getImageWidth(),
            asset.getImageHeight(),
            asset.getAltText(),
            asset.getDescription(),
            asset.getUploadedBy(),
            asset.getCreatedAt()
        );
    }
}
