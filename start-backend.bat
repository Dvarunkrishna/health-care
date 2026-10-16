@echo off
echo Starting Healthcare Appointment Backend on http://localhost:8080 ...
cd /d "%~dp0backend"
mvn spring-boot:run
pause
