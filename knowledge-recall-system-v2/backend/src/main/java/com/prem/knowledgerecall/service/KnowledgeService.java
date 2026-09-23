package com.prem.knowledgerecall.service;

import com.prem.knowledgerecall.model.Knowledge;
import com.prem.knowledgerecall.repository.KnowledgeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class KnowledgeService {

    private final KnowledgeRepository repository;

    public KnowledgeService(KnowledgeRepository repository) {
        this.repository = repository;
    }

    public List<Knowledge> findAll(String search, String category) {
        if (search != null && !search.isBlank()) {
            return repository
                    .findByTitleContainingIgnoreCaseOrQuestionContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByUpdatedAtDesc(
                            search, search, search);
        }

        if (category != null && !category.isBlank()) {
            return repository.findByCategoryIgnoreCaseOrderByUpdatedAtDesc(category);
        }

        return repository.findAllByOrderByUpdatedAtDesc();
    }

    public Knowledge findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge not found: " + id));
    }

    public Knowledge create(Knowledge knowledge) {
        Instant now = Instant.now();
        knowledge.setCreatedAt(now);
        knowledge.setUpdatedAt(now);
        knowledge.setReviewCount(0);
        knowledge.setCorrectCount(0);
        knowledge.setConfidence(0);
        knowledge.setLastReviewedAt(null);
        knowledge.setNextReviewAt(null);
        return repository.save(knowledge);
    }

    public Knowledge update(String id, Knowledge request) {
        Knowledge existing = findById(id);

        existing.setTitle(request.getTitle());
        existing.setQuestion(request.getQuestion());
        existing.setAnswer(request.getAnswer());
        existing.setCategory(request.getCategory());
        existing.setTags(request.getTags());
        existing.setExample(request.getExample());
        existing.setNotes(request.getNotes());
        existing.setUpdatedAt(Instant.now());

        return repository.save(existing);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Knowledge not found: " + id);
        }
        repository.deleteById(id);
    }
}
