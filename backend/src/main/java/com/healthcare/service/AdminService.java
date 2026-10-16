package com.healthcare.service;

import com.healthcare.dto.AppointmentDto;
import com.healthcare.dto.DashboardStatsDto;
import com.healthcare.dto.DoctorDto;
import com.healthcare.dto.UserDto;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.User;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DepartmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminService(UserRepository userRepository,
                        DoctorRepository doctorRepository,
                        DepartmentRepository departmentRepository,
                        AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        long totalPatients = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equals("PATIENT"))
                .count();
        long totalDoctors = doctorRepository.count();
        long totalAppointments = appointmentRepository.count();
        long totalDepartments = departmentRepository.count();

        LocalDate today = LocalDate.now();
        long appointmentsToday = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentDate() != null && a.getAppointmentDate().equals(today))
                .count();
        long pendingAppointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.CONFIRMED)
                .count();

        return new DashboardStatsDto(totalPatients, totalDoctors, totalAppointments, totalDepartments,
                appointmentsToday, pendingAppointments);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserDto).collect(Collectors.toList());
    }

    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toAppointmentDto)
                .collect(Collectors.toList());
    }

    public DashboardStatsDto getDashboard() {
        return getDashboardStats();
    }

    public List<UserDto> getUsers() {
        return getAllUsers();
    }

    public List<AppointmentDto> getAppointments() {
        return getAllAppointments();
    }

    public Map<String, Object> getReports() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Long> byStatus = new HashMap<>();
        for (AppointmentStatus status : AppointmentStatus.values()) {
            byStatus.put(status.name(), 0L);
        }
        for (Appointment appointment : appointmentRepository.findAll()) {
            if (appointment.getStatus() != null) {
                byStatus.merge(appointment.getStatus().name(), 1L, Long::sum);
            }
        }
        DashboardStatsDto dashboard = getDashboardStats();
        result.put("dashboard", dashboard);
        result.put("appointmentsByStatus", byStatus);
        result.put("totalUsers", userRepository.count());
        return result;
    }

    private UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.getRole() == null ? null : user.getRole().name(), user.getCreatedAt());
    }

    private AppointmentDto toAppointmentDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        var patient = appointment.getPatient();
        var doctor = appointment.getDoctor();
        var doctorUser = doctor == null ? null : doctor.getUser();
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
