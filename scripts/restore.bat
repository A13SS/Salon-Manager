@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: ============================================
:: CONFIGURACIÓN
:: ============================================
set BACKUP_DIR=D:\backups\salon-manager
set DB_USER=alejandro
set DB_PASS=123456
set DB_NAME=salon_db
set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 9.5\bin

echo ========================================
echo   RESTAURACION SALON MANAGER
echo   Base de datos: %DB_NAME%
echo   Usuario: %DB_USER%
echo   Fecha: %date% %time%
echo ========================================
echo.

:: Verificar que existan backups
if not exist "%BACKUP_DIR%" (
    echo [ERROR] No existe la carpeta: %BACKUP_DIR%
    pause
    exit /b 1
)

echo Backups disponibles:
echo.
dir /b "%BACKUP_DIR%\*.sql*" 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] No se encontraron backups
    pause
    exit /b 1
)

echo.
set /p BACKUP_FILE="Nombre del backup: "

if not exist "%BACKUP_DIR%\%BACKUP_FILE%" (
    echo [ERROR] Archivo no encontrado
    pause
    exit /b 1
)

echo.
echo [1/4] Deteniendo aplicación...
taskkill /IM java.exe /F >nul 2>&1
echo [OK] Proceso Java detenido

echo.
echo [2/4] Preparando base de datos...
set /p CONFIRM="¿ELIMINAR datos actuales y restaurar? (S/N): "
if /i "%CONFIRM%" neq "S" (
    echo [CANCEL] Cancelado por el usuario
    pause
    exit /b 0
)

"%MYSQL_BIN%\mysql.exe" -u%DB_USER% -p%DB_PASS% -e "DROP DATABASE IF EXISTS %DB_NAME%;"
"%MYSQL_BIN%\mysql.exe" -u%DB_USER% -p%DB_PASS% -e "CREATE DATABASE %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo.
echo [3/4] Restaurando...
if "%BACKUP_FILE:~-3%"==".gz" (
    echo [INFO] Descomprimiendo y restaurando...
    "C:\Program Files\7-Zip\7z.exe" e -so "%BACKUP_DIR%\%BACKUP_FILE%" | "%MYSQL_BIN%\mysql.exe" -u%DB_USER% -p%DB_PASS% %DB_NAME%
) else (
    echo [INFO] Restaurando archivo SQL...
    "%MYSQL_BIN%\mysql.exe" -u%DB_USER% -p%DB_PASS% %DB_NAME% < "%BACKUP_DIR%\%BACKUP_FILE%"
)

if %errorlevel% equ 0 (
    echo [OK] Restauración completada
) else (
    echo [ERROR] Fallo en restauración
    pause
    exit /b 1
)

echo.
echo [4/4] Verificando datos...
"%MYSQL_BIN%\mysql.exe" -u%DB_USER% -p%DB_PASS% -N -e "SELECT COUNT(*) FROM %DB_NAME%.usuario;" > "%TEMP%\count.txt"
set /p USUARIOS=<"%TEMP%\count.txt"
"%MYSQL_BIN%\mysql.exe" -u%DB_USER% -p%DB_PASS% -N -e "SELECT COUNT(*) FROM %DB_NAME%.cita;" > "%TEMP%\count2.txt"
set /p CITAS=<"%TEMP%\count2.txt"
"%MYSQL_BIN%\mysql.exe" -u%DB_USER% -p%DB_PASS% -N -e "SELECT COUNT(*) FROM %DB_NAME%.servicio;" > "%TEMP%\count3.txt"
set /p SERVICIOS=<"%TEMP%\count3.txt"

echo.
echo ========================================
echo   RESTAURACION COMPLETADA
echo ========================================
echo   Usuarios: %USUARIOS%
echo   Citas: %CITAS%
echo   Servicios: %SERVICIOS%
echo ========================================
echo.
echo [INFO] Inicia manualmente el backend desde IntelliJ
echo.

pause