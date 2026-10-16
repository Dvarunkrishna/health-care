package com.healthcare.controller;

import com.healthcare.dto.AvailabilityDto;
import com.healthcare.dto.CreateAvailabilityRequest;
import com.healthcare.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/{id}/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityDto>> getAvailability(@PathVariable("id") Long doctorId) {
        return ResponseEntity.ok(availabilityService.getByDoctor(doctorId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<AvailabilityDto> create(@PathVariable("id") Long doctorId,
                                                  @Valid @RequestBody CreateAvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.create(doctorId, request, currentUserEmail()));
    }
}