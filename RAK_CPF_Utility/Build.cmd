@REM Copyright (c) 2004 NEWGEN All Rights Reserved.

@REM *********************JAR BUILDING***************************************************************************
@REM Modify these variables to match your environment
	cls
	set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_202"
	set MYCLASSPATH=bin
	set JARPATH=..
@REM ************************************************************************************************

 	cd %MYCLASSPATH%

@REM mqsocketserver jar
    %JAVA_HOME%\bin\jar -cvfm %JARPATH%\rak_cpf_utility.jar ..\MANIFEST.MF com\newgen\common\*.class
    %JAVA_HOME%\bin\jar -uvf %JARPATH%\rak_cpf_utility.jar com\newgen\CPF\*.class
	%JAVA_HOME%\bin\jar -uvf %JARPATH%\rak_cpf_utility.jar com\newgen\main\*.class
	%JAVA_HOME%\bin\jar -uvf %JARPATH%\rak_cpf_utility.jar ..\src\com\newgen\common\*.java
	%JAVA_HOME%\bin\jar -uvf %JARPATH%\rak_cpf_utility.jar ..\src\com\newgen\CPF\*.java
	%JAVA_HOME%\bin\jar -uvf %JARPATH%\rak_cpf_utility.jar ..\src\com\newgen\main\*.java
	pause
@REM ************************************************************************************************