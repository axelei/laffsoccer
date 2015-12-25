SuperStrict

Import "t_match_mode.bmx"

Type t_match_bench_substitutions Extends t_match_mode
	
	Field bench_size:Int
	
	Method New()
		
		Self.bench_size = match_settings.bench_size
		
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
		
		''move selection
		If (bench_status.input_device.y_moved())
			
			''if remaining substitutions
			If (bench_status.team.subs_count < match_settings.substitutions)
				bench_status.selected_pos = rotate(bench_status.selected_pos, -1, Self.bench_size-1, bench_status.input_device.get_y())
			EndIf
			
			''reset positions
			For Local i:Int = 0 To Self.bench_size -1
				Local ply:t_player = bench_status.team.lineup_at_index(TEAM_SIZE +i)
				If (Not ply.get_active_state().check_name("state_outside"))
					ply.set_state("state_bench_sitting")
				EndIf
			Next
			
			''move selected player
			If (bench_status.selected_pos <> -1)
				Local ply:t_player = bench_status.team.lineup_at_index(TEAM_SIZE +bench_status.selected_pos)
				If (Not ply.get_active_state().check_name("state_outside"))
					''coach call the player
					coach[bench_status.team.index].status = CS_DOWN
					coach[bench_status.team.index].timer = 250
					
					ply.set_state("state_bench_standing")
				EndIf
			EndIf
		EndIf
		
		If (bench_status.input_device.fire1_down())
			If (bench_status.selected_pos = -1)
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_FORMATION)
				Return
			Else
				''if no previous selection
				If (bench_status.for_subs = -1)
					
					''call the player for subs
					Local ply:t_player = bench_status.team.lineup_at_index(TEAM_SIZE +bench_status.selected_pos)
					
					If (Not ply.get_active_state().check_name("state_outside"))
						ply.set_state("state_bench_out")
						
						bench_status.for_subs = TEAM_SIZE +bench_status.selected_pos
						
						bench_status.selected_pos = bench_status.team.easy_subs(ply.role)
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_FORMATION)
						Return
					EndIf
					
				EndIf
			EndIf
		EndIf
		
		If (bench_status.input_device.x_released() Or KeyDown(KEY_ESCAPE))
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_EXIT)
			Return
		EndIf
		
	End Method
	
	Method render()
		Super.render()
		Self.draw_bench_players()
	End Method
	
	Method draw_bench_players()
		Local light:Int = $FF
		
		Local w:Int	= 250
		Local h:Int	= 16
		
		Local x:Int	= game_settings.screen_width / 3 +2
		Local y:Int	= 0.5*game_settings.screen_height -100 +2
		
		''--- SHADOWS ---
		SetColor(0, 0.1*light, 0)
		
		''title
		draw_frame(x, y, w, h +2)
		
		''image
		DrawRect(x +0.5*w -41, y +40, 82, 66)
		
		''list
		draw_frame(x, y+125, w, Self.bench_size * h +6)
		
		''--- SLOTS ---
		Local color:Int = rgb(0.1*light, 0.1*light, 0.1*light)
		
		If (bench_status.selected_pos = -1)
			color = sweep_color(color, rgb(0.3*light, 0.3*light, light))
		EndIf
		
		fade_rect(x, y +2, x +w -2, y +h, 0.5, color, light)
		
		For Local pos:Int = 0 To Self.bench_size -1
			Local ply:t_player = bench_status.team.lineup_at_index(TEAM_SIZE +pos)
			color = rgb(0.1*light, 0.1*light, 0.1*light)
			
			If (bench_status.selected_pos = pos)
				color = sweep_color(color, rgb(0.3*light, 0.3*light, light))
			EndIf
			fade_rect(x, y +125 +4 +pos*h, x+w-2, y +125 +2 +(pos+1)*h, 0.5, color, light)
		Next
		
		x = x -2
		y = y -2
		
		''--- OBJECTS ---
		SetColor(light, light, light)
		
		''title
		draw_frame(x, y, w, h +2)
		
		''image
		DrawSubImageRect(img_tactics, x+0.5*w -41, y +40, 82, 66, 0, 0, 82, 66)
		
		''list
		draw_frame(x, y +125, w, Self.bench_size*h +6)
		
		text10u("BENCH", x+0.5*w, y +2, img_ucode10g, 0)
		
		For Local pos:Int = 0 To Self.bench_size -1
			Local ply:t_player = bench_status.team.lineup_at_index(TEAM_SIZE +pos)
			
			''if player is not out
			If (Not ply.get_active_state().check_name("state_outside"))
				text10u(ply.number, x +25, y +4 +125 +pos*h, img_ucode10g, 0)
				Local name:String
				If (Len(ply.surname) > 0)
					name = ply.surname
					If (Len(ply.name) > 0)
						name = Left(ply.name, 1) + ". " + name
					EndIf
				Else
					name = ply.name
				EndIf
				text10u(name, x +45, y +4 +125 +pos*h, img_ucode10g, 1)
				text10u(player_roles[ply.role], x +w -20, y +4 +125 +pos*h, img_ucode10g, 0)
			EndIf
		Next
		
	End Method
	
End Type
