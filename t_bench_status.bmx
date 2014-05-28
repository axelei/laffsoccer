SuperStrict

Import "common.bmx"

Global bench_status:t_bench_status

Type t_bench_status
	Field team:t_team
	Field input_device:t_input
	Field target_x:Int
	Field target_y:Int
	Field old_target_x:Int
	Field old_target_y:Int
	Field selected_pos:Int
	Field for_subs:Int
End Type
