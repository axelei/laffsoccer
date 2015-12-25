SuperStrict

Import "t_match_mode.bmx"

Type t_match_pause Extends t_match_mode
	
	Field key_pause:Int
	Field waiting_no_p_key:Int
	
	Method New()
		
		Self.display_controlled_player = False
		Self.display_time = False
		Self.display_radar = False
		
		Self.key_pause = KeyDown(KEY_P)
		Self.waiting_no_p_key = True
		
	End Method
	
	Method update()
		
		Super.update()
		
		''resume on 'P'
		If (Self.waiting_no_p_key)
			If (Not Self.key_pause)
				Self.waiting_no_p_key = False
			EndIf
		Else
			If (Not KeyDown(KEY_P) And Self.key_pause = 1)
				game_action_queue.push(AT_RESTORE_FOREGROUND)
				Return
			EndIf
		EndIf
		Self.key_pause = KeyDown(KEY_P)
		
		''resume on fire button
		For Local d:t_input = EachIn input_devices
			If (d.fire1_down())
				game_action_queue.push(AT_RESTORE_FOREGROUND)
				Return
			EndIf
		Next
		
		''resume on 'Esc'
		If (KeyDown(KEY_ESCAPE))
			game_action_queue.push(AT_RESTORE_FOREGROUND)
			Return
		EndIf
		
	End Method
	
	Method render()

		Super.render()
		
		DrawImage(img_pause, 34, 28)
		
	End Method
	
End Type
