package com.healthcare.service;

import com.healthcare.ai.AiRecommendationService;
import com.healthcare.ai.NoShowPredictionService;
import com.healthcare.dto.NoShowPredictionDto;
import com.healthcare.dto.RecommendationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AiService {

    private final AiRecommendationService aiRecommendationService;
    private final NoShowPredictionService noShowPredictionService;

    public AiService(AiRecommendationService aiRecommendationService,
                     NoShowPredictionService noShowPredictionService) {
        this.aiRecommendationService = aiRecommendationService;
        this.noShowPredictionService = noShowPredictionService;
    }

    public RecommendationResponse recommendDoctors(String specialty, java.time.LocalDate date,
                                                  java.time.LocalTime time, Double maxFee) {
        return aiRecommendationService.recommend(specialty, date, time, maxFee);
    }

    public NoShowPredictionDto predictNoShow(Long appointmentId) {
        return noShowPredictionService.predict(appointmentId);
    }
}
