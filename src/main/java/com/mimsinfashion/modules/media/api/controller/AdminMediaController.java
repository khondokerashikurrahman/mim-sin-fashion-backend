package com.mimsinfashion.modules.media.api.controller;

import com.mimsinfashion.modules.media.api.dto.MediaAssetResponse;
import com.mimsinfashion.modules.media.api.dto.MediaMetadataRequest;
import com.mimsinfashion.modules.media.application.service.MediaAssetService;
import com.mimsinfashion.modules.media.domain.entity.MediaAsset;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/media")
@PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'MEDIA_MANAGE')")
public class AdminMediaController {

    private final MediaAssetService mediaAssetService;

    public AdminMediaController(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaAssetResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String altText,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal Jwt jwt) {

        Long uploadedBy = Long.valueOf(jwt.getSubject());

        MediaAsset asset = mediaAssetService.upload(
            file,
            altText,
            description,
            uploadedBy
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(MediaAssetResponse.from(asset));
    }

    @GetMapping
    public ResponseEntity<List<MediaAssetResponse>> getAllMedia() {
        List<MediaAssetResponse> media = mediaAssetService.getAllActiveMedia()
            .stream()
            .map(MediaAssetResponse::from)
            .toList();

        return ResponseEntity.ok(media);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaAssetResponse> getMediaById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            MediaAssetResponse.from(mediaAssetService.getMediaById(id))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MediaAssetResponse> updateMetadata(
            @PathVariable Long id,
            @Valid @RequestBody MediaMetadataRequest request) {

        MediaAsset updated = mediaAssetService.updateMetadata(
            id,
            request.altText(),
            request.description()
        );

        return ResponseEntity.ok(MediaAssetResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        mediaAssetService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
}
