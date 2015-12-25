SuperStrict

Import "t_match_mode.bmx"

Type t_match_end_positions Extends t_match_mode
	
	Method New()
		
		Self.display_controlled_player = False
		Self.display_statistics = True
		Self.display_time = False
		Self.display_wind_vane = False
		Self.display_radar = False
		
		match_status.period = match_status.UNDEFINED
		
		ball.set_position(0, 0)
		ball.update_prediction()
		
		camera.offx = 0
		camera.offy = 0
		
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				ply.set_tx(TOUCH_LINE +80)
				ply.set_ty(0)
				ply.set_state("state_outside")
			Next
		Next
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		Local move:Int
		For Local subframe:Int = 1 To SUBFRAMES
			
			move = Self.update_players(False)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_FAST)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_FAST)
			
		Next
		
		If (move = False)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_END)
			Return
		EndIf
		
		If (KeyDown(KEY_ESCAPE))
			Self.quit_match()
			Return
		EndIf
		
		If (KeyDown(KEY_R))
			Self.replay()
			Return
		EndIf
		
		If (KeyDown(KEY_P))
			Self.pause()
			Return
		EndIf
		
	End Method
	
End Type
