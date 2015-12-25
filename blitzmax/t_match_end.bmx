SuperStrict

Import "t_match_mode.bmx"

Type t_match_end Extends t_match_mode
	
	Field timer:Int
	
	Method New()
		
		Self.display_controlled_player = False
		Self.display_statistics = True
		Self.display_time = False
		Self.display_wind_vane = False
		Self.display_radar = False
		
		Self.timer = 0
		
		match_status.period = match_status.UNDEFINED
		
	End Method
	
	Method update()
		
		Super.update()
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			Self.timer = Self.timer +1
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_NORMAL)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
		Next
		
		If (KeyDown(KEY_H) And (hl_recorded > 0))
			current_highlight = 0
			game_action_queue.push(AT_FADE_OUT)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_SHOW_HIGHLIGHT)
			game_action_queue.push(AT_FADE_IN)
			Return
		EndIf
		
		If (team[HOME].fire1_up() Or team[AWAY].fire1_up())
			Self.quit_match()
			Return
		EndIf
		
		If (KeyDown(KEY_ESCAPE))
			Self.quit_match()
			Return
		EndIf
		
		If (KeyDown(KEY_P))
			Self.pause()
			Return
		EndIf
		
		If (Self.timer > 20*SECOND)
			Self.quit_match()
			Return
		EndIf
		
	End Method
	
End Type
