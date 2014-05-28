SuperStrict

Import "t_camera.bmx"
Import "lib_data.bmx"
Import "t_function_key.bmx"

''--- CONSTANTS ---''

''ball speed
Const V0:Float  = 180 ''pass

Const V1:Float  = 270 ''low-shot
Const VZ1:Float = 170

Const V2:Float  = 280 ''mid-shot
Const VZ2:Float = 225

Const V3:Float  = 320 ''high-shot
Const VZ3:Float = 300	


''--- GLOBALS ---''

''number of frames
Global frames:Int

''coaches
Global coach:t_coach[2]

''images
Global img_tactics:TImage
Global img_goal_btm:TImage
Global img_jumper:TImage
Global img_replay:TImage
Global img_pause:TImage
Global img_speed:TImage
Global img_wind:TImage
Global img_ucode14g:TImage
Global img_score:TImage
Global img_time:TImage

''commentary
Global chn_comm:TChannel
Global comm_goal:TSound[6]
Global comm_owngoal:TSound[6]
Global comm_corner:TSound[6]
			
''goals
Global g_goal_list:TList

''list of scorers
Global score:String[2]		'0 = home, 1 = away



''--- FUNCTIONS ---''

Function add_goal(tm:Int)
	
	g_goal = New t_goal
	g_goal.player = ball.goal_owner
	g_goal.minute = match_status.get_minute()
	If (team[tm] = g_goal.player.team)
		g_goal.typ = t_goal.NORMAL
		g_goal.player.goals = g_goal.player.goals +1
	Else
		g_goal.typ = t_goal.OWN_GOAL
	EndIf
	
	ListAddLast(g_goal_list, g_goal)
	
	build_scorer_lists()
	
End Function


Function build_scorer_lists()
	
	score[home] = ""
	score[away] = ""
	
	For Local goal:t_goal = EachIn g_goal_list
		
		Local tm:Int = goal.player.team.index
		If (goal.typ = t_goal.OWN_GOAL)
			tm = Not tm
		EndIf
		
		If Not(already_scored(goal, tm))
			
			''If the list is not empty add a 'carriage return'
			If (Len(score[tm]) > 0)
				score[tm] = score[tm] + CRLF
			EndIf
			
			score[tm] = score[tm] + goal.player.get_single_name() + " " + goal.minute
			
			''owngoal/penalty
			Select goal.typ
				Case t_goal.OWN_GOAL
					score[tm] = score[tm] + "(OG)"
				Case t_goal.PENALTY
					score[tm] = score[tm] + "(PEN)"
			End Select
			
			add_other_goals(goal, tm)
			
		EndIf
		
	Next
	
	For Local tm:Int = home To away
		score[tm] = limit_string(score[tm], 20-3*team[tm].national)
	Next
End Function


Function already_scored:Int(g:t_goal, gteam:Int)
	
	For Local goal:t_goal = EachIn g_goal_list
		
		''if we found the same item end the search
		If (goal = g)
			Return False
		EndIf
		
		Local tm:Int = goal.player.team.index
		If (goal.typ = t_goal.OWN_GOAL)
			tm = Not tm
		EndIf
		
		''check if the scorer is in the right list
		If (tm = gteam)
			
			''if he is the same player
			If (g.player = goal.player)
				Return True
			EndIf
			
		EndIf
	Next
	
End Function


Function add_other_goals:Int(g:t_goal, gteam:Int)
	
	For Local goal:t_goal = EachIn g_goal_list
		
		If (goal <> g)
			Local tm:Int = goal.player.team.index
			If (goal.typ = t_goal.OWN_GOAL)
				tm = Not tm
			EndIf
			
			''check if the scorer is in the right list	
			If (tm = gteam)
				''if he is the same player
				If (goal.player = g.player)
					score[tm] = score[tm] + "," + goal.minute
					''owngoal
					Select goal.typ
						Case t_goal.OWN_GOAL
							score[tm] = score[tm] + "(OG)"
						Case t_goal.PENALTY
							score[tm] = score[tm] + "(PEN)"
					End Select
				EndIf
			EndIf
		EndIf
	Next
	
End Function
