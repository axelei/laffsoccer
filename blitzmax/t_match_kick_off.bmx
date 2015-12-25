SuperStrict

Import "t_match_mode.bmx"

Type t_match_kick_off Extends t_match_mode
	
	Field kick_off_player:t_player
	Field is_kicking_off:Int
	
	Method New()
		
		Self.display_ball_owner = True
		Self.display_score = True
		
	End Method
	
	Method on_resume()
		
		Self.is_kicking_off = False
		
		If (game_settings.sound_enabled)
			SetChannelVolume(channel.whistle, 0.1 * location_settings.sound_vol)
			PlaySound(sound.whistle, channel.whistle)
		EndIf
		
		Local kick_off_team:t_team = team[match_status.kick_off_team]
		kick_off_team.update_frame_distance()
		kick_off_team.find_nearest()
		Self.kick_off_player = kick_off_team.near1
		
		Self.kick_off_player.set_tx(ball.x -7*Self.kick_off_player.team.side +1)
		Self.kick_off_player.set_ty(ball.y +1)
		
		If (kick_off_team.uses_automatic_input_device())
			Self.kick_off_player.set_input_device(kick_off_team.input_device)
		EndIf
		
	End Method
	
	Method on_pause()
		Self.set_starting_positions(match_status.kick_off_team)
		Self.kick_off_player.set_state("state_reach_target")
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		Local move:Int
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			move = Self.update_players(True)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_FAST)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_FAST)
			
		Next
		
		If (move = False And Self.is_kicking_off = False)
			Self.kick_off_player.set_state("state_kick_off")
			Self.is_kicking_off = True
		EndIf
		
		If (dist(ball.x, ball.y, 0, 0) > 10)
			For Local t:Int = HOME To AWAY
				For Local i:Int = 0 To TEAM_SIZE -1
					Local ply:t_player = team[t].lineup_at_index(i)
					If (ply <> Self.kick_off_player)
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
