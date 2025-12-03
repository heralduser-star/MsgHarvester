@echo off
REM gradlew.bat (standard Gradle wrapper batch)
setlocal
set DIRNAME=%~dp0
set APP_HOME=%DIRNAME%..
"%JAVA_HOME%\bin\java" -classpath "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
