SuperStrict

Import "t_match_mode.bmx"

Type t_match_bench_enter Extends t_match_mode
	
	Field camera_x:Int
	Field camera_y:Int
	
	Method New()
		
		bench_status.old_target_x:Int = vcamera_x[frame] -CENTER_X +game_settings.screen_width / (2*game_settings.zoom/100.0)
		bench_status.old_target_y:Int = vcamera_y[frame] -CENTER_Y +game_settings.screen_height / (2*game_settings.zoom/100.0)
		
		bench_status.selected_pos = -1
		bench_status.for_subs     = -1
		
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].lineup_at_index(i)
				If (team[t].uses_automatic_input_device())
					ply.set_input_device(ply.get_ai())
				EndIf
				ply.set_state("state_reach_target")
			Next
		Next
		
		Self.camera_x = camera.x
		Self.camera_y = camera.y
		
	End Method
	
	Method update()
		
		Super.update()
		
		For Local subframe:Int = 1 To SUBFRAMES
			
			ball.update()
			ball.in_field_keep()
			
			Self.update_players(True)
			
			frame = Self.next_frame()
			
			''--- SAVE DATA ---
			ball.save(frame)
			team[HOME].save(frame)
			team[AWAY].save(frame)
			
			vcamera_x[frame] = camera.updatex(CF_TARGET, CS_FAST, bench_status.target_x, False)
			vcamera_y[frame] = camera.updatey(CF_TARGET, CS_WARP, bench_status.target_y)
			
		Next
		
		Local dx:Int = Self.camera_x -camera.x
		Local dy:Int = Self.camera_y -camera.y
		
		If (dx <= 1 And dy <= 1)
			coach[bench_status.team.index].status = CS_STAND
			coach[bench_status.team.index].x = BENCH_X +8
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_SUBSTITUTIONS)
			Return
		EndIf
		
		Self.camera_x = camera.x
		Self.camera_y = camera.y
		
		If (KeyDown(KEY_ESCAPE))
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MATCH_BENCH_EXIT)
			Return
		EndIf
		
	End Method
	
End Type
