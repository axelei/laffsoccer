SuperStrict

Import "t_match_mode.bmx"

Type t_match_goal Extends t_match_mode
	
	Field replay_done:Int
	
	Method New()
		
		Self.display_goal_scorer = True
		Self.display_controlled_player = False
		Self.replay_done = False
		
		If (team[HOME].side = ball.side.y)
			match_status.kick_off_team = HOME
		ElseIf (team[AWAY].side = ball.side.y)
			match_status.kick_off_team = AWAY
		Else
			RuntimeError("cannot decide kick_off_team!")
		EndIf
		
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				If (team[t].uses_automatic_input_device())
					ply.set_input_device(ply.get_ai())
				EndIf
			Next
		Next
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		''set states
		If (g_goal.typ = t_goal.OWN_GOAL)
			Self.set_states_for_own_goal()
		Else
			Self.set_states_for_goal()
		EndIf
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			ball.collision_goal()
			ball.collision_net()
			
			Self.update_players(True)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			If ((ball.v > 0) Or (ball.vz <> 0))
				''follow ball
				vcamera_x[frame] = camera.updatex(CF_NONE)
				vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
			Else
				''follow scorer
				vcamera_x[frame] = camera.updatex(CF_TARGET, CS_FAST, g_goal.player.x)
				vcamera_y[frame] = camera.updatey(CF_TARGET, CS_FAST, g_goal.player.y)
			EndIf
		Next
		
		If ((ball.v = 0) And (ball.vz = 0) And (Self.frame_diff(frame, Self.frame0) > 3*SECOND))
			
			If ((match_settings.auto_replay = True) And Not Self.replay_done)
				Self.replay()
				Self.replay_done = True
				Return
			Else
				''record highlights
				record_action()
				
				ball.set_position(0, 0)
				ball.update_prediction()
				camera.offx = 0
				camera.offy = 0
				
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_STARTING_POSITIONS)
				Return
			EndIf
			
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
	
	Method set_states_for_goal()
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				Local active_state:t_state = ply.get_active_state()
				If (active_state.check_name("state_stand_run") Or active_state.check_name("state_keeper_positioning"))
					ply.set_tx(ply.x)
					ply.set_ty(ply.y)
					If ((t = g_goal.player.team.index) And (ply = g_goal.player))
						ply.set_state("state_goal_scorer")
					ElseIf ((t = g_goal.player.team.index) And (dist(ply.x, ply.y, g_goal.player.x, g_goal.player.y) < 150 * Rand(0, 3)))
						ply.set_state("state_goal_mate")
					Else
						ply.set_state("state_reach_target")
					EndIf
				EndIf
			Next
		Next
	End Method
	
	Method set_states_for_own_goal()
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				Local active_state:t_state = ply.get_active_state()
				If (active_state.check_name("state_stand_run") Or active_state.check_name("state_keeper_positioning"))
					ply.set_tx(ply.x)
					ply.set_ty(ply.y)
					If (ply = g_goal.player)
						ply.set_state("state_own_goal_scorer")
					Else
						ply.set_state("state_reach_target")
					EndIf
				EndIf
			Next
		Next
	End Method
	
End Type
