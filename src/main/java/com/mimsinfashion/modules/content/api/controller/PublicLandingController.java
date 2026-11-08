package com.mimsinfashion.modules.content.api.controller;

import com.mimsinfashion.modules.content.api.dto.LandingSectionResponse;
import com.mimsinfashion.modules.content.application.service.LandingSectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/landing")
public class PublicLandingController {

    private final LandingSectionService landingSectionService;

    public PublicLandingController(
            LandingSectionService landingSectionService) {
        this.landingSectionService = landingSectionService;
    }

    @GetMapping("/sections")
    public List<LandingSectionResponse> getLandingSections() {
        return landingSectionService.getPublishedSections();
    }
}
