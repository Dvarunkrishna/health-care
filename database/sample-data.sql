-- Sample seed data for healthcare_db.
--
-- Password hashes are real Spring Security BCrypt hashes:
--   admin@healthcare.com / Admin@123
--   doctor1..5@healthcare.com / Doctor@123
--   patient1@healthcare.com / Patient@123
--   patient2@healthcare.com / Patient@123
--
-- This script is written for a fresh database. On an existing database the
-- primary keys/unique emails below may already exist; INSERT IGNORE keeps the
-- script from failing in that case.

USE healthcare_db;

INSERT IGNORE INTO departments (id, department_name, description) VALUES
(1, 'Cardiology', 'Heart and cardiovascular care'),
(2, 'Neurology', 'Brain and nervous system care'),
(3, 'Pediatrics', 'Medical care for children'),
(4, 'Orthopedics', 'Bones, joints, and muscles'),
(5, 'Dermatology', 'Skin, hair, and nail care');

INSERT IGNORE INTO users (id, full_name, email, phone, password, role, created_at) VALUES
(1, 'System Administrator', 'admin@healthcare.com', '9999999999', '$2a$10$43pn8zSsqUHtSZ6wlArKSeOrRNAGPsKuRkW.TSIlpNUD4TlhVpEmy', 'ADMIN', NOW()),
(2, 'Doctor 1', 'doctor1@healthcare.com', '8888888880', '$2a$10$Igd2gtcbjVk0kMaiepZHGu16l1ATDimvKxQAW6hA6DBls.CEKC/7q', 'DOCTOR', NOW()),
(3, 'Doctor 2', 'doctor2@healthcare.com', '8888888881', '$2a$10$Igd2gtcbjVk0kMaiepZHGu16l1ATDimvKxQAW6hA6DBls.CEKC/7q', 'DOCTOR', NOW()),
(4, 'Doctor 3', 'doctor3@healthcare.com', '8888888882', '$2a$10$Igd2gtcbjVk0kMaiepZHGu16l1ATDimvKxQAW6hA6DBls.CEKC/7q', 'DOCTOR', NOW()),
(5, 'Doctor 4', 'doctor4@healthcare.com', '8888888883', '$2a$10$Igd2gtcbjVk0kMaiepZHGu16l1ATDimvKxQAW6hA6DBls.CEKC/7q', 'DOCTOR', NOW()),
(6, 'Doctor 5', 'doctor5@healthcare.com', '8888888884', '$2a$10$Igd2gtcbjVk0kMaiepZHGu16l1ATDimvKxQAW6hA6DBls.CEKC/7q', 'DOCTOR', NOW()),
(7, 'Patient 1', 'patient1@healthcare.com', '7777777770', '$2a$10$ZfU4tigd93rO1fzkSv63Vemz.meUGhjRAENmK2Wo2V.scHGuLgc9i', 'PATIENT', NOW()),
(8, 'Patient 2', 'patient2@healthcare.com', '7777777771', '$2a$10$ZfU4tigd93rO1fzkSv63Vemz.meUGhjRAENmK2Wo2V.scHGuLgc9i', 'PATIENT', NOW());

INSERT IGNORE INTO doctors (id, user_id, department_id, specialization, qualification, experience_years, consultation_fee, description) VALUES
(1, 2, 1, 'Cardiology', 'MD, DM', 10, 500.00, 'Experienced cardiologist'),
(2, 3, 2, 'Neurology', 'MD, DM', 8, 600.00, 'Neurology specialist'),
(3, 4, 3, 'Pediatrics', 'MD, DCH', 6, 400.00, 'Pediatric care expert'),
(4, 5, 4, 'Orthopedics', 'MS Ortho', 12, 700.00, 'Orthopedic surgeon'),
(5, 6, 5, 'Dermatology', 'MD Dermatology', 7, 350.00, 'Dermatology specialist');

-- Create a small appointment schedule for each seeded doctor for the next 7 days.
-- 30-minute slots are generated from 09:00 through 11:30.
INSERT INTO doctor_availability (doctor_id, available_date, start_time, end_time, is_available)
SELECT d.id,
       DATE_ADD(CURDATE(), INTERVAL days.n DAY),
       MAKETIME(9 + FLOOR(slots.n / 2), IF(slots.n % 2 = 0, 0, 30), 0),
       MAKETIME(9 + FLOOR((slots.n + 1) / 2), IF((slots.n + 1) % 2 = 0, 0, 30), 0),
       TRUE
FROM doctors d
CROSS JOIN (
    SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3
    UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
) AS days
CROSS JOIN (
    SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3
    UNION ALL SELECT 4 UNION ALL SELECT 5
) AS slots
WHERE NOT EXISTS (
    SELECT 1
    FROM doctor_availability existing
    WHERE existing.doctor_id = d.id
      AND existing.available_date = DATE_ADD(CURDATE(), INTERVAL days.n DAY)
      AND existing.start_time = MAKETIME(9 + FLOOR(slots.n / 2), IF(slots.n % 2 = 0, 0, 30), 0)
);
