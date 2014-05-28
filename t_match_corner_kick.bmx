SuperStrict

Import "t_match_mode.bmx"

Type t_match_corner_kick Extends t_match_mode
	
	Field corner_kick_team:t_team
	Field corner_kick_player:t_player
	Field is_kicking:Int
	
	Method New()
		
		Self.display_ball_owner = True
		Self.display_score = True
		
		Self.corner_kick_team = team[Not(ball.owner_last.team.index)]
		
		camera.offx = -30 * ball.side.x
		camera.offy = -30 * ball.side.y
		
		If (game_settings.sound_enabled And match_settings.commentary)
			SetChannelVolume(chn_comm, 0.1 * location_settings.sound_vol)
			PlaySound(comm_corner[Rand(0, comm_corner.length -1)], chn_comm)
		EndIf
		
	End Method
	
	Method on_resume()
		
		Self.is_kicking = False
		
		Self.corner_kick_team.update_frame_distance()
		Self.corner_kick_team.find_nearest()
		Self.corner_kick_player = Self.corner_kick_team.near1
		
		Self.corner_kick_player.set_target(ball.x +7 * ball.side.x, ball.y)
		Self.corner_kick_player.set_state("state_reach_target")
		
	End Method
	
	Method on_pause()
		
		team[HOME].update_tactics()
		team[AWAY].update_tactics()
		
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
			
			Self.corner_kick_player.set_state("state_corner_kick_angle")
			If (Self.corner_kick_player.team.uses_automatic_input_device())
				Self.corner_kick_player.set_input_device(Self.corner_kick_player.team.input_device)
			EndIf
			Self.is_kicking = True
		EndIf
		
		If (ball.v > 0)
			For Local t:Int = HOME To AWAY
				For Local i:Int = 0 To TEAM_SIZE -1
					Local ply:t_player = team[t].lineup_at_index(i)
					If (ply <> Self.corner_kick_player)
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
