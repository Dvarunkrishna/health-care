package com.healthcare.controller;

import com.healthcare.dto.AppointmentDto;
import com.healthcare.dto.DashboardStatsDto;
import com.healthcare.dto.UserDto;
import com.healthcare.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> dashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> users() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDto>> appointments() {
        return ResponseEntity.ok(adminService.getAllAppointments());
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> reports() {
        return ResponseEntity.ok(adminService.getReports());
    }
}