@echo off
echo Starting Healthcare Appointment System...
start "Healthcare Backend" cmd /k "cd /d "%~dp0backend" && mvn spring-boot:run"
timeout /t 5 /nobreak >nul
start "Healthcare Frontend" cmd /k "cd /d "%~dp0frontend" && npm run dev"
echo Both servers are launching!
echo Backend: http://localhost:8080
echo Frontend: http://localhost:5173
