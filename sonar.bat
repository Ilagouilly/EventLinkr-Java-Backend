@echo off

REM Fetch SONAR_TOKEN from .env file
for /f "tokens=1,* delims==" %%a in ('findstr /b "SONAR_TOKEN=" .env') do set SONAR_TOKEN=%%b

REM Print out the token for verification
echo SONAR_TOKEN is: %SONAR_TOKEN%
:: Start SonarQube if not already running
docker-compose -f docker-compose-sonar.yml up -d

:: Wait for SonarQube to be ready
echo Waiting for SonarQube to start...
:wait_loop
curl -s http://localhost:9000/api/system/status | findstr /C:"\"status\":\"UP\"" >nul
if %errorlevel% neq 0 (
    timeout /t 5 /nobreak >nul
    goto wait_loop
)
echo SonarQube is up and running

:: Run the analysis - PLEASE MAKE SURE TO SET SONAR_TOKEN in .env VARIABLE
cd user-service
mvn clean verify sonar:sonar -Dsonar.login=%SONAR_TOKEN% -Dsonar.host.url=http://localhost:9000
