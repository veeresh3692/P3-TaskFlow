@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "JAVA_HOME_DEFAULT=C:\Program Files\Android\Android Studio\jbr"
if "%JAVA_HOME%"=="" (
    if exist "%JAVA_HOME_DEFAULT%" (
        set "JAVA_HOME=%JAVA_HOME_DEFAULT%"
    )
)

if not "%JAVA_HOME%"=="" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

set "MAVEN_OPTS=--enable-native-access=ALL-UNNAMED %MAVEN_OPTS%"

where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    call mvn %*
    exit /b %ERRORLEVEL%
)

if exist "%USERPROFILE%\.tools\apache-maven-3.9.6\bin\mvn.cmd" (
    call "%USERPROFILE%\.tools\apache-maven-3.9.6\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)

echo Error: Maven executable not found. Please install Maven or add to PATH.
exit /b 1
