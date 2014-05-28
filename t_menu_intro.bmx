SuperStrict

Import "t_game_mode.bmx"

Type t_menu_intro Extends t_game_mode

	Field license:String[6]

	Method New()
		Self.license[0] = "YSoccer 14, Copyright (C) 2014"
		Self.license[1] = "by Massimo Modica, Daniele Giannarini, Marco Modica"
		Self.license[2] = ""
		Self.license[3] = "YSoccer comes with ABSOLUTELY NO WARRANTY; for details press 'W'."
		Self.license[4] = "This is free software, and you are welcome to redistribute it"
		Self.license[5] = "under certain conditions; press 'C' for details."
	End Method

	Method update()
		If KeyDown(KEY_C)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_COPYING)
		ElseIf KeyDown(KEY_W) 
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_WARRANTY)
		ElseIf GetChar() Or MouseDown(1)
			game_action_queue.push(AT_FADE_OUT)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
			game_action_queue.push(AT_FADE_IN)
		EndIf
	End Method
	
	Method render()
		Cls
		SetColor(light, light, light)
		For Local i:Int = 0 To 5
			Draw_Text Self.license[i], 512, 312 +12*i, 0
		Next
		Draw_Text "Press any key to continue", 512, 452, 0
	End Method
		
End Type
