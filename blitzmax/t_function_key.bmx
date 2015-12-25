SuperStrict

Import "lib_data.bmx"

Global function_key:t_function_key

''onscreen messages
Global message:String
Global message_timer:Int
Global now:Int

Type t_function_key
	Field key_pause:Int
	Field key_record_action:Int
	Field key_sound_fx_up:Int
	Field key_sound_fx_down:Int
	Field key_commentary:Int
	Field key_crowd_chants:Int
	Field key_radar:Int
	Field key_auto_replay:Int
	
	Method update()
		
		If (KeyDown(KEY_SPACE) And Self.key_record_action = 0)
			record_action()
			
			message = dictionary.gettext("ACTION RECORDED")
			
			message_timer = now
		EndIf
		
		If (KeyDown(KEY_F5) And Self.key_sound_fx_down = 0)
			location_settings.sound_vol = Max(0, location_settings.sound_vol - 1)
			SetChannelVolume(channel.intro, 0.1 * location_settings.sound_vol)
			SetChannelVolume(channel.crowd, 0.1 * location_settings.sound_vol)
			
			Self.set_message_sound_effects()
			
			message_timer = now
		EndIf
		
		If (KeyDown(KEY_F6) And Self.key_sound_fx_up = 0)
			location_settings.sound_vol = Min(10, location_settings.sound_vol + 1)
			SetChannelVolume(channel.intro, 0.1 * location_settings.sound_vol)
			SetChannelVolume(channel.crowd, 0.1 * location_settings.sound_vol)
			
			Self.set_message_sound_effects()
			
			message_timer = now
		EndIf
		
		If (KeyDown(KEY_F10) And Self.key_crowd_chants = 0)
			game_settings.crowd_chants = Not game_settings.crowd_chants
			
			message = dictionary.gettext("CROWD CHANTS") + " "
			
			If (game_settings.crowd_chants)
				message :+ dictionary.gettext("ON")
			Else
				message :+ dictionary.gettext("OFF")
			EndIf
			
			message_timer = now
		EndIf
		
		If (KeyDown(KEY_F8) And Self.key_commentary = 0)
			match_settings.commentary = Not match_settings.commentary
			
			message = dictionary.gettext("COMMENTARY") + " "
			
			If (match_settings.commentary)
				message :+ dictionary.gettext("ON")
			Else
				message :+ dictionary.gettext("OFF")
			EndIf
			
			message_timer = now
		EndIf
		
		If (KeyDown(KEY_F11) And Self.key_radar = 0)
			match_settings.radar = Not match_settings.radar
			
			message = dictionary.gettext("RADAR") + " "
			
			If (match_settings.radar)
				message :+ dictionary.gettext("ON")
			Else
				message :+ dictionary.gettext("OFF")
			EndIf
			
			message_timer = now
		EndIf
		
		If (KeyDown(KEY_F12) And Self.key_auto_replay = 0)
			match_settings.auto_replay = Not match_settings.auto_replay
			
			message = dictionary.gettext("AUTO REPLAYS") + " "
			
			If (match_settings.auto_replay)
				message :+ dictionary.gettext("ON")
			Else
				message :+ dictionary.gettext("OFF")
			EndIf
			
			message_timer = now
		EndIf
		
		Self.key_pause         = KeyDown(KEY_P)
		Self.key_record_action = KeyDown(KEY_SPACE)
		Self.key_sound_fx_down = KeyDown(KEY_F5)
		Self.key_sound_fx_up   = KeyDown(KEY_F6)
		Self.key_commentary    = KeyDown(KEY_F8)
		Self.key_crowd_chants  = KeyDown(KEY_F10)
		Self.key_radar         = KeyDown(KEY_F11)
		Self.key_auto_replay   = KeyDown(KEY_F12)
		
	End Method
	
	Method set_message_sound_effects()
		message = dictionary.gettext("SOUND EFFECTS") + " <"
		
		For Local i:Int = 1 To 10
			If (i <= location_settings.sound_vol)
				message :+ "|"
			Else
				message :+ " "
			EndIf
		Next
		message :+ ">"
	End Method
	
End Type
