@echo off
echo.
echo Deteniendo todos los microservicios...
echo.

taskkill /FI "WINDOWTITLE eq Discovery Service*" /F >nul 2>nul
taskkill /FI "WINDOWTITLE eq Gateway Service*" /F >nul 2>nul
taskkill /FI "WINDOWTITLE eq Auth Service*" /F >nul 2>nul
taskkill /FI "WINDOWTITLE eq Product Service*" /F >nul 2>nul
taskkill /FI "WINDOWTITLE eq Sale Service*" /F >nul 2>nul

echo Todos los servicios detenidos.
pause
