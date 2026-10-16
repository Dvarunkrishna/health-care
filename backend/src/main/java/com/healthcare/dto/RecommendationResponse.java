package com.healthcare.dto;

import java.util.List;

public class RecommendationResponse {

    private List<RecommendedDoctor> recommendations;
    private String query;

    public RecommendationResponse() {
    }

    public RecommendationResponse(List<RecommendedDoctor> recommendations) {
        this.recommendations = recommendations;
    }

    public RecommendationResponse(List<RecommendedDoctor> recommendations, String query) {
        this.recommendations = recommendations;
        this.query = query;
    }

    public List<RecommendedDoctor> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendedDoctor> recommendations) {
        this.recommendations = recommendations;
    }

    // Alias for callers/code that refer to the list as recommendedDoctors.
    public List<RecommendedDoctor> getRecommendedDoctors() {
        return recommendations;
    }

    public void setRecommendedDoctors(List<RecommendedDoctor> recommendedDoctors) {
        this.recommendations = recommendedDoctors;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public static class RecommendedDoctor {

        private DoctorDto doctor;
        private double score;
        private List<String> reasons;
        private List<String> availableSlots;

        public RecommendedDoctor() {
        }

        public RecommendedDoctor(DoctorDto doctor, double score, List<String> reasons, List<String> availableSlots) {
            this.doctor = doctor;
            this.score = score;
            this.reasons = reasons;
            this.availableSlots = availableSlots;
        }

        public DoctorDto getDoctor() {
            return doctor;
        }

        public void setDoctor(DoctorDto doctor) {
            this.doctor = doctor;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public List<String> getReasons() {
            return reasons;
        }

        public void setReasons(List<String> reasons) {
            this.reasons = reasons;
        }

        public List<String> getAvailableSlots() {
            return availableSlots;
        }

        public void setAvailableSlots(List<String> availableSlots) {
            this.availableSlots = availableSlots;
        }
    }
}