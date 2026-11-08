package com.mimsinfashion.modules.content.domain.entity;

import com.mimsinfashion.common.domain.entity.BaseEntity;
import com.mimsinfashion.modules.content.domain.enums.LandingSectionType;
import jakarta.persistence.*;

@Entity
@Table(
    name = "landing_sections",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_landing_section_key",
            columnNames = "section_key"
        )
    },
    indexes = {
        @Index(name = "idx_landing_section_type", columnList = "section_type"),
        @Index(name = "idx_landing_section_active", columnList = "active"),
        @Index(name = "idx_landing_section_order", columnList = "sort_order")
    }
)
public class LandingSection extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false, length = 50)
    private LandingSectionType sectionType;

    @Column(name = "section_key", nullable = false, length = 100)
    private String sectionKey;

    @Column(length = 200)
    private String eyebrow;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(length = 250)
    private String subtitle;

    @Column(length = 3000)
    private String description;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "button_text", length = 100)
    private String buttonText;

    @Column(name = "button_url", length = 1000)
    private String buttonUrl;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean published = false;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    protected LandingSection() {
    }

    public static LandingSection create(
            LandingSectionType sectionType,
            String sectionKey,
            String title) {

        LandingSection section = new LandingSection();
        section.setSectionType(sectionType);
        section.setSectionKey(sectionKey);
        section.setTitle(title);
        return section;
    }

    public LandingSectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(LandingSectionType sectionType) {
        this.sectionType = sectionType;
    }

    public String getSectionKey() {
        return sectionKey;
    }

    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey == null
            ? null
            : sectionKey.trim().toLowerCase();
    }

    public String getEyebrow() {
        return eyebrow;
    }

    public void setEyebrow(String eyebrow) {
        this.eyebrow = eyebrow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonUrl() {
        return buttonUrl;
    }

    public void setButtonUrl(String buttonUrl) {
        this.buttonUrl = buttonUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
