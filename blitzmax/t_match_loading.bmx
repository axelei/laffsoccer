SuperStrict

Import "t_match_mode.bmx"
Import "t_goal_top_a.bmx"
Import "t_goal_top_b.bmx"
Import "t_flagpost.bmx"

Type t_match_loading Extends t_match_mode

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
		team[HOME].side = 3-2*Rand(2)		''-1 = upside, 1 = downside  
		team[AWAY].side = -team[HOME].side
	
		''generate bench side
		match_settings.bench_side = 3-2*Rand(2)  ''1 = home upside, -1 = home downside
	
		''--- CREATE & INITIALIZE ---
		
		sprites = New TList
		
		ball:t_ball = New t_ball
		
		ListAddLast(sprites, ball)
		
		team[HOME].index = HOME
		team[AWAY].index = AWAY
		
		function_key = New t_function_key
	
		''reset message timer
		message_timer:Int	= 0
	
		match_settings.commentary  = game_settings.commentary
		match_settings.radar       = game_settings.radar
		match_settings.auto_replay = game_settings.auto_replay
		
		location_settings.setup()
		
		''match status
		match_status        = New t_match_status
		match_status.length = match_length[game_settings.length]*60*1000
		match_status.period = match_status.FIRST_HALF
		
		match_status.coin_toss     = Rand(0, 1)  ''0 = home begins, 1 = away begins
		match_status.kick_off_team = match_status.coin_toss
		
		bench_status = New t_bench_status
		bench_status.target_x = -TOUCH_LINE -140 +game_settings.screen_width / (2*game_settings.zoom/100.0)
		bench_status.target_y = -20
	
		''bench size
		Select menu.status
			Case MS_FRIENDLY
				match_settings.substitutions = game_settings.substitutions
				match_settings.bench_size = game_settings.bench_size
			Case MS_COMPETITION
				match_settings.substitutions = competition.substitutions
				match_settings.bench_size = competition.bench_size
		End Select
	
		''substitutions made
		team[HOME].subs_count = 0
		team[AWAY].subs_count = 0
	
		''statistics
		stats[HOME] = New t_stats
		stats[AWAY] = New t_stats
		
		''scores
		For Local t:Int = HOME To AWAY
			score[t] = ""
		Next
		g_goal_list = New TList
		
		''replay vectors
		For Local i:Int = 0 To VSIZE-1
			vcamera_x[i] = 0.5*(PITCH_W - game_settings.screen_width/(game_settings.zoom/100.0))
			vcamera_y[i] = 0 ''CENTER_Y - game_settings.screen_height/2
		Next
	
		''lineup
		For Local t:Int = HOME To AWAY
			team[t].lineup.Clear()
			For Local i:Int = 0 To TEAM_SIZE +match_settings.bench_size -1
				team[t].lineup.AddLast(team[t].player_at_index(i))
			Next
		Next
		
		''reset input devices
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				If (team[t].uses_automatic_input_device())
					ply.set_input_device(ply.get_ai())
				EndIf
			Next
		Next
		
		''camera
		camera:t_camera	= New t_camera
		camera.set_x(0.5 * (PITCH_W -game_settings.screen_width/(game_settings.zoom/100.0)))
		camera.set_y(0)
		
		''highlights
		hl_recorded = 0
		
		''--- LOAD & EDIT IMAGES ---
		Local dir:String = "images"
		
		SetClsColor(0, 0, 0)
		Cls
		Flip
		
		pitch = New t_pitch
	
		''--- PLAYERS ---	
		Local k:Int = 0
		
		For Local t:Int = HOME To AWAY

			Local i:Int = 1
			For Local ply:t_player = EachIn team[t].lineup
				
				ply.load_image_by_skin()
	
				ListAddLast(sprites, ply)
				
				''progress bar
				k = (t*team[HOME].lineup.Count() +i) * 100 / (team[HOME].lineup.Count() +team[AWAY].lineup.Count())
				draw_bar(k*0.5)
				i = i +1
			Next
		Next
	
		''load hair
		For Local t:Int = HOME To AWAY
			
			Local i:Int = 1
			For Local ply:t_player = EachIn team[t].lineup
				
				ply.load_hair()
				
				''progress bar
				k = (t*team[HOME].lineup.Count() +i) * 100 / (team[HOME].lineup.Count() +team[AWAY].lineup.Count())
				draw_bar(0.5*(k+100))
				i = i +1
			Next
	
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
		For Local t:Int = HOME To AWAY
			coach[t] = New t_coach
			coach[t].x = BENCH_X
			coach[t].img = load_image(dir, "coach.png",	MASKEDIMAGE, 0)

			''use colors of the first kit
			team[t].kit_at_index(0).render_image(coach[t].img)
			If (1-2*t) = match_settings.bench_side
				coach[t].y	= BENCH_Y_UP + 32
			Else
				coach[t].y	= BENCH_Y_DOWN + 32
			EndIf
			ListAddLast(sprites, coach[t])
		Next
		
		''--- other images ---
		For Local i:Int = 0 To 4
			img_crowd[i] = LoadImage("images/stadium/tile_" + Chr(97+i) + ".png", DYNAMICIMAGE|MASKEDIMAGE)
			team[HOME].kit_at_index(0).render_image(img_crowd[i])
		Next
		
		img_goal_btm  = load_image(dir,  "goal-btm.png", 0, $00F700)
		ball.img      = load_image(dir,  location_settings.ball_filename(), DYNAMICIMAGE|MASKEDIMAGE, 0)
		img_keeper_cd = load_pixmap(dir, "keeper_cd.png")
		img_replay    = load_image(dir,  "replay.png", MASKEDIMAGE, 0)
		img_pause     = load_image(dir,  "pause.png", MASKEDIMAGE, 0)
		img_speed     = load_image(dir,  "rspeed.png", 0, 0)
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
		img_tiny_num = load_image(dir, "tiny_number.png", MASKEDIMAGE, 0)
		img_ucode10g = load_image(dir, "ucode_10.png", MASKEDIMAGE, 0)
		img_ucode14g = load_image(dir, "ucode_14.png", MASKEDIMAGE, 0)
		img_tactics  = load_image(dir, "tactics.png", MASKEDIMAGE, 0)
	
		''--- LOAD CLUB LOGOS / NATIONAL FLAGS ---
		For Local t:Int = HOME To AWAY
			team[t].load_clnf(1) 
		Next
	
		''crowd
		Local crowd_limit:Int  = rank_match(team[HOME], team[AWAY])
		If (menu.status = MS_COMPETITION)
			crowd_limit = crowd_limit + 1
		EndIf
		crowd_list.set_limit(crowd_limit)
		
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
	
		''load commentary
		dir = "sfx/commentary"
	
		''goal
		For Local i:Int = 1 To comm_goal.length
			comm_goal[i-1]	= load_sound (dir,	"10" + i + ".ogg",	False)
		Next
		
		''own goal
		For Local i:Int = 1 To comm_owngoal.length
			comm_owngoal[i-1]	= load_sound (dir,	"20" + i + ".ogg",	False)
		Next
		
		''corner
		For Local i:Int = 1 To comm_corner.length
			comm_corner[i-1]	= load_sound (dir,	"30" + i + ".ogg",	False)
		Next
		
		''alloc sound channels
		chn_comm = AllocChannel()
	
		SetChannelVolume(channel.intro, 0.1 * location_settings.sound_vol)
		PlaySound(sound.intro, channel.intro)
	
		SetChannelVolume(channel.crowd, 0.1 * location_settings.sound_vol)
		PlaySound(sound.crowd, channel.crowd)
		
	End Method

	Method update()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_INTRO)
	End Method
	
	Method render()
	End Method
	
End Type
