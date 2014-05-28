SuperStrict

Import "t_match_mode.bmx"

Type t_match_throw_in Extends t_match_mode
	
	Field throw_in_team:t_team
	Field throw_in_player:t_player
	Field is_throwing_in:Int
	
	Method New()
		
		Self.display_ball_owner = True
		Self.display_score = True
		Self.throw_in_team = team[Not(ball.owner_last.team.index)]
		
	End Method
	
	Method on_resume()
		
		Self.is_throwing_in = False
		
		Self.throw_in_team.update_frame_distance()
		Self.throw_in_team.find_nearest()
		Self.throw_in_player = Self.throw_in_team.near1
		
		Self.throw_in_player.set_target(ball.x, ball.y)
		Self.throw_in_player.set_state("state_reach_target")
		
	End Method
	
	Method on_pause()
		
		team[HOME].update_tactics()
		team[AWAY].update_tactics()
		
		ball.set_position(match_status.throw_in_x, match_status.throw_in_y)
		ball.update_prediction()
		
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
		
		If (move = False And Self.is_throwing_in = False)
			If (game_settings.sound_enabled)
				SetChannelVolume(channel.whistle, 0.1 * location_settings.sound_vol)
				PlaySound(sound.whistle, channel.whistle)
			EndIf
			
			Self.throw_in_player.set_state("state_throw_in_angle")
			If (Self.throw_in_player.team.uses_automatic_input_device())
				Self.throw_in_player.set_input_device(Self.throw_in_player.team.input_device)
			EndIf
			Self.is_throwing_in = True
		EndIf
		
		If (Abs(ball.x) < TOUCH_LINE)
			For Local t:Int = HOME To AWAY
				For Local i:Int = 0 To TEAM_SIZE -1
					Local ply:t_player = team[t].lineup_at_index(i)
					If (ply <> Self.throw_in_player)
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
