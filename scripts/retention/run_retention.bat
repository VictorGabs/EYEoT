
@echo off
setlocal

set "PSQL=C:\Program Files\PostgreSQL\18\bin\psql.exe"
set "SCRIPT_DIR=%~dp0"

REM Configuracoes de conexao
set "PGUSER=%DB_USERNAME%"
set "PGPASSWORD=%DB_PASSWORD%"

if not defined PGUSER (
    echo ERRO: DB_USERNAME nao foi configurada.
    exit /b 1
)

if not defined PGPASSWORD (
    echo ERRO: DB_PASSWORD nao foi configurada.
    exit /b 1
)

REM Executa a retencao permanente
"%PSQL%" -X -w -h localhost -p 5432 -d eyeot ^
    -v ON_ERROR_STOP=1 ^
    -f "%SCRIPT_DIR%retention_telemetry.sql"

IF ERRORLEVEL 1 (
    echo ERRO: Falha na execucao da retencao.
    exit /b 1
)

echo Retencao executada com sucesso.

exit /b 0
