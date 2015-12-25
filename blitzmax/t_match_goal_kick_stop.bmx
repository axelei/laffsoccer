SuperStrict

Import "t_match_mode.bmx"

Type t_match_goal_kick_stop Extends t_match_mode
	
	Field side_x:Int
	Field side_y:Int
	
	Method New()
		
		Self.display_controlled_player = False
		
		If (game_settings.sound_enabled)
			SetChannelVolume(channel.whistle, 0.1 * location_settings.sound_vol)
			PlaySound(sound.whistle, channel.whistle)
		EndIf
		
		Self.side_x = ball.side.x
		Self.side_y = ball.side.y
		
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				If (team[t].uses_automatic_input_device())
					ply.set_input_device(ply.get_ai())
				EndIf
				ply.set_state("state_reach_target")
			Next
		Next
		
		Local goal_kick_team:t_team = team[Not(ball.owner_last.team.index)]
		goal_kick_team.lineup_at_position(0).set_target(ball.x / 4, goal_kick_team.side * (GOAL_LINE -8))
		team[Not goal_kick_team.index].lineup_at_position(0).set_target(0, team[Not goal_kick_team.index].side * (GOAL_LINE -8))
		team[HOME].update_tactics(True)
		team[AWAY].update_tactics(True)
		
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
			ball.set_position((GOAL_AREA_W / 2) * Self.side_x, (GOAL_LINE -GOAL_AREA_H) * Self.side_y)
			ball.update_prediction()
			
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_GOAL_KICK)
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
