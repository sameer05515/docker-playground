package com.prem.knowledgerecall.controller;

import com.prem.knowledgerecall.model.Knowledge;
import com.prem.knowledgerecall.service.KnowledgeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService service;

    public KnowledgeController(KnowledgeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Knowledge> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        return service.findAll(search, category);
    }

    @GetMapping("/{id}")
    public Knowledge findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Knowledge create(@Valid @RequestBody Knowledge knowledge) {
        return service.create(knowledge);
    }

    @PutMapping("/{id}")
    public Knowledge update(
            @PathVariable String id,
            @Valid @RequestBody Knowledge knowledge) {
        return service.update(id, knowledge);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
