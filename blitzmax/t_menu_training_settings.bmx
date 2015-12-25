SuperStrict

Import "t_game_mode.bmx"

Type t_menu_training_settings Extends t_game_mode
	
	Field img_light:TImage
	Field img_pitches:TImage
	Field img_weather:TImage
	
	Field w_time:t_widget
	Field w_time_picture:t_widget
	
	Field w_pitch:t_widget
	Field w_pitch_picture:t_widget
	
	Field w_weather:t_widget
	Field w_weather_picture:t_widget
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		music_mute = True
		
		''background
		Self.img_background = backgrounds.get("menu_training.jpg")
		
		''load images
		Self.img_light   = load_image("images", "light.png", 0, $000000)
		Self.img_pitches = load_image("images", "pitches.png", 0, $000000)
		Self.img_weather = load_image("images", "weather.png", 0, $000000)
		
		location_settings = New t_location_settings
		location_settings.sound_vol      = game_settings.sound_vol
		location_settings.time           = game_settings.time
		location_settings.pitch_type     = game_settings.pitch_type
		location_settings.weather_effect = game_settings.weather_effect
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*600, 30, 600, 40)
		w.set_colors($008080, $00B2B4, $004040)
		w.set_text(dictionary.gettext("TRAINING"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''time label
		w = New t_button
		w.set_geometry(512 -360, 150, 300, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("TIME"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''time button
		w = New t_button
		w.set_geometry(512 +60, 150, 300, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_time", ["+1"])
		w.bind("fire1_hold", "bc_time", ["+1"])
		w.bind("fire2_down", "bc_time", ["-1"])
		w.bind("fire2_hold", "bc_time", ["-1"])
		Self.w_time = w
		Self.update_time_button()
		Self.widgets.AddLast(w)
		
		''time picture
		w = New t_button
		w.set_geometry(512-26, 150+17-26, 50, 50)
		w.set_colors($000000, $8F8D8D, $404040)
		w.image = Self.img_light
		w.active = False
		Self.w_time_picture = w
		Self.update_time_picture()
		Self.widgets.AddLast(w)
		
		''pitch label
		w = New t_button
		w.set_geometry(512 -360, 220, 300, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("PITCH"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''pitch button
		w = New t_button
		w.set_geometry(512 +60, 220, 300, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_pitch", ["+1"])
		w.bind("fire1_hold", "bc_pitch", ["+1"])
		w.bind("fire2_down", "bc_pitch", ["-1"])
		w.bind("fire2_hold", "bc_pitch", ["-1"])
		Self.w_pitch = w
		Self.update_pitch_button()
		Self.widgets.AddLast(w)
		
		''pitch picture
		w = New t_button
		w.set_geometry(512-26, 220+17-26, 50, 50)
		w.set_colors($000000, $8F8D8D, $404040)
		w.image = Self.img_pitches
		w.active = False
		Self.w_pitch_picture = w
		Self.update_pitch_picture()
		Self.widgets.AddLast(w)
		
		''weather effect label
		w = New t_button
		w.set_geometry(512 -360, 290, 300, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("WEATHER"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''weather effect button
		w = New t_button
		w.set_geometry(512 +60, 290, 300, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_weather")
		w.bind("fire1_hold", "bc_weather")
		Self.w_weather = w
		Self.update_weather_button()
		Self.widgets.AddLast(w)
		
		''weather effect picture
		w = New t_button
		w.set_geometry(512-26, 290+17-26, 50, 50)
		w.set_colors($000000, $8F8D8D, $404040)
		w.image = Self.img_weather
		w.active = False
		Self.w_weather_picture = w
		Self.update_weather_picture()
		Self.widgets.AddLast(w)
		
		''free training
		w = New t_button
		w.set_geometry(512 -0.5*340, 400, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("FREE TRAINING"), 0, 14)
		w.bind("fire1_down", "bc_training_loading")
		Self.widgets.AddLast(w)
		
		Self.selected_widget = w
		
		''free kicks
		w = New t_button
		w.set_geometry(512 -0.5*340, 480, 340, 40)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("FREE KICKS"), 0, 14)
'		If ((game_settings.full_screen = True) And (graphics_mode[game_settings.gr_mode, 2] = False))
			w.active = False
			w.set_colors($666666, $8F8D8D, $404040)
'		EndIf
		Self.widgets.AddLast(w)
		
		''penalty kicks
		w = New t_button
		w.set_geometry(512 -0.5*340, 560, 340, 40)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("PENALTY KICKS"), 0, 14)
'		If ((game_settings.full_screen = True) And (graphics_mode[game_settings.gr_mode, 2] = False))
			w.active = False
			w.set_colors($666666, $8F8D8D, $404040)
'		EndIf
		Self.widgets.AddLast(w)
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*144, 708, 144, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
		
		If (Not Self.selected_widget.active)
			Self.selected_widget = w
		EndIf
		
	End Method
	
	Method bc_time(n:Int)
		location_settings.rotate_time(n)
		Self.update_time_picture()
		Self.update_time_button()
	End Method
	
	Method bc_pitch(n:Int)
		location_settings.rotate_pitch_type(n)
		Self.update_pitch_button()
		Self.update_pitch_picture()
		Self.update_weather_button()
		Self.update_weather_picture()
	End Method
	
	Method bc_weather()
		location_settings.rotate_weather()
		Self.update_weather_button()
		Self.update_weather_picture()
	End Method
	
	Method bc_training_loading()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.TRAINING_LOADING)
	End Method
	
	Method bc_exit()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAM)
	End Method
	
	Method update_time_button()
		Self.w_time.set_text(location_settings.time_name())
	End Method
	
	Method update_time_picture()
		Self.w_time_picture.set_frame(47, 46, location_settings.time, 0)
	End Method
	
	Method update_pitch_button()
		Self.w_pitch.set_text(pitch_types[location_settings.pitch_type])
	End Method
	
	Method update_pitch_picture()
		Self.w_pitch_picture.set_frame(47, 46, location_settings.pitch_type, 0)
	End Method
	
	Method update_weather_button()
		Self.w_weather.set_text(location_settings.weather_name())
	End Method
	
	Method update_weather_picture()
		Self.w_weather_picture.set_frame(47, 46, location_settings.weather_offset(), 0)
	End Method
	
End Type
