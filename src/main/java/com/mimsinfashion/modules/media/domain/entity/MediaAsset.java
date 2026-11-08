package com.mimsinfashion.modules.media.domain.entity;

import com.mimsinfashion.common.domain.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(
    name = "media_assets",
    indexes = {
        @Index(name = "idx_media_asset_active", columnList = "active"),
        @Index(name = "idx_media_asset_mime_type", columnList = "mime_type"),
        @Index(name = "idx_media_asset_created_at", columnList = "created_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_media_asset_stored_filename",
            columnNames = "stored_filename"
        )
    }
)
public class MediaAsset extends BaseEntity {

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    @Column(name = "storage_path", nullable = false, length = 1000)
    private String storagePath;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "image_width")
    private Integer imageWidth;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "alt_text", length = 500)
    private String altText;

    @Column(length = 1000)
    private String description;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

    @Column(nullable = false)
    private boolean active = true;

    protected MediaAsset() {
    }

    public static MediaAsset create(
            String originalFilename,
            String storedFilename,
            String storagePath,
            String imageUrl,
            String mimeType,
            long fileSize,
            Integer imageWidth,
            Integer imageHeight,
            Long uploadedBy) {

        MediaAsset asset = new MediaAsset();
        asset.setOriginalFilename(originalFilename);
        asset.setStoredFilename(storedFilename);
        asset.setStoragePath(storagePath);
        asset.setImageUrl(imageUrl);
        asset.setMimeType(mimeType);
        asset.setFileSize(fileSize);
        asset.setImageWidth(imageWidth);
        asset.setImageHeight(imageHeight);
        asset.setUploadedBy(uploadedBy);
        return asset;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
