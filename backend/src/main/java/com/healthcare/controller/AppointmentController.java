package com.healthcare.controller;

import com.healthcare.dto.AppointmentDto;
import com.healthcare.dto.AppointmentRequest;
import com.healthcare.dto.RescheduleRequest;
import com.healthcare.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> book(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.book(currentUserEmail(), request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentDto>> myAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointments(currentUserEmail()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<AppointmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id, currentUserEmail()));
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> reschedule(@PathVariable Long id,
                                                   @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(appointmentService.reschedule(id, currentUserEmail(), request));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancel(id, currentUserEmail()));
    }
}