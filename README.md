# Health Care Appointment (Powered By AI) and Doctor Recommendations

A small full-stack healthcare appointment management web application built with React, Spring Boot, MySQL, and rule-based AI recommendations.

## Architecture
- React (Vite) frontend at `http://localhost:5173`
- Spring Boot REST backend at `http://localhost:8080`
- MySQL database `healthcare_db`
- JWT-based authentication with roles PATIENT, DOCTOR, ADMIN

## Folder Structure
```
health-care-appointment/
  frontend/
  backend/
  database/
  ai-dataset/
  README.md
  .gitignore
```

## How to Run

### 1. Prerequisites
- Java 17 (JDK)
- Maven 3.8+
- Node.js 18+
- MySQL 8 running on localhost:3306

### 2. Database
Create the database and tables:
```bash
mysql -u root -p < database/schema.sql
```
Optional demo data:
```bash
mysql -u root -p healthcare_db < database/sample-data.sql
```
Note: The backend also seeds an admin, five departments, five doctors, and seven days of availability on first run.

### 3. Configure backend credentials
Edit `backend/src/main/resources/application.properties`:
```
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Start backend
```bash
cd backend
mvn spring-boot:run
```
Backend starts at `http://localhost:8080`.

### 5. Start frontend
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```
Frontend starts at `http://localhost:5173`.

## Sample Accounts
- Admin: `admin@healthcare.com` / `Admin@123`
- Doctors: `doctor1@healthcare.com` .. `doctor5@healthcare.com` / `Doctor@123`
- Patients: `patient1@healthcare.com`, `patient2@healthcare.com` / `Patient@123`

## AI Features
- `GET /api/ai/recommend-doctors` returns ranked doctor recommendations with scores, explanations, and available slots.
- `GET /api/ai/no-show-prediction/{appointmentId}` returns a demo risk score (LOW/MEDIUM/HIGH) with reminder suggestions.

These AI features support appointment navigation and reminders only. They do not diagnose illness, prescribe medicine, or replace a clinician.

## Testing Checklist
- Register a patient and log in.
- Search doctors, view profiles, and get recommendations.
- Book an appointment slot, then cancel or reschedule.
- Log in as a doctor and manage availability/appointment status.
- Log in as admin and view dashboard, users, doctors, departments, appointments, reports.
- Test unauthorized access to admin endpoints as a patient.
- Call the no-show prediction endpoint for a booked appointment.

## Troubleshooting
- If MySQL connection fails, verify credentials and that the database is reachable on port 3306.
- If tables are missing, ensure `spring.jpa.hibernate.ddl-auto=update` and that `healthcare_db` exists.
- If the frontend cannot reach the backend, check `VITE_API_BASE_URL` in `frontend/.env`.
- If login fails after running SQL manually, confirm passwords are stored as BCrypt hashes or rely on the backend DataInitializer.
- Do not commit real passwords or JWT secrets. `application.properties` is provided with demo values only.