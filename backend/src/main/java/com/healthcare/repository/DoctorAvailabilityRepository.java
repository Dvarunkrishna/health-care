package com.healthcare.repository;

import com.healthcare.entity.Doctor;
import com.healthcare.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctor(Doctor doctor);
    List<DoctorAvailability> findByDoctorIdAndAvailableDate(Long doctorId, LocalDate date);
    boolean existsByDoctorIdAndAvailableDateAndStartTime(Long doctorId, LocalDate availableDate, LocalTime startTime);
}
