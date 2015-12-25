SuperStrict

Import "t_match_mode.bmx"

Type t_match_half_time_wait Extends t_match_mode
	
	Method New()
		
		Self.display_controlled_player = False
		Self.display_statistics = True
		Self.display_radar = False
		
		team[HOME].side = -team[HOME].side
		team[AWAY].side = -team[AWAY].side
		
		match_status.kick_off_team = Not match_status.coin_toss
		
	End Method
	
	Method update()
		
		Super.update()
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_NORMAL)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
		Next
		
		If (team[HOME].fire1_down() Or team[AWAY].fire1_down() Or (Self.frame_diff(frame, Self.frame0) > 3*SECOND))
			match_status.period = match_status.SECOND_HALF
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_HALF_TIME_ENTER)
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
		
	End Method
	
End Type
