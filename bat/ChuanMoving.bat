@echo on

set MOVING_DATE=%date:~10,4%_%date:~7,2%_%date:~4,2%
echo %MOVING_DATE%
REM -- Set folder location
set DESKTOP=C:\Users\525897\Desktop
set GOOGLE_MAP_DIR="C:\Users\525897\Desktop\Google Map"
set MAP_PLAN_DIR="C:\Users\525897\Desktop\Map Plan"

if not exist %MAP_PLAN_DIR% mkdir %MAP_PLAN_DIR%
if not exist %GOOGLE_MAP_DIR% mkdir %GOOGLE_MAP_DIR%

REM -- Loop through Desktop to locate the file
FOR /f "tokens=*" %%a in ('dir /b /a:-d "%DESKTOP%\IMG*.jpg"') DO (
if not exist %MAP_PLAN_DIR%\%MOVING_DATE% mkdir %MAP_PLAN_DIR%\%MOVING_DATE%
move %%a %MAP_PLAN_DIR%\%MOVING_DATE%\%%a
)

FOR /f "tokens=*" %%a in ('dir /b /a:-d "%DESKTOP%\m*.jpg"') DO (
if not exist %GOOGLE_MAP_DIR%\%MOVING_DATE% mkdir %GOOGLE_MAP_DIR%\%MOVING_DATE%
move %%a %GOOGLE_MAP_DIR%\%MOVING_DATE%\%%a
)

pause