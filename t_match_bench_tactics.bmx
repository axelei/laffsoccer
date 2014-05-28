SuperStrict

Import "t_match_mode.bmx"

Type t_match_bench_tactics Extends t_match_mode
	
	Field selected_tactics:Int
	
	Method New()
		Self.selected_tactics = bench_status.team.tactics
	End Method
	
	Method update()
		
		Super.update()
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			ball.in_field_keep()
			
			Self.update_players(True)
			coach[HOME].update()
			coach[AWAY].update()
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_TARGET, CS_FAST, bench_status.target_x, False)
			vcamera_y[frame] = camera.updatey(CF_TARGET, CS_FAST, bench_status.target_y)
			
		Next
		
		''change selected tactics
		If (bench_status.input_device.y_moved())
			Self.selected_tactics = rotate(Self.selected_tactics, 0, 17, bench_status.input_device.get_y())
		EndIf
		
		''set selected tactics and go back to bench
		If (bench_status.input_device.fire1_down())
			If (Self.selected_tactics <> bench_status.team.tactics)
				coach[bench_status.team.index].status = CS_SPEAK
				coach[bench_status.team.index].timer = 500
				bench_status.team.tactics = Self.selected_tactics
			EndIf
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_SUBSTITUTIONS)
			Return
		EndIf
		
		''go back to bench
		If (bench_status.input_device.x_released() Or KeyDown(KEY_ESCAPE))
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_SUBSTITUTIONS)
			Return
		EndIf
		
	End Method
	
	Method render()
		Super.render()
		Self.draw_tactics_switch()
	End Method
	
	Method draw_tactics_switch()
		Local light:Int = $FF
		
		Local x:Int	= game_settings.screen_width / 3 +35 +2
		Local y:Int	= 0.5*game_settings.screen_height -186 +2
		
		Local w:Int	= 180
		Local h:Int	= 16
		
		''--- SHADOWS ---
		SetColor(0, 0.1*light, 0)
		
		''image shadow
		DrawRect(x +0.5*w -41, y, 82, 66)
		
		''frame shadow
		draw_frame(x, y +80, w, 18*h +6)
		
		''--- SLOTS ---
		For Local i:Int = 0 To 17
			Local color:Int = rgb(0, 0, 0)
			If (i = bench_status.team.tactics)
				color = rgb(250, 40, 0)
			EndIf
			If (i = Self.selected_tactics)
				color = sweep_color(color, rgb(0.2*light, 0.2*light, light))
			EndIf
			fade_rect(x, y +84 +h*i, x +w -2, y +82 +h*(i +1), 0.5, color, light)
		Next
		
		x = x -2
		y = y -2
		
		''--- OBJECTS ---
		SetColor(light, light, light)
		
		DrawSubImageRect(img_tactics, x +0.5*w -41, y, 82, 66, 82, 0, 82, 66)
		
		draw_frame(x, y +80, w, 18*h +6)
		
		For Local i:Int = 0 To 17
			text10u(tactics_name[i], x +0.5*w, y +84 +16*i, img_ucode10g, 0)
		Next
		
	End Method
	
End Type
