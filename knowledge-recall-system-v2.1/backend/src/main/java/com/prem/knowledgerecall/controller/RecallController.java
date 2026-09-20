package com.prem.knowledgerecall.controller;

import com.prem.knowledgerecall.model.Knowledge;
import com.prem.knowledgerecall.model.RecallHistory;
import com.prem.knowledgerecall.service.RecallService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recall")
public class RecallController {

    private final RecallService service;

    public RecallController(RecallService service) {
        this.service = service;
    }

    @GetMapping("/due")
    public List<Knowledge> due() {
        return service.getDueItems();
    }

    @PostMapping("/{id}/review")
    public Knowledge review(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return service.review(id, body.get("rating"));
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return service.stats();
    }

    @GetMapping("/history")
    public List<RecallHistory> history() {
        return service.history();
    }
}
