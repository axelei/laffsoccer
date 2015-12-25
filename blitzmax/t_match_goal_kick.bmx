SuperStrict

Import "t_match_mode.bmx"

Type t_match_goal_kick Extends t_match_mode
	
	Field goal_kick_team:t_team
	Field goal_kick_player:t_player
	Field is_kicking:Int
	
	Method New()
		
		Self.display_ball_owner = True
		Self.display_score = True
		
		Self.goal_kick_team = team[Not(ball.owner_last.team.index)]
		
		camera.offx = -30 * ball.side.x
		camera.offy = -30 * ball.side.y
		
	End Method
	
	Method on_resume()
		
		Self.is_kicking = False
		
		Self.goal_kick_player = Self.goal_kick_team.lineup_at_position(0)
		Self.goal_kick_player.set_target(ball.x, ball.y +6 * ball.side.y)
		Self.goal_kick_player.set_state("state_reach_target")
		
	End Method
	
	Method on_pause()
		
		Self.goal_kick_player.set_target(ball.x / 4, Self.goal_kick_team.side * (GOAL_LINE -8))
		team[Not Self.goal_kick_team.index].lineup_at_position(0).set_target(0, team[Not Self.goal_kick_team.index].side * (GOAL_LINE -8))
		team[HOME].update_tactics(True)
		team[AWAY].update_tactics(True)
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		Local move:Int
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			ball.in_field_keep()
			
			move = Self.update_players(True)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_FAST)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_FAST)
			
		Next
		
		If (move = False And Self.is_kicking = False)
			If (game_settings.sound_enabled)
				SetChannelVolume(channel.whistle, 0.1 * location_settings.sound_vol)
				PlaySound(sound.whistle, channel.whistle)
			EndIf
			
			Self.goal_kick_player.set_state("state_goal_kick")
			If (Self.goal_kick_player.team.uses_automatic_input_device())
				Self.goal_kick_player.set_input_device(Self.goal_kick_player.team.input_device)
			EndIf
			Self.is_kicking = True
		EndIf
		
		If (ball.v > 0)
			For Local t:Int = HOME To AWAY
				For Local i:Int = 0 To TEAM_SIZE -1
					Local ply:t_player = team[t].lineup_at_index(i)
					If (ply <> Self.goal_kick_player)
						ply.set_state("state_stand_run")
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
		
		''BENCH
		Self.bench(team[HOME], team[HOME].fire2_down())
		Self.bench(team[AWAY], team[AWAY].fire2_down())
		
	End Method
	
End Type
