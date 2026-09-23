package com.prem.fileapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "files")
public class FileMetadata {

    @Id
    private String id;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private long size;
    private Instant uploadedAt;

    public FileMetadata() {}

    public FileMetadata(String originalFileName, String storedFileName,
                         String contentType, long size, Instant uploadedAt) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }

    public String getId() { return id; }
    public String getOriginalFileName() { return originalFileName; }
    public String getStoredFileName() { return storedFileName; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public Instant getUploadedAt() { return uploadedAt; }

    public void setId(String id) { this.id = id; }
    public void setOriginalFileName(String v) { this.originalFileName = v; }
    public void setStoredFileName(String v) { this.storedFileName = v; }
    public void setContentType(String v) { this.contentType = v; }
    public void setSize(long v) { this.size = v; }
    public void setUploadedAt(Instant v) { this.uploadedAt = v; }
}
