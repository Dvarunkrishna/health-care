@echo off
echo ===================================================
echo Pushing Healthcare Appointment to GitHub...
echo Repository: https://github.com/Dvarunkrishna/health-care
echo ===================================================
cd /d "%~dp0"
git push -u origin main
echo.
if %ERRORLEVEL% EQU 0 (
    echo ===================================================
    echo SUCCESS! Your code has been pushed to GitHub.
    echo ===================================================
) else (
    echo ===================================================
    echo Push encountered an issue. See message above.
    echo ===================================================
)
pause
