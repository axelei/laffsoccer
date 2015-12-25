SuperStrict

Import "t_game_mode.bmx"

Type t_menu_match_options Extends t_game_mode
	
	Field w_match_length:t_widget
	Field w_weather_effects:t_widget
	Field w_radar:t_widget
	Field w_auto_replays:t_widget
	Field w_resolution:t_widget
	Field w_zoom:t_widget
	Field w_rotation:t_widget
	Field w_sound_effects:t_widget
	Field w_commentary:t_widget
	
	Method New()

		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_match_options.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_colors($6101D7, $7D1DFF, $3A0181)
		w.set_geometry(512 -200, 30, 400, 40)
		w.set_text(dictionary.gettext("MATCH OPTIONS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''match length
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 130, 470, 36)
		w.set_text(dictionary.gettext("FRIENDLY/DIY GAME LENGTH"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($B32D95, $D555B8, $6E1C5C)
		w.set_geometry(512 +120, 130, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_match_length", ["+1"])
		w.bind("fire1_hold", "bc_match_length", ["+1"])
		w.bind("fire2_down", "bc_match_length", ["-1"])
		w.bind("fire2_hold", "bc_match_length", ["-1"])
		Self.w_match_length = w
		Self.update_match_length_button()
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
		''weather effects
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 190, 470, 36)
		w.set_text(dictionary.gettext("WEATHER EFFECTS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($2B4A61, $3F6A8B, $1E3242)
		w.set_geometry(512 +120, 190, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_weather_effects", ["+1"])
		w.bind("fire1_hold", "bc_weather_effects", ["+1"])
		w.bind("fire2_down", "bc_weather_effects", ["-1"])
		w.bind("fire2_hold", "bc_weather_effects", ["-1"])
		Self.w_weather_effects = w
		Self.update_weather_effects_button()
		Self.widgets.AddLast(w)
		
		''radar
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 250, 470, 36)
		w.set_text(dictionary.gettext("RADAR"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($B32D95, $D555B8, $6E1C5C)
		w.set_geometry(512 +120, 250, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_radar")
		w.bind("fire2_down", "bc_radar")
		Self.w_radar = w
		Self.update_radar_button()
		Self.widgets.AddLast(w)
				
		''autoreplays
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 310, 470, 36)
		w.set_text(dictionary.gettext("AUTO REPLAYS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($2B4A61, $3F6A8B, $1E3242)
		w.set_geometry(512 +120, 310, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_auto_replays")
		w.bind("fire2_down", "bc_auto_replays")
		Self.w_auto_replays = w
		Self.update_auto_replays_button()
		Self.widgets.AddLast(w)
				
		''resolution
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 370, 470, 36)
		w.set_text(dictionary.gettext("RESOLUTION"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($B32D95, $D555B8, $6E1C5C)
		w.set_geometry(512 +120, 370, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_resolution", ["+1"])
		w.bind("fire1_hold", "bc_resolution", ["+1"])
		w.bind("fire2_down", "bc_resolution", ["-1"])
		w.bind("fire2_hold", "bc_resolution", ["-1"])
		Self.w_resolution = w
		Self.update_resolution_button()
		Self.widgets.AddLast(w)
	
		''zoom
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 430, 470, 36)
		w.set_text(dictionary.gettext("ZOOM"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($2B4A61, $3F6A8B, $1E3242)
		w.set_geometry(512 +120, 430, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_zoom", ["+1"])
		w.bind("fire1_hold", "bc_zoom", ["+1"])
		w.bind("fire2_down", "bc_zoom", ["-1"])
		w.bind("fire2_hold", "bc_zoom", ["-1"])
		If (GetGraphicsDriver() <> GLMax2DDriver())
			w.active = False
			w.set_colors($666666, $8F8D8D, $404040)
		EndIf
		Self.w_zoom = w
		Self.update_zoom_button()
		Self.widgets.AddLast(w)
	
		''rotation
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 490, 470, 36)
		w.set_text(dictionary.gettext("ROTATION"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($B32D95, $D555B8, $6E1C5C)
		w.set_geometry(512 +120, 490, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_rotation")
		w.bind("fire2_down", "bc_rotation")
		Self.w_rotation = w
		Self.update_rotation_button()
		Self.widgets.AddLast(w)
	
		''sound effects
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 550, 470, 36)
		w.set_text(dictionary.gettext("SOUND EFFECTS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($2B4A61, $3F6A8B, $1E3242)
		w.set_geometry(512 +120, 550, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_sound_effects", ["+1"])
		w.bind("fire1_hold", "bc_sound_effects", ["+1"])
		w.bind("fire2_down", "bc_sound_effects", ["-1"])
		w.bind("fire2_hold", "bc_sound_effects", ["-1"])
		Self.w_sound_effects = w
		Self.update_sound_effects_button()
		Self.widgets.AddLast(w)
	
		''commentary
		w = New t_button
		w.set_colors($76683C, $A99757, $463E24)
		w.set_geometry(110, 610, 470, 36)
		w.set_text(dictionary.gettext("COMMENTARY"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_colors($B32D95, $D555B8, $6E1C5C)
		w.set_geometry(512 +120, 610, 240, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_commentary")
		w.bind("fire2_down", "bc_commentary")
		Self.w_commentary = w
		Self.update_commentary_button()
		Self.widgets.AddLast(w)
		
		''exit
		w = New t_button
		w.set_colors($C84200, $FF6519, $803300)
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MAIN)])
		Self.widgets.AddLast(w)
	
	End Method
	
	Method bc_match_length(n:Int)
		game_settings.length = slide(game_settings.length, 0, Len(match_length)-1, n)
		Self.update_match_length_button()
	End Method
	
	Method bc_weather_effects(n:Int)
		game_settings.weather_max = slide(game_settings.weather_max, 0, 2, n)
		Self.update_weather_effects_button()
	End Method	
	
	Method bc_radar()
		game_settings.radar = Not game_settings.radar
		Self.update_radar_button()
	End Method
	
	Method bc_auto_replays()
		game_settings.auto_replay = Not game_settings.auto_replay
		Self.update_auto_replays_button()
	End Method
	
	Method bc_resolution(n:Int)
		graphics_mode_index = slide(graphics_mode_index, 0, graphics_modes.Count() -1, n)
		Self.update_resolution_button()
	End Method
	
	Method bc_zoom(n:Int)
		game_settings.zoom = slide(game_settings.zoom, 100, 200, 10*n)
		Self.update_zoom_button()
	End Method
	
	Method bc_rotation()
		game_settings.rotation = Not game_settings.rotation
		Self.update_resolution_button()
		Self.update_rotation_button()
	End Method
	
	Method bc_sound_effects(n:Int)
		game_settings.sound_vol = slide(game_settings.sound_vol, 0, 10, n)
		Self.update_sound_effects_button()
	End Method
	
	Method bc_commentary()
		game_settings.commentary = Not game_settings.commentary
		Self.update_commentary_button()
	End Method
	
	Method update_match_length_button()
		''// NOTE: %n is replaced automatically by the game 
		Self.w_match_length.set_text(Replace(dictionary.gettext("%n MINUTES"), "%n", match_length[game_settings.length]))
	End Method
	
	Method update_weather_effects_button()
		Select (game_settings.weather_max)
			Case WI.NONE
				Self.w_weather_effects.set_text(dictionary.gettext("OFF"))
			Case WI.LIGHT
				Self.w_weather_effects.set_text(dictionary.gettext("LIGHT"))
			Case WI.STRONG
				Self.w_weather_effects.set_text(dictionary.gettext("STRONG"))
		End Select
	End Method
	
	Method update_radar_button()
		If (game_settings.radar)
			Self.w_radar.set_text(dictionary.gettext("ON"))
		Else
			Self.w_radar.set_text(dictionary.gettext("OFF"))
		EndIf
	End Method
	
	Method update_auto_replays_button()
		If (game_settings.auto_replay)
			Self.w_auto_replays.set_text(dictionary.gettext("ON"))
		Else
			Self.w_auto_replays.set_text(dictionary.gettext("OFF"))
		EndIf
	End Method
	
	Method update_resolution_button()
		If (game_settings.rotation)
			game_settings.screen_width = TGraphicsMode(graphics_modes.ValueAtIndex(graphics_mode_index)).height
			game_settings.screen_height = TGraphicsMode(graphics_modes.ValueAtIndex(graphics_mode_index)).width
		Else
			game_settings.screen_width = TGraphicsMode(graphics_modes.ValueAtIndex(graphics_mode_index)).width
			game_settings.screen_height = TGraphicsMode(graphics_modes.ValueAtIndex(graphics_mode_index)).height
		EndIf
		Self.w_resolution.set_text(game_settings.screen_width + " " + Chr(215) + " " + game_settings.screen_height)
	End Method
	
	Method update_zoom_button()
		Self.w_zoom.set_text(game_settings.zoom + "%")
	End Method
	
	Method update_rotation_button()
		If (game_settings.rotation)
			Self.w_rotation.set_text(dictionary.gettext("ON"))
		Else
			Self.w_rotation.set_text(dictionary.gettext("OFF"))
		EndIf
	End Method
	
	Method update_sound_effects_button()
		If (game_settings.sound_vol > 0)
			Self.w_sound_effects.set_text(game_settings.sound_vol)
		Else
			Self.w_sound_effects.set_text(dictionary.gettext("OFF"))
		EndIf
	End Method
	
	Method update_commentary_button()
		If (game_settings.commentary)
			Self.w_commentary.set_text(dictionary.gettext("ON"))
		Else
			Self.w_commentary.set_text(dictionary.gettext("OFF"))
		EndIf
	End Method
	
End Type
