package com.healthcare.repository;

import com.healthcare.entity.Department;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByDepartment(Department department);
    Optional<Doctor> findByUser(User user);
    List<Doctor> findAll();
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
    Optional<Doctor> findByUser_Id(Long userId);
}
