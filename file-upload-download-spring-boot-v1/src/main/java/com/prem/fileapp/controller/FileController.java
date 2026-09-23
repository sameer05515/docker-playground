package com.prem.fileapp.controller;

import com.prem.fileapp.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

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
    public String upload(@RequestParam MultipartFile file, Model model) {
        try {
            storage.store(file);
            model.addAttribute("message", "File uploaded successfully.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("files", storage.list());
        return "index";
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) throws IOException {
        Resource resource = storage.load(fileName);
        String contentType = Files.probeContentType(resource.getFile().toPath());
        if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(resource.getFilename())
                                .build().toString())
                .body(resource);
    }

    @PostMapping("/delete/{fileName:.+}")
    public String delete(@PathVariable String fileName, Model model) {
        try {
            storage.delete(fileName);
            model.addAttribute("message", "File deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("files", storage.list());
        return "index";
    }
}
