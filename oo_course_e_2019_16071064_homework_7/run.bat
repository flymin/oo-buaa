@echo off
set num=0
:start
set /a num+=1
echo ================No. %num% time======================= >> err.txt
python gen.py | "C:\Program Files\Java\jdk-11.0.2\bin\java.exe" -cp out\production\oo_course_e_2019_16071064_homework_6;..\..\elevator-test-suit-0-3.jar Main >> err.txt 2>&1
echo ================    over      ======================= >> err.txt
goto start