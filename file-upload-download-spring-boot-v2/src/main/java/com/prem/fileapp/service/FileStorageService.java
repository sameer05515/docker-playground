package com.prem.fileapp.service;

import com.prem.fileapp.model.FileMetadata;
import com.prem.fileapp.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageLocation;
    private final FileMetadataRepository repository;

    public FileStorageService(
            @Value("${file.storage.location:./uploads}") String location,
            FileMetadataRepository repository) {

        this.storageLocation = Paths.get(location).toAbsolutePath().normalize();
        this.repository = repository;

        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory.", e);
        }
    }

    public FileMetadata store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file.");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename());

        if (originalName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        // UUID avoids collisions and prevents using the user supplied name as a disk path.
        String storedName = UUID.randomUUID() + "_" + originalName;
        Path target = storageLocation.resolve(storedName).normalize();

        if (!target.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid file path.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file.", e);
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        FileMetadata metadata = new FileMetadata(
                originalName,
                storedName,
                contentType,
                file.getSize(),
                Instant.now()
        );

        return repository.save(metadata);
    }

    public List<FileMetadata> list() {
        return repository.findAll();
    }

    public FileMetadata find(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found: " + id));
    }

    public Resource loadResource(String id) {
        FileMetadata metadata = find(id);

        try {
            Path file = storageLocation.resolve(metadata.getStoredFileName()).normalize();

            if (!file.startsWith(storageLocation)) {
                throw new IllegalArgumentException("Invalid file path.");
            }

            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Physical file not found.");
            }

            return resource;
        } catch (IOException e) {
            throw new RuntimeException("Could not read file.", e);
        }
    }

    public void delete(String id) {
        FileMetadata metadata = find(id);

        try {
            Path file = storageLocation.resolve(metadata.getStoredFileName()).normalize();

            if (file.startsWith(storageLocation)) {
                Files.deleteIfExists(file);
            }

            repository.deleteById(id);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file.", e);
        }
    }
}
