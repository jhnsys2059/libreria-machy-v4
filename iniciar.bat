@echo off
REM ============================================
REM  Libreria Machy v4.0 - Inicio Rapido
REM ============================================
REM  INSTRUCCIONES:
REM  1. Edita el archivo .env con tu password de NeonDB
REM  2. Ejecuta este script: iniciar.bat
REM  3. Abre http://localhost:8761 (Eureka Dashboard)
REM  4. Abre http://localhost:8080 (Gateway)
REM ============================================

echo.
echo ========================================
echo   LIBRERIA MACHY v4.0 - MICROSERVICIOS
echo ========================================
echo.

REM Verificar que Maven este disponible
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven no encontrado. Agrega Maven al PATH.
    echo Descarga desde: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Verificar que Java este disponible
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java no encontrado.
    pause
    exit /b 1
)

echo [1/6] Iniciando Discovery Service (Eureka) en puerto 8761...
start "Discovery Service" cmd /k "cd /d %~dp0backend\discovery-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [2/6] Iniciando Gateway Service en puerto 8080...
start "Gateway Service" cmd /k "cd /d %~dp0backend\gateway-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [3/6] Iniciando Auth Service en puerto 8081...
start "Auth Service" cmd /k "cd /d %~dp0backend\auth-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [4/6] Iniciando Product Service en puerto 8082...
start "Product Service" cmd /k "cd /d %~dp0backend\product-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [5/6] Iniciando Sale Service en puerto 8083...
start "Sale Service" cmd /k "cd /d %~dp0backend\sale-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [6/6] Iniciando Frontend Service en puerto 8084...
start "Frontend Service" cmd /k "cd /d %~dp0frontend\frontend-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo   TODOS LOS SERVICIOS INICIADOS
echo ========================================
echo.
echo   Eureka Dashboard:  http://localhost:8761
echo   Gateway API:       http://localhost:8080
echo   Auth Service:      http://localhost:8081
echo   Product Service:   http://localhost:8082
echo   Sale Service:      http://localhost:8083
echo   Frontend:          http://localhost:8084
echo.
echo   Usuarios por defecto:
echo     admin / admin123
echo     ana / vendedor123
echo     miguel / vendedor123
echo.
echo ========================================
pause
