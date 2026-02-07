@echo off
echo ==========================================
echo      1. PREPARING WINDOWS ENVIRONMENT
echo ==========================================
:: Enable Windows Desktop support in Flutter config
call flutter config --enable-windows-desktop >nul 2>&1

:: Ensure the 'windows' folder exists in your project
if not exist "windows\" (
    echo [!] Windows runner not found. Creating it now...
    call flutter create --platforms=windows .
)

echo.
echo ==========================================
echo      2. KILLING ZOMBIE PROCESSES
echo ==========================================
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM dart.exe >nul 2>&1
taskkill /F /IM adb.exe >nul 2>&1
taskkill /F /IM flutter.exe >nul 2>&1

echo.
echo ==========================================
echo      3. LAUNCHING APP (Target: windows)
echo ==========================================
:: FIX: Changed 'windows-x64' to 'windows'
flutter run -d windows

echo.
echo ==========================================
echo      APP STOPPED.
echo ==========================================
pause