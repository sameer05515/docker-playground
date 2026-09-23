package com.prem.knowledgerecall.repository;

import com.prem.knowledgerecall.model.RecallHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecallHistoryRepository extends MongoRepository<RecallHistory, String> {
    List<RecallHistory> findTop20ByOrderByReviewedAtDesc();
}
