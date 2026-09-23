package com.prem.fileapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

@Service
public class FileStorageService {
    private final Path storageLocation;

    public FileStorageService(@Value("${file.storage.location:./uploads}") String location) {
        storageLocation = Paths.get(location).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public void store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file.");
        }

        String name = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename());

        if (name.isBlank()) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        Path target = storageLocation.resolve(name).normalize();
        if (!target.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid file path.");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file.", e);
        }
    }

    public Resource load(String fileName) {
        try {
            Path file = resolveSafe(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found: " + fileName);
            }
            return resource;
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + fileName, e);
        }
    }

    public List<String> list() {
        try (var stream = Files.list(storageLocation)) {
            return stream.filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list files.", e);
        }
    }

    public void delete(String fileName) {
        try {
            if (!Files.deleteIfExists(resolveSafe(fileName))) {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + fileName, e);
        }
    }

    private Path resolveSafe(String fileName) {
        String name = StringUtils.cleanPath(fileName == null ? "" : fileName);
        Path file = storageLocation.resolve(name).normalize();
        if (name.isBlank() || !file.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid file path.");
        }
        return file;
    }
}
