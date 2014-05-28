SuperStrict

Import "t_game_mode.bmx"
Import "match.bmx"
Import "t_bench_status.bmx"

Type t_match_mode Extends t_game_mode

	Field frame0:Int
	Field display_controlled_player:Int
	Field display_ball_owner:Int
	Field display_goal_scorer:Int
	Field display_time:Int
	Field display_wind_vane:Int
	Field display_score:Int
	Field display_statistics:Int
	Field display_radar:Int
	
	Method New()
		music_mute = True
		Self.frame0 = frame
		
		Self.display_controlled_player = True
		Self.display_ball_owner = False
		Self.display_goal_scorer = False
		Self.display_time = True
		Self.display_wind_vane = True
		Self.display_score = False
		Self.display_statistics = False
		Self.display_radar = True
	End Method
	
	Method update()
		Super.update()
		function_key.update()
	End Method
	
	Method update_ai()
		For Local t:Int = HOME To AWAY
			If (team[t] <> Null)
				team[t].update_lineup_ai()
			EndIf
		Next
	End Method
	
	Method render()
	
		SetColor light, light, light
		
		If (GetGraphicsDriver() = GLMax2DDriver())
			glMatrixMode(GL_PROJECTION)
			glLoadIdentity()
			gluOrtho2D(0, game_settings.screen_width * 100.0 / game_settings.zoom, game_settings.screen_height * 100.0 / game_settings.zoom, 0)
		EndIf
		
		SetOrigin(CENTER_X -vcamera_x[frame], CENTER_Y -vcamera_y[frame])
		
		pitch.draw()
					
		crowd_list.draw()
	
		Self.draw_shadows()
	
		SortList(sprites, True, compare_sprites_by_y)
	
		For Local s:t_sprite = EachIn sprites
			s.draw(frame)
		Next
	
		''redraw bottom goal
		draw_image(img_goal_btm, GOAL_BTM_X, GOAL_BTM_Y)
		
		ball.draw_shadow_on_crossbar(frame)
		
		''redraw ball over bottom goal
		If (ball.data[frame].y > GOAL_LINE) And (Sqr((1.5*(ball.data[frame].y - GOAL_LINE))^2 + ball.data[frame].z^2) > CROSSBAR_H)
			ball.draw(frame)
		EndIf
		
		''redraw jumpers
		''top
		If (ball.data[frame].y < -JUMPER_Y)
			draw_image(img_jumper, -JUMPER_X, -JUMPER_Y -40) 
			draw_image(img_jumper, +JUMPER_X, -JUMPER_Y -40) 
		EndIf
		''bottom
		If (ball.data[frame].y < +JUMPER_Y)
			draw_image(img_jumper, -JUMPER_X, +JUMPER_Y -40) 
			draw_image(img_jumper, +JUMPER_X, +JUMPER_Y -40) 
		EndIf
	
		location_settings.draw_weather_effects(frame)
		
		Self.draw_controlled_players_numbers()
		
		''debugging info (in the pitch)
		If (debug)
			
			''ball prediction
			For Local frm:Int = 0 To BALL_PREDICTION -1
				Local x0:Int = ball.prediction[frm].x
				Local y0:Int = ball.prediction[frm].y -ball.prediction[frm].z
				DrawLine x0, y0 -1, x0, y0
			Next
			
		EndIf
		
		If (GetGraphicsDriver() = GLMax2DDriver())
			glMatrixMode(GL_PROJECTION)
			glLoadIdentity()
			gluOrtho2D(0, game_settings.screen_width, game_settings.screen_height, 0)
		EndIf
		
		SetOrigin(0, 0)
		
		''debugging info (fixed positions)
		If (debug Or (TEAM_SIZE < 11))
	
			''possession_last
			Rem
			SetColor 255*(ball.owner_last And ball.owner_last.team=team[AWAY]),0,255*(ball.owner_last And ball.owner_last.team=team[HOME])
			DrawRect game_settings.screen_width/2+20, 10, 15, 15	
			SetColor 255,255,255
			''possession
			SetColor 255*(ball.owner And ball.owner.team=team[AWAY]),0,255*(ball.owner And ball.owner.team=team[HOME])
			DrawRect game_settings.screen_width/2+60, 10, 20, 20	
			SetColor 255,255,255
			EndRem
		
			Local i:Int=40
			Rem
			i=i+12;	DrawText "team[HOME].player_at_index(0).tx: " + team[HOME].player_at_index(0).tx, 100,i
			i=i+12;	DrawText "team[AWAY].player_at_index(0).tx: " + team[AWAY].player_at_index(0).tx, 100,i
			i=i+12;	DrawText "contr[HOME]: " + contr[HOME], 100,i
			i=i+12;	DrawText "contr[AWAY]: " + contr[AWAY], 100,i
			i=i+12;	DrawText "team[HOME].tactics: " + team[HOME].tactics, 100,i
			i=i+12;	DrawText "team[AWAY].tactics: " + team[AWAY].tactics, 100,i
			i=i+12;	DrawText "memalloced: " + GCMemAlloced(),100,i
			i=i+12;	DrawText "stats[HOME].poss: " + stats[HOME].poss, 100, i
			i=i+12;	DrawText "stats[AWAY].poss: " + stats[AWAY].poss, 100, i
			i=i+12;	DrawText "facing_player(HOME)" + facing_player(HOME), 100, i
			i=i+12;	DrawText "rgbk" + rgbk, 100, i
			i=i+12;	DrawText "match_settings.sound_vol: " + match_settings.sound_vol, 100, i
			i=i+12;	DrawText "  " + (camera.y - CENTER_Y +game_settings.screen_height/2), 100, i
			EndRem
			
			i=i+12;	DrawText "ball.v  " + ball.v, 100, i
			FlushMem()
			Rem	
			Color 255,255,255
		
			''nearest players number
			If near1(0)  
				Color 0, 0, 255
				Text player(0,near1(0))\x +1, player(0,near1(0))\y -40, player(0,near1(0))\nr, 1
			EndIf
		
			If near1(1)
				Color 255, 0, 0
				Text player(1,near1(1))\x +1, player(1,near1(1))\y -40, player(1,near1(1))\nr, 1
			EndIf
			
			If near2(0)	
				Color 255, 255, 0
				Text player(0,near2(0))\x +1, player(0,near2(0))\y -40, player(0,near2(0))\nr, 1
			EndIf
			
			If near2(1)
				Color 255, 255, 0
				Text player(1,near2(1))\x +1, player(1,near2(1))\y -40, player(1,near2(1))\nr, 1
			EndIf
				
			Color 255, 255, 255
			EndRem
			
			''ball zone points
			Rem	
			For i = -5*BALL_ZONE_DX/2 To 5*BALL_ZONE_DX/2 Step BALL_ZONE_DX
				DrawLine i, -GOAL_LINE, i, GOAL_LINE
			Next
			For i = -7*BALL_ZONE_DY/2 To 7*BALL_ZONE_DY/2 Step BALL_ZONE_DY
				DrawLine -TOUCH_LINE, i, TOUCH_LINE, i
			Next
			SetColor 0,255,0
			DrawOval (ball.zone_x) * BALL_ZONE_DX -10, (ball.zone_y) * BALL_ZONE_DY -10, 20, 20
			SetColor 255,0,0
			DrawOval (ball.zone_x_next) * BALL_ZONE_DX -5, (ball.zone_y_next) * BALL_ZONE_DY -5, 10, 10
			SetColor 255,255,255
			EndRem
			
			''FRAME DISTANCE
			Rem
			For Local t:Int = HOME To AWAY
				For Local i:Int = 0 To BASE_TEAM -1
					Local ply:t_player = team[t].player_at_index(i)
					Draw_Text ply.frame_distance, ply.x, ply.y, 0
				Next
			Next
			EndRem
		
		EndIf ''END DEBUG **********************
	
		''ball owner
		If (Self.display_ball_owner And ball.owner)
			ball.owner.draw_number_and_name()
		EndIf
		
		''goal scorer
		If (Self.display_goal_scorer)
			If ((frame Mod 160) > 80)
				ball.goal_owner.draw_number_and_name()
			EndIf
		EndIf
		
		''clock
		If (Self.display_time)
			Self.draw_time()
		EndIf
		
		''radar
		If (Self.display_radar And match_settings.radar)
			Self.draw_radar()
		EndIf
		
		''wind vane
		If (Self.display_wind_vane And (location_settings.wind.speed > 0))
			set_color($FFFFFF, light)
			DrawSubImageRect(img_wind, game_settings.screen_width -50, 20, 30, 30, 30*location_settings.wind.direction, 30*(location_settings.wind.speed -1), 30, 30)
		EndIf
		
		''score
		If (Self.display_score)
			Self.draw_score()			
		EndIf
		
		''messages
		If (message.length > 0)
			set_color($FFFFFF, light)
			text10u(message, 0.5*game_settings.screen_width, 1, img_ucode10g, 0)
		EndIf
		If (now -message_timer > 1200)
			message = ""
		EndIf
		now = MilliSecs()
	
		''statistics
		If ((Self.display_statistics) And (hypo(camera.vx, camera.vy) < 0.000002))
			Self.draw_statistics(light)
		EndIf
	
	End Method
	
	Method draw_shadows()
		SetBlend(ALPHABLEND)
		location_settings.set_alpha()
		
		ball.draw_shadow(frame)
		
		For Local t:Int = HOME To AWAY
			If (team[t] <> Null)
				For Local ply:t_player = EachIn team[t].lineup
					ply.draw_shadow(frame)
				Next
			EndIf
		Next
	
		SetBlend(MASKBLEND)
		SetAlpha(1)
	End Method
	
	Method draw_controlled_players_numbers()
		If (Self.display_controlled_player)
			For Local t:Int = HOME To AWAY
				If (team[t] <> Null)
					team[t].draw_controlled_players_numbers()
				EndIf
			Next
		EndIf
	End Method
	
	Method draw_time()
	
		Local minute:Int = match_status.get_minute()
		
		''"mins"
		DrawSubImageRect(img_time, 46, 22, 48, 20, 120, 0, 48, 20)
		
		''units
		Local digit:Int = minute Mod 10
		DrawSubImageRect(img_time, 34, 22, 12, 20, digit*12, 0, 12, 20)
		
		''tens
		minute = (minute - digit) / 10
		digit = minute Mod 10
		If (minute > 0)
			DrawSubImageRect(img_time, 22, 22, 12, 20, digit*12, 0, 12, 20)
		EndIf
		
		''hundreds
		minute = (minute - digit) / 10
		digit = minute Mod 10
		If (digit > 0)
			DrawSubImageRect(img_time, 10, 22, 12, 20, digit*12, 0, 12, 20)
		EndIf
		
	End Method
	
	Method draw_score()
		
		''default values
		Local w0:Int = 0
		Local h0:Int = 0
		Local w1:Int = 0
		Local h1:Int = 0
		Local lm:Int = 84	''left margin
		Local rm:Int = 84	''right margin
	
		''max rows of scorers list
		Local rows:Int = Max(rows_counter(score[HOME]),rows_counter(score[AWAY]))
				
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
		
		Local y0:Int = Max(h0, h1) 
		y0 = Max(y0, 14*rows)
		y0 = game_settings.screen_height -16 -y0
		
		''club logos / national flags 
		If (team[HOME].clnf <> Null)
			If (team[HOME].national = False)
				DrawImage(team[HOME].clnf_sh, 12, y0 +10)
			EndIf
			SetColor(light, light, light)
			DrawImage(team[HOME].clnf, 10, y0 +8)
		EndIf
		If (team[AWAY].clnf <> Null)
			If (team[AWAY].national = False)
				DrawImage(team[AWAY].clnf_sh, game_settings.screen_width -w1 -8, y0 +10)
			EndIf
			SetColor(light, light, light)
			DrawImage(team[AWAY].clnf, game_settings.screen_width -w1 -10, y0 +8)
		EndIf
		
		''teams
		text14u(team[HOME].name, 10, y0 -22, img_ucode14g, +1)
		text14u(team[AWAY].name, game_settings.screen_width -8, y0 -22, img_ucode14g, -1)
		
		''bars
		SetColor(light, light, light)
		DrawLine(10,               y0,    0.5*game_settings.screen_width -13, y0)
		DrawLine(10,               y0 +1, 0.5*game_settings.screen_width -13, y0 +1)
		DrawLine(0.5*game_settings.screen_width +12, y0,    game_settings.screen_width -11,     y0)
		DrawLine(0.5*game_settings.screen_width +12, y0 +1, game_settings.screen_width -11,     y0 +1)
		SetColor(0, 0.1*light, 0)
		DrawLine(12,               y0 +2, 0.5*game_settings.screen_width -11, y0 +2)
		DrawLine(12,               y0 +3, 0.5*game_settings.screen_width -11, y0 +3)
		DrawLine(0.5*game_settings.screen_width +14, y0 +2, game_settings.screen_width -9,      y0 +2)
		DrawLine(0.5*game_settings.screen_width +14, y0 +3, game_settings.screen_width -9,      y0 +3)
	
		''home score
		Local f0:Int = stats[HOME].goal Mod 10
		Local f1:Int = ((stats[HOME].goal - f0) / 10) Mod 10 
	
		SetColor(light, light, light)
		If (f1 > 0)
			DrawSubImageRect(img_score, 0.5*game_settings.screen_width -15 -48, y0 -40, 24, 38, f1*24, 0, 24, 38)
		EndIf
		DrawSubImageRect(img_score, 0.5*game_settings.screen_width -15 -24, y0 -40, 24, 38, f0*24, 0, 24, 38)
		
		''"-"
		DrawSubImageRect(img_score, 0.5*game_settings.screen_width -9, y0 -40, 24, 38, 10*24, 0, 24, 38)
	
		''away score
		f0 = stats[AWAY].goal Mod 10
		f1 = (stats[AWAY].goal - f0) / 10 Mod 10 
		
		If (f1 > 0)
			DrawSubImageRect(img_score, 0.5*game_settings.screen_width +17, y0 -40, 24, 38, f1*24, 0, 24, 38)
			DrawSubImageRect(img_score, 0.5*game_settings.screen_width +17 +24, y0 -40, 24, 38, f0*24, 0, 24, 38)
		Else
			DrawSubImageRect(img_score, 0.5*game_settings.screen_width +17, y0 -40, 24, 38, f0*24, 0, 24, 38)
		EndIf
		
		''scorers
		text10u(score[HOME], 0.5*game_settings.screen_width -12, y0 +4, img_ucode10g, -1)
		text10u(score[AWAY], 0.5*game_settings.screen_width +14, y0 +4, img_ucode10g, 1)
	
	End Method
	
	Method draw_statistics(light:Int)
		
		Local l:Int = 13 +0.2*(game_settings.screen_width -640) +2
		Local r:Int = game_settings.screen_width -l +2
		Local w:Int = r -l
		Local t:Int = 20 +0.2*(game_settings.screen_height -400) +2
		Local b:Int = game_settings.screen_height -t +8 +2
		Local h:Int = b -t
		Local hw:Int = 0.5*game_settings.screen_width
		
		''fading
		set_color($FFFFFF, light)
		fade_rect(l, t +2, r -2, t +h/10 +1, 0.35, $000000, light)
		
		Local i:Int = t +h/10 +2
		For Local j:Int = 1 To 8
			fade_rect(l +2, i +1, r -2, i +h/10 -1, 0.35, $000000, light)
			i = i +h/10
		Next
		fade_rect(l +2, i +1, r -2, b -2, 0.35, $000000, light)
		
		''frame shadow
		SetColor(0, 0.1*light, 0)
		draw_frame(l, t, r -l, b -t)
		
		l = l -2
		r = r -2
		t = t -2
		b = b -2
		
		''frame
		SetColor(light,light,light)
		draw_frame(l, t, r -l, b -t)
		
		SetColor(light,light,light)
		
		Local poss_home:Int = round(100 * stats[HOME].poss / (stats[HOME].poss +stats[AWAY].poss)) 
		Local poss_away:Int = 100 -poss_home
		
		''text
		i = t +h/20 -8
		text14u(dictionary.gettext("MATCH STATS"), hw, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(team[HOME].name, l +0.2*w, i, img_ucode14g, 0)
		text14u(team[AWAY].name, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(stats[HOME].goal, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("GOALS"), hw, i, img_ucode14g, 0)
		text14u(stats[AWAY].goal, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(poss_home, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("POSSESSION"), hw, i, img_ucode14g, 0)
		text14u(poss_away, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(stats[HOME].shot, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("GOAL ATTEMPTS"), hw, i, img_ucode14g, 0)
		text14u(stats[AWAY].shot, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(stats[HOME].cent, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("ON TARGET"), hw, i, img_ucode14g, 0)
		text14u(stats[AWAY].cent, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(stats[HOME].corn, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("CORNERS WON"), hw, i, img_ucode14g, 0)
		text14u(stats[AWAY].corn, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(stats[HOME].foul, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("FOULS CONCEDED"), hw, i, img_ucode14g, 0)
		text14u(stats[AWAY].foul, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(stats[HOME].yell, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("BOOKINGS"), hw, i, img_ucode14g, 0)
		text14u(stats[AWAY].yell, r -0.2*w, i, img_ucode14g, 0)
		
		i = i +h/10
		text14u(stats[HOME].reds, l +0.2*w, i, img_ucode14g, 0)
		text14u(dictionary.gettext("SENDINGS OFF"), hw, i, img_ucode14g, 0)
		text14u(stats[AWAY].reds, r -0.2*w, i, img_ucode14g, 0)
		
	End Method
	
	Method draw_radar()
		Const RX:Int = 10
		Const RY:Int = 60
		Const RW:Int = 132
		Const RH:Int = 166
		
		fade_rect(RX, RY, RX +RW, RY+RH, 0.6, location_settings.grass.dark_shadow, light)
		
		set_color($000000, light)
		DrawLine RX,     RY,        RX +RW, RY
		DrawLine RX +RW, RY,        RX +RW, RY+RH
		DrawLine RX,     RY+0.5*RH, RX +RW, RY+0.5*RH
		DrawLine RX,     RY+RH,     RX +RW, RY+RH
		DrawLine RX,     RY,        RX,     RY+RH
		
		''prepare y-sorted list
		Local players:TList = New TList
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				players.addLast(team[t].lineup_at_index(i))
			Next
		Next
		SortList(players, True, compare_sprites_by_y)
		
		''get shirt colors
		Local shirt1:Int[2]
		Local shirt2:Int[2]
		For Local t:Int = HOME To AWAY
			Local kit:t_kit = team[t].kit_at_index(team[t].kit)
			shirt1[t] = kit_color[kit.shirt1, 0]
			shirt2[t] = kit_color[kit.shirt2, 0]
		Next
		
		''draw placeholders
		For Local ply:t_player = EachIn players
			If (ply.data[frame].is_visible)
				Local dx:Int = RX +0.5*RW +ply.data[frame].x / 8.0
				Local dy:Int = RY +0.5*RH +ply.data[frame].y / 8.0
				
				set_color($242424, light)
				DrawLine(dx -3, dy -3, dx +2, dy -3)
				DrawLine(dx -4, dy -2, dx -4, dy +1)
				DrawLine(dx -3, dy +2, dx +2, dy +2)
				DrawLine(dx +3, dy -2, dx +3, dy +1)
				
				set_color(shirt1[ply.team.index], light)
				DrawRect(dx -3, dy -2, 3, 4)
				
				set_color(shirt2[ply.team.index], light)
				DrawRect(dx,    dy -2, 3, 4)
			EndIf
		Next
		
		set_color($FFFFFF, light)
		
		''draw controlled players numbers
		If (Self.display_controlled_player)
			For Local ply:t_player = EachIn players
				If ((ply.data[frame].is_visible) And (ply.input_device <> ply.ai))
					Local dx:Int = RX +0.5*RW +ply.data[frame].x / 8.0 +1
					Local dy:Int = RY +0.5*RH +ply.data[frame].y / 8.0 -10
					
					Local f0:Int = ply.number Mod 10
					Local f1:Int = (ply.number - f0) / 10 Mod 10
					
					Local w0:Int, w1:Int
					If (f1 > 0)
						w0 = 4 -2 * (f0 = 1)
						w1 = 4 -2 * (f1 = 1)
						dx = dx -(w0 + w1) / 2
						DrawSubImageRect(img_tiny_num, dx, dy, w1, 6, (f1 +1) * 4 -w1, 0, w1, 6)
						dx = dx +w1
						DrawSubImageRect(img_tiny_num, dx, dy, w0, 6, (f0 +1) * 4 -w0, 0, w0, 6)
					Else
						w0 = 4 -2 * (f0 = 1)
						dx = dx -w0 / 2
						DrawSubImageRect(img_tiny_num, dx, dy, w0, 6, (f0 +1) * 4 -w0, 0, w0, 6)
					EndIf
				EndIf
			Next
		EndIf
		
	End Method
	
	Method next_frame:Int()
		Return ((frame +1) Mod VSIZE)
	End Method
	
	Method frame_diff:Int(frame1:Int, frame2:Int)
		Return ((frame1 -frame2 +VSIZE) Mod VSIZE)
	End Method
	
	Method pause()
		game_action_queue.push(AT_HOLD_FOREGROUND, GM.MATCH_PAUSE)
	End Method
	
	Method replay()
		game_action_queue.push(AT_FADE_OUT)
		game_action_queue.push(AT_HOLD_FOREGROUND, GM.MATCH_REPLAY)
		game_action_queue.push(AT_FADE_IN)
	End Method
	
	Method bench(t:t_team, id:t_input)
		If (id <> Null)
			bench_status.team = t
			bench_status.input_device = id
			game_action_queue.push(AT_HOLD_FOREGROUND, GM.MATCH_BENCH_ENTER)
		EndIf
	End Method
	
	Method quit_match()
		
		''stop the crowd
		StopChannel channel.crowd
		StopChannel channel.homegoal
	
		''release images
		For Local t:Int = HOME To AWAY
			For Local ply:t_player = EachIn team[t].players
				ply.img = Null
				ply.hair = Null
			Next
			For Local sk:Int = 0 To 9
				img_player[t, sk] = Null
				img_keeper[t, sk] = Null
			Next
		Next
	
		img_goal_btm = Null
		img_keeper_cd = Null
		img_player_shadows[0] = Null
		If (location_settings.time = TI.NIGHT)
			For Local i:Int = 1 To 3
				img_player_shadows[i] = Null
			Next
		EndIf
		img_replay = Null
		img_pause = Null
		Select (location_settings.weather_effect)
			Case WE.WIND
				img_wind = Null
			Case WE.RAIN
				img_rain = Null
			Case WE.SNOW
				img_snow = Null
			Case WE.FOG
				img_fog = Null
		End Select
		img_jumper = Null
		img_time = Null
		img_score = Null
		img_number = Null
		img_tiny_num = Null
		img_ucode10g = Null
		img_ucode14g = Null
		
		''restore menu graphics
		EndGraphics
		If (game_settings.full_screen)
			Graphics 1024, 768, 32
		Else
			Graphics 1024, 768, 0
		EndIf
		HideMouse()

		''load images
		img_ucode10 	= load_image("images", "ucode_10.png", MASKEDIMAGE, $0000FF)
		img_ucode14 	= load_image("images", "ucode_14.png", MASKEDIMAGE, $0000FF)
		img_arrow		= load_image("images", "arrow.png", MASKEDIMAGE, $0000FF)		
			
		Select menu.status
			Case MS_FRIENDLY
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_REPLAY_MATCH)
			Case MS_COMPETITION
				menu.show_result = True
				Select competition.typ
					Case CT_LEAGUE
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
					Case CT_CUP
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
				End Select							
		End Select
	End Method
	
	Method quit_training()
		
		''stop the crowd
		StopChannel channel.crowd
		StopChannel channel.homegoal
		
		''release images
		Local t:Int = HOME
		For Local ply:t_player = EachIn team[t].lineup
			ply.img = Null
			ply.hair = Null
		Next
		For Local sk:Int = 0 To 9
			img_player[t, sk] = Null
			img_keeper[t, sk] = Null
		Next
		
		img_goal_btm = Null
		img_keeper_cd = Null
		img_player_shadows[0] = Null
		If (location_settings.time = TI.NIGHT)
			For Local i:Int = 1 To 3
				img_player_shadows[i] = Null
			Next
		EndIf
		img_replay = Null
		img_pause = Null
		Select (location_settings.weather_effect)
			Case WE.WIND
				img_wind = Null
			Case WE.RAIN
				img_rain = Null
			Case WE.SNOW
				img_snow = Null
			Case WE.FOG
				img_fog = Null
		End Select
		img_jumper = Null
		img_time = Null
		img_score = Null
		img_number = Null
		img_ucode10g = Null
		img_ucode14g = Null
		
		''restore menu graphics
		EndGraphics
		If (game_settings.full_screen)
			Graphics 1024, 768, 32
		Else
			Graphics 1024, 768, 0
		EndIf
		HideMouse()
		
		''load images
		img_ucode10 = load_image("images", "ucode_10.png", MASKEDIMAGE, $0000FF)
		img_ucode14 = load_image("images", "ucode_14.png", MASKEDIMAGE, $0000FF)
		img_arrow   = load_image("images", "arrow.png", MASKEDIMAGE, $0000FF)		
		
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_TRAINING_SETTINGS)
	End Method
	
	Method update_players:Int(limit:Int)
		
		Local move:Int = False
		
		move = team[HOME].update_players(limit) Or move
		move = team[AWAY].update_players(limit) Or move
		
		Return move
		
	End Method
	
	Method set_starting_positions(t:Int)
		Local pos:Int
		RestoreData starting_positions_label
		For Local k:Int = 0 To 1
			For Local i:Int = 0 To TEAM_SIZE -1
				Local player:t_player = team[t].lineup_at_index(i)
				ReadData pos
				player.tx = pos * -team[t].side
				ReadData pos
				player.ty = pos * -team[t].side
			Next
			t = Not t
		Next
	End Method
	
End Type


#starting_positions_label
''starting team
DefData 0, -630
DefData -240, -180, -120, -320, +120, -320, 240, -180
DefData -378, 0, -100, -140, 100, -140, +212, 0
DefData -58, 0, 10, -10

''opponent team
DefData 0, -630
DefData -240, -370, -120, -480, +120, -480, 240, -370
DefData -320, -150, -60, -240, +60, -240, +320, -150
DefData -100, -86, 100, -86
