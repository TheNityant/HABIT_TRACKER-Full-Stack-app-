@echo off
echo ==========================================
echo      1. KILLING ZOMBIE PROCESSES
echo ==========================================
:: >nul 2>&1 hides the "Process not found" errors
:: DO NOT kill java.exe (That is your Backend!), Uncomment the next line if you want to kill it too:
:: taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM dart.exe >nul 2>&1
taskkill /F /IM adb.exe >nul 2>&1

echo.
echo ==========================================
echo      2. CLEANING BUILD (Optional Safety)
echo ==========================================
:: If you still get locks, uncomment the next line:
:: flutter clean

echo.
echo ==========================================
echo      3. LAUNCHING ON DEVICE (Phone/Windows-x64)
echo ==========================================
flutter run -d nvnbt46h49amcywo

echo.
echo ==========================================
echo      APP STOPPED.
echo ==========================================
pause