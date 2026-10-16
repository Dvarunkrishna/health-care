package com.healthcare.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityDto {

    private Long id;

    private Long doctorId;

    private LocalDate availableDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean isAvailable;

    public AvailabilityDto() {
    }

    public AvailabilityDto(Long id, Long doctorId, LocalDate availableDate, LocalTime startTime, LocalTime endTime, Boolean isAvailable) {
        this.id = id;
        this.doctorId = doctorId;
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return Boolean.TRUE.equals(isAvailable);
    }
}