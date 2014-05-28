SuperStrict

Import "t_menu_main.bmx"
Import "t_menu_game_options.bmx"
Import "t_menu_match_options.bmx"
Import "t_menu_control.bmx"
Import "t_menu_friendly.bmx"
Import "t_menu_diy_competition.bmx"
Import "t_menu_design_diy_league.bmx"
Import "t_menu_select_teams.bmx"
Import "t_menu_play_league.bmx"
Import "t_menu_view_statistics.bmx"
Import "t_menu_top_scorers.bmx"
Import "t_menu_league_info.bmx"
Import "t_menu_select_squad_to_view.bmx"
Import "t_menu_view_team.bmx"
Import "t_menu_competition_warning.bmx"
Import "t_menu_load_teams.bmx"
Import "t_menu_clubs_confederation.bmx"
Import "t_menu_clubs_country.bmx"
Import "t_menu_clubs_division.bmx"
Import "t_menu_nationals_confederation.bmx"
Import "t_menu_select_team.bmx"
Import "t_menu_view_selected_teams.bmx"
Import "t_menu_match_presentation.bmx"
Import "t_menu_replay_match.bmx"
Import "t_menu_edit_players.bmx"
Import "t_menu_edit_team.bmx"
Import "t_menu_set_team.bmx"
Import "t_menu_intro.bmx"
Import "t_menu_test_match.bmx"
Import "t_match_loading.bmx"
Import "t_menu_design_diy_cup.bmx"
Import "t_menu_play_cup.bmx"
Import "t_menu_cup_info.bmx"
Import "t_menu_copying.bmx"
Import "t_menu_warranty.bmx"
Import "t_match_intro.bmx"
Import "t_match_starting_positions.bmx"
Import "t_match_kick_off.bmx"
Import "t_match_main.bmx"
Import "t_match_goal.bmx"
Import "t_match_corner_stop.bmx"
Import "t_match_corner_kick.bmx"
Import "t_match_goal_kick_stop.bmx"
Import "t_match_goal_kick.bmx"
Import "t_match_keeper_stop.bmx"
Import "t_match_throw_in_stop.bmx"
Import "t_match_throw_in.bmx"
Import "t_match_half_time_stop.bmx"
Import "t_match_half_time_positions.bmx"
Import "t_match_half_time_wait.bmx"
Import "t_match_half_time_enter.bmx"
Import "t_match_full_time_stop.bmx"
Import "t_match_extra_time_stop.bmx"
Import "t_match_half_extra_time_stop.bmx"
Import "t_match_full_extra_time_stop.bmx"
Import "t_match_bench_enter.bmx"
Import "t_match_bench_substitutions.bmx"
Import "t_match_bench_formation.bmx"
Import "t_match_bench_tactics.bmx"
Import "t_match_bench_exit.bmx"
Import "t_match_replay.bmx"
Import "t_match_pause.bmx"
Import "t_match_show_highlight.bmx"
Import "t_match_end_positions.bmx"
Import "t_match_end.bmx"
Import "t_menu_select_tactics_to_edit.bmx"
Import "t_menu_edit_tactics.bmx"
Import "t_menu_tactics_save_warning.bmx"
Import "t_menu_save_tactics.bmx"
Import "t_menu_tactics_abort_warning.bmx"
Import "t_menu_tactics_import.bmx"
Import "t_menu_tactics_load.bmx"
Import "t_menu_training_settings.bmx"
Import "t_training_loading.bmx"
Import "t_training_main.bmx"

Global game:t_game

Type t_game
	
	Field fg_mode:t_game_mode
	Field hold_mode:t_game_mode
	Field saved_frame:Int
	Field current_action:t_game_action
	Field game_timer:TTimer
	
	Method New()
		Self.fg_mode = Null
		Self.hold_mode = Null
		Self.current_action = Null
		
		''force graphics driver (windows version only)
		''SetGraphicsDriver GLMax2DDriver()
		''SetGraphicsDriver D3D7Max2DDriver()
		''SetGraphicsDriver D3D9Max2DDriver()
		
		debug = True
		debug = False
		
		AppTitle = "YSoccer"
		
		''count and initialize joysticks
		joysticks = JoyCount()
		
		''max 2 joysticks for now
		joysticks = Min(2, joysticks)
		
		''seed random value generator
		SeedRnd MilliSecs()
		Rand(1)
		
		''search available graphics modes
		graphics_modes = New TList
		Local last_mode:TGraphicsMode
		For Local mode:TGraphicsMode = EachIn GraphicsModes()
			If ((last_mode = Null) Or (mode.width <> last_mode.width) Or (mode.height <> last_mode.height))
				graphics_modes.AddLast(mode)
			EndIf
			last_mode = mode
		Next
		graphics_modes.Sort(True, compare_graphics_modes_by_dimensions)
		
		dictionary = New t_dictionary
		
		menu = New t_menu
		
		mouse = New t_mouse
		
		''--- input devices ---
		input_devices = New t_input_devices
		
		''keyboard
		For Local i:Int = 0 To 1
			Local d:t_input = New t_keyboard
			d.set_port(i)
			input_devices.AddLast(d)
		Next
		
		''joysticks
		For Local i:Int = 0 To joysticks -1
			Local d:t_input = New t_joystick
			d.set_port(i)
			input_devices.AddLast(d)
		Next
		
		''match lengths
		match_length[0] = 3
		match_length[1] = 5
		match_length[2] = 7
		match_length[3] = 10
		
		game_settings = New t_game_settings
		
		''audio drivers
		audio_drivers = New TList
		ListAddLast(audio_drivers, "auto")
		
		Local detected_audio_drivers:String[] = AudioDrivers()
		For Local i:Int = 0 To detected_audio_drivers.length-1
			ListAddLast(audio_drivers, detected_audio_drivers[i])
			
			''set default to freeaudio
			If (detected_audio_drivers[i] = "FreeAudio")
				game_settings.audio_driver = i +1
			EndIf
		Next
		
		game_settings.read()
		
		''search nearest to settings graphics mode
		For graphics_mode_index:Int = graphics_modes.Count() -1 To 0 Step -1
			Local mode:TGraphicsMode = TGraphicsMode(graphics_modes.ValueAtIndex(graphics_mode_index))
			If (game_settings.rotation)
				If (mode.height <= game_settings.screen_width) And (mode.width <= game_settings.screen_height)
					game_settings.screen_width = mode.height
					game_settings.screen_height = mode.width
					Exit
				EndIf
			Else
				If (mode.width <= game_settings.screen_width) And (mode.height <= game_settings.screen_height)
					game_settings.screen_width = mode.width
					game_settings.screen_height = mode.height
					Exit
				EndIf
			EndIf
		Next
				
		If (GetGraphicsDriver() <> GLMax2DDriver())
			game_settings.zoom = 100
		EndIf
		
		Local audio_driver_name:String = String(audio_drivers.valueatindex(game_settings.audio_driver))
		If (audio_driver_name <> "auto")
			SetAudioDriver(audio_driver_name)
		EndIf
		
		match_settings = New t_match_settings
		
		''fetch language
		dictionary.fetch_language(game_settings.language)
		associations_files = t_associations_files.Create()
		
		''highlights
		hl_recorded		= 0
		bnk_highlights	= CreateBank(HL_MAXNUMBER * HL_BANKSIZE)
		
		''backgrounds preloading
		backgrounds = New t_backgrounds
		
		''create teams 
		team[home] = New t_team
		team[away] = New t_team
		
		set_tactics_names()
		
		''tactics
		For Local i:Int = 0 To 17
			tactics_array[i] = New t_tactics
			tactics_array[i].load_file("tactics/preset/" + tactics_file[i] + ".TAC")
		Next
		
		set_player_types()
		
		set_pitch_types()
		
		set_months_names()
		
		associations_list = t_associations_list.Create()
		associations_flags = t_associations_flags.Create()
		
		crc_init(crc_table)
		
		load_calendars()
		
		crowd_list = t_crowd_list.Create()
		
		''width of unicode characters
		load_ucode_table(10)
		load_ucode_table(14)
		
		''menu music
		menu_music = New t_menu_music
		
		If (game_settings.sound_enabled)
			music_volume = 0
			music_mute = False
			menu_music.init("music")
			menu_music.set_mode(game_settings.music_mode)
			menu_music.set_volume(0.01*music_volume)
		EndIf
		
		Self.game_timer = CreateTimer(40)
		
		''force window mode if 1024x768 full screen is not available 
		If ((game_settings.full_screen = True) And (GraphicsModeExists(1024, 768) = False))
			game_settings.full_screen = False
		EndIf
		
		If (game_settings.full_screen)
			Graphics 1024, 768, 32
		Else
			Graphics 1024, 768, 0
		EndIf	
		HideMouse()
		img_ucode10 	= load_image("images", "ucode_10.png", MASKEDIMAGE, $0000FF)
		img_ucode14 	= load_image("images", "ucode_14.png", MASKEDIMAGE, $0000FF)
		img_arrow		= load_image("images", "arrow.png", MASKEDIMAGE, $0000FF)		
		
		light = 0	
		
	End Method
	
	Method pop_action()
		
		Self.current_action = game_action_queue.pop()
		
		Select (current_action.typ)
			
			Case AT_FADE_IN
				current_action.timer = 32
				
			Case AT_FADE_OUT
				current_action.timer = 32
				
			Case AT_NEW_FOREGROUND
				Self.fg_mode = new_mode(current_action.mode)
				
			Case AT_HOLD_FOREGROUND
				Self.hold_mode = Self.fg_mode
				Self.saved_frame = frame
				Self.fg_mode = new_mode(current_action.mode)
				
			Case AT_RESTORE_FOREGROUND
				frame = Self.saved_frame
				Self.fg_mode = Self.hold_mode
				Self.hold_mode = Null
				
		End Select
		
	End Method
	
	Method loop()
		
		Local quit:Int = False
		
		While (Not quit)
			
			Local frames:Int = WaitTimer(Self.game_timer)
			
			Local do_update:Int = True
			
			''fade in/out
			If (Self.current_action And current_action.typ = AT_FADE_OUT)
				light = 8*(current_action.timer -1)
				do_update = False
			EndIf
			If (Self.current_action And current_action.typ = AT_FADE_IN)
				light = 255 -8*(current_action.timer -1)
				do_update = False
			EndIf
			
			''update foreground
			If (Self.fg_mode And do_update)
				If (Self.current_action And (current_action.typ = AT_NEW_FOREGROUND Or current_action.typ = AT_RESTORE_FOREGROUND))
					Self.fg_mode.on_resume()
				EndIf
				If (Self.current_action And (current_action.typ = AT_HOLD_FOREGROUND))
					Self.hold_mode.on_pause()
				EndIf
				Self.fg_mode.update()
			EndIf
			
			''update current action
			If (Self.current_action)
				Self.current_action.update()
				If (current_action.timer = 0)
					Self.current_action = Null
				EndIf
			EndIf
			
			''get new action
			If (Not Self.current_action)
				If (game_action_queue.count() > 0)
					Self.pop_action()
				EndIf
			EndIf
			
			''quit
			If (KeyDown(KEY_LALT) And KeyHit(KEY_F1)) Or AppTerminate() Or (Self.fg_mode = Null)
				game_settings.write()
				DebugLog "quit"
				quit = True
			EndIf
			
			''render foreground
			If (Self.fg_mode)
				Self.fg_mode.render()
			EndIf
			
			If debug DrawText megabytes(GCMemAlloced()), 10, 36
			FlushMem()
			If debug DrawText megabytes(GCMemAlloced()), 10, 46
			
			Flip
		Wend
		
		EndGraphics
		
	End Method
	
	Method new_mode:t_game_mode(number:Int)
		
		Select (number)
			
			Case GM.MENU_INTRO
				Return New t_menu_intro
				
			Case GM.MENU_COPYING
				Return New t_menu_copying
				
			Case GM.MENU_WARRANTY
				Return New t_menu_warranty
				
			Case GM.MENU_MAIN
				Return New t_menu_main
				
			Case GM.MENU_GAME_OPTIONS
				Return New t_menu_game_options
				
			Case GM.MENU_MATCH_OPTIONS
				Return New t_menu_match_options
				
			Case GM.MENU_CONTROL
				Return New t_menu_control
				
			Case GM.MENU_FRIENDLY
				Return New t_menu_friendly
				
			Case GM.MENU_DIY_COMPETITION
				Return New t_menu_diy_competition
				
			Case GM.MENU_LOAD_TEAMS
				Return New t_menu_load_teams
				
			Case GM.MENU_CLUBS_CONFEDERATION
				Return New t_menu_clubs_confederation
				
			Case GM.MENU_CLUBS_COUNTRY
				Return New t_menu_clubs_country
				
			Case GM.MENU_CLUBS_DIVISION
				Return New t_menu_clubs_division
				
			Case GM.MENU_NATIONALS_CONFEDERATION
				Return New t_menu_nationals_confederation
				
			Case GM.MENU_SELECT_TEAMS
				Return New t_menu_select_teams
				
			Case GM.MENU_SELECT_TEAM
				Return New t_menu_select_team
				
			Case GM.MENU_VIEW_SELECTED_TEAMS
				Return New t_menu_view_selected_teams
				
			Case GM.MENU_SET_TEAM
				Return New t_menu_set_team
				
			Case GM.MENU_EDIT_PLAYERS
				Return New t_menu_edit_players
				
			Case GM.MENU_EDIT_TEAM
				Return New t_menu_edit_team
				
			Case GM.MENU_MATCH_PRESENTATION
				Return New t_menu_match_presentation
				
			Case GM.MENU_REPLAY_MATCH
				Return New t_menu_replay_match
				
			Case GM.MENU_DESIGN_DIY_LEAGUE
				Return New t_menu_design_diy_league
				
			Case GM.MENU_PLAY_LEAGUE
				Return New t_menu_play_league
				
			Case GM.MENU_VIEW_STATISTICS
				Return New t_menu_view_statistics
				
			Case GM.MENU_TOP_SCORERS
				Return New t_menu_top_scorers
				
			Case GM.MENU_LEAGUE_INFO
				Return New t_menu_league_info
				
			Case GM.MENU_SELECT_SQUAD_TO_VIEW
				Return New t_menu_select_squad_to_view
				
			Case GM.MENU_VIEW_TEAM
				Return New t_menu_view_team
				
			Case GM.MENU_COMPETITION_WARNING
				Return New t_menu_competition_warning
				
			Case GM.MENU_TEST_MATCH
				Return New t_menu_test_match
				
			Case GM.MATCH_LOADING
				Return New t_match_loading
				
			Case GM.MENU_DESIGN_DIY_CUP
				Return New t_menu_design_diy_cup
				
			Case GM.MENU_PLAY_CUP
				Return New t_menu_play_cup
				
			Case GM.MENU_CUP_INFO
				Return New t_menu_cup_info
				
			Case GM.MATCH_INTRO
				Return New t_match_intro
				
			Case GM.MATCH_STARTING_POSITIONS
				Return New t_match_starting_positions
				
			Case GM.MATCH_KICK_OFF
				Return New t_match_kick_off
				
			Case GM.MATCH_MAIN
				Return New t_match_main
				
			Case GM.MATCH_GOAL
				Return New t_match_goal
				
			Case GM.MATCH_CORNER_STOP
				Return New t_match_corner_stop
				
			Case GM.MATCH_CORNER_KICK
				Return New t_match_corner_kick
				
			Case GM.MATCH_GOAL_KICK_STOP
				Return New t_match_goal_kick_stop
				
			Case GM.MATCH_GOAL_KICK
				Return New t_match_goal_kick
				
			Case GM.MATCH_KEEPER_STOP
				Return New t_match_keeper_stop
				
			Case GM.MATCH_THROW_IN_STOP
				Return New t_match_throw_in_stop
				
			Case GM.MATCH_THROW_IN
				Return New t_match_throw_in
				
			Case GM.MATCH_HALF_TIME_STOP
				Return New t_match_half_time_stop
				
			Case GM.MATCH_HALF_TIME_POSITIONS
				Return New t_match_half_time_positions
				
			Case GM.MATCH_HALF_TIME_WAIT
				Return New t_match_half_time_wait
				
			Case GM.MATCH_HALF_TIME_ENTER
				Return New t_match_half_time_enter
				
			Case GM.MATCH_FULL_TIME_STOP
				Return New t_match_full_time_stop
				
			Case GM.MATCH_EXTRA_TIME_STOP
				Return New t_match_extra_time_stop
				
			Case GM.MATCH_HALF_EXTRA_TIME_STOP
				Return New t_match_half_extra_time_stop
				
			Case GM.MATCH_FULL_EXTRA_TIME_STOP
				Return New t_match_full_extra_time_stop
				
			Case GM.MATCH_BENCH_ENTER
				Return New t_match_bench_enter
				
			Case GM.MATCH_BENCH_SUBSTITUTIONS
				Return New t_match_bench_substitutions
				
			Case GM.MATCH_BENCH_FORMATION
				Return New t_match_bench_formation
				
			Case GM.MATCH_BENCH_TACTICS
				Return New t_match_bench_tactics
				
			Case GM.MATCH_BENCH_EXIT
				Return New t_match_bench_exit
				
			Case GM.MATCH_REPLAY
				Return New t_match_replay
				
			Case GM.MATCH_PAUSE
				Return New t_match_pause
				
			Case GM.MATCH_SHOW_HIGHLIGHT
				Return New t_match_show_highlight
				
			Case GM.MATCH_END_POSITIONS
				Return New t_match_end_positions
				
			Case GM.MATCH_END
				Return New t_match_end
				
			Case GM.MENU_SELECT_TACTICS_TO_EDIT
				Return New t_menu_select_tactics_to_edit
				
			Case GM.MENU_EDIT_TACTICS
				Return New t_menu_edit_tactics
				
			Case GM.MENU_TACTICS_SAVE_WARNING
				Return New t_menu_tactics_save_warning
				
			Case GM.MENU_SAVE_TACTICS
				Return New t_menu_save_tactics
				
			Case GM.MENU_TACTICS_ABORT_WARNING
				Return New t_menu_tactics_abort_warning
				
			Case GM.MENU_TACTICS_IMPORT
				Return New t_menu_tactics_import
				
			Case GM.MENU_TACTICS_LOAD
				Return New t_menu_tactics_load
				
			Case GM.MENU_TRAINING_SETTINGS
				Return New t_menu_training_settings
				
			Case GM.TRAINING_LOADING
				Return New t_training_loading
				
			Case GM.TRAINING_MAIN
				Return New t_training_main
				
			Case GM.QUIT
				Return Null
				
			Default
				RuntimeError("invalid game mode")
				
		End Select
		
	End Method
	
End Type
