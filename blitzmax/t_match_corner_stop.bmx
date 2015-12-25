SuperStrict

Import "t_match_mode.bmx"

Type t_match_corner_stop Extends t_match_mode
	
	Field corner_x:Int
	Field corner_y:Int
	
	Method New()
		
		If (team[HOME].side = -ball.side.y)
			stats[HOME].corn = stats[HOME].corn +1
		Else
			stats[AWAY].corn = stats[AWAY].corn +1
		EndIf
		
		If (game_settings.sound_enabled)
			SetChannelVolume(channel.whistle, 0.1 * location_settings.sound_vol)
			PlaySound(sound.whistle, channel.whistle)
		EndIf
		
		Self.corner_x = (TOUCH_LINE -12) * ball.side.x
		Self.corner_y = (GOAL_LINE -12) * ball.side.y
		
		''set the player targets relative to corner zone
		''even before moving the ball itself
		ball.update_zone(Self.corner_x, Self.corner_y, 0, 0)
		team[HOME].update_tactics()
		team[AWAY].update_tactics()
		team[HOME].lineup_at_index(0).set_target(0, team[HOME].side * (GOAL_LINE -8))
		team[AWAY].lineup_at_index(0).set_target(0, team[AWAY].side * (GOAL_LINE -8))
		
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
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			ball.in_field_keep()
			ball.collision_goal()
			ball.collision_jumpers()
			ball.collision_net_out()
			
			Self.update_players(True)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_NORMAL)
			vcamera_y[frame] = camera.updatey(CF_NONE)
			
		Next
		
		If ((ball.v < 5) And (ball.vz < 5))
			ball.set_position(Self.corner_x, Self.corner_y)
			ball.update_prediction()
			
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_CORNER_KICK)
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
