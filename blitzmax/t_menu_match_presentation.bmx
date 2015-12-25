SuperStrict

Import "t_game_mode.bmx"

Type t_menu_match_presentation Extends t_game_mode

	Field img_block:TImage
	Field img_light:TImage
	Field img_pitches:TImage
	Field img_weather:TImage
	
	Field w_time:t_widget
	Field w_time_label:t_widget
	
	Field w_pitch:t_widget
	Field w_pitch_label:t_widget
	
	Field w_weather:t_widget
	Field w_weather_label:t_widget
	
	Field w_block:t_widget[2]
	Field w_image:t_widget[2]
	
	Method New()

		Self.type_id = TTypeId.ForObject(Self)
		
		music_mute = True
		
		''try to find kits with diferrent kit colors
		#kits_loop
		For Local i:Int = 0 To team[HOME].kits.Count() -1
			For Local j:Int = 0 To team[AWAY].kits.Count() -1
				Local home_kit:t_kit = team[HOME].kit_at_index(i)
				Local away_kit:t_kit = team[AWAY].kit_at_index(j)
				Local home_rgb1:Int = kit_color[home_kit.shirt1, 0]
				Local home_rgb2:Int = kit_color[home_kit.shirt2, 0]
				Local away_rgb1:Int = kit_color[away_kit.shirt1, 0]
				Local away_rgb2:Int = kit_color[away_kit.shirt2, 0]
				Local difference_pair:Float = rgb_difference(home_rgb1, away_rgb1) + rgb_difference(home_rgb2, away_rgb2)
				Local difference_swap:Float = rgb_difference(home_rgb1, away_rgb2) + rgb_difference(home_rgb2, away_rgb1)
				Local difference:Float = Min(difference_pair, difference_swap)
				If (difference > 40)
					team[HOME].kit = i
					team[AWAY].kit = j
					Exit kits_loop
				EndIf
			Next
		Next
		
		''background
		Self.img_background = backgrounds.get("menu_match_presentation.jpg")
		
		''load images	
		Self.img_block		= load_image("images", "block.png", 0, $000000)
		Self.img_light		= load_image("images", "light.png", 0, $000000)
		Self.img_pitches	= load_image("images", "pitches.png", 0, $000000)
		Self.img_weather	= load_image("images", "weather.png", 0, $000000)
	
		For Local t:Int = home To away
	
			''load raw kit
			img_kit_raw[t] = LoadImage(team[t].get_image_path("kit"), MASKEDIMAGE|DYNAMICIMAGE)
	
			''render kit
			img_kit[t] = copy_image(img_kit_raw[t])
			team[t].kit_at_index(team[t].kit).render_image(img_kit[t])
	
			''create shadow
			img_kit_shad[t] = copy_image(img_kit_raw[t])
			image2shadow(img_kit_shad[t], $242424)
	
			''club logo / national flag
			team[t].load_clnf(0)
		Next
		
		location_settings = New t_location_settings
		
		Select menu.status

			Case MS_FRIENDLY
				location_settings.time           = game_settings.time
				location_settings.pitch_type     = game_settings.pitch_type
				location_settings.weather_effect = WE.RANDOM

			Case MS_COMPETITION
				location_settings.time           = competition.time
				location_settings.pitch_type     = competition.get_pitch_type()
				location_settings.weather_effect = Rand(0, 3) ''WE.WIND, WE.RAIN, WE.SNOW, WE.FOG
				
				location_settings.weather_intensity = 2 -round(Log10(Rand(1, 16)) / Log10(4))	''WI.NONE, WI.LIGHT, WI.STRONG

				''intensity cap #1: game settings
				location_settings.weather_intensity = Min(location_settings.weather_intensity, game_settings.weather_max)
			
				''intensity cap #2: pitch type
				If (location_settings.weather_intensity > weather_cap[location_settings.pitch_type, location_settings.weather_effect])
					location_settings.weather_intensity = weather_cap[location_settings.pitch_type, location_settings.weather_effect]
				EndIf

				''sky
				location_settings.sky = SK.CLOUDY
				If (location_settings.weather_intensity = WI.NONE) 
					location_settings.sky = SK.CLEAR
				Else If (location_settings.weather_effect = WE.WIND)
					location_settings.sky = SK.CLEAR
				EndIf
				
		End Select
	
		''faders
		Local fader:t_fader
		fader = New t_fader		
		fader.set_geometry(512 -0.5*840, 74, 840, 231)
		fader.set_alpha_color(0.35)
		Self.widgets.AddLast(fader)

		fader = New t_fader		
		fader.set_geometry(45, 370, 512-90, 365)
		fader.set_alpha_color(0.35)
		Self.widgets.AddLast(fader)
		
		fader = New t_fader		
		fader.set_geometry(512+45, 370, 512-90, 365)
		fader.set_alpha_color(0.35)
		Self.widgets.AddLast(fader)
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*840, 30, 840, 44)
		w.set_colors($008080, $00B2B4, $004040)
		Local s:String
		Select menu.status
			Case MS_FRIENDLY
				s = dictionary.gettext("FRIENDLY")
				
			Case MS_COMPETITION
				s = competition.get_menu_title()
		End Select
		w.set_text(s, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''time (day/night)
		w = New t_button
		w.set_geometry(512-250-2, 117-2, 50, 50)
		w.set_colors($000000, $8F8D8D, $404040)
		w.image = Self.img_light
		w.set_frame(47, 46, location_settings.time, 0)
		w.bind("fire1_down", "bc_time", ["+1"])
		w.bind("fire1_hold", "bc_time", ["+1"])
		w.bind("fire2_down", "bc_time", ["-1"])
		w.bind("fire2_hold", "bc_time", ["-1"])
		w.active = (menu.status = MS_FRIENDLY)
		Self.w_time = w
		Self.widgets.AddLast(w)
	
		w = New t_label
		w.set_text(dictionary.gettext("TIME") + ":", -1, 10)
		w.set_position(0.5*1024, 190)
		Self.widgets.AddLast(w)
		
		w = New t_label
		w.set_text(location_settings.time_name(), 1, 10)
		w.set_position(0.5*1024 -10, 190)
		Self.w_time_label = w
		Self.widgets.AddLast(w)

		''pitch
		w = New t_button
		w.set_geometry(512-250-2, 187-2, 50, 50)
		w.set_colors($000000, $8F8D8D, $404040)
		w.image = Self.img_pitches
		w.set_frame(47, 46, location_settings.pitch_type, 0)
		w.bind("fire1_down", "bc_pitch", ["+1"])
		w.bind("fire1_hold", "bc_pitch", ["+1"])
		w.bind("fire2_down", "bc_pitch", ["-1"])
		w.bind("fire2_hold", "bc_pitch", ["-1"])
		w.active = (menu.status = MS_FRIENDLY)
		Self.w_pitch = w
		Self.widgets.AddLast(w)
	
		w = New t_label
		w.set_text(dictionary.gettext("PITCH") + ":", -1, 10)
		w.set_position(0.5*1024, 210)
		Self.widgets.AddLast(w)
		
		w = New t_label
		w.set_text(location_settings.pitch_type_name(), 1, 10)
		w.set_position(0.5*1024 -10, 210)
		Self.w_pitch_label = w
		Self.widgets.AddLast(w)

		''weather effect
		w = New t_button
		w.set_geometry(512-250-2, 257-2, 50, 50)
		w.set_colors($000000, $8F8D8D, $404040)
		w.image = Self.img_weather
		w.set_frame(47, 46, location_settings.weather_offset(), 0)
		w.bind("fire1_down", "bc_weather")
		w.bind("fire1_hold", "bc_weather")
		w.active = (menu.status = MS_FRIENDLY)
		Self.w_weather = w
		Self.widgets.AddLast(w)
	
		w = New t_label
		w.set_text(dictionary.gettext("WEATHER") + ":", -1, 10)
		w.set_position(0.5*1024, 230)
		Self.widgets.AddLast(w)
		
		w = New t_label
		w.set_text(location_settings.weather_name(), 1, 10)
		w.set_position(0.5*1024 -10, 230)
		Self.w_weather_label = w
		Self.widgets.AddLast(w)

		''home kit - prev
		w = New t_button
		w.set_geometry(512 -2*40 -170, 640, 40, 40)
		w.set_colors($1D6DFF, $5B99FF, $0042AE)
		w.set_text("<", 0, 14)
		w.bind("fire1_down", "bc_team_kit", [String(HOME), "-1"])
		w.bind("fire1_hold", "bc_team_kit", [String(HOME), "-1"])
		Self.widgets.AddLast(w)
	
		''home kit - next
		w = New t_button
		w.set_geometry(512 -40 -170, 640, 40, 40)
		w.set_colors($1D6DFF, $5B99FF, $0042AE)
		w.set_text(">", 0, 14)
		w.bind("fire1_down", "bc_team_kit", [String(HOME), "+1"])
		w.bind("fire1_hold", "bc_team_kit", [String(HOME), "+1"])
		Self.widgets.AddLast(w)
	
		''away kit - prev
		w = New t_button
		w.set_geometry(512 +170, 640, 40, 40)
		w.set_colors($1D6DFF, $5B99FF, $0042AE)
		w.set_text("<", 0, 14)
		w.bind("fire1_down", "bc_team_kit", [String(AWAY), "-1"])
		w.bind("fire1_hold", "bc_team_kit", [String(AWAY), "-1"])
		Self.widgets.AddLast(w)
	
		''away kit - next
		w = New t_button
		w.set_geometry(512 +40 +170, 640, 40, 40)
		w.set_colors($1D6DFF, $5B99FF, $0042AE)
		w.set_text(">", 0, 14)
		w.bind("fire1_down", "bc_team_kit", [String(AWAY), "+1"])
		w.bind("fire1_hold", "bc_team_kit", [String(AWAY), "+1"])
		Self.widgets.AddLast(w)
	
		''play match
		w = New t_button
		w.set_geometry(512 -160, 262, 240, 44)
		w.set_colors($DC0000, $FF4141, $8C0000)
		w.set_text(dictionary.gettext("PLAY MATCH"), 0, 14)
		w.bind("fire1_down", "bc_play")
		Self.widgets.AddLast(w)
		
		Self.selected_widget = w
		
		''exit
		w = New t_button
		w.set_geometry(512 +110, 264, 144, 40)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
		
		If (Not Self.selected_widget.active)
			Self.selected_widget = w
		EndIf
		
		For Local t:Int = HOME To AWAY
	
			''team name
			w = New t_button
			w.set_geometry(45 + t * 512, 340, 512 -90, 30)
			w.set_colors($1F1F95, $3030D4, $151563)
			w.set_text(team[t].name, 0, 14)
			w.active = False
			Self.widgets.AddLast(w)
	
			''kit selectors
			''home
			w = New t_button
			w.set_geometry(65 + t*(1024 -2*65 -24*5), 400 +295 +10, 24, 18)
			w.set_colors($DA2A70, $E45C92, $A41C52)
			w.active = False
			Self.widgets.AddLast(w)
	
			''away
			w = New t_button
			w.set_geometry(65 + 24 + t*(1024 -2*65 -24*5), 400 +295 +10, 24, 18)
			w.set_colors($DA2A70, $E45C92, $A41C52)
			w.active = False
			Self.widgets.AddLast(w)
	
			''third
			w = New t_button
			w.set_geometry(65 + 24*2 + t*(1024 -2*65 -24*5), 400 +295 +10, 24, 18)
			w.set_colors($DA2A70, $E45C92, $A41C52)
			w.active = False
			Self.widgets.AddLast(w)
	
			''change1
			w = New t_button
			w.set_geometry(65 + 24*3 + t*(1024 -2*65 -24*5), 400 +295 +10, 24, 18)
			If (team[t].kit_count => 4)
				w.set_colors($DA2A70, $E45C92, $A41C52)
			Else
				w.set_colors($666666, $8F8D8D, $404040)
			EndIf		
			w.active = False
			Self.widgets.AddLast(w)
	
			''change2
			w = New t_button
			w.set_geometry(65 + 24*4 + t*(1024 -2*65 -24*5), 400 +295 +10, 24, 18)
			If (team[t].kit_count = 5)
				w.set_colors($DA2A70, $E45C92, $A41C52)
			Else
				w.set_colors($666666, $8F8D8D, $404040)
			EndIf
			w.active = False
			Self.widgets.AddLast(w)
			
			''block						
			w = New t_picture
			w.image = Self.img_block
			w.set_geometry(65 +t*(1024-2*65) +5 +24*(team[t].kit-5*t), 400 +295 +12, 14, 14)
			Self.w_block[t] = w
			Self.widgets.AddLast(w)
		
			''club logo / national flag
			w = New t_picture
			w.image = team[t].clnf
			Local lgw:Int = ImageWidth(team[t].clnf)
			Local lgh:Int = ImageHeight(team[t].clnf)
			Local lgx:Int = 302 + t*(1024-2*302) -0.5*lgw
			Local lgy:Int = 490 -0.5*lgh
			w.set_geometry(lgx, lgy, lgw, lgh)
			Self.widgets.AddLast(w)
			
			''kit
			w = New t_picture
			w.image = img_kit_shad[t]
			Local kit_x:Int = 45
			Local kit_y:Int = 400
			Local kit_w:Int = ImageWidth(img_kit[t])
			Local kit_h:Int = ImageHeight(img_kit[t])
			w.set_geometry(kit_x +t*(1024 -2*kit_x -kit_w) +2, kit_y +2, kit_w, kit_h)
			Self.widgets.AddLast(w)
			
			w = New t_picture
			w.image = img_kit[t]
			w.set_geometry(kit_x +t*(1024 -2*kit_x -kit_w), kit_y, kit_w, kit_h)
			Self.w_image[t] = w
			Self.widgets.AddLast(w)
		Next		

		''city
		If (team[HOME].city <> "")
			w = New t_label
			w.set_text(dictionary.gettext("CITY") + ":", -1, 10)
			w.set_position(0.5*1024, 130)
			Self.widgets.AddLast(w)
			
			w = New t_label
			w.set_text(team[HOME].city, 1, 10)
			w.set_position(0.5*1024 -10, 130)
			Self.widgets.AddLast(w)
		EndIf
		
		''stadium
		If (team[HOME].stadium <> "")
			w = New t_label
			w.set_text(dictionary.gettext("STADIUM") + ":", -1, 10)
			w.set_position(0.5*1024, 150)
			Self.widgets.AddLast(w)
			
			w = New t_label
			w.set_text(team[HOME].stadium, 1, 10)
			w.set_position(0.5*1024 -10, 150)
			Self.widgets.AddLast(w)
		EndIf
		
	End Method

	Method bc_time(n:Int)
		location_settings.rotate_time(n)
		Self.w_time.frame_x = location_settings.time
		Self.w_time_label.set_text(location_settings.time_name(), 1, 10)
	End Method
	
	Method bc_pitch(n:Int)
		location_settings.rotate_pitch_type(n)
		Self.w_pitch.frame_x = location_settings.pitch_type
		Self.w_pitch_label.set_text(location_settings.pitch_type_name(), 1, 10)
		Self.w_weather.frame_x = location_settings.weather_offset()
		Self.w_weather_label.set_text(location_settings.weather_name(), 1, 10)
	End Method
	
	Method bc_weather()
		location_settings.rotate_weather()
		Self.w_weather.frame_x = location_settings.weather_offset()
		Self.w_weather_label.set_text(location_settings.weather_name(), 1, 10)
	End Method
	
	Method bc_team_kit(t:Int, n:Int)
		team[t].kit = rotate(team[t].kit, 0, team[t].kit_count -1, n)
		img_kit_raw[t] = LoadImage(team[t].get_image_path("kit"), MASKEDIMAGE|DYNAMICIMAGE)
		img_kit[t] = copy_image(img_kit_raw[t])
		team[t].kit_at_index(team[t].kit).render_image(img_kit[t])
		img_kit_shad[t] = copy_image(img_kit_raw[t])
		image2shadow(img_kit_shad[t], $242424)
		Self.w_block[t].x = 65 +t*(1024 -2*65) +5 +24*(team[t].kit -5*t)
		Self.w_image[t].image = img_kit[t]
	End Method
	
	Method bc_play()
		If (menu.status = MS_FRIENDLY)
			team_list = Null
		EndIf
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_LOADING)
	End Method
	
	Method bc_exit()
		Select menu.status
			Case MS_FRIENDLY
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAMS)
			Case MS_COMPETITION
				Select competition.typ
					Case CT_LEAGUE
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
					Case CT_CUP
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
				End Select
		End Select
	End Method

End Type
