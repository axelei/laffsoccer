SuperStrict

Import "t_match_mode.bmx"

Type t_match_bench_exit Extends t_match_mode
	
	Method New()
		coach[bench_status.team.index].status = CS_BENCH
		coach[bench_status.team.index].x = BENCH_X
		
		''reset positions
		For Local i:Int = 0 To match_settings.bench_size -1
			Local ply:t_player = bench_status.team.lineup_at_index(TEAM_SIZE +i)
			If (Not ply.get_active_state().check_name("state_outside"))
				ply.set_state("state_bench_sitting")
			EndIf
		Next
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
			
			vcamera_x[frame] = camera.updatex(CF_TARGET, CS_FAST, bench_status.old_target_x, False)
			vcamera_y[frame] = camera.updatey(CF_TARGET, CS_WARP, bench_status.old_target_y)
			
		Next
		
		Local d1:Int = camera.x -bench_status.old_target_x -CENTER_X +game_settings.screen_width / (2*game_settings.zoom/100.0)
		Local d2:Int = camera.y -bench_status.old_target_y -CENTER_Y +game_settings.screen_height / (2*game_settings.zoom/100.0)
		
		If (hypo(d1, d2) < 1)
			game_action_queue.push(AT_RESTORE_FOREGROUND)
			Return
		EndIf
		
	End Method
	
End Type
