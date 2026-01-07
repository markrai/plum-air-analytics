@echo off
REM Build and deploy script for Plum Air Analytics
REM This script builds the Maven project, creates a Docker image, and pushes it to Docker Hub

set IMAGE_NAME=markraidc/plumairanalytics
set TAG=latest

echo ========================================
echo Building Plum Air Analytics Docker Image
echo ========================================
echo.

REM Step 1: Build Maven project
echo [1/4] Building Maven project...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven build failed!
    exit /b %ERRORLEVEL%
)
echo Maven build completed successfully.
echo.

REM Step 2: Build Docker image
echo [2/4] Building Docker image: %IMAGE_NAME%:%TAG%
docker build -t %IMAGE_NAME%:%TAG% .
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Docker build failed!
    exit /b %ERRORLEVEL%
)
echo Docker image built successfully.
echo.

REM Step 3: Verify Docker login
echo [3/4] Verifying Docker Hub login...
docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Docker may not be running or you may not be logged in.
    echo Please ensure you are logged into Docker Hub:
    echo   docker login
    echo.
)

REM Step 4: Push to Docker Hub
echo [4/4] Pushing image to Docker Hub: %IMAGE_NAME%:%TAG%
docker push %IMAGE_NAME%:%TAG%
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Docker push failed!
    echo Make sure you are logged into Docker Hub: docker login
    exit /b %ERRORLEVEL%
)
echo.

echo ========================================
echo Deployment completed successfully!
echo Image: %IMAGE_NAME%:%TAG%
echo ========================================
