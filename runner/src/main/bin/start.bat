@echo off

rem Set the log dir path
set logsDir=../logs
set gcLogFile=%logsDir%/gc.log

rem Create the logs directory
IF not exist "%logsDir%" (md "%logsDir%")

rem Should be set in case  not set by env
rem JAVA_HOME=

rem JVM arguments - GC log options
set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:-UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=5M -Xloggc:%gcLogFile%"

rem JVM arguments - GC options. using parallel CPU(s) for young gen (with desired pause time) & concurrent mark and sweep for old gen
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseParNewGC -XX:MaxGCPauseMillis=200 -XX:+UseConcMarkSweepGC"

rem JVM arguments - Heap dump on OutOfMemory.
set "JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%logsDir%"

rem JVM arguments - remote debug argument
set "JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

rem JVM arguments - enable jmx remote access
set "JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

rem JVM arguments - print startup JVM argument
set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintCommandLineFlags"

rem Start Server
"%JAVA_HOME%/bin/java" %JAVA_OPTS% -jar ../webapp/WEB-INF/lib/task-list-runner-1.0-SNAPSHOT.jar
