
@echo off
setlocal

set "PSQL=C:\Program Files\PostgreSQL\18\bin\psql.exe"
set "SCRIPT_DIR=%~dp0"

REM Recebe o summary_period como argumento
set "SUMMARY_PERIOD=%~1"

REM Verifica se o parametro foi informado
if not defined SUMMARY_PERIOD (
    echo ERRO: Informe o summary_period.
    echo Valores permitidos: 30, 60, 720 ou 1440.
    exit /b 1
)

REM Aceita somente os periodos definitivos
if "%SUMMARY_PERIOD%"=="30" goto valid_period
if "%SUMMARY_PERIOD%"=="60" goto valid_period
if "%SUMMARY_PERIOD%"=="720" goto valid_period
if "%SUMMARY_PERIOD%"=="1440" goto valid_period

echo ERRO: Periodo invalido.
echo Valores permitidos: 30, 60, 720 ou 1440.
exit /b 1ks

:valid_period

REM Configuracoes de conexao
set "PGUSER=%DB_USERNAME%"
set "PGPASSWORD=%DB_PASSWORD%"

REM Executa a agregacao para o periodo informado
"%PSQL%" -X -w -h localhost -p 5432 -d eyeot ^
    -v ON_ERROR_STOP=1 ^
    -v summary_period=%SUMMARY_PERIOD% ^
    -f "%SCRIPT_DIR%aggregate_sensor_statistics.sql"

IF ERRORLEVEL 1 (
    echo ERRO: Falha na agregacao do periodo %SUMMARY_PERIOD%.
    exit /b 1
)

echo Agregacao do periodo %SUMMARY_PERIOD% executada com sucesso.

exit /b 0
