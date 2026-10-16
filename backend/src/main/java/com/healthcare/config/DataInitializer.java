package com.healthcare.config;

import com.healthcare.entity.Department;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.DoctorAvailability;
import com.healthcare.entity.Role;
import com.healthcare.entity.User;
import com.healthcare.repository.DepartmentRepository;
import com.healthcare.repository.DoctorAvailabilityRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           DoctorRepository doctorRepository,
                           DoctorAvailabilityRepository availabilityRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@healthcare.com")) {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmail("admin@healthcare.com");
            admin.setPhone("9999999999");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (departmentRepository.count() == 0) {
            List<Department> departments = List.of(
                    new Department(null, "Cardiology", "Heart and cardiovascular care"),
                    new Department(null, "Neurology", "Brain and nervous system care"),
                    new Department(null, "Pediatrics", "Medical care for children"),
                    new Department(null, "Orthopedics", "Bones, joints, and muscles"),
                    new Department(null, "Dermatology", "Skin, hair, and nail care")
            );
            departmentRepository.saveAll(departments);
        }

        if (doctorRepository.count() == 0) {
            List<Department> departments = departmentRepository.findAll();
            String[] specializations = {"Cardiology", "Neurology", "Pediatrics", "Orthopedics", "Dermatology"};
            String[] qualifications = {"MD, DM", "MD, DM", "MD, DCH", "MS Ortho", "MD Dermatology"};
            int[] experience = {10, 8, 6, 12, 7};
            double[] fees = {500.0, 600.0, 400.0, 700.0, 350.0};
            String[] descriptions = {
                    "Experienced cardiologist",
                    "Neurology specialist",
                    "Pediatric care expert",
                    "Orthopedic surgeon",
                    "Dermatology specialist"
            };

            int limit = Math.min(departments.size(), specializations.length);
            for (int i = 0; i < limit; i++) {
                Department department = departments.get(i);
                if (userRepository.existsByEmail("doctor" + (i + 1) + "@healthcare.com")) {
                    continue;
                }
                User doctorUser = new User();
                doctorUser.setFullName("Doctor " + (i + 1));
                doctorUser.setEmail("doctor" + (i + 1) + "@healthcare.com");
                doctorUser.setPhone("888888888" + i);
                doctorUser.setPassword(passwordEncoder.encode("Doctor@123"));
                doctorUser.setRole(Role.DOCTOR);
                userRepository.save(doctorUser);

                Doctor doctor = new Doctor();
                doctor.setUser(doctorUser);
                doctor.setDepartment(department);
                doctor.setSpecialization(specializations[i]);
                doctor.setQualification(qualifications[i]);
                doctor.setExperienceYears(experience[i]);
                doctor.setConsultationFee(fees[i]);
                doctor.setDescription(descriptions[i]);
                doctorRepository.save(doctor);

                seedAvailability(doctor);
            }
        }
    }

    private void seedAvailability(Doctor doctor) {
        LocalDate today = LocalDate.now();
        for (int day = 0; day < 7; day++) {
            LocalDate date = today.plusDays(day);
            for (int hour = 9; hour < 12; hour++) {
                for (int minute = 0; minute < 60; minute += 30) {
                    LocalTime start = LocalTime.of(hour, minute);
                    LocalTime end = start.plusMinutes(30);
                    if (end.isAfter(LocalTime.of(12, 0))) {
                        continue;
                    }
                    if (availabilityRepository.existsByDoctorIdAndAvailableDateAndStartTime(doctor.getId(), date, start)) {
                        continue;
                    }
                    DoctorAvailability availability = new DoctorAvailability();
                    availability.setDoctor(doctor);
                    availability.setAvailableDate(date);
                    availability.setStartTime(start);
                    availability.setEndTime(end);
                    availability.setIsAvailable(Boolean.TRUE);
                    availabilityRepository.save(availability);
                }
            }
        }
    }
}
