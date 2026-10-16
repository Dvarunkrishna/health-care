package com.healthcare.ai;

import com.healthcare.dto.DoctorDto;
import com.healthcare.dto.RecommendationResponse;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.DoctorAvailability;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorAvailabilityRepository;
import com.healthcare.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AiRecommendationService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;

    public AiRecommendationService(DoctorRepository doctorRepository,
                                  DoctorAvailabilityRepository availabilityRepository,
                                  AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public RecommendationResponse recommend(String specializationOrDepartment, LocalDate date, LocalTime time, Double maxFee) {
        List<Doctor> allDoctors = doctorRepository.findAll();
        List<Doctor> candidates = new ArrayList<>();

        if (specializationOrDepartment != null && !specializationOrDepartment.isBlank()) {
            String query = specializationOrDepartment.toLowerCase(Locale.ROOT);
            for (Doctor doctor : allDoctors) {
                boolean matches = (doctor.getSpecialization() != null
                        && doctor.getSpecialization().toLowerCase(Locale.ROOT).contains(query))
                        || (doctor.getDepartment() != null
                        && doctor.getDepartment().getDepartmentName() != null
                        && doctor.getDepartment().getDepartmentName().toLowerCase(Locale.ROOT).contains(query));
                if (matches) {
                    candidates.add(doctor);
                }
            }
        }
        if (candidates.isEmpty()) {
            candidates = new ArrayList<>(allDoctors);
        }

        List<Appointment> dayAppointments = (date == null)
                ? List.of()
                : appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentDate() != null
                        && a.getAppointmentDate().equals(date)
                        && a.getStatus() != AppointmentStatus.CANCELLED)
                .collect(Collectors.toList());

        List<ScoredDoctor> scored = new ArrayList<>();
        for (Doctor doctor : candidates) {
            List<String> availableSlots = new ArrayList<>();
            List<String> reasons = new ArrayList<>();
            double score = 0;

            boolean specMatch = false;
            if (specializationOrDepartment != null && !specializationOrDepartment.isBlank()) {
                String query = specializationOrDepartment.toLowerCase(Locale.ROOT);
                specMatch = (doctor.getSpecialization() != null
                        && doctor.getSpecialization().toLowerCase(Locale.ROOT).contains(query))
                        || (doctor.getDepartment() != null
                        && doctor.getDepartment().getDepartmentName() != null
                        && doctor.getDepartment().getDepartmentName().toLowerCase(Locale.ROOT).contains(query));
            }
            if (specMatch) {
                score += 50;
                reasons.add("Specialization/department matches " + specializationOrDepartment);
            }

            if (date != null) {
                List<DoctorAvailability> slots = availabilityRepository.findByDoctorIdAndAvailableDate(doctor.getId(), date);
                boolean anySlot = false;
                for (DoctorAvailability slot : slots) {
                    if (!Boolean.TRUE.equals(slot.getIsAvailable())) {
                        continue;
                    }
                    boolean coversRequestedTime = (time == null)
                            || (!time.isBefore(slot.getStartTime()) && time.isBefore(slot.getEndTime()));
                    if (!coversRequestedTime) {
                        continue;
                    }
                    boolean occupied = isSlotOccupied(doctor, date, slot, dayAppointments);
                    if (occupied) {
                        continue;
                    }
                    availableSlots.add(slot.getStartTime().toString());
                    anySlot = true;
                }
                if (anySlot) {
                    score += 20;
                    reasons.add("Available appointment slot on the requested date");
                } else {
                    continue;
                }
            }

            int experience = doctor.getExperienceYears() == null ? 0 : doctor.getExperienceYears();
            if (experience > 0) {
                double expScore = Math.min(experience, 20);
                score += expScore;
                reasons.add(experience + " years of experience");
            }

            Double fee = doctor.getConsultationFee();
            if (maxFee == null || (fee != null && fee <= maxFee)) {
                score += 10;
                reasons.add("Consultation fee fits the requested budget");
            } else {
                reasons.add("Consultation fee is above the requested budget");
            }

            scored.add(new ScoredDoctor(doctor, score, reasons, deduplicateAndSort(availableSlots)));
        }

        scored.sort(Comparator.comparingDouble(ScoredDoctor::getScore).reversed());

        List<RecommendationResponse.RecommendedDoctor> top = scored.stream()
                .limit(3)
                .map(s -> new RecommendationResponse.RecommendedDoctor(
                        toDoctorDto(s.doctor),
                        s.score,
                        s.reasons,
                        s.availableSlots))
                .collect(Collectors.toList());

        // Fallback conversion using service instance? Use helper below instead.
        return new RecommendationResponse(top, specializationOrDepartment);
    }

    private com.healthcare.dto.DoctorDto toDoctorDto(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        com.healthcare.entity.User user = doctor.getUser();
        com.healthcare.entity.Department department = doctor.getDepartment();
        return new com.healthcare.dto.DoctorDto(
                doctor.getId(),
                user == null ? null : user.getId(),
                user == null ? null : user.getFullName(),
                user == null ? null : user.getEmail(),
                user == null ? null : user.getPhone(),
                department == null ? null : department.getId(),
                department == null ? null : department.getDepartmentName(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperienceYears(),
                doctor.getConsultationFee(),
                doctor.getDescription()
        );
    }

    private boolean isSlotOccupied(Doctor doctor, LocalDate date, DoctorAvailability slot, List<Appointment> dayAppointments) {
        for (Appointment appointment : dayAppointments) {
            if (appointment.getDoctor() == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
                continue;
            }
            LocalTime aptTime = appointment.getAppointmentTime();
            if (aptTime == null) {
                continue;
            }
            if (!aptTime.isBefore(slot.getStartTime()) && aptTime.isBefore(slot.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private List<String> deduplicateAndSort(List<String> slots) {
        return slots.stream().distinct().sorted().collect(Collectors.toList());
    }

    private static class ScoredDoctor {
        final Doctor doctor;
        final double score;
        final List<String> reasons;
        final List<String> availableSlots;

        ScoredDoctor(Doctor doctor, double score, List<String> reasons, List<String> availableSlots) {
            this.doctor = doctor;
            this.score = score;
            this.reasons = reasons;
            this.availableSlots = availableSlots;
        }

        double getScore() {
            return score;
        }
    }
}
