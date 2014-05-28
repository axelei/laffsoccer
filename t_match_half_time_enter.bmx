SuperStrict

Import "t_match_mode.bmx"

Type t_match_half_time_enter Extends t_match_mode
	
	Field entering_counter:Int
	
	Method New()
		
		Self.display_controlled_player = False
		Self.set_starting_positions(match_status.kick_off_team)
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		If ((Self.entering_counter Mod 4) = 0 And Self.entering_counter / 4 < TEAM_SIZE)
			For Local t:Int = HOME To AWAY
				Local i:Int = Self.entering_counter / 4
				Local ply:t_player = team[t].lineup_at_index(i)
				ply.set_state("state_reach_target")
			Next
		EndIf
		Self.entering_counter :+ 1
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			Self.update_players(False)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_FAST)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_FAST)
			
		Next
		
		If (Self.entering_counter / 4 = TEAM_SIZE)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_STARTING_POSITIONS)
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
