package com.prem.fileapp.repository;

import com.prem.fileapp.model.FileMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    List<FileMetadata> findByOriginalFileNameContainingIgnoreCaseOrderByUploadedAtDesc(String search);
}
