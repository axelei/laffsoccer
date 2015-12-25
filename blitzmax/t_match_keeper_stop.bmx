SuperStrict

Import "t_match_mode.bmx"

Type t_match_keeper_stop Extends t_match_mode
	
	Field keeper:t_player
	
	Method New()
		
		Self.display_ball_owner = True
		
		Self.keeper = ball.holder
		
		stats[Not(Self.keeper.team.index)].shot :+ 1
		
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				If (team[t].uses_automatic_input_device())
					If (ply <> Self.keeper)
						ply.set_input_device(ply.get_ai())
					Else
						ply.set_input_device(ply.team.input_device)
					EndIf
				EndIf
				If (ply <> Self.keeper)
					ply.set_state("state_reach_target")
				EndIf
			Next
		Next
		
		team[HOME].update_tactics(True)
		team[AWAY].update_tactics(True)
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			Self.update_players(True)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_NONE)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
			
		Next
		
		If (Not ball.holder)
			For Local t:Int = HOME To AWAY
				For Local i:Int = 0 To TEAM_SIZE -1
					Local p:t_player = team[t].lineup_at_index(i)
					If (p <> Self.keeper)
						p.set_state("state_stand_run")
					EndIf
				Next
			Next
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_MAIN)
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
