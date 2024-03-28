@REM Copyright (c) 2004 NEWGEN All Rights Reserved.

@REM ************************************************************************************************
@REM Modify these variables to match your environment
	cls
	set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_202"
	set JTS_LIBPATH=lib
	set MYCLASSPATH=bin
	set LIBCLASSPATH=%JTS_LIBPATH%\DataEncryption.jar;%JTS_LIBPATH%\log4j-1.2.14.jar;%JTS_LIBPATH%\ngejbcallbroker.jar;%JTS_LIBPATH%\omnishared.jar;%JTS_LIBPATH%\SecurityAPI.jar;%JTS_LIBPATH%\wfdesktop.jar;%JTS_LIBPATH%\wfsclient.jar;%JTS_LIBPATH%\com.ibm.ws.ejb.thinclient_8.5.0.jar;%JTS_LIBPATH%\stubswfscustom.jar;%JTS_LIBPATH%\stubwfs.jar;%JTS_LIBPATH%\Amazon.jar;%JTS_LIBPATH%\aws-java-sdk-1.11.40.jar;%JTS_LIBPATH%\azure-storage-5.0.0.jar;%JTS_LIBPATH%\commons-io-2.0.1.jar;%JTS_LIBPATH%\ISPack.jar;%JTS_LIBPATH%\jdts.jar;%JTS_LIBPATH%\NIPLJ.jar;%JTS_LIBPATH%\nsms.jar;%JTS_LIBPATH%\commons-codec-1.7.jar;%JTS_LIBPATH%\ejb.jar;%JTS_LIBPATH%\ejbclient.jar;%JTS_LIBPATH%\stubs.jar;%JTS_LIBPATH%\itextpdf-5.3.2.jar;%JTS_LIBPATH%\json-simple-1.1.1.jar;%JTS_LIBPATH%\guava-17.0.jar;%JTS_LIBPATH%\poi-3.8-20120326.jar;%JTS_LIBPATH%\poi-ooxml-3.8-20120326.jar;%JTS_LIBPATH%\poi-ooxml-schemas-3.8-20120326.jar;%JTS_LIBPATH%\dom4j-1.6.jar;%JTS_LIBPATH%\xmlbeans-2.6.0.jar;%JTS_LIBPATH%\poi-3.17.jar;%JTS_LIBPATH%\poi-ooxml-3.17.jar;%JTS_LIBPATH%\poi-ooxml-schemas-3.17.jar
@REM ************************************************************************************************

@REM ************************************************************************************************
@REM Compile SockectClient

	%JAVA_HOME%\bin\javac -d %MYCLASSPATH% -classpath %LIBCLASSPATH%;%MYCLASSPATH% src\com\newgen\common\*.java
	%JAVA_HOME%\bin\javac -d %MYCLASSPATH% -classpath %LIBCLASSPATH%;%MYCLASSPATH% src\com\newgen\CPF\*.java
	%JAVA_HOME%\bin\javac -d %MYCLASSPATH% -classpath %LIBCLASSPATH%;%MYCLASSPATH% src\com\newgen\main\*.java

	pause
@REM ************************************************************************************************