package com.prem.fileapp;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController public class Health{@GetMapping("/api/health")public Map<String,String> health(){return Map.of("status","UP");}}
