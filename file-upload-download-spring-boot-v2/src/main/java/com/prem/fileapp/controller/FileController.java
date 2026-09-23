package com.prem.fileapp.controller;

import com.prem.fileapp.model.FileMetadata;
import com.prem.fileapp.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

    private final FileStorageService storage;

    public FileController(FileStorageService storage) {
        this.storage = storage;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("files", storage.list());
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            storage.store(file);
            model.addAttribute("message", "File uploaded successfully.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("files", storage.list());
        return "index";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        FileMetadata metadata = storage.find(id);
        Resource resource = storage.loadResource(id);

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

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id, Model model) {
        try {
            storage.delete(id);
            model.addAttribute("message", "File deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("files", storage.list());
        return "index";
    }
}
