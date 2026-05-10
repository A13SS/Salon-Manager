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
set PROJECT_DIR=D:\TODOO\TEMARIOO\2DAW\ENTORNO SERVIDOR\Salon-Manager

:: Ruta de MySQL/MariaDB
set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 9.5\bin

echo ========================================
echo   BACKUP SALON MANAGER
echo   Proyecto: %PROJECT_DIR%
echo   Base de datos: %DB_NAME%
echo   Usuario: %DB_USER%
echo   Fecha: %date% %time%
echo ========================================
echo.

:: Crear directorio de backups si no existe
if not exist "%BACKUP_DIR%" (
    mkdir "%BACKUP_DIR%"
    echo [OK] Carpeta creada: %BACKUP_DIR%
)

:: Obtener fecha y hora en formato válido
for /f "tokens=2 delims==" %%i in ('wmic os get localdatetime /value') do set dt=%%i
set FECHA=%dt:~0,8%_%dt:~8,6%

echo [1/3] Realizando backup de base de datos...

"%MYSQL_BIN%\mysqldump.exe" -u%DB_USER% -p%DB_PASS% %DB_NAME% > "%BACKUP_DIR%\backup_%FECHA%.sql"

if %errorlevel% equ 0 (
    echo [OK] Backup creado: backup_%FECHA%.sql
) else (
    echo [ERROR] Fallo en backup
    echo [ERROR] Verifica que MariaDB/MySQL esté ejecutándose
    pause
    exit /b 1
)

echo.
echo [2/3] Comprimiendo backup...
if exist "C:\Program Files\7-Zip\7z.exe" (
    cd /d "%BACKUP_DIR%"
    "C:\Program Files\7-Zip\7z.exe" a -tgzip "backup_%FECHA%.sql.gz" "backup_%FECHA%.sql" >nul 2>&1
    if %errorlevel% equ 0 (
        del "backup_%FECHA%.sql"
        echo [OK] Backup comprimido: backup_%FECHA%.sql.gz
    )
) else (
    echo [INFO] 7-Zip no encontrado, se mantiene sin comprimir
)

echo.
echo [3/3] Eliminando backups antiguos...
forfiles /p "%BACKUP_DIR%" /s /m *.sql /d -30 /c "cmd /c del @path" 2>nul
forfiles /p "%BACKUP_DIR%" /s /m *.sql.gz /d -30 /c "cmd /c del @path" 2>nul
echo [OK] Backups antiguos eliminados

echo.
echo ========================================
echo   BACKUP COMPLETADO
echo ========================================
echo   Ubicación: %BACKUP_DIR%
echo.
echo   Archivos:
dir /b "%BACKUP_DIR%"
echo ========================================

pause