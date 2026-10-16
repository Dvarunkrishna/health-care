package com.healthcare.dto;

import java.util.List;

public class NoShowPredictionDto {

    private Long appointmentId;

    private double noShowProbability;

    private String predictionLabel;

    private List<String> riskFactors;

    private List<String> suggestedActions;

    private String disclaimer;

    public NoShowPredictionDto() {
    }

    public NoShowPredictionDto(Long appointmentId, double noShowProbability, String predictionLabel,
                              List<String> riskFactors, List<String> suggestedActions, String disclaimer) {
        this.appointmentId = appointmentId;
        this.noShowProbability = noShowProbability;
        this.predictionLabel = predictionLabel;
        this.riskFactors = riskFactors;
        this.suggestedActions = suggestedActions;
        this.disclaimer = disclaimer;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public double getNoShowProbability() {
        return noShowProbability;
    }

    public void setNoShowProbability(double noShowProbability) {
        this.noShowProbability = noShowProbability;
    }

    public String getPredictionLabel() {
        return predictionLabel;
    }

    public void setPredictionLabel(String predictionLabel) {
        this.predictionLabel = predictionLabel;
    }

    public List<String> getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }

    public List<String> getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(List<String> suggestedActions) {
        this.suggestedActions = suggestedActions;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}