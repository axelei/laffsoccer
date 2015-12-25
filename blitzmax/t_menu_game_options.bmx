SuperStrict

Import "t_game_mode.bmx"

Type t_menu_game_options Extends t_game_mode
	
	Field w_screen_mode:t_widget
	Field w_player_country:t_widget
	Field w_music_mode:t_widget
	Field w_music_volume:t_widget
	Field w_mouse:t_widget
	Field w_language:t_widget
	Field w_audio_driver:t_widget
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_game_options.jpg")
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_colors($536B90, $7090C2, $263142)
		w.set_geometry(512 -200, 30, 400, 40)
		w.set_text(dictionary.gettext("GAME OPTIONS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''screen mode
		w = New t_button
		w.set_colors($800000, $B40000, $400000)
		w.set_geometry(52, 150, 440, 36)
		w.set_text(dictionary.gettext("SCREEN MODE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($3C3C78, $5858AC, $202040)
		w.set_geometry(512 +20, 150, 440, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_screen_mode")
		w.bind("fire2_down", "bc_screen_mode")
		Self.w_screen_mode = w
		Self.update_screen_mode_button()
		Self.widgets.AddLast(w)
		
		Self.selected_widget = w
		
		''player country
		w = New t_button
		w.set_colors($800000, $B40000, $400000)
		w.set_geometry(52, 210, 440, 36)
		w.set_text(dictionary.gettext("PLAYER'S COUNTRY"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_geometry(512 +20, 210, 440, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_player_country")
		w.bind("fire2_down", "bc_player_country")
		Self.w_player_country = w
		Self.update_player_country_button()
		Self.widgets.AddLast(w)
		
		''music mode
		w = New t_button
		w.set_colors($800000, $B40000, $400000)
		w.set_geometry(52, 270, 440, 36)
		w.set_text(dictionary.gettext("MUSIC MODE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($3C3C78, $5858AC, $202040)
		w.set_geometry(512 +20, 270, 440, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_music_mode", ["+1"])
		w.bind("fire1_hold", "bc_music_mode", ["+1"])
		w.bind("fire2_down", "bc_music_mode", ["-1"])
		w.bind("fire2_hold", "bc_music_mode", ["-1"])
		Self.w_music_mode = w
		Self.update_music_mode_button()
		Self.widgets.AddLast(w)
		
		''music volume
		w = New t_button
		w.set_colors($800000, $B40000, $400000)
		w.set_geometry(52, 330, 440, 36)
		w.set_text(dictionary.gettext("MUSIC VOLUME"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_geometry(512 +20, 330, 440, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_music_volume", ["+1"])
		w.bind("fire1_hold", "bc_music_volume", ["+1"])
		w.bind("fire2_down", "bc_music_volume", ["-1"])
		w.bind("fire2_hold", "bc_music_volume", ["-1"])
		Self.w_music_volume = w
		Self.update_music_volume_button()
		Self.widgets.AddLast(w)
		
		''mouse
		w = New t_button
		w.set_colors($800000, $B40000, $400000)
		w.set_geometry(52, 390, 440, 36)
		w.set_text(dictionary.gettext("MOUSE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($3C3C78, $5858AC, $202040)
		w.set_geometry(512 +20, 390, 440, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_mouse")
		w.bind("fire2_down", "bc_mouse")
		Self.w_mouse = w
		Self.update_mouse_button()
		Self.widgets.AddLast(w)
		
		''language
		w = New t_button
		w.set_colors($800000, $B40000, $400000)
		w.set_geometry(52, 450, 440, 36)
		w.set_text(dictionary.gettext("LANGUAGE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_geometry(512 +20, 450, 440, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_language", ["+1"])
		w.bind("fire1_hold", "bc_language", ["+1"])
		w.bind("fire2_down", "bc_language", ["-1"])
		w.bind("fire2_hold", "bc_language", ["-1"])
		Self.w_language = w
		Self.update_language_button()
		Self.widgets.AddLast(w)
		
		''audio driver
		w = New t_button
		w.set_colors($800000, $B40000, $400000)
		w.set_geometry(52, 510, 440, 36)
		w.set_text(dictionary.gettext("AUDIO DRIVER"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($3C3C78, $5858AC, $202040)
		w.set_geometry(512 +20, 510, 440, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_audio_driver", ["+1"])
		w.bind("fire1_hold", "bc_audio_driver", ["+1"])
		w.bind("fire2_down", "bc_audio_driver", ["-1"])
		w.bind("fire2_hold", "bc_audio_driver", ["-1"])
		Self.w_audio_driver = w
		Self.update_audio_driver_button()
		Self.widgets.AddLast(w)
		
		''quit to os
		w = New t_button
		w.set_colors($008080, $00B2B4, $004040)
		w.set_geometry(512 -0.5*300, 590, 300, 36)
		w.set_text(dictionary.gettext("QUIT TO OS"), 0, 14)
		w.bind("fire1_down", "bc_quit_to_os")
		Self.widgets.AddLast(w)
		
		''exit
		w = New t_button
		w.set_colors($C84200, $FF6519, $803300)
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
		
	End Method
	
	Method bc_screen_mode()
		game_settings.full_screen = Not game_settings.full_screen
		Self.update_screen_mode_button()
	End Method
	
	Method bc_player_country()
		game_settings.use_flags = Not game_settings.use_flags
		Self.update_player_country_button()
	End Method
	
	Method bc_music_mode(n:Int)
		game_settings.music_mode = rotate(game_settings.music_mode, menu_music.get_mode_min(), menu_music.get_mode_max(), n)
		menu_music.set_mode(game_settings.music_mode)
		Self.update_music_mode_button()
	End Method
	
	Method bc_music_volume(n:Int)
		game_settings.music_vol = slide(game_settings.music_vol, 0, 100, 10*n)
		Self.update_music_volume_button()
	End Method
	
	Method bc_mouse()
		game_settings.mouse_enabled = Not game_settings.mouse_enabled
		Self.update_mouse_button()	
	End Method
	
	Method bc_language(n:Int)
		game_settings.language = rotate(game_settings.language, 0, dictionary.language_files.count()-1, n)
		
		''reload dictionary and language dependant data
		dictionary.fetch_language(game_settings.language)
		associations_files = t_associations_files.Create()
		set_tactics_names()
		set_player_types()
		set_pitch_types()
		set_months_names()
		
		Self.update_language_button()
	End Method
	
	Method bc_audio_driver(n:Int)
		game_settings.audio_driver = rotate(game_settings.audio_driver, 0, audio_drivers.count()-1, n)
		Self.update_audio_driver_button()
	End Method
	
	Method bc_quit_to_os()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.QUIT)
	End Method
	
	Method bc_exit()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
	End Method
	
	Method update_screen_mode_button()
		Select (game_settings.full_screen)
			Case True
				Self.w_screen_mode.text = dictionary.gettext("FULL SCREEN")
			Case False
				Self.w_screen_mode.text = dictionary.gettext("WINDOW")
		End Select
	End Method
	
	Method update_player_country_button()
		If (game_settings.use_flags)
			Self.w_player_country.text = dictionary.gettext("FLAG")
		Else
			Self.w_player_country.text = dictionary.gettext("CODE")
		EndIf
	End Method
	
	Method update_music_mode_button()
		Select (game_settings.music_mode)
			
			Case MM_ALL
				Self.w_music_mode.text = dictionary.gettext("ALL")
				
			Case MM_SHUFFLE	
				Self.w_music_mode.text = dictionary.gettext("SHUFFLE")
				
			Default
				Local track_name:String = menu_music.get_current_track_name()
				Local dot_position:Int = track_name.FindLast(".")
				If (dot_position <> -1)
					track_name = Left(track_name, dot_position)
				EndIf
				Self.w_music_mode.text = Upper(truncate(track_name, 28, "..."))
				
		End Select
	End Method
	
	Method update_music_volume_button()
		If (game_settings.music_vol > 0)
			Self.w_music_volume.text = game_settings.music_vol / 10
		Else
			Self.w_music_volume.text = dictionary.gettext("OFF")
		EndIf
	End Method
	
	Method update_mouse_button()
		If (game_settings.mouse_enabled)
			Self.w_mouse.text = dictionary.gettext("ON")
		Else
			Self.w_mouse.text = dictionary.gettext("OFF")
		EndIf
	End Method
	
	Method update_language_button()
		''// NOTE: the name of the language, e.g.: ENGLISH, ITALIANO, DEUTSCH
		Self.w_language.text = dictionary.gettext("// THIS LANGUAGE NAME //")
	End Method
	
	Method update_audio_driver_button()
		Self.w_audio_driver.text = Upper(String(audio_drivers.valueatindex(game_settings.audio_driver)))
	End Method
	
End Type
