package com.healthcare.service;

import com.healthcare.dto.AppointmentDto;
import com.healthcare.dto.AppointmentRequest;
import com.healthcare.dto.RescheduleRequest;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.DoctorAvailability;
import com.healthcare.entity.Role;
import com.healthcare.entity.User;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorAvailabilityRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              UserRepository userRepository,
                              DoctorAvailabilityRepository availabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public AppointmentDto bookAppointment(String patientEmail, AppointmentRequest req) {
        return book(patientEmail, req);
    }

    public AppointmentDto book(String patientEmail, AppointmentRequest req) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (patient.getRole() != Role.PATIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients can book appointments");
        }

        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        if (req.getAppointmentDate() == null || req.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }

        List<DoctorAvailability> slots = availabilityRepository.findByDoctorIdAndAvailableDate(doctor.getId(), req.getAppointmentDate());
        boolean slotAvailable = slots.stream().anyMatch(slot ->
                Boolean.TRUE.equals(slot.getIsAvailable())
                        && req.getAppointmentTime().compareTo(slot.getStartTime()) >= 0
                        && req.getAppointmentTime().isBefore(slot.getEndTime()));
        if (!slotAvailable) {
            throw new IllegalArgumentException("Requested time is not within an available slot");
        }

        List<Appointment> sameDay = appointmentRepository.findByDoctorAndAppointmentDate(doctor, req.getAppointmentDate());
        boolean duplicate = sameDay.stream().anyMatch(existing ->
                existing.getAppointmentTime().equals(req.getAppointmentTime())
                        && existing.getStatus() != AppointmentStatus.CANCELLED
                        && !existing.getId().equals(-1L));
        if (duplicate) {
            throw new IllegalArgumentException("This time slot is already booked");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(req.getAppointmentDate());
        appointment.setAppointmentTime(req.getAppointmentTime());
        appointment.setReason(req.getReason());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setCreatedAt(java.time.LocalDateTime.now());

        appointmentRepository.save(appointment);
        return toDto(appointment);
    }

    public List<AppointmentDto> getMyAppointments(String email) {
        User patient = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return appointmentRepository.findByPatient(patient).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AppointmentDto getById(Long id) {
        Appointment appointment = findAppointment(id);
        return toDto(appointment);
    }

    public AppointmentDto getById(Long id, String requesterEmail) {
        Appointment appointment = findAppointment(id);
        ensureCanView(appointment, requesterEmail);
        return toDto(appointment);
    }

    public AppointmentDto reschedule(Long id, String requesterEmail, RescheduleRequest req) {
        Appointment appointment = findAppointment(id);

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean admin = requester.getRole() == Role.ADMIN;
        boolean ownerPatient = appointment.getPatient() != null && appointment.getPatient().getEmail().equals(requesterEmail);
        if (!admin && !ownerPatient) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to reschedule this appointment");
        }

        if (req == null || req.getNewDate() == null || req.getNewTime() == null) {
            throw new IllegalArgumentException("New date and time are required");
        }
        if (req.getNewDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }

        Doctor doctor = appointment.getDoctor();
        List<DoctorAvailability> slots = availabilityRepository.findByDoctorIdAndAvailableDate(doctor.getId(), req.getNewDate());
        boolean slotAvailable = slots.stream().anyMatch(slot ->
                Boolean.TRUE.equals(slot.getIsAvailable())
                        && req.getNewTime().compareTo(slot.getStartTime()) >= 0
                        && req.getNewTime().isBefore(slot.getEndTime()));
        if (!slotAvailable) {
            throw new IllegalArgumentException("Requested time is not within an available slot");
        }

        List<Appointment> sameDay = appointmentRepository.findByDoctorAndAppointmentDate(doctor, req.getNewDate());
        boolean duplicate = sameDay.stream().anyMatch(existing ->
                !existing.getId().equals(appointment.getId())
                        && existing.getAppointmentTime().equals(req.getNewTime())
                        && existing.getStatus() != AppointmentStatus.CANCELLED);
        if (duplicate) {
            throw new IllegalArgumentException("This time slot is already booked");
        }

        appointment.setAppointmentDate(req.getNewDate());
        appointment.setAppointmentTime(req.getNewTime());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
        return toDto(appointment);
    }

    public AppointmentDto cancel(Long id, String requesterEmail) {
        Appointment appointment = findAppointment(id);

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean admin = requester.getRole() == Role.ADMIN;
        boolean ownerPatient = appointment.getPatient() != null && appointment.getPatient().getEmail().equals(requesterEmail);
        boolean ownerDoctor = appointment.getDoctor() != null && appointment.getDoctor().getUser() != null
                && appointment.getDoctor().getUser().getEmail().equals(requesterEmail);
        if (!admin && !ownerPatient && !ownerDoctor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return toDto(appointment);
    }

    public List<AppointmentDto> getDoctorAppointments(String doctorEmail) {
        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor profile not found"));
        return appointmentRepository.findByDoctor(doctor).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AppointmentDto updateStatus(Long id, String doctorEmail, String status) {
        Appointment appointment = findAppointment(id);

        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean admin = user.getRole() == Role.ADMIN;
        boolean ownerDoctor = appointment.getDoctor() != null && appointment.getDoctor().getUser() != null
                && appointment.getDoctor().getUser().getEmail().equals(doctorEmail);
        if (!admin && !ownerDoctor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to update this appointment");
        }

        AppointmentStatus parsed;
        try {
            parsed = AppointmentStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid appointment status: " + status);
        }
        appointment.setStatus(parsed);
        appointmentRepository.save(appointment);
        return toDto(appointment);
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
    }

    private void ensureCanView(Appointment appointment, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (requester.getRole() == Role.ADMIN) {
            return;
        }
        boolean patientOwner = appointment.getPatient() != null && appointment.getPatient().getEmail().equals(requesterEmail);
        boolean doctorOwner = appointment.getDoctor() != null && appointment.getDoctor().getUser() != null
                && appointment.getDoctor().getUser().getEmail().equals(requesterEmail);
        if (!patientOwner && !doctorOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this appointment");
        }
    }

    private AppointmentDto toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        User patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        User doctorUser = doctor == null ? null : doctor.getUser();
        return new AppointmentDto(
                appointment.getId(),
                patient == null ? null : patient.getId(),
                patient == null ? null : patient.getFullName(),
                doctor == null ? null : doctor.getId(),
                doctorUser == null ? null : doctorUser.getFullName(),
                doctor == null ? null : doctor.getSpecialization(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getStatus() == null ? null : appointment.getStatus().name(),
                appointment.getCreatedAt()
        );
    }
}
