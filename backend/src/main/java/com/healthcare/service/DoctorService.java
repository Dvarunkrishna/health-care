package com.healthcare.service;

import com.healthcare.dto.DoctorDto;
import com.healthcare.entity.Department;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Role;
import com.healthcare.entity.User;
import com.healthcare.repository.DepartmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository,
                         UserRepository userRepository,
                         DepartmentRepository departmentRepository,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<DoctorDto> getAll(Long departmentId, String specialization) {
        Department department = null;
        if (departmentId != null) {
            department = departmentRepository.findById(departmentId).orElse(null);
        }
        final Department filterDepartment = department;
        return doctorRepository.findAll().stream()
                .filter(d -> filterDepartment == null || (d.getDepartment() != null && d.getDepartment().getId().equals(filterDepartment.getId())))
                .filter(d -> specialization == null || specialization.isBlank()
                        || (d.getSpecialization() != null && d.getSpecialization().toLowerCase(Locale.ROOT).contains(specialization.toLowerCase(Locale.ROOT))))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DoctorDto getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        return toDto(doctor);
    }

    public DoctorDto create(DoctorDto req) {
        if (req == null) {
            throw new IllegalArgumentException("Doctor request is required");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode("Doctor@123"));
        user.setRole(Role.DOCTOR);
        userRepository.save(user);

        Department department = null;
        if (req.getDepartmentId() != null) {
            department = departmentRepository.findById(req.getDepartmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        }

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(department);
        doctor.setSpecialization(req.getSpecialization());
        doctor.setQualification(req.getQualification());
        doctor.setExperienceYears(req.getExperienceYears() == null ? 0 : req.getExperienceYears());
        doctor.setConsultationFee(req.getConsultationFee() == null ? 0.0 : req.getConsultationFee());
        doctor.setDescription(req.getDescription());
        doctorRepository.save(doctor);

        return toDto(doctor);
    }

    public DoctorDto update(Long id, DoctorDto req) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        User user = doctor.getUser();

        if (req != null) {
            if (req.getFullName() != null && !req.getFullName().isBlank()) {
                user.setFullName(req.getFullName());
            }
            if (req.getEmail() != null && !req.getEmail().isBlank() && !req.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(req.getEmail())) {
                    throw new IllegalArgumentException("Email is already registered");
                }
                user.setEmail(req.getEmail());
            }
            if (req.getPhone() != null) {
                user.setPhone(req.getPhone());
            }

            if (req.getDepartmentId() != null) {
                Department department = departmentRepository.findById(req.getDepartmentId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
                doctor.setDepartment(department);
            }
            if (req.getSpecialization() != null) {
                doctor.setSpecialization(req.getSpecialization());
            }
            if (req.getQualification() != null) {
                doctor.setQualification(req.getQualification());
            }
            if (req.getExperienceYears() != null) {
                doctor.setExperienceYears(req.getExperienceYears());
            }
            if (req.getConsultationFee() != null) {
                doctor.setConsultationFee(req.getConsultationFee());
            }
            if (req.getDescription() != null) {
                doctor.setDescription(req.getDescription());
            }

            userRepository.save(user);
            doctorRepository.save(doctor);
        }

        return toDto(doctor);
    }

    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        User user = doctor.getUser();
        doctorRepository.delete(doctor);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public DoctorDto toDto(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        User user = doctor.getUser();
        Department department = doctor.getDepartment();
        return new DoctorDto(
                doctor.getId(),
                user == null ? null : user.getId(),
                user == null ? null : user.getFullName(),
                user == null ? null : user.getEmail(),
                user == null ? null : user.getPhone(),
                department == null ? null : department.getId(),
                department == null ? null : department.getDepartmentName(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperienceYears(),
                doctor.getConsultationFee(),
                doctor.getDescription()
        );
    }
}
