상황 요약 : 
1. aws에 ec2로 build를 하기 위해 jar파일을 생성하고 싶었다.
2. ./gradlew build 명령어를 해도 build/libs 파일이 만들어지지 않아서 
이클립스에서 run.configuration으로 gradle을 wrapper했다.

3.이후 git.bash 에서 ./gradlew build를 했고 BUILD SUCCESSFUL가 되었으나 libs 파일은 여전히 생기지 않았다. 
4. 혹시 gradle 버전 문제인가 해서 gradle --version을 찾았는데 gradle 버전은 적당하다.
5. ./gradle run에서 main class를 못찾는다고 한다.    
6. mainClass를 설정해줬다. 근데 또 오류가 난다. 이번에는 java version의 문제라고 한다. 그래서 
$ cat gradle-wrapper.properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-6.8-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
org.gradle.java.home=C:/DEV/zulu11

마지막에 org.gradle.java.home 을 추가해줬다.
  그래도 ./gradle run이 해결이 안되서 다른 방법을 찾다가 
 ./gradlew.bat 을 사용했다.  
  7.  ./gradlew.bat tasks 는 ./gradlew.bat 사용방법을 알려준다.
    
8. ./gradle build / run 처럼 ./gradlew.bat도 build/bootRun을 해봤는데 
$ ./gradlew.bat build 는 성공하나
$ ./gradlew.bat bootRun는 실패했다.
9. 하지만 여기에서 libs 폴더에 java -jar파일이 생겼다. 그래서 java -jar파일을 실행했는데 여전히mainClass를 못찾는 문제가 생긴다.

**
  ## 결론 : build 할때 mainClass가 왜 안잡히는지 이유를 모르겠다. 
    

로그 기록
======================================================================================================================
======================================================================================================================
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ll
total 512
-rw-r--r-- 1 MSH 197121  241 Mar 12 23:28 README.md
drwxr-xr-x 1 MSH 197121    0 Mar 14 16:13 bin/
drwxr-xr-x 1 MSH 197121    0 Mar 19 20:56 build/
-rw-r--r-- 1 MSH 197121 2426 Mar 20 15:13 build.gradle
drwxr-xr-x 1 MSH 197121    0 Mar 20 22:34 gradle/
-rwxr-xr-x 1 MSH 197121 5766 Mar 20 22:34 gradlew*
-rw-r--r-- 1 MSH 197121 2763 Mar 20 22:34 gradlew.bat
drwxr-xr-x 1 MSH 197121    0 Mar 12 13:15 src/
 
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew build

Welcome to Gradle 6.8!

Here are the highlights of this release:
 - Faster Kotlin DSL script compilation
 - Vendor selection for Java toolchains
 - Convenient execution of tasks in composite builds
 - Consistent dependency resolution

For more details see https://docs.gradle.org/6.8/release-notes.html


BUILD SUCCESSFUL in 8s
3 actionable tasks: 3 up-to-date

======================================================================================================================
======================================================================================================================
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew run

FAILURE: Build failed with an exception.

* What went wrong:
Task 'run' not found in root project 'huss'.

* Try:
Run gradlew tasks to get a list of available tasks. Run with --stack to get the stack trace. Run with --info or --debug option to get mot. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 6s 

======================================================================================================================
======================================================================================================================
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew --version

------------------------------------------------------------
Gradle 6.8
------------------------------------------------------------

Build time:   2021-01-08 16:38:46 UTC
Revision:     b7e82460c5373e194fb478a998c4fcfe7da53a7e

Kotlin:       1.4.20
Groovy:       2.5.12
Ant:          Apache Ant(TM) version 1.10.9 compiled on September 27
JVM:          11.0.19 (Azul Systems, Inc. 11.0.19+7-LTS)
OS:           Windows 10 10.0 amd64
  

MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew run

FAILURE: Build failed with an exception.

* Where:
Build file 'F:\▒▒Ʈ▒▒▒▒▒▒\huss\build.gradle' line: 10

* What went wrong:
A problem occurred evaluating root project 'huss'.
> Could not set unknown property 'mainClassName' for root project 'h org.gradle.api.Project.

* Try:
Run with --stacktrace option to get the stack trace. Run with --infooption to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

======================================================================================================================
======================================================================================================================
 

MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss/gradle/wrapper (master)
$ cat gradle-wrapper.properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-6.8-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
org.gradle.java.home=C:/DEV/zulu11

======================================================================================================================
======================================================================================================================

MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ cat build.gradle
plugins {
        id 'org.springframework.boot' version '2.4.5'
        id 'io.spring.dependency-management' version '1.0.11.RELEASE'
        id 'java'
}
repositories {
        mavenCentral()
}
bootJar {
    mainClassName = 'com.sch.AdvertisingApplication'
}
group = 'com.sch'
version = '0.0.1-SNAPSHOT'

sourceCompatibility = '1.8'

apply plugin: 'java'

configurations {
        compileOnly {
                extendsFrom annotationProcessor
        }
}



dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-aop'
        implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
        implementation 'org.springframework.boot:spring-boot-starter-jdbc'
        implementation 'org.springframework.boot:spring-boot-starter-security'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        implementation 'net.sf.ehcache:ehcache'
        implementation 'org.springframework.boot:spring-boot-starter-cache'
        implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0'
        implementation 'org.bgee.log4jdbc-log4j2:log4jdbc-log4j2-jdbc4.1:1.16'
        // implementation 'org.springframework.session:spring-session-core' // 순천향은 세션 사용안함
        developmentOnly 'org.springframework.boot:spring-boot-devtools'
        runtimeOnly 'mysql:mysql-connector-java'
        runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
        annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'

        compileOnly 'org.projectlombok:lombok'
        annotationProcessor 'org.projectlombok:lombok'

        implementation 'io.springfox:springfox-boot-starter:3.0.0'
        implementation 'io.jsonwebtoken:jjwt:0.9.1'

        implementation 'org.apache.commons:commons-collections4:4.4'
        implementation 'commons-io:commons-io:2.8.0'

        implementation group: 'org.apache.poi', name: 'poi', version: '3.11'
        implementation group: 'org.apache.poi', name: 'poi-ooxml', version: '3.11'

        implementation 'javax.mail:mail:1.4.7'

        implementation 'javax.xml.bind:jaxb-api:2.3.1'
        implementation 'com.googlecode.json-simple:json-simple:1.1.1'

        implementation 'com.google.zxing:javase:3.4.1'

        implementation group: 'commons-io', name: 'commons-io', version: '2.4'
        implementation group: 'commons-fileupload', name: 'commons-fileupload', version: '1.3.3'

        implementation group: 'com.google.firebase', name: 'firebase-admin', version: '7.1.0'

}

test {
        useJUnitPlatform()
}
task run(type: JavaExec, dependsOn: 'classes') {
        main = 'com.sch.AdvertisingApplication'
        classpath = sourceSets.main.runtimeClasspath
}


======================================================================================================================
======================================================================================================================  
======================================================================================================================
======================================================================================================================
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew.bat

> Task :help

Welcome to Gradle 6.8.

To run a build, run gradlew <task> ...

To see a list of available tasks, run gradlew tasks

To see a list of command-line options, run gradlew --help

To see more detail about a task, run gradlew help --task <task>

For troubleshooting, visit https://help.gradle.org

BUILD SUCCESSFUL in 5s
1 actionable task: 1 executed

MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ gradlew tasks
bash: gradlew: command not found

MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ cat gradlew.bat
@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%GRADLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
 
======================================================================================================================
======================================================================================================================
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew.bat tasks

> Task :tasks

------------------------------------------------------------
Tasks runnable from root project 'huss'
------------------------------------------------------------

Application tasks
-----------------
bootRun - Runs this project as a Spring Boot application.

Build tasks
-----------
assemble - Assembles the outputs of this project.
bootBuildImage - Builds an OCI image of the application using the output of the bootJar task
bootJar - Assembles an executable jar archive containing the main classes and their dependencies.
bootJarMainClassName - Resolves the name of the application's main class for the bootJar task.
bootRunMainClassName - Resolves the name of the application's main class for the bootRun task.
build - Assembles and tests this project.
buildDependents - Assembles and tests this project and all projects that depend on it.
buildNeeded - Assembles and tests this project and all projects it depends on.
classes - Assembles main classes.
clean - Deletes the build directory.
jar - Assembles a jar archive containing the main classes.
testClasses - Assembles test classes.

Build Setup tasks
-----------------
init - Initializes a new Gradle build.
wrapper - Generates Gradle wrapper files.

Documentation tasks
-------------------
javadoc - Generates Javadoc API documentation for the main source code.

Help tasks
----------
buildEnvironment - Displays all buildscript dependencies declared in root project 'huss'.
dependencies - Displays all dependencies declared in root project 'huss'.
dependencyInsight - Displays the insight into a specific dependency in root project 'huss'.
dependencyManagement - Displays the dependency management declared in root project 'huss'.
help - Displays a help message.
javaToolchains - Displays the detected java toolchains. [incubating]
outgoingVariants - Displays the outgoing variants of root project 'huss'.
projects - Displays the sub-projects of root project 'huss'.
properties - Displays the properties of root project 'huss'.
tasks - Displays the tasks runnable from root project 'huss'.

Verification tasks
------------------
check - Runs all checks.
test - Runs the unit tests.

Rules
-----
Pattern: clean<TaskName>: Cleans the output files of a task.
Pattern: build<ConfigurationName>: Assembles the artifacts of a configuration.
Pattern: upload<ConfigurationName>: Assembles and uploads the artifacts belonging to a configuration.

To see all tasks and more detail, run gradlew tasks --all

To see more detail about a task, run gradlew help --task <task>

BUILD SUCCESSFUL in 6s
1 actionable task: 1 executed
 
======================================================================================================================
======================================================================================================================  
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew.bat build

BUILD SUCCESSFUL in 11s
3 actionable tasks: 1 executed, 2 up-to-date

MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss (master)
$ ./gradlew.bat bootRun
> Task :bootRun FAILED
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':bootRun'.
> Failed to calculate the value of task ':bootRun' property 'mainClass'.
   > Main class name has not been configured and it could not be resolved

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 8s
4 actionable tasks: 2 executed, 2 up-to-date
 
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss/build (master)
$ ls -al
total 0
drwxr-xr-x 1 MSH 197121 0 Mar 19 20:56 ./
drwxr-xr-x 1 MSH 197121 0 Mar 19 18:36 ../
-rw-r--r-- 1 MSH 197121 0 Mar 21 01:01 bootRunMainClassName
drwxr-xr-x 1 MSH 197121 0 Mar 19 20:56 classes/
drwxr-xr-x 1 MSH 197121 0 Mar 21 00:41 libs/
drwxr-xr-x 1 MSH 197121 0 Mar 19 20:56 resources/
drwxr-xr-x 1 MSH 197121 0 Mar 20 16:06 tmp/
 
MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss/build/libs (master)
$ ls -al
total 91392
drwxr-xr-x 1 MSH 197121        0 Mar 21 00:41 ./
drwxr-xr-x 1 MSH 197121        0 Mar 19 20:56 ../
-rw-r--r-- 1 MSH 197121 93534578 Mar 21 01:00 huss-0.0.1-SNAPSHOT.jar

MSH@DESKTOP-BSKEU0A MINGW64 /f/포트폴리오/huss/build/libs (master)
$ java -jar huss-0.0.1-SNAPSHOT.jar
Exception in thread "main" java.lang.ClassNotFoundException: com.sch.AdvertisingApplication
        at java.base/java.net.URLClassLoader.findClass(URLClassLoader.java:476)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:589) 
 
