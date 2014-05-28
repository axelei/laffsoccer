SuperStrict

Import "t_match_mode.bmx"

Type t_match_main Extends t_match_mode
	
	Field missed_goal:Int
	
	Method New()
		
		Self.display_ball_owner = True
		
		Self.missed_goal = False
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		''crowd chants			
		If (game_settings.sound_enabled)
			If ((Abs(MilliSecs()) Mod 16000) < 2000) And (Not ChannelPlaying(channel.chant))
				SetChannelVolume(channel.chant, 0.1 * location_settings.sound_vol * game_settings.crowd_chants)
				PlaySound(sound.chant, channel.chant)
			EndIf
		EndIf
		
		''timing
		match_status.timer = match_status.timer +1000/REFRATE
		
		''DEBUG 'ALT + D' **
		If KeyDown(KEY_LALT) And KeyDown(KEY_D)
			debug = Not(debug)
			Delay 100
			FlushKeys()
		EndIf
		
		team[HOME].update_frame_distance()
		team[AWAY].update_frame_distance()
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			
			If (ball.zmax < ball.z)
				ball.zmax = ball.z ''** useful to set speed of the shots
			EndIf
			
			''--- COLLISIONS & EVENTS CHECK ---
			
			Local attacking_team:Int
			Local defending_team:Int
			If (team[HOME].side = -ball.side.y)
				attacking_team = HOME
			Else
				attacking_team = AWAY
			EndIf
			defending_team = Not attacking_team
			
			If (ball.holder)
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_KEEPER_STOP)
				Return
			EndIf
			
			ball.collision_flagposts() 
			
			If (ball.collision_goal())
				stats[attacking_team].shot = stats[attacking_team].shot + 1 
			EndIf
			
			''goal/corner/goal-kick
			If (ball.y*ball.side.y => (GOAL_LINE + BALL_R))
				''goal
				If (is_in(ball.x, -POST_X, POST_X) And (ball.z <= CROSSBAR_H))
					stats[attacking_team].goal = stats[attacking_team].goal +1 
					stats[attacking_team].shot = stats[attacking_team].shot +1
					add_goal(attacking_team)
					If (game_settings.sound_enabled)
						SetChannelVolume(channel.homegoal, 0.1 * location_settings.sound_vol)
						PlaySound(sound.homegoal, channel.homegoal)
					
						If (match_settings.commentary = True)
							SetChannelVolume(chn_comm, 0.1 * location_settings.sound_vol)
							If (ball.goal_owner.team = team[attacking_team])
								PlaySound(comm_goal[Rand(0, comm_goal.length -1)], chn_comm)
							Else
								PlaySound(comm_owngoal[Rand(0, comm_owngoal.length -1)], chn_comm)
							EndIf
						EndIf
					EndIf
					game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_GOAL)
					Return
				Else
					''corner/goal-kick
					If (ball.owner_last.team = team[defending_team])
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_CORNER_STOP)
						Return
					Else
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_GOAL_KICK_STOP)
						Return
					EndIf
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
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_THROW_IN_STOP)
				Return
			EndIf
			
			''--- MATCH PERIODS ----
			Select match_status.period
				
				Case match_status.FIRST_HALF
					If (match_status.timer > (match_status.length * 45/90))
						''ball is not near the penalty area and is not going toward the goals
						If (Not((Abs(ball.zone_x) <= 1) And (Abs(ball.zone_y) = 3))) And (Abs(ball.y) < Abs(ball.y0))
							game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_HALF_TIME_STOP)
							Return
						EndIf
					EndIf
					
				Case match_status.SECOND_HALF
					If (match_status.timer > match_status.length)
						''ball is not near the penalty area and is not going toward the goals
						If (Not((Abs(ball.zone_x) <= 1) And (Abs(ball.zone_y) = 3))) And (Abs(ball.y) < Abs(ball.y0))
							Select menu.status
							
								Case MS_FRIENDLY
									game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_FULL_TIME_STOP)
									Return
									
								Case MS_COMPETITION
									Select competition.typ
									
										Case CT_LEAGUE
											league.set_result(stats[HOME].goal, stats[AWAY].goal)
											game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_FULL_TIME_STOP)
											Return
											
										Case CT_CUP
											cup.set_result(stats[HOME].goal, stats[AWAY].goal, RT_AFTER_90_MINS)
											If cup.play_extra_time()
												game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_EXTRA_TIME_STOP)
												Return
											Else
												game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_FULL_TIME_STOP)
												Return
											EndIf
											
									End Select
									
							End Select
						EndIf
					EndIf
					
				Case match_status.FIRST_EXTRA_TIME
					If (match_status.timer > (match_status.length * 105/90))
						''ball is not near the penalty area and is not going toward the goals
						If (Not((Abs(ball.zone_x) <= 1) And (Abs(ball.zone_y) = 3))) And (Abs(ball.y) < Abs(ball.y0))
							game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_HALF_EXTRA_TIME_STOP)
							Return
						EndIf
					EndIf
					
				Case match_status.SECOND_EXTRA_TIME
					If (match_status.timer > (match_status.length * 120/90))
						''ball is not near the penalty area and is not going toward the goals
						If (Not((Abs(ball.zone_x) <= 1) And (Abs(ball.zone_y) = 3))) And (Abs(ball.y) < Abs(ball.y0))
							''at present an extra time is only possible in cups...
							cup.set_result(stats[HOME].goal, stats[AWAY].goal, RT_AFTER_EXTRA_TIME)
							game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_FULL_EXTRA_TIME_STOP)
							Return
						EndIf
					EndIf
					
			End Select
			
			Self.update_players(True)
			team[HOME].find_nearest()
			team[AWAY].find_nearest()
			
			For Local t:Int = HOME To AWAY
				If (team[t].uses_automatic_input_device())
					team[t].automatic_input_device_selection()
				EndIf
			Next
			
			ball.update_zone(ball.x, ball.y, ball.v, ball.a)
			team[HOME].update_tactics()
			team[AWAY].update_tactics()
			
			If ((frame Mod REFRATE) = 0)
				ball.update_prediction()
			EndIf
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_BALL, CS_NORMAL)
			vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL)
			
		Next
		
		''--- BALL POSSESSION STATISTICS ---
		If (ball.owner)
			stats[ball.owner.team.index].poss = stats[ball.owner.team.index].poss +1
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
	
	Method out_of_screen:Int(x:Int, y:Int)
		
		Local dx:Int = round(x) + CENTER_X -vcamera_x[frame]
		If ((dx < 0) Or (dx > game_settings.screen_width))
			Return True
		EndIf
		
		Local dy:Int = round(y) + CENTER_Y -vcamera_y[frame]
		If ((dy < 0) Or (dy > game_settings.screen_height))
			Return True
		EndIf
		
		Return False
		
	End Method
	
End Type
