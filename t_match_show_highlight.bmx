SuperStrict

Import "t_match_mode.bmx"

Type t_match_show_highlight Extends t_match_mode
	
	Field position:Int
	Field key_slow:Int
	Field slow_motion:Int
	
	Method New()
		
		Self.display_controlled_player = False
		Self.display_radar = False
		Self.display_time = False
		Self.display_score = True
		
		''position of current frame inside the highlights vector
		Self.position = 0
		
		''slow motion
		Self.key_slow = 0
		Self.slow_motion = False
		
		''store initial frame
		Self.frame0 = frame
		
		''copy highlights data into objects
		Local offset:Int = current_highlight * HL_BANKSIZE
		
		''if more than HL_MAXNUMBER actions have been recorded, the oldest have been overwritten
		If (hl_recorded > HL_MAXNUMBER)
			offset = ((hl_recorded + current_highlight) Mod HL_MAXNUMBER) * HL_BANKSIZE
		EndIf
		
		''copy highlights
		
		''if the end of the bank has been reached, then restart
		If (offset = HL_MAXNUMBER * HL_BANKSIZE)
			offset = 0
		EndIf
		
		For Local j:Int = 1 To 2 * VFRAMES
			
			''ball
			ball.data[frame].x = short2c2(PeekShort(bnk_highlights, offset))	; offset = offset + 2
			ball.data[frame].y = short2c2(PeekShort(bnk_highlights, offset))	; offset = offset + 2
			ball.data[frame].z = short2c2(PeekShort(bnk_highlights, offset))	; offset = offset + 2
			ball.data[frame].fmx = PeekByte(bnk_highlights, offset)				; offset = offset + 1
			
			''players
			For Local t:Int = HOME To AWAY
				For Local i:Int = 0 To TEAM_SIZE -1
					Local ply:t_player = team[t].player_at_index(i)
					ply.data[frame].x = short2c2(PeekShort(bnk_highlights, offset))	; offset = offset + 2
					ply.data[frame].y = short2c2(PeekShort(bnk_highlights, offset))	; offset = offset + 2
					ply.data[frame].fmx = PeekByte(bnk_highlights, offset)	; offset = offset + 1
					ply.data[frame].fmy = PeekByte(bnk_highlights, offset)	; offset = offset + 1
					ply.data[frame].is_visible = PeekByte(bnk_highlights, offset)	; offset = offset + 1
				Next
			Next
			
			''camera
			vcamera_x[frame] = short2c2(PeekShort(bnk_highlights, offset))	; offset = offset + 2
			vcamera_y[frame] = short2c2(PeekShort(bnk_highlights, offset))	; offset = offset + 2
			
			frame = (frame + SUBFRAMES/2) Mod VSIZE
			
		Next
		
	End Method
	
	Method update()
		
		Super.update()
		
		''TOGGLE SLOW-MOTION if 'R'
		If (KeyDown(KEY_R) And Self.key_slow = 0)
			Self.slow_motion = Not Self.slow_motion
		EndIf
		Self.key_slow = KeyDown(KEY_R)
		
		''set replay speed
		Local speed:Int = SUBFRAMES
		
		''slow motion
		If (Self.slow_motion)
			speed = SUBFRAMES / 2
		EndIf
		
		Self.position = Self.position +speed
		If (Self.position > VSIZE)
			Self.position = VSIZE
		EndIf
		
		frame = (frame0 + position) Mod VSIZE
		
		''QUIT ON 'ESC'
		If (KeyDown(KEY_ESCAPE))
			Self.quit()
			Return
		EndIf
		
		''QUIT ON FIRE BUTTON
		If (team[HOME].fire1_up() Or team[AWAY].fire1_up())
			Self.quit()
			Return
		EndIf
		
		''QUIT ON FINISH
		If (position = VSIZE)
			current_highlight = current_highlight +1
			If (current_highlight = Min(hl_recorded, HL_MAXNUMBER))
				Self.quit()
				Return
			Else
				game_action_queue.push(AT_FADE_OUT)
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_SHOW_HIGHLIGHT)
				game_action_queue.push(AT_FADE_IN)
				Return
			EndIf
		EndIf
		
	End Method
	
	Method quit()
		game_action_queue.push(AT_FADE_OUT)
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_END)
		game_action_queue.push(AT_FADE_IN)
	End Method
	
End Type
