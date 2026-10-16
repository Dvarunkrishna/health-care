package com.healthcare.controller;

import com.healthcare.dto.DoctorDto;
import com.healthcare.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAll(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String specialization) {
        return ResponseEntity.ok(doctorService.getAll(departmentId, specialization));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> create(@Valid @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.create(doctorDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<DoctorDto> update(@PathVariable Long id,
                                            @Valid @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.update(id, doctorDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}