package com.mimsinfashion.modules.media.infrastructure.persistence;

import com.mimsinfashion.modules.media.domain.entity.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaAssetJpaRepository extends JpaRepository<MediaAsset, Long> {

    Optional<MediaAsset> findByStoredFilename(String storedFilename);

    Optional<MediaAsset> findByStoredFilenameAndActiveTrue(String storedFilename);

    boolean existsByStoredFilename(String storedFilename);

    List<MediaAsset> findAllByActiveTrueOrderByCreatedAtDesc();

    List<MediaAsset> findAllByActiveTrueAndMimeTypeStartingWithOrderByCreatedAtDesc(
        String mimeTypePrefix
    );
}
