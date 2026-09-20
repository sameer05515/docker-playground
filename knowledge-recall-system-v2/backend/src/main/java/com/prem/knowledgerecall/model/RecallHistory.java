package com.prem.knowledgerecall.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "recall_history")
public class RecallHistory {

    @Id
    private String id;

    private String knowledgeId;
    private String rating;
    private Instant reviewedAt;

    public RecallHistory() {}

    public RecallHistory(String knowledgeId, String rating, Instant reviewedAt) {
        this.knowledgeId = knowledgeId;
        this.rating = rating;
        this.reviewedAt = reviewedAt;
    }

    public String getId() { return id; }
    public String getKnowledgeId() { return knowledgeId; }
    public String getRating() { return rating; }
    public Instant getReviewedAt() { return reviewedAt; }

    public void setId(String id) { this.id = id; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }
    public void setRating(String rating) { this.rating = rating; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }
}
