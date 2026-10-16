package com.healthcare.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class RescheduleRequest {

    @NotNull(message = "New date is required")
    private LocalDate newDate;

    @NotNull(message = "New time is required")
    private LocalTime newTime;

    public RescheduleRequest() {
    }

    public RescheduleRequest(LocalDate newDate, LocalTime newTime) {
        this.newDate = newDate;
        this.newTime = newTime;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public LocalTime getNewTime() {
        return newTime;
    }

    public void setNewTime(LocalTime newTime) {
        this.newTime = newTime;
    }
}