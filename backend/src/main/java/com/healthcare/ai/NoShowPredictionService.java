package com.healthcare.ai;

import com.healthcare.dto.NoShowPredictionDto;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentPrediction;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.RiskLevel;
import com.healthcare.entity.User;
import com.healthcare.repository.AppointmentPredictionRepository;
import com.healthcare.repository.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NoShowPredictionService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentPredictionRepository predictionRepository;

    public NoShowPredictionService(AppointmentRepository appointmentRepository,
                                 AppointmentPredictionRepository predictionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.predictionRepository = predictionRepository;
    }

    public NoShowPredictionDto predict(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        User patient = appointment.getPatient();
        List<Appointment> history = patient == null
                ? List.of()
                : appointmentRepository.findByPatient(patient).stream()
                .filter(a -> !a.getId().equals(appointment.getId()))
                .toList();

        int totalAppointments = history.size();
        int cancelledCount = (int) history.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .count();

        LocalDate createdDate = appointment.getCreatedAt() == null ? appointment.getAppointmentDate() : appointment.getCreatedAt().toLocalDate();
        long leadTimeDays = 0;
        if (createdDate != null && appointment.getAppointmentDate() != null) {
            leadTimeDays = ChronoUnit.DAYS.between(createdDate, appointment.getAppointmentDate());
            if (leadTimeDays < 0) {
                leadTimeDays = 0;
            }
        }

        int hour = 9;
        if (appointment.getAppointmentTime() != null) {
            hour = appointment.getAppointmentTime().getHour();
        }
        int dayOfWeek = appointment.getAppointmentDate() == null ? 1 : appointment.getAppointmentDate().getDayOfWeek().getValue();

        List<String> riskFactors = new ArrayList<>();
        double probability = 0.1;

        if (leadTimeDays <= 1) {
            probability += 0.15;
            riskFactors.add("Appointment booked with very short lead time");
        }
        if (hour < 9 || hour > 16) {
            probability += 0.1;
            riskFactors.add("Appointment scheduled outside peak clinic hours");
        }
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            probability += 0.05;
            riskFactors.add("Appointment scheduled on a weekend");
        }
        if (totalAppointments > 0) {
            double noShowRate = cancelledCount / (double) totalAppointments;
            probability += noShowRate * 0.4;
            if (cancelledCount > 0) {
                riskFactors.add("Patient has prior cancelled/no-show appointments");
            }
        }
        if (riskFactors.isEmpty()) {
            riskFactors.add("No major risk factors identified");
        }

        probability = Math.max(0.0, Math.min(1.0, probability));

        RiskLevel riskLevel;
        if (probability < 0.33) {
            riskLevel = RiskLevel.LOW;
        } else if (probability < 0.66) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.HIGH;
        }

        List<String> suggestedActions = new ArrayList<>();
        if (riskLevel == RiskLevel.HIGH) {
            suggestedActions.add("Send confirmation reminder 24 hours before the appointment");
            suggestedActions.add("Contact the patient by phone one day in advance");
            suggestedActions.add("Offer flexible rescheduling options");
        } else if (riskLevel == RiskLevel.MEDIUM) {
            suggestedActions.add("Send an automated reminder 48 hours before the appointment");
            suggestedActions.add("Allow easy online rescheduling");
        } else {
            suggestedActions.add("Send a standard appointment reminder");
        }

        String disclaimer = "This prediction is a heuristic estimate based on appointment history and is not a medical diagnosis or clinical assessment.";

        AppointmentPrediction prediction = predictionRepository.findByAppointment(appointment)
                .orElseGet(AppointmentPrediction::new);
        prediction.setAppointment(appointment);
        prediction.setNoShowProbability(probability);
        prediction.setPredictionLabel(riskLevel.name());
        predictionRepository.save(prediction);

        return new NoShowPredictionDto(
                appointment.getId(),
                probability,
                riskLevel.name(),
                riskFactors,
                suggestedActions,
                disclaimer
        );
    }
}
