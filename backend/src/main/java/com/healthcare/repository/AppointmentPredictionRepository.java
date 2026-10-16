package com.healthcare.repository;

import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentPredictionRepository extends JpaRepository<AppointmentPrediction, Long> {
    Optional<AppointmentPrediction> findByAppointment(Appointment appointment);
}
