package com.prem.fileapp.controller;

import com.prem.fileapp.model.FileMetadata;
import com.prem.fileapp.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService storage;

    public FileController(FileStorageService storage) {
        this.storage = storage;
    }

    @GetMapping
    public List<FileMetadata> list(@RequestParam(required = false) String search) {
        return storage.list(search);
    }

    @PostMapping
    public ResponseEntity<FileMetadata> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storage.store(file));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        FileMetadata metadata = storage.find(id);
        Resource resource = storage.load(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .contentLength(metadata.getSize())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(metadata.getOriginalFileName())
                                .build()
                                .toString()
                )
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        storage.delete(id);
        return ResponseEntity.ok(Map.of("message", "File deleted successfully."));
    }
}
