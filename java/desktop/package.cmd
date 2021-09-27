set launch4j="C:\Program Files (x86)\Launch4j\launch4jc.exe"
set jdk="D:\java\corretto\jdk17.0.0_35"

rmdir build\windows /q /s

rem %jdk%\bin\jar --update --file build\libs\desktop-1.0.jar --main-class com.ygames.ysoccer.desktop.DesktopLauncher

%launch4j% launch4j.xml

mkdir build\windows
copy build\libs\Charnego-Internatiolaff-Soccer.exe build\windows
xcopy /E/H %jdk% build\windows\jre\
xcopy /E/H ..\android\assets build\windows\