SuperStrict

Import "t_match_mode.bmx"

Type t_match_bench_formation Extends t_match_mode
	
	Field for_swap:Int
	
	Method New()
		Self.for_swap = -1
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
		
		''change selected player
		If (bench_status.input_device.y_moved())
			bench_status.selected_pos = rotate(bench_status.selected_pos, -1, TEAM_SIZE -1, bench_status.input_device.get_y())
		EndIf
		
		If (bench_status.input_device.fire1_down())
			''switch to tactics mode
			If (bench_status.selected_pos = -1)
				
				''reset eventually pending swap or substitution
				Self.for_swap = -1
				If (bench_status.for_subs <> -1)
					Local ply:t_player = bench_status.team.lineup_at_index(bench_status.for_subs)
					ply.set_state("state_bench_standing")
					
					bench_status.selected_pos = bench_status.for_subs -TEAM_SIZE
					bench_status.for_subs = -1
				EndIf
				
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_TACTICS)
				Return
				
			''swap and substitutions
			Else
				''if already selected for swap then deselect
				If (Self.for_swap = bench_status.selected_pos)
					Self.for_swap = -1
				''if no swap or substitution then select for swap
				Else If ((Self.for_swap = -1) And (bench_status.for_subs = -1))
					Self.for_swap = bench_status.selected_pos
				''swap or substitution
				Else
					Local base_tactics:Int = tactics_array[bench_status.team.tactics].based_on
					Local i1:Int = tactics_order[base_tactics, bench_status.selected_pos]
					Local i2:Int
					
					''if substitution
					If (bench_status.for_subs <> -1)
						coach[bench_status.team.index].status = CS_CALL
						coach[bench_status.team.index].timer = 500
						
						bench_status.team.subs_count = bench_status.team.subs_count +1
						
						i2 = tactics_order[base_tactics, bench_status.for_subs]
						bench_status.team.lineup_at_index(i1).set_state("state_outside")
						bench_status.team.lineup_at_index(i2).set_state("state_reach_target")
						bench_status.for_subs = -1
					''if swap
					Else
						coach[bench_status.team.index].status = CS_SPEAK
						coach[bench_status.team.index].timer = 500
						
						i2 = tactics_order[base_tactics, Self.for_swap]
						bench_status.team.lineup_at_index(i1).set_state("state_reach_target")
						bench_status.team.lineup_at_index(i2).set_state("state_reach_target")
						Self.for_swap = -1
					EndIf
					
					''swap players
					swap_elements(bench_status.team.lineup, i1, i2)
					
					''swap positions
					Local ply1:t_player = bench_status.team.lineup_at_index(i1)
					Local ply2:t_player = bench_status.team.lineup_at_index(i2)
					
					Local tx:Int = ply1.tx
					ply1.set_tx(ply2.tx)
					ply2.set_tx(tx)
					
					Local ty:Int = ply1.ty
					ply1.set_ty(ply2.ty)
					ply2.set_ty(ty)
					
				EndIf
			EndIf
		EndIf
		
		''go back to bench substitutions
		If (bench_status.input_device.x_released() Or KeyDown(KEY_ESCAPE))
			bench_status.selected_pos = -1
			
			''reset eventually pending swap or substitution
			Self.for_swap = -1
			If (bench_status.for_subs <> -1)
				Local ply:t_player = bench_status.team.lineup_at_index(bench_status.for_subs)
				ply.set_state("state_bench_standing")
				
				bench_status.selected_pos = bench_status.for_subs -TEAM_SIZE
				bench_status.for_subs = -1
			EndIf
			
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_SUBSTITUTIONS)
			Return
		EndIf
		
	End Method
	
	Method render()
		Super.render()
		Self.draw_formation()
	End Method
	
	Method draw_formation()
		Local light:Int = $FF
		
		Local w:Int	= 250
		Local h:Int	= 16
		
		Local x:Int	= game_settings.screen_width / 3 +2
		Local y:Int	= 0.5*game_settings.screen_height -150 +2
		
		''--- SHADOWS ---
		SetColor(0, 0.1*light, 0)
		
		''title
		draw_frame(x, y, w, h +2)
		
		''image
		DrawRect(x +0.5*w -41, y +40, 82, 66)
		
		''list
		draw_frame(x, y +125, w, TEAM_SIZE*h +6)
		
		''--- SLOTS ---
		Local color:Int = rgb(0, 0, 0)
		If (bench_status.selected_pos = -1)
			''yellow
			If (bench_status.for_subs <> -1)
				color = sweep_color(rgb(0, 0, 0), rgb(light, light, 0.2*light))
			''blue
			Else
				color = sweep_color(rgb(0, 0, 0), rgb(0.2*light, 0.2*light, light))
			EndIf
		EndIf
		fade_rect(x, y +2, x +w -2, y +h, 0.5, color, light)
		
		For Local pos:Int = 0 To TEAM_SIZE -1
			color = rgb(0, 0, 0)
			If (pos = Self.for_swap)
				color = rgb(0.2*light, light, 0.2*light)
			EndIf
			
			If (pos = bench_status.selected_pos)
				''yellow
				If (bench_status.for_subs <> -1)
					color = sweep_color(rgb(0, 0, 0), rgb(light, light, 0.2*light))
				''blue
				Else
					color = sweep_color(color, rgb(0.2*light,0.2*light,light))
				EndIf
			EndIf
			fade_rect(x, y +125 +4 +pos*h, x +w -2, y +125 +2 +(pos+1)*h, 0.5, color, light)
		Next
		
		x = x -2
		y = y -2
		
		''--- OBJECTS ---
		SetColor(light, light, light)
		
		''title
		draw_frame(x, y, w, h +2)
		
		''image
		DrawSubImageRect(img_tactics, x +0.5*w -41, y +40, 82, 66, 0, 0, 82, 66)
		
		''list
		draw_frame(x, y +125, w, TEAM_SIZE*h +6)
		
		text10u("FORMATION", x +0.5*w, y +2, img_ucode10g, 0)
		
		For Local pos:Int = 0 To TEAM_SIZE -1
			
			Local ply:t_player = bench_status.team.lineup_at_position(pos)
			
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
		Next
		
	End Method
	
End Type
