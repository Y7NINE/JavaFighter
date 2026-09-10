@echo off
echo ========================================
echo    文字格斗游戏 - 启动中...
echo ========================================
echo.

REM 检查是否已编译
if not exist "bin\com\fighting\GameApp.class" (
    echo 游戏未编译，正在编译...
    call compile.bat
)

echo 启动游戏...
java -cp bin com.fighting.GameApp

pause
