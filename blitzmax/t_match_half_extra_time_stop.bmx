SuperStrict

Import "t_match_mode.bmx"

Type t_match_half_extra_time_stop Extends t_match_mode
	
	Method New()
		
		Self.display_controlled_player = False
		
		If (game_settings.sound_enabled)
			SetChannelVolume(channel.endgame, 0.1 * location_settings.sound_vol)
			PlaySound(sound.endgame, channel.endgame)
		EndIf
		
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				If (team[t].uses_automatic_input_device())
					ply.set_input_device(ply.get_ai())
				EndIf
				ply.set_state("state_idle")
			Next
		Next
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			ball.in_field_keep()
			
			Self.update_players(True)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_NORMAL)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
			
		Next
		
		If (Self.frame_diff(frame, Self.frame0) > 3*SECOND)
			
			ball.set_position(0, 0)
			ball.update_prediction()
			
			camera.offx = 0
			camera.offy = 0
			
			team[HOME].side = -team[HOME].side
			team[AWAY].side = -team[AWAY].side
			
			match_status.kick_off_team = Not match_status.coin_toss
			
			match_status.period = match_status.SECOND_EXTRA_TIME
			match_status.timer = match_status.length * 105 / 90
			
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
