SuperStrict

Import "t_match_mode.bmx"
Import "t_goal_top_a.bmx"
Import "t_goal_top_b.bmx"
Import "t_flagpost.bmx"

Type t_training_loading Extends t_match_mode
	
	Method New()
		
		''set graphics mode
		EndGraphics
		If (game_settings.full_screen)
			Graphics(game_settings.screen_width, game_settings.screen_height, 32)
		Else
			Graphics(game_settings.screen_width, game_settings.screen_height, 0)
		EndIf
		HideMouse()
		
		memline = 0	''***
		memqty = GCMemAlloced()	''***
		
		If (debug)
			memory("inizio match")
		EndIf
		
		''generate teams side
		team[HOME].side = -1	''-1 = upside, 1 = downside
		
		
		''--- CREATE & INITIALIZE ---
		sprites = New TList
		
		ball:t_ball = New t_ball
		
		ListAddLast(sprites, ball)
		
		team[HOME].index = HOME
		
		function_key = New t_function_key
		
		''reset message timer
		message_timer:Int = 0
		
		location_settings.setup()
		
		''replay vectors
		For Local i:Int = 0 To VSIZE-1
			vcamera_x[i] = 0.5*(PITCH_W -game_settings.screen_width/(game_settings.zoom/100.0)) -3
			vcamera_y[i] = CENTER_Y -game_settings.screen_height/(2*(game_settings.zoom/100.0))
		Next
		
		''lineup
		team[HOME].lineup = team[HOME].players.Copy()
		
		''data
		For Local f:Int = 0 To VSIZE-1
			''ball
			ball.data[f] = New t_data
			
			''players
			For Local ply:t_player = EachIn team[HOME].lineup
				ply.data[f] = New t_data
			Next
		Next
		
		''camera
		camera:t_camera	= New t_camera
		camera.set_x(0.5*(PITCH_W -game_settings.screen_width/(game_settings.zoom/100.0)) -3)
		camera.set_y(CENTER_Y -game_settings.screen_height/(2*(game_settings.zoom/100.0)))
		
		''highlights
		hl_recorded = 0
		
		''--- LOAD & EDIT IMAGES ---
		Local dir:String = "images"
		
		SetClsColor(0, 0, 0)
		Cls
		Flip
		
		pitch = New t_pitch
		
		''--- PLAYERS ---
		
		Self.set_players_input_devices()
		
		Local k:Int = 0
		Local i:Int = 1
		For Local ply:t_player = EachIn team[HOME].lineup
			
			ply.load_image_by_skin()
			
			ListAddLast(sprites, ply)
			
			''progress bar
			k = i * 100 / team[HOME].lineup.Count()
			draw_bar(k*0.5)
			i = i +1
		Next
		
		''load hair
		i = 1
		For Local ply:t_player = EachIn team[HOME].lineup
			
			ply.load_hair()
			
			''progress bar
			k = i * 100 / team[HOME].lineup.Count()
			draw_bar(0.5*(k+100))
			i = i +1
		Next
		
		''--- other sprites ---
		
		ListAddLast(sprites, New t_goal_top_a)
		
		ListAddLast(sprites, New t_goal_top_b)
		
		For Local side_x:Int = -1 To 1 Step 2
			For Local side_y:Int = -1 To 1 Step 2
				ListAddLast(sprites, t_flagpost.Create(side_x, side_y))
			Next
		Next
		
		''coaches
		coach[HOME] = New t_coach
		coach[HOME].x = BENCH_X
		coach[HOME].y = BENCH_Y_UP +32
		coach[HOME].img = load_image(dir, "coach.png",	MASKEDIMAGE, 0)
		
		''use colors of the first kit
		team[HOME].kit_at_index(0).render_image(coach[HOME].img)
		ListAddLast(sprites, coach[HOME])
		
		''--- other images ---
		For Local i:Int = 0 To 4
			img_crowd[i] = LoadImage("images/stadium/tile_" + Chr(97+i) + ".png", DYNAMICIMAGE|MASKEDIMAGE)
			team[HOME].kit_at_index(0).render_image(img_crowd[i])
		Next
		
		img_goal_btm  = load_image(dir, "goal-btm.png", 0, $00F700)
		ball.img      = load_image(dir, location_settings.ball_filename(), DYNAMICIMAGE|MASKEDIMAGE, 0)
		img_keeper_cd = load_pixmap(dir, "keeper_cd.png")
		img_replay    = load_image(dir, "replay.png", MASKEDIMAGE, 0)
		img_pause     = load_image(dir, "pause.png", MASKEDIMAGE, 0)
		img_speed     = load_image(dir, "rspeed.png", 0, 0)
		Select (location_settings.weather_effect)
			Case WE.WIND
				img_wind = load_image(dir, "wind.png", MASKEDIMAGE, 0)
			Case WE.RAIN
				img_rain = load_image(dir, "rain.png", MASKEDIMAGE, 0)
			Case WE.SNOW
				img_snow = load_image(dir, "snow.png", MASKEDIMAGE, 0)
			Case WE.FOG
				img_fog = load_image(dir, "fog.png", MASKEDIMAGE, 0)
		End Select
		img_jumper   = load_image(dir, "jumper.png", 0, 0)
		img_time     = load_image(dir, "time.png", MASKEDIMAGE, 0)
		img_score    = load_image(dir, "score.png", MASKEDIMAGE, 0)
		img_number   = load_image(dir, "number.png", MASKEDIMAGE, 0)
		img_ucode10g = load_image(dir, "ucode_10.png", MASKEDIMAGE, 0)
		img_ucode14g = load_image(dir, "ucode_14.png", MASKEDIMAGE, 0)
		img_tactics  = load_image(dir, "tactics.png", MASKEDIMAGE, 0)
		
		''--- LOAD CLUB LOGOS / NATIONAL FLAGS ---
		team[HOME].load_clnf(1)
		
		''crowd
		crowd_list.set_limit(rank_match(team[HOME], team[HOME]) -1)
		
		''--- SHADOWS ---
		
		''day
		If (location_settings.time = TI.DAY)
			
			''swap colors
			Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
			rgb_pairs.add($404040, location_settings.grass.dark_shadow)
			
			''players
			img_player_shadows[0] = load_and_edit_png("images/player/shadows/player_0.png", rgb_pairs, MASKEDIMAGE, $00F700)
			img_keeper_shadows[0] = load_and_edit_png("images/player/shadows/keeper_0.png", rgb_pairs, MASKEDIMAGE, $00F700)
			
			''ball
			replace_image_color(ball.img, $FF005200, set_alpha(location_settings.grass.light_shadow, $FF))
			replace_image_color(ball.img, $FF001800, set_alpha(location_settings.grass.dark_shadow, $FF))
			
		''night
		Else
			
			''swap colors
			Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
			rgb_pairs.add($404040, location_settings.grass.light_shadow)
			
			''players
			For Local i:Int = 0 To 3
				img_player_shadows[i] = load_and_edit_png("images/player/shadows/player_" + i + ".png", rgb_pairs, MASKEDIMAGE, $00F700)
				img_keeper_shadows[i] = load_and_edit_png("images/player/shadows/keeper_" + i + ".png", rgb_pairs, MASKEDIMAGE, $00F700)
			Next
			
			''ball
			replace_image_color(ball.img, $FF005200, set_alpha(location_settings.grass.light_shadow, $FF))
			replace_image_color(ball.img, $FF001800, set_alpha(location_settings.grass.light_shadow, $FF))
			
		EndIf
		
		''--- LOAD SOUNDS ---
		sound = t_sound.Create("sfx")
		
		''sound channels
		channel = New t_channel
		
	End Method
	
	Method update()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.TRAINING_MAIN)
	End Method
	
	Method render()
	End Method
	
	Method set_players_input_devices()
		Local assigned_devices:Int = 0
		Local i:Int = 10
		While (i < team[HOME].lineup.Count())
			Local ply:t_player = team[HOME].lineup_at_index(i)
			If (assigned_devices < input_devices.Count())
				If (ply.role <> PR.GOALKEEPER)
					ply.set_input_device(input_devices.ValueAtIndex(assigned_devices))
					assigned_devices = assigned_devices +1
				EndIf
			Else
				ply.set_input_device(ply.get_ai())
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
	
End Type
