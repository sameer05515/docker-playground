package com.prem.knowledgerecall.repository;

import com.prem.knowledgerecall.model.Knowledge;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface KnowledgeRepository extends MongoRepository<Knowledge, String> {

    List<Knowledge> findAllByOrderByUpdatedAtDesc();

    List<Knowledge> findByCategoryIgnoreCaseOrderByUpdatedAtDesc(String category);

    List<Knowledge> findByTitleContainingIgnoreCaseOrQuestionContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByUpdatedAtDesc(
            String title, String question, String category);

    List<Knowledge> findByNextReviewAtIsNullOrNextReviewAtLessThanEqualOrderByNextReviewAtAsc(Instant now);
}
