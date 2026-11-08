package com.mimsinfashion.modules.media.application.service;

import com.mimsinfashion.common.error.ResourceNotFoundException;
import com.mimsinfashion.modules.media.domain.entity.MediaAsset;
import com.mimsinfashion.modules.media.infrastructure.persistence.MediaAssetJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class MediaAssetService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final long MAX_IMAGE_PIXELS = 40_000_000L;

    private final MediaAssetJpaRepository repository;
    private final Path storageDirectory;

    public MediaAssetService(
            MediaAssetJpaRepository repository,
            @Value("${app.media.storage-directory:uploads/media}") String storageDirectory) {
        this.repository = repository;
        this.storageDirectory = Paths.get(storageDirectory).toAbsolutePath().normalize();
    }

    public MediaAsset upload(MultipartFile file, String altText, String description, Long uploadedBy) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image to upload.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image size must not exceed 10 MB.");
        }

        ImageDetails details = inspectImage(file);
        String storedFilename = UUID.randomUUID() + "." + details.extension();
        Path destination = storageDirectory.resolve(storedFilename).normalize();

        if (!destination.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Invalid image destination.");
        }

        try {
            Files.createDirectories(storageDirectory);

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination);
            }

            MediaAsset asset = MediaAsset.create(
                sanitizeOriginalFilename(file.getOriginalFilename()),
                storedFilename,
                destination.toString(),
                "/media/" + storedFilename,
                details.mimeType(),
                file.getSize(),
                details.width(),
                details.height(),
                uploadedBy
            );

            asset.setAltText(cleanOptionalText(altText, 500));
            asset.setDescription(cleanOptionalText(description, 1000));

            return repository.save(asset);

        } catch (IOException | RuntimeException exception) {
            try {
                Files.deleteIfExists(destination);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw new IllegalStateException("Image upload failed.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<MediaAsset> getAllActiveMedia() {
        return repository.findAllByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public MediaAsset getMediaById(Long id) {
        return repository.findById(id)
            .filter(MediaAsset::isActive)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Media asset not found: " + id
            ));
    }

    @Transactional(readOnly = true)
    public MediaAsset getActiveMediaByStoredFilename(String storedFilename) {
        return repository.findByStoredFilenameAndActiveTrue(storedFilename)
            .orElseThrow(() -> new ResourceNotFoundException("Media asset not found."));
    }

    public MediaAsset updateMetadata(Long id, String altText, String description) {
        MediaAsset asset = getMediaById(id);
        asset.setAltText(cleanOptionalText(altText, 500));
        asset.setDescription(cleanOptionalText(description, 1000));
        return repository.save(asset);
    }

    public void deleteMedia(Long id) {
        MediaAsset asset = getMediaById(id);
        asset.setActive(false);
        repository.save(asset);
    }

    private ImageDetails inspectImage(MultipartFile file) {
        try (ImageInputStream input =
                 ImageIO.createImageInputStream(file.getInputStream())) {

            if (input == null) {
                throw new IllegalArgumentException("Invalid image file.");
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException(
                    "Only JPEG, PNG and GIF images are supported."
                );
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input, true, true);

                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0 || height <= 0 ||
                    (long) width * height > MAX_IMAGE_PIXELS) {
                    throw new IllegalArgumentException(
                        "Image dimensions are invalid or too large."
                    );
                }

                return switch (format) {
                    case "jpeg", "jpg" ->
                        new ImageDetails("image/jpeg", "jpg", width, height);
                    case "png" ->
                        new ImageDetails("image/png", "png", width, height);
                    case "gif" ->
                        new ImageDetails("image/gif", "gif", width, height);
                    default -> throw new IllegalArgumentException(
                        "Only JPEG, PNG and GIF images are supported."
                    );
                };

            } finally {
                reader.dispose();
            }

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                "Unable to read uploaded image.", exception
            );
        }
    }

    private String sanitizeOriginalFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "image";
        }

        String cleaned = Paths.get(
            filename.replace('\\', '/')
        ).getFileName().toString();

        cleaned = cleaned.replaceAll("[^A-Za-z0-9._ -]", "_").trim();

        if (cleaned.isBlank()) {
            return "image";
        }

        return cleaned.length() > 255
            ? cleaned.substring(cleaned.length() - 255)
            : cleaned;
    }

    private String cleanOptionalText(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String cleaned = value.trim();

        return cleaned.length() > maxLength
            ? cleaned.substring(0, maxLength)
            : cleaned;
    }

    private record ImageDetails(
        String mimeType,
        String extension,
        int width,
        int height
    ) {
    }
}
