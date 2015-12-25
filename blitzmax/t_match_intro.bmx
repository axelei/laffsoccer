SuperStrict

Import "t_match_mode.bmx"

Type t_match_intro Extends t_match_mode
	
	Field quit_timeout:Int
	Field camera_delay:Int
	Field entering_counter:Int
	
	Method New()
		
		Self.display_time = False
		Self.display_wind_vane = False
		Self.display_controlled_player = False
		Self.display_radar = False
		
		FlushKeys()
		
		light = 255
		
		''players starting state & position
		For Local t:Int = HOME To AWAY
			Local i:Int = 0
			Local r1:Int = 0
			Local r2:Int = 0
			For Local ply:t_player = EachIn team[t].lineup
				If (i < TEAM_SIZE)
					ply.set_state("state_outside")
					
					ply.set_x(TOUCH_LINE +80)
					ply.set_y(10*team[t].side +20)
					ply.set_z(0)
					
					ply.set_target(ply.x, ply.y)
				Else
					ply.set_x(BENCH_X)
					If ((1 -2*t) = match_settings.bench_side)
						ply.set_y(BENCH_Y_UP +14*(i -TEAM_SIZE) +46)
					Else
						ply.set_y(BENCH_Y_DOWN +14*(i -TEAM_SIZE) +46)
					EndIf
					ply.set_state("state_bench_sitting")
				EndIf
				
				i :+ 1
			Next
		Next
		
		''reset data
		Self.update_players(False)
		For Local f:Int = 0 To VSIZE -1
			ball.save(f)
			team[HOME].save(f)
			team[AWAY].save(f)
		Next
		
	End Method
	
	Method update()
		
		Super.update()
		
		Super.update_ai()
		
		If ((Self.entering_counter Mod 4) = 0 And Self.entering_counter / 4 < TEAM_SIZE)
			For Local t:Int = HOME To AWAY
				Local i:Int = Self.entering_counter / 4
				Local ply:t_player = team[t].lineup_at_index(i)
				ply.set_state("state_reach_target")
				ply.set_tx(TOUCH_LINE -180 +7*i)
				ply.set_ty((60 +2*i)*team[t].side -20*((i Mod 2) = 0))
			Next
		EndIf
		Self.entering_counter :+ 1
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			Self.update_players(False)
			
			For Local t:Int = HOME To AWAY
				
				For Local i:Int = 0 To TEAM_SIZE -1
					Local player:t_player = team[t].lineup_at_index(i)
					If (player.get_active_state().check_name("state_idle"))
						player.set_state("state_photo")
					EndIf
				Next
			Next
			
			Self.camera_delay :+ 1
			Self.quit_timeout :+ 1
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_NONE, CS_NORMAL) 
			If (Self.camera_delay < SECOND)
				vcamera_y[frame] = camera.updatey(CF_NONE) 
			Else
				vcamera_y[frame] = camera.updatey(CF_BALL, CS_NORMAL) 
			EndIf
			
		Next
		
		If (Self.check_quit_conditions())
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
	
	Method render:Int()
		Super.render()
		draw_rosters()
	End Method
	
	Method check_quit_conditions:Int()
		
		If (Self.entering_counter / 4 < TEAM_SIZE)
			Return False
		EndIf
		
		For Local d:t_input = EachIn input_devices
			If (d.fire1_down())
				Return True
			EndIf
		Next
		
		If (Self.quit_timeout >= 20*SECOND)
			Return True
		EndIf
		
		Return False
	End Method
	
	Method draw_rosters()
		
		Local l:Int = 13 +0.2*(game_settings.screen_width -640) +2
		Local r:Int = game_settings.screen_width -l +2
		Local w:Int = r -l
		Local t:Int = 20 +0.2*(game_settings.screen_height -400) +2
		Local b:Int = game_settings.screen_height -t +8 +2
		Local h:Int = b -t
		Local m1:Int = t +0.12*h +2
		Local m2:Int = t +0.325*h +2
		Local hw:Int = 0.5*game_settings.screen_width +2
		
		fade_rect(l +2, t +2, r -2, b -2, 0.35, $000000, light)
		
		''frame shadow
		SetColor(0, 0.1*light, 0)
		
		draw_frame(l, t, r -l, b -t)
		
		l = l -2
		r = r -2
		t = t -2
		b = b -2
		m1 = m1 -2
		m2 = m2 -2
		hw = hw -2
		
		''frame
		SetColor(light,light,light)
		
		draw_frame(l, t, r -l, b -t)
		
		''middle 
		DrawLine(hw -0.2*w,	m1, 	hw +0.2*w,	m1)
		DrawLine(hw -0.2*w, m1 +1, 	hw +0.2*w,	m1 +1)
		
		''middle left
		DrawLine(l +0.05*w,	m2, 	hw -0.05*w,	m2)
		DrawLine(l +0.05*w, m2 +1, 	hw -0.05*w,	m2 +1)
		
		''middle right
		DrawLine(hw +0.05*w, m2, 	r -0.05*w,	m2)
		DrawLine(hw +0.05*w, m2 +1,	r -0.05*w,	m2 +1)
		
		''title
		Local y:Int = t +0.044*h
		If (menu.status = MS_FRIENDLY)
			text14u(dictionary.gettext("FRIENDLY"), 0.5*game_settings.screen_width, y, img_ucode14g, 0) 
		Else
			text14u(competition.get_menu_title(), 0.5*game_settings.screen_width, y, img_ucode14g, 0) 
		EndIf
		
		''city & stadium
		If (team[HOME].city <> "")
			If (team[HOME].stadium <> "")
				y = t +0.163*h 
				text10u(team[HOME].stadium + ", " + team[HOME].city, 0.5*game_settings.screen_width, y, img_ucode10g, 0)
			EndIf
		EndIf
		
		''club logos / national flags
		y = t + 0.13*h
		
		Local w0:Int = 0
		Local h0:Int = 0
		Local w1:Int = 0
		Local h1:Int = 0
		Local lm:Int = 84 ''left margin
		Local rm:Int = 84 ''right margin
		
		''size of club logos / national flags
		If (team[HOME].clnf <> Null)
			w0 = ImageWidth(team[HOME].clnf)
			h0 = ImageHeight(team[HOME].clnf)
			lm = w0 +18
		EndIf
		If (team[AWAY].clnf <> Null)
			w1 = ImageWidth(team[AWAY].clnf)
			h1 = ImageHeight(team[AWAY].clnf)
			rm = w1 +18
		EndIf
		
		''draw club logos / national flags
		If (team[HOME].clnf <> Null)
			If (team[HOME].national = False)
				DrawImage team[HOME].clnf_sh, l +0.044*w, y -0.5*h0 +2
			EndIf
			SetColor light, light, light
			DrawImage team[HOME].clnf, l +0.044*w -2, y -0.5*h0
		EndIf
		If (team[AWAY].clnf <> Null)
			If (team[AWAY].national = False)
				DrawImage team[AWAY].clnf_sh, r -0.044*w -w1, y -0.5*h0 +2
			EndIf
			SetColor light, light, light
			DrawImage team[AWAY].clnf, r -0.044*w -w1 -2,	y -0.5*h0
		EndIf
		
		''team name
		y = t +0.25*h
		text14u(team[HOME].name, l +0.24*w, y, img_ucode14g, 0)
		text14u(team[AWAY].name, l +0.72*w, y, img_ucode14g, 0)
		
		''players
		For Local tm:Int = HOME To AWAY
			
			y = t +0.38*h
			
			For Local pos:Int = 1 To TEAM_SIZE
				
				Local player:t_player = team[tm].player_at_position(pos -1)
				
				''number
				text10u(player.number, l +(0.10 +0.48*tm)*w, y, img_ucode10g, 0)
				
				''name
				If (player.surname <> "")
					text10u(player.surname, l +(0.14 +0.48*tm)*w, y, img_ucode10g, 1)
				Else
					text10u(player.name, l +(0.14 +0.48*tm)*w, y, img_ucode10g, 1)
				EndIf
				
				y = y +0.044*h
				
			Next
			
		Next
		
		''coach
		y = t +0.875*h
		text10u(dictionary.gettext("COACH") + ":", l +0.08*w, y, img_ucode10g, 1)
		text10u(dictionary.gettext("COACH") + ":", l +0.56*w, y, img_ucode10g, 1)
		
		y = t +0.925*h
		text10u(team[HOME].coach_name, l +0.24*w, y, img_ucode10g, 0)
		text10u(team[AWAY].coach_name, l +0.72*w, y, img_ucode10g, 0)
		
	End Method
	
End Type
