SuperStrict

Import "t_match_mode.bmx"

Type t_training_main Extends t_match_mode

	Field missed_goal:Int
	Field last_trained:t_player
	
	Method New()
		
		Self.display_ball_owner = True
		Self.display_time = False
		Self.display_radar = False
		
		Self.missed_goal = False
		
		Self.last_trained = team[HOME].lineup_at_index(0)
		
		ball.set_position(0, team[HOME].side * (GOAL_LINE - 1.5*PENALTY_AREA_H))
		
		''set positions
		Local i:Int = 0
		For Local ply:t_player = EachIn team[HOME].lineup
			ply.v      = 0
			ply.a      = 270
			ply.speed  = 158
			ply.x      = 18 * (-team[HOME].lineup.Count() +2*i) + 8*Cos(70*(ply.number))
			ply.y      = team[HOME].side * (15 +5 * (i Mod 2)) + 8*Sin(70*(ply.number))
			i = i +1
		Next
		
		''set states
		For Local ply:t_player = EachIn team[HOME].lineup
			If (ply = team[HOME].lineup_at_index(0) And ply.role = PR.GOALKEEPER)
				ply.set_state("state_keeper_positioning")
			Else
				ply.set_state("state_stand_run")
			EndIf
		Next
		
		Local assigned_devices:Int = 0
		i = 10
		While (assigned_devices < input_devices.Count() And i < team[HOME].lineup.Count())
			Local ply:t_player = team[HOME].lineup_at_index(i)
			If (ply.role <> PR.GOALKEEPER)
				Local angle:Int = 90 +Ceil(assigned_devices/2.0) * 20 * Sgn(assigned_devices)
				ply.tx = 200 * Cos(angle)
				ply.ty = team[HOME].side * (GOAL_LINE -GOAL_AREA_H - 200*Sin(angle))
				assigned_devices = assigned_devices +1
			EndIf
			If (i <= 10)
				i = i -1
				If (i = 0)
					i = 11
				EndIf
			Else
				i = i +1
			EndIf
		Wend
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		team[HOME].side = ball.side.y
		
		For Local ply:t_player = EachIn team[HOME].lineup
			If (ply.input_device = ply.get_ai() And ply <> team[HOME].lineup_at_index(0))
				ply.watch_ball()
			EndIf
		Next
		
		''crowd chants
		If (game_settings.sound_enabled)
			If ((Abs(MilliSecs()) Mod 16000) < 2000) And (Not ChannelPlaying(channel.chant))
				SetChannelVolume(channel.chant, 0.1 * location_settings.sound_vol * game_settings.crowd_chants)
				PlaySound(sound.chant, channel.chant)
			EndIf
		EndIf
		
		''DEBUG 'ALT + D' **
		If KeyDown(KEY_LALT) And KeyDown(KEY_D)
			debug = Not(debug)
			Delay 100
			FlushKeys()
		EndIf
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			
			If (ball.zmax < ball.z)
				ball.zmax = ball.z ''** useful to set speed of the shots
			EndIf
			
			''--- COLLISIONS & EVENTS CHECK ---
			
			If (team[HOME].lineup_at_index(0).role = PR.GOALKEEPER)
				team[HOME].lineup_at_index(0).keeper_collision()
			EndIf
			
			ball.collision_flagposts()
			
			ball.collision_goal()
			
			ball.in_field_keep()
			
			''goal/corner/goal-kick
			If (ball.y*ball.side.y => (GOAL_LINE + BALL_R))
				''goal
				If (is_in(ball.x, -POST_X, POST_X) And (ball.z <= CROSSBAR_H))
					ball.collision_net()
				Else
					''corner/goal-kick
					ball.collision_jumpers()
					ball.collision_net_out()
				EndIf
				If (ball.v = 0)
					ball.x = Self.last_trained.x +(BALL_R -1) * Cos(Self.last_trained.a)
					ball.y = Self.last_trained.y +(BALL_R -1) * Sin(Self.last_trained.a)
				EndIf
			EndIf
			
			''missed-goal sound
			If (Abs(ball.y*ball.side.y -GOAL_LINE) < 20) And is_in(ball.x, -POST_X-20, POST_X+20 ) And (ball.z < CROSSBAR_H +10)
				If (Self.missed_goal = False)
					Self.missed_goal = True
					If (game_settings.sound_enabled)
						SetChannelVolume(channel.missgoal, 0.1 * location_settings.sound_vol)
						PlaySound(sound.missgoal, channel.missgoal)
					EndIf
				EndIf
			Else
				Self.missed_goal = False
			EndIf
			
			''--- THROW-IN ---
			If (Abs(ball.x) > (TOUCH_LINE +BALL_R))
				If (ball.v = 0)
					ball.x = Self.last_trained.x +(BALL_R -1) * Cos(Self.last_trained.a)
					ball.y = Self.last_trained.y +(BALL_R -1) * Sin(Self.last_trained.a)
				EndIf
			EndIf
			
			''--- UPDATE PLAYERS ---
			team[HOME].update_players(True)
			
			If ((frame Mod REFRATE) = 0)
				ball.update_prediction()
			EndIf
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_NORMAL)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
			
		Next
		
		For Local ply:t_player = EachIn team[HOME].lineup
			ply.update_frame_distance()
		Next
		
		If (ball.owner = Null) And (ball.owner_last <> Null)
			If (team[HOME].near1.ball_distance < ball.owner_last.ball_distance And team[HOME].near1.ball_distance < 20)
				If (team[HOME].near1 <> team[HOME].lineup_at_index(0))
					If (ball.owner_last.input_device <> ball.owner_last.get_ai()) And (team[HOME].near1.input_device = team[HOME].near1.get_ai())
						If (team[HOME].near1.role = PR.GOALKEEPER)
							''swap goalkeepers
							list_swap_objects(team[HOME].lineup, team[HOME].lineup_at_index(0), team[HOME].near1)
						Else
							''transfer input device
							team[HOME].near1.set_input_device(ball.owner_last.input_device)
							ball.owner_last.set_input_device(ball.owner_last.get_ai())
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		
		If (ball.owner <> Null And ..
			ball.owner <> team[HOME].lineup_at_index(0) And ..
			ball.owner.input_device <> ball.owner.get_ai())
			Self.last_trained = ball.owner
		EndIf
		
		If (KeyDown(KEY_ESCAPE))
			Self.quit_training()
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
