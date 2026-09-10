@echo off
echo ========================================
echo    文字格斗游戏 - 编译脚本
echo ========================================
echo.

REM 创建输出目录
if not exist "bin" mkdir bin
if not exist "data" mkdir data

echo 正在编译...
javac -d bin -sourcepath src/main/java src/main/java/com/fighting/GameApp.java

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo    编译成功！
    echo ========================================
    echo.
    echo 运行命令: java -cp bin com.fighting.GameApp
    echo 或双击 run.bat 启动游戏
) else (
    echo.
    echo ========================================
    echo    编译失败，请检查错误信息
    echo ========================================
)

pause
