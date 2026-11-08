package com.mimsinfashion.modules.content.api.controller;

import com.mimsinfashion.modules.content.api.dto.LandingSectionRequest;
import com.mimsinfashion.modules.content.api.dto.LandingSectionResponse;
import com.mimsinfashion.modules.content.application.service.LandingSectionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content/landing-sections")
@PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'CONTENT_MANAGE')")
public class AdminLandingSectionController {

    private final LandingSectionService landingSectionService;

    public AdminLandingSectionController(
            LandingSectionService landingSectionService) {
        this.landingSectionService = landingSectionService;
    }

    @GetMapping
    public List<LandingSectionResponse> getAllSections() {
        return landingSectionService.getAllSections();
    }

    @GetMapping("/{id}")
    public LandingSectionResponse getSection(@PathVariable Long id) {
        return landingSectionService.getSectionById(id);
    }

    @PostMapping
    public LandingSectionResponse createSection(
            @Valid @RequestBody LandingSectionRequest request) {
        return landingSectionService.createSection(request);
    }

    @PutMapping("/{id}")
    public LandingSectionResponse updateSection(
            @PathVariable Long id,
            @Valid @RequestBody LandingSectionRequest request) {
        return landingSectionService.updateSection(id, request);
    }

    @PatchMapping("/{id}/publication")
    public LandingSectionResponse updatePublication(
            @PathVariable Long id,
            @RequestParam boolean published) {
        return landingSectionService.updatePublication(id, published);
    }

    @DeleteMapping("/{id}")
    public void deleteSection(@PathVariable Long id) {
        landingSectionService.deleteSection(id);
    }
}
