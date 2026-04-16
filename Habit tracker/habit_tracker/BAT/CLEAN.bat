@echo off
taskkill /F /IM java.exe
taskkill /F /IM dart.exe
taskkill /F /IM adb.exe
flutter clean
echo "Cleanup Done! Ready to run."
pause