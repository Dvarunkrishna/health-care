package com.healthcare.repository;

import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate appointmentDate);
    List<Appointment> findAllByOrderByCreatedAtDesc();

    long countByDoctor(Doctor doctor);
    long countByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
    long countByPatient(User patient);
}
