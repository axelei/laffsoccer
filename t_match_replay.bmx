SuperStrict

Import "t_match_mode.bmx"

Type t_match_replay Extends t_match_mode
	
	Field frame0:Int
	Field paused:Int
	Field slow_motion:Int
	Field key_slow:Int
	Field key_pause:Int
	Field position:Int
	Field controlling_device:t_input
	
	Method New()
		
		Self.display_controlled_player = False
		Self.display_time = False
		Self.display_radar = False
		
		Self.frame0 = frame
	
		frame = (Self.frame0 +1) Mod VSIZE
		
		Self.paused = False		
		Self.slow_motion = False
		
		''control keys
		Self.key_slow = KeyDown(KEY_R)
		Self.key_pause = KeyDown(KEY_P)
			
		''position of current frame inside the replay vector
		Self.position = 0
		
		Self.controlling_device = Null
		
	End Method
	
	Method update()
		
		Super.update()
		
		''toggle pause
		If (KeyDown(KEY_P) And Self.key_pause = 0)
			Self.paused = Not Self.paused
		EndIf
		Self.key_pause = KeyDown(KEY_P)

		''toggle slow-motion
		If (KeyDown(KEY_R) And Self.key_slow = 0)
			Self.slow_motion = Not Self.slow_motion
		EndIf
		Self.key_slow = KeyDown(KEY_R)

		''set/unset controlling device
		If (Not Self.controlling_device)
			For Local d:t_input = EachIn input_devices
				If (d.fire2_down())
					Self.controlling_device = d
				EndIf
			Next
		Else
			If (Self.controlling_device.fire2_down())
				Self.controlling_device = Null
			EndIf
		EndIf
		
		''set speed
		Local speed:Int
		If (Self.controlling_device)
			Local d:t_input = Self.controlling_device
			speed = 12 * d.get_x() -2 * d.get_y() +8 * Abs(d.get_x()) * d.get_y()
		ElseIf (Self.slow_motion = True)
			speed = SUBFRAMES/2
		Else
			speed = SUBFRAMES
		EndIf
		
		''set position
		If (Not Self.paused)
			Self.position = Self.position +speed
		
			''limits
			Self.position = Max(Self.position, 1)
			Self.position = Min(Self.position, VSIZE)

			frame = (Self.frame0 +Self.position) Mod VSIZE
		EndIf
		
		''quit on 'ESC'
		If (KeyDown(KEY_ESCAPE))
			Self.quit()
			Return
		EndIf

		''quit on fire button
		For Local d:t_input = EachIn input_devices
			If (d.fire1_down())
				Self.quit()
				Return
			EndIf
		Next

		''quit on last position
		If ((Self.position = VSIZE) And (Not Self.controlling_device))
			Self.quit()
			Return
		EndIf

	End Method
	
	Method render()
		Super.render()
		
		Local f:Int = round(frame/SUBFRAMES) Mod 32
		DrawSubImageRect(img_replay, 34, 28, 40, 46, 40*(f & $F) , 46*(f Shr 4), 40, 46)
		If (Self.controlling_device)
			Local frame_x:Int = 1 + Self.controlling_device.get_x()
			Local frame_y:Int = 1 + Self.controlling_device.get_y()
			DrawSubImageRect(img_speed, game_settings.screen_width -50, game_settings.screen_height -50, 29, 29, 29 * frame_x, 29 * frame_y, 29, 29)
		EndIf	

	End Method
	
	Method quit()
		''if final frame is different from starting frame then fadeout
		If (Self.position <> VSIZE)
			game_action_queue.push(AT_FADE_OUT)
		EndIf
		
		game_action_queue.push(AT_RESTORE_FOREGROUND)
		
		''if final frame is different from starting frame then fadein
		If (Self.position <> VSIZE)
			game_action_queue.push(AT_FADE_IN)
		EndIf
	End Method
	
End Type
