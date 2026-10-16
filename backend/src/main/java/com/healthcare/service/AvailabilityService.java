package com.healthcare.service;

import com.healthcare.dto.AvailabilityDto;
import com.healthcare.dto.CreateAvailabilityRequest;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.DoctorAvailability;
import com.healthcare.entity.Role;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorAvailabilityRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AvailabilityService(DoctorAvailabilityRepository availabilityRepository,
                               DoctorRepository doctorRepository,
                               UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    public List<AvailabilityDto> getByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        return availabilityRepository.findByDoctor(doctor).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AvailabilityDto create(Long doctorId, CreateAvailabilityRequest req, String requesterEmail) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean allowed = requester.getRole() == Role.ADMIN
                || (requester.getRole() == Role.DOCTOR && doctorRepository.findByUser(requester)
                .map(d -> d.getId().equals(doctor.getId())).orElse(false));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to manage this doctor's availability");
        }

        if (req == null || req.getAvailableDate() == null || req.getStartTime() == null || req.getEndTime() == null) {
            throw new IllegalArgumentException("Availability details are required");
        }
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setAvailableDate(req.getAvailableDate());
        availability.setStartTime(req.getStartTime());
        availability.setEndTime(req.getEndTime());
        availability.setIsAvailable(req.getIsAvailable() == null ? Boolean.TRUE : req.getIsAvailable());
        availability = availabilityRepository.save(availability);

        return toDto(availability);
    }

    private AvailabilityDto toDto(DoctorAvailability availability) {
        if (availability == null) {
            return null;
        }
        Doctor doctor = availability.getDoctor();
        return new AvailabilityDto(
                availability.getId(),
                doctor == null ? null : doctor.getId(),
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime(),
                availability.getIsAvailable()
        );
    }
}
