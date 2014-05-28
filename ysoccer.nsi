;YSoccer NSIS Installation Script

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "YSoccer"
  OutFile "ysoccer_setup.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\YSoccer"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\YSoccer" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user

;--------------------------------
;Variables

  Var StartMenuFolder

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "license\gpl.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  
  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\YSoccer" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "YSoccer" SecDummy

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File "main_opengl.exe"
  File "main_directx9.exe"
  File "main_directx7.exe"
  File /r "codes"
  File /r "data"
  File /r "images"
  File /r "keycodes"
  File /r "language"
  File /r "music"
  File /r "sfx"
  
  ;Store installation folder
  WriteRegStr HKCU "Software\YSoccer" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\YSoccer (OPENGL).lnk" "$INSTDIR\main_opengl.exe" "" "$INSTDIR\main_opengl.exe" 0
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\YSoccer (DX9).lnk" "$INSTDIR\main_directx9.exe" "" "$INSTDIR\main_directx9.exe" 0
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\YSoccer (DX7).lnk" "$INSTDIR\main_directx7.exe" "" "$INSTDIR\main_directx7.exe" 0
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "YSoccer components."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\main_opengl.exe"
  Delete "$INSTDIR\main_directx9.exe"
  Delete "$INSTDIR\main_directx7.exe"
  Delete "$INSTDIR\ysoccer.ini"
  Delete "$INSTDIR\Uninstall.exe"

  RMDir /r "$INSTDIR\codes"
  RMDir /r "$INSTDIR\data"
  RMDir /r "$INSTDIR\images"
  RMDir /r "$INSTDIR\keycodes"
  RMDir /r "$INSTDIR\language"
  RMDir /r "$INSTDIR\music"
  RMDir /r "$INSTDIR\sfx"
  RMDir "$INSTDIR"
  
  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
    
  Delete "$SMPROGRAMS\$StartMenuFolder\YSoccer (OPENGL).lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\YSoccer (DX9).lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\YSoccer (DX7).lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"
  
  DeleteRegKey /ifempty HKCU "Software\Modern UI Test"

SectionEnd
