SuperStrict

Import "constants.bmx"
Import "t_dictionary.bmx"
Import "lib_misc.bmx"
Import "data_weather_cap.bmx"
Import "t_game_settings.bmx"
Import "data_grass.bmx"
Import "t_wind.bmx"

Global location_settings:t_location_settings
Global pitch_types:String[10]
Global img_rain:TImage
Global img_snow:TImage
Global img_fog:TImage

''replay vectors
Global vcamera_x:Int[VSIZE]
Global vcamera_y:Int[VSIZE]

Type t_location_settings

	Field sound_vol:Int
	Field time:Int					''TI.DAY, TI.NIGHT, TI.RANDOM
	Field sky:Int					''SK.CLEAR, SK.CLOUDY
	Field pitch_type:Int			''PT.FROZEN, PT.MUDDY, PT.WET, PT.SOFT, PT.NORMAL, PT.DRY, PT.HARD, PT.SNOWED, PT.WHITE, PT.RANDOM
	Field grass:t_grass
	Field weather_effect:Int		''WE.WIND, WE.RAIN, WE.SNOW, WE.FOG, WE.RANDOM
	Field weather_intensity:Int		''WI.NONE, WI.LIGHT, WI.STRONG
	Field wind:t_wind
	
	Field mod_w:Int
	Field mod_h:Int
	Field mod_x:Int
	Field mod_y:Int
		
	Method New()
		Self.sound_vol = game_settings.sound_vol
		Self.grass = New t_grass
		Self.wind = New t_wind
		
		Self.mod_w = VFRAMES
		Self.mod_h = 2*VFRAMES
		Self.mod_x = Ceil(PITCH_W/Float(mod_w))
		Self.mod_y = Ceil(PITCH_H/Float(mod_h)) 
	End Method
	
	Method rotate_time(direction:Int)
		Self.time = rotate(Self.time, 0, 2, direction)
	End Method
	
	Method rotate_pitch_type(direction:Int)
		Self.pitch_type = rotate(Self.pitch_type, 0, 9, direction)
		Self.weather_effect = WE.RANDOM
		Self.sky = SK.CLOUDY
	End Method
	
	Method rotate_weather()
		If (Self.pitch_type = PT.RANDOM)
			Return
		EndIf
		
		If ((Self.weather_intensity = WI.NONE) And (Self.sky = SK.CLEAR))
			Self.sky = SK.CLOUDY
		Else
			Local found:Int
			Repeat
				found = True
				If (Self.weather_effect = WE.RANDOM)
					Self.weather_effect = WE.WIND
					Self.weather_intensity = WI.NONE
				Else If (Self.weather_intensity < WI.STRONG)
					Self.weather_intensity = Self.weather_intensity +1
				Else
					Self.weather_effect = (Self.weather_effect +1) Mod 5
					Self.weather_intensity = WI.LIGHT
				EndIf

				''weather possibility check
				If (Self.weather_effect <> WE.RANDOM)
					If (Self.weather_intensity > weather_cap[Self.pitch_type, Self.weather_effect]) 
						found = False
					EndIf
					If (Self.weather_intensity > game_settings.weather_max)
						found = False
					EndIf
				EndIf

				''sky
				Self.sky = SK.CLOUDY
				If (Self.weather_intensity = WI.NONE) 
					Self.sky = SK.CLEAR
				Else If (Self.weather_effect = WE.WIND)
					Self.sky = SK.CLEAR
				EndIf
			Until found 
		EndIf
	End Method
	
	Method time_name:String()
		Select (Self.time)
			Case TI.DAY
				Return dictionary.gettext("DAY")
			Case TI.NIGHT
				Return dictionary.gettext("NIGHT")
			Case TI.RANDOM
				Return dictionary.gettext("RANDOM")
		End Select
	End Method
	
	Method pitch_type_name:String()
		Return pitch_types[Self.pitch_type]
	End Method
	
	Method weather_name:String()
		If (Self.weather_effect = WE.RANDOM)
			Return dictionary.gettext("RANDOM")
		Else 
			Select (Self.weather_intensity)
				Case WI.NONE
					If (Self.sky = SK.CLEAR)
						Return dictionary.gettext("CLEAR")
					Else
						Return dictionary.gettext("CLOUDY")
					EndIf
				Case WI.LIGHT
					Select (Self.weather_effect)
						Case WE.WIND
							Return dictionary.gettext("LIGHT WIND")
						Case WE.RAIN
							Return dictionary.gettext("LIGHT RAIN")
						Case WE.SNOW
							Return dictionary.gettext("LIGHT SNOW")
						Case WE.FOG
							Return dictionary.gettext("THIN FOG")
					End Select
				Case WI.STRONG
					Select (Self.weather_effect)
						Case WE.WIND
							Return dictionary.gettext("STRONG WIND")
						Case WE.RAIN
							Return dictionary.gettext("STRONG RAIN")
						Case WE.SNOW
							Return dictionary.gettext("STRONG SNOW")
						Case WE.FOG
							Return dictionary.gettext("THICK FOG")
					End Select
			End Select
		EndIf
	End Method
	
	Method weather_offset:Int()
			If (Self.weather_effect = WE.RANDOM)
			Return 10
		Else
			If (Self.weather_intensity = WI.NONE)
				Return Self.sky
			Else 
				Return 2 * Self.weather_effect +Self.weather_intensity +1
			EndIf
		EndIf
	End Method
	
	Method setup()
		Self.resolve_random_time()
		
		Self.resolve_random_pitch_type()
		
		Self.init_grass()
	
		Self.resolve_random_weather()
		
		Self.init_wind()
		
		Self.adjust_grass_friction()
		
		Self.adjust_grass_bounce()
	End Method
	
	Method resolve_random_time()
		If (Self.time = TI.RANDOM)
			Self.time = Rand(TI.DAY, TI.NIGHT)
		EndIf
	End Method
	
	Method resolve_random_pitch_type()
		If (Self.pitch_type = PT.RANDOM)
			Self.pitch_type = Rand(PT.FROZEN, PT.WHITE)
		EndIf
	End Method
	
	Method init_grass()
		Self.grass.copy(grass_array[Self.pitch_type])
	End Method
	
	Method resolve_random_weather()
		
		If (Self.weather_effect = WE.RANDOM)
			
			Self.weather_effect = Rand(WE.WIND, WE.FOG)
			Self.weather_intensity = 2 -round(Log10(Rand(1, 16)) / Log10(4))	''WI.NONE, WI.LIGHT, WI.STRONG
			
			''constrain by settings
			Self.weather_intensity = Min(Self.weather_intensity, game_settings.weather_max)
			
			''constrain by pitch_type
			Self.weather_intensity = Min(Self.weather_intensity, weather_cap[Self.pitch_type, Self.weather_effect])
	
			''sky
			Self.sky = SK.CLOUDY
			If (Self.weather_intensity = WI.NONE) 
				Self.sky = SK.CLEAR
			Else If (Self.weather_effect = WE.WIND)
				Self.sky = SK.CLEAR
			EndIf
		EndIf
		
	End Method
	
	Method init_wind()
		If (Self.weather_effect = WE.WIND)
			Self.wind.init(Self.weather_intensity)
		EndIf
	End Method
	
	Method adjust_grass_friction()
		If (Self.weather_intensity > WI.NONE)
			Select (Self.weather_effect)
				Case WE.RAIN
					Self.grass.friction = Self.grass.friction -1 * Self.weather_intensity
				Case WE.SNOW
					Self.grass.friction = Self.grass.friction +1 * Self.weather_intensity
			End Select
		EndIf
	End Method
	
	Method adjust_grass_bounce()
		If (Self.weather_intensity > WI.NONE)
			Select (Self.weather_effect)
				Case WE.RAIN
					Self.grass.bounce = Self.grass.bounce -3 * Self.weather_intensity
				Case WE.SNOW
					Self.grass.bounce = Self.grass.bounce -2 * Self.weather_intensity
			End Select
		EndIf
	End Method
	
	Method pitch_name:String()
		Local names:String[] = ["frozen", "muddy", "wet", "soft", "normal", "dry", "hard", "snowed", "white"]
		Return names[Self.pitch_type]
	End Method
	
	Method ball_filename:String()
		Local filename:String = "ball.png"
		If ((Self.pitch_type = PT.SNOWED) Or (Self.pitch_type = PT.WHITE))
			filename = "ballsnow.png"
		EndIf
		If ((Self.pitch_type = PT.FROZEN) And (Self.weather_effect = WE.SNOW) And (Self.weather_intensity > WI.NONE))
			filename = "ballsnow.png"
		EndIf
		Return filename
	End Method
	
	Method set_alpha()
		If (Self.time = TI.NIGHT)
			SetAlpha(0.4)
		Else
			SetAlpha(0.75)
		EndIf
	End Method
	
	Method draw_weather_effects(frame:Int)
		Select (Self.weather_effect)
			Case WE.RAIN
				SetBlend(ALPHABLEND)
				SetAlpha(0.6)
				
				SeedRnd(1)
				Rand(1)
				Local x:Int, y:Int, h:Int
				For Local i:Int = 1 To 40 * Self.weather_intensity
					x = Rand(0, Self.mod_w -1)
					y = Rand(0, Self.mod_h -1)
					h = (Rand(0, Self.mod_h -1) +frame) Mod Self.mod_h
					If (h > 0.3*Self.mod_h)
						For Local fx:Int = 0 To Self.mod_x
							For Local fy:Int = 0 To Self.mod_y
								Local px:Int = ((x +Self.mod_w -round(frame/Float(SUBFRAMES))) Mod Self.mod_w) +Self.mod_w*(fx -1)
								Local py:Int = ((y +4*round(frame/SUBFRAMES)) Mod Self.mod_h) +Self.mod_h*(fy -1)
								Local rf:Int = 3*h/Self.mod_h
								If (h > 0.9*Self.mod_h)
									rf = 3
								EndIf
								draw_sub_image_rect(img_rain, -CENTER_X +px , -CENTER_Y +py , 30, 30, 30*rf, 0, 30, 30)
							Next
						Next
					EndIf
				Next
				SetBlend(MASKBLEND)
				SetAlpha(1)
				
			Case WE.SNOW
				SetBlend(ALPHABLEND)
				SetAlpha(0.7)
				
				SeedRnd(1)
				Rand(1)
				Local x:Int, y:Int, h:Int, s:Int, a:Int
				For Local i:Int = 1 To 30 * Self.weather_intensity
					x = Rand(0, Self.mod_w -1)
					y = Rand(0, Self.mod_h -1)
					h = (Rand(0, Self.mod_h -1) +frame) Mod Self.mod_h
					s = i Mod 3
					a = Rand(0, 359)
					For Local fx:Int = 0 To Self.mod_x
						For Local fy:Int = 0 To Self.mod_y
							Local px:Int = ((x + Self.mod_w + 30*Sin(360*frame/Float(VSIZE) +a)) Mod Self.mod_w) +Self.mod_w*(fx -1)
							Local py:Int = ((y +2*round(frame/SUBFRAMES)) Mod Self.mod_h) +Self.mod_h*(fy -1)
							Local rf:Int = Rand(0, 2)
							draw_sub_image_rect(img_snow, -CENTER_X +px, -CENTER_Y +py, 3, 3, 3*s, 0, 3, 3)
						Next
					Next
				Next
				SetBlend(MASKBLEND)
				SetAlpha(1)
				
			Case WE.FOG
				SetBlend(ALPHABLEND)
				SetAlpha(0.25 * Self.weather_intensity)
				
				Const TILE_WIDTH:Int = 256
				Local fog_x:Int = -CENTER_X +vcamera_x[frame] -2*TILE_WIDTH +((CENTER_X -vcamera_x[frame]) Mod TILE_WIDTH +2*TILE_WIDTH) Mod TILE_WIDTH
				Local fog_y:Int = -CENTER_Y +vcamera_y[frame] -2*TILE_WIDTH +((CENTER_Y -vcamera_y[frame]) Mod TILE_WIDTH +2*TILE_WIDTH) Mod TILE_WIDTH
				Local x:Int = fog_x
				While (x < (fog_x + game_settings.screen_width + 2*TILE_WIDTH))
					Local y:Int = fog_y
					While (y < (fog_y +game_settings.screen_height +2*TILE_WIDTH))
						draw_image(img_fog, x +((frame/SUBFRAMES) Mod TILE_WIDTH), y +((2*frame/SUBFRAMES) Mod TILE_WIDTH))
						y = y +TILE_WIDTH
					Wend
					x = x +TILE_WIDTH
				Wend
				SetBlend(MASKBLEND)
				SetAlpha(1)
		End Select
	End Method
	
End Type
