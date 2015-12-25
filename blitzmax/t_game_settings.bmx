SuperStrict

Import "constants.bmx"
Import "t_json.bmx"

Global game_settings:t_game_settings

''keysets
Type t_key_set
	Field key_up:Int
	Field key_down:Int
	Field key_left:Int
	Field key_right:Int
	Field button_1:Int
	Field button_2:Int
End Type

Type t_game_settings
	Field screen_width:Int		''screen size during the match
	Field screen_height:Int		''
	Field full_screen:Int		''true = fullscreen, false = windowed
	Field zoom:Int              ''100% to 200%
	Field rotation:Int
	Field length:Int			''index of vector "match_length"

	Field weather_max:Int		''WI.NONE, WI.LIGHT, WI.STRONG
	Field substitutions:Int		''number of substitutions in a friendly
	Field bench_size:Int		''bench size in a friendly

	Field use_flags:Int			''flags/code to identificate players country
	Field sound_vol:Int			''sound effects volume
	Field commentary:Int		''match commentary
		
	Field music_mode:Int		''menu music mode
	Field music_vol:Int			''menu music volume
	
	Field radar:Int
	Field auto_replay:Int		''autoreplay after a goal
	Field mouse_enabled:Int
	
	Field language:Int			''menu language
	Field audio_driver:Int
	
	Field sound_enabled:Int		''sound effects on/off
	Field weather_effect:Int	''WE.WIND, WE.RAIN, WE.SNOW, WE.FOG, WE.RANDOM
	Field time:Int				''TI.DAY, TI.NIGHT, TI.RANDOM
''	Field sky:int				''SK.CLEAR, SK.CLOUDY
	Field pitch_type:Int		''PT.FROZEN, PT.MUDDY, PT.WET, PT.SOFT, PT.NORMAL, PT.DRY, PT.HARD, PT.SNOWED, PT.WHITE, PT.RANDOM
	Field crowd_chants:Int		''crowd chants
	
	Field key:t_key_set[2]
	
	Method New()
		Self.screen_width   = 1024
		Self.screen_height  = 768
		Self.full_screen 	= False
		Self.zoom           = 100
		Self.rotation       = False
		Self.length			= 0		''3 min

		Self.weather_max	= 1		''light
		Self.substitutions	= 2
		Self.bench_size		= 5 

		Self.use_flags		= True
		Self.sound_vol		= 4
		Self.commentary		= True

		Self.music_mode		= MM_ALL
		Self.music_vol		= 40
		
		Self.radar          = True
		Self.auto_replay    = True	
		Self.mouse_enabled  = True
		
		Self.language		= 0
		Self.audio_driver	= 0		''auto

		Self.sound_enabled	= True
		Self.weather_effect	= 4		''random

		Self.time			= 2		''random
		Self.pitch_type		= 9		''random
		Self.crowd_chants	= True
		
		Self.key[0] = New t_key_set
		Self.key[0].key_up		= KEY_UP
		Self.key[0].key_down	= KEY_DOWN
		Self.key[0].key_left	= KEY_LEFT
		Self.key[0].key_right	= KEY_RIGHT
		Self.key[0].button_1	= KEY_RSHIFT
		Self.key[0].button_2	= KEY_RCONTROL
		
		Self.key[1] = New t_key_set
		Self.key[1].key_up		= KEY_W
		Self.key[1].key_down	= KEY_Z
		Self.key[1].key_left	= KEY_A
		Self.key[1].key_right	= KEY_S
		Self.key[1].button_1	= KEY_C
		Self.key[1].button_2	= KEY_V

	End Method
	
	Method read()
	
		Local file_in:TStream
	
		file_in = ReadFile("ysoccer.ini")
		If file_in = Null Then Return
		
		Self.screen_width   = ReadInt(file_in)
		Self.screen_height  = ReadInt(file_in)
		Self.full_screen	= ReadInt(file_in)
		Self.zoom           = ReadInt(file_in)
		Self.rotation       = ReadInt(file_in)
		Self.length			= ReadInt(file_in)
		
		Self.weather_max	= ReadInt(file_in)
		Self.substitutions	= ReadInt(file_in)
		Self.bench_size		= ReadInt(file_in)
		
		Self.use_flags		= ReadInt(file_in)
		Self.sound_vol		= ReadInt(file_in)
		Self.commentary		= ReadInt(file_in)
		
		Self.music_mode		= ReadInt(file_in)
		Self.music_vol		= ReadInt(file_in)
		
		Self.radar          = ReadInt(file_in)
		Self.auto_replay    = ReadInt(file_in)
		Self.mouse_enabled  = ReadInt(file_in)
		
		Self.language		= ReadInt(file_in)
		Self.audio_driver	= ReadInt(file_in)
		
		Self.key[0].key_up	 	= ReadInt(file_in)
		Self.key[0].key_down	= ReadInt(file_in)
		Self.key[0].key_left	= ReadInt(file_in)
		Self.key[0].key_right	= ReadInt(file_in)
		Self.key[0].button_1	= ReadInt(file_in)
		Self.key[0].button_2	= ReadInt(file_in)
		
		Self.key[1].key_up		= ReadInt(file_in)
		Self.key[1].key_down	= ReadInt(file_in)
		Self.key[1].key_left	= ReadInt(file_in)
		Self.key[1].key_right	= ReadInt(file_in)
		Self.key[1].button_1	= ReadInt(file_in)
		Self.key[1].button_2	= ReadInt(file_in)
		
		CloseFile(file_in)
	
	End Method
	
	Method write()
	
		Local file_out:TStream
		file_out = WriteFile("ysoccer.ini")
		If file_out = Null Then Return
	
		WriteInt(file_out, Self.screen_width)
		WriteInt(file_out, Self.screen_height)
		WriteInt(file_out, Self.full_screen)
		WriteInt(file_out, Self.zoom)
		WriteInt(file_out, Self.rotation)
		WriteInt(file_out, Self.length)
		
		WriteInt(file_out, Self.weather_max)
		WriteInt(file_out, Self.substitutions)
		WriteInt(file_out, Self.bench_size)
		
		WriteInt(file_out, Self.use_flags)
		WriteInt(file_out, Self.sound_vol)
		WriteInt(file_out, Self.commentary)
		
		WriteInt(file_out, Self.music_mode)
		WriteInt(file_out, Self.music_vol)
		
		WriteInt(file_out, Self.radar)
		WriteInt(file_out, Self.auto_replay)
		WriteInt(file_out, Self.mouse_enabled)
		
		WriteInt(file_out, Self.language)
		WriteInt(file_out, Self.audio_driver)
		
		WriteInt(file_out, Self.key[0].key_up)
		WriteInt(file_out, Self.key[0].key_down)
		WriteInt(file_out, Self.key[0].key_left)
		WriteInt(file_out, Self.key[0].key_right)
		WriteInt(file_out, Self.key[0].button_1)
		WriteInt(file_out, Self.key[0].button_2)
		
		WriteInt(file_out, Self.key[1].key_up)
		WriteInt(file_out, Self.key[1].key_down)
		WriteInt(file_out, Self.key[1].key_left)
		WriteInt(file_out, Self.key[1].key_right)
		WriteInt(file_out, Self.key[1].button_1)
		WriteInt(file_out, Self.key[1].button_2)
	
		CloseFile( file_out )
		
		Local s:String = JSON.stringify(Self, "~t")
		Local ts:TStream = WriteFile("settings.json")
		If (ts = Null) Then Return
		WriteString(ts, s)
		CloseFile(ts)
	
	End Method

End Type
