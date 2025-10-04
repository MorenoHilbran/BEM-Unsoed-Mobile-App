@echo off
echo Deploying Firestore indexes...
echo.

REM Check if Firebase CLI is installed
firebase --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Firebase CLI not found!
    echo Please install Firebase CLI first: npm install -g firebase-tools
    echo Then login with: firebase login
    pause
    exit /b 1
)

echo Deploying indexes to Firebase...
firebase deploy --only firestore:indexes

if %errorlevel% equ 0 (
    echo.
    echo SUCCESS: Firestore indexes deployed successfully!
    echo The composite index for comments query is now available.
    echo.
) else (
    echo.
    echo ERROR: Failed to deploy indexes!
    echo Please check your Firebase project configuration.
    echo.
)

pause
