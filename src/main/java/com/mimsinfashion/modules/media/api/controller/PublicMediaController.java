package com.mimsinfashion.modules.media.api.controller;

import com.mimsinfashion.common.error.ResourceNotFoundException;
import com.mimsinfashion.modules.media.application.service.MediaAssetService;
import com.mimsinfashion.modules.media.domain.entity.MediaAsset;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/media")
public class PublicMediaController {

    private static final Pattern SAFE_FILENAME =
        Pattern.compile("^[a-f0-9-]{36}\\.(jpg|png|gif)$");

    private final MediaAssetService mediaAssetService;

    public PublicMediaController(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @GetMapping("/{storedFilename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String storedFilename) throws IOException {

        if (!SAFE_FILENAME.matcher(storedFilename).matches()) {
            return ResponseEntity.notFound().build();
        }

        MediaAsset asset;
        try {
            asset = mediaAssetService.getActiveMediaByStoredFilename(
                storedFilename
            );
        } catch (ResourceNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }

        Path imagePath = Path.of(asset.getStoragePath()).toAbsolutePath().normalize();

        if (!imagePath.getFileName().toString().equals(storedFilename)
                || !Files.isRegularFile(imagePath)
                || !Files.isReadable(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(imagePath);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(asset.getMimeType()))
            .contentLength(Files.size(imagePath))
            .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + storedFilename + "\"")
            .body(resource);
    }
}
