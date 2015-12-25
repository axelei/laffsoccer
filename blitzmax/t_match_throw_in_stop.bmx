SuperStrict

Import "t_match_mode.bmx"

Type t_match_throw_in_stop Extends t_match_mode
	
	Method New()
		
		If (game_settings.sound_enabled)
			SetChannelVolume(channel.whistle, 0.1 * location_settings.sound_vol)
			PlaySound(sound.whistle, channel.whistle)
		EndIf
		
		match_status.throw_in_x = ball.side.x * TOUCH_LINE
		match_status.throw_in_y = ball.y
		
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				If (team[t].uses_automatic_input_device())
					ply.set_input_device(ply.get_ai())
				EndIf
				ply.set_state("state_reach_target")
			Next
		Next
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		Local move:Int
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			ball.in_field_keep()
			
			move = Self.update_players(True)
			team[HOME].update_tactics()
			team[AWAY].update_tactics()
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_NONE)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
			
		Next
		
		If (Not move)
			ball.set_position(match_status.throw_in_x, match_status.throw_in_y)
			ball.update_prediction()
			
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_THROW_IN)
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
