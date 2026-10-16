package com.healthcare.controller;

import com.healthcare.dto.AppointmentDto;
import com.healthcare.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/appointments")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    public DoctorAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(currentUserEmail()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentDto> updateStatus(@PathVariable Long id,
                                                     @RequestBody Map<String, String> body) {
        String status = body == null ? null : body.get("status");
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
        return ResponseEntity.ok(appointmentService.updateStatus(id, currentUserEmail(), status));
    }
}