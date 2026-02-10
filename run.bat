@echo off
REM Pac-Man Game - Build and Run Script

echo Compiling Pac-Man Game...
javac -d build/classes -sourcepath src/main/java src/main/java/com/pacman/model/*.java src/main/java/com/pacman/ui/*.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful! Starting game...
echo.
pause

java -cp build/classes com.pacman.ui.GameFrame
