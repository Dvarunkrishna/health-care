package com.healthcare.service;

import com.healthcare.dto.DepartmentDto;
import com.healthcare.entity.Department;
import com.healthcare.entity.Doctor;
import com.healthcare.repository.DepartmentRepository;
import com.healthcare.repository.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    public DepartmentService(DepartmentRepository departmentRepository, DoctorRepository doctorRepository) {
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public DepartmentDto create(DepartmentDto req) {
        Department department = new Department();
        department.setDepartmentName(req == null ? null : req.getDepartmentName());
        department.setDescription(req == null ? null : req.getDescription());
        department = departmentRepository.save(department);
        return toDto(department);
    }

    public DepartmentDto update(Long id, DepartmentDto req) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        if (req != null) {
            if (req.getDepartmentName() != null && !req.getDepartmentName().isBlank()) {
                department.setDepartmentName(req.getDepartmentName());
            }
            if (req.getDescription() != null) {
                department.setDescription(req.getDescription());
            }
        }
        departmentRepository.save(department);
        return toDto(department);
    }

    public void delete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        List<Doctor> doctors = doctorRepository.findByDepartment(department);
        if (doctors != null && !doctors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department has assigned doctors");
        }
        departmentRepository.delete(department);
    }

    private DepartmentDto toDto(Department department) {
        if (department == null) {
            return null;
        }
        return new DepartmentDto(department.getId(), department.getDepartmentName(), department.getDescription());
    }
}
