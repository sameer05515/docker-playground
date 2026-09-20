package com.prem.knowledgerecall.service;

import com.prem.knowledgerecall.model.Knowledge;
import com.prem.knowledgerecall.model.RecallHistory;
import com.prem.knowledgerecall.repository.KnowledgeRepository;
import com.prem.knowledgerecall.repository.RecallHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class RecallService {

    private final KnowledgeRepository knowledgeRepository;
    private final RecallHistoryRepository historyRepository;

    public RecallService(
            KnowledgeRepository knowledgeRepository,
            RecallHistoryRepository historyRepository) {
        this.knowledgeRepository = knowledgeRepository;
        this.historyRepository = historyRepository;
    }

    public List<Knowledge> getDueItems() {
        return knowledgeRepository
                .findByNextReviewAtIsNullOrNextReviewAtLessThanEqualOrderByNextReviewAtAsc(Instant.now());
    }

    public Knowledge review(String id, String rating) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge not found: " + id));

        String normalized = rating == null ? "" : rating.trim().toUpperCase();

        long days;
        switch (normalized) {
            case "AGAIN" -> days = 1;
            case "HARD" -> days = 2;
            case "GOOD" -> days = 5;
            case "EASY" -> days = 10;
            default -> throw new IllegalArgumentException(
                    "Rating must be AGAIN, HARD, GOOD or EASY");
        }

        Instant now = Instant.now();

        knowledge.setLastReviewedAt(now);
        knowledge.setNextReviewAt(now.plus(days, ChronoUnit.DAYS));
        knowledge.setReviewCount(knowledge.getReviewCount() + 1);

        if (!"AGAIN".equals(normalized)) {
            knowledge.setCorrectCount(knowledge.getCorrectCount() + 1);
        }

        int confidence = switch (normalized) {
            case "AGAIN" -> Math.max(0, knowledge.getConfidence() - 10);
            case "HARD" -> Math.min(100, knowledge.getConfidence() + 3);
            case "GOOD" -> Math.min(100, knowledge.getConfidence() + 8);
            case "EASY" -> Math.min(100, knowledge.getConfidence() + 12);
            default -> knowledge.getConfidence();
        };

        knowledge.setConfidence(confidence);

        knowledgeRepository.save(knowledge);
        historyRepository.save(new RecallHistory(id, normalized, now));

        return knowledge;
    }

    public Map<String, Object> stats() {
        List<Knowledge> all = knowledgeRepository.findAll();
        long due = getDueItems().size();

        long reviews = all.stream().mapToLong(Knowledge::getReviewCount).sum();
        long correct = all.stream().mapToLong(Knowledge::getCorrectCount).sum();

        int accuracy = reviews == 0 ? 0 : (int) Math.round((correct * 100.0) / reviews);

        long weak = all.stream().filter(k -> k.getConfidence() < 50).count();

        return Map.of(
                "total", all.size(),
                "due", due,
                "reviewCount", reviews,
                "accuracy", accuracy,
                "weakTopics", weak
        );
    }

    public List<RecallHistory> history() {
        return historyRepository.findTop20ByOrderByReviewedAtDesc();
    }
}
