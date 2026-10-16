package com.healthcare.controller;

import com.healthcare.dto.NoShowPredictionDto;
import com.healthcare.dto.RecommendationResponse;
import com.healthcare.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/ai")
@PreAuthorize("isAuthenticated()")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/recommend-doctors")
    public ResponseEntity<RecommendationResponse> recommendDoctors(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime time,
            @RequestParam(required = false) Double maxFee) {
        String querySpecialty = (specialty != null && !specialty.isBlank()) ? specialty : department;
        return ResponseEntity.ok(aiService.recommendDoctors(querySpecialty, date, time, maxFee));
    }

    @GetMapping("/no-show-prediction/{appointmentId}")
    public ResponseEntity<NoShowPredictionDto> predictNoShow(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(aiService.predictNoShow(appointmentId));
    }
}