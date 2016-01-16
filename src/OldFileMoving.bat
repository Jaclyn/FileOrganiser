@echo on

set MOVING_DATE=%date:~10,4%_%date:~7,2%_%date:~4,2%
echo %MOVING_DATE%
REM -- Set folder location
set TARGET_DIR=%cd%
set GOOGLE_MAP_DIR="C:\Users\525897\Desktop\Google Map"
set MAP_PLAN_DIR="C:\Users\525897\Desktop\Map Plan"

REM -- Get file creation date
FOR /f "tokens=*" %%a in ('dir /b /a:-d "%TARGET_DIR%\*.jpg"') DO (
  set FILE_YEA in (wmic datafile where name='%TARGET_DIR%\%%a' get 'LAST MODIFIED')
)

pause