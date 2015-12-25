SuperStrict

Import "constants.bmx"
Import "t_vec2d.bmx"
Import "t_state_machine.bmx"
Import "lib_main.bmx"
Import "lib_misc.bmx"
Import "t_dictionary.bmx"
Import "t_input_devices.bmx"
Import "t_tactics.bmx"
Import "t_tactics_stack.bmx"
Import "data_board_tactics.bmx"
Import "data_player_map.bmx"
Import "data_hair_map.bmx"
Import "data_kit_colors.bmx"
Import "data_tactics_order.bmx"
Import "data_tactics_files.bmx"
Import "data_hair_colors.bmx"
Import "data_skin_colors.bmx"
Import "data_hair_cuts.bmx"
Import "data_shaved_table.bmx"
Import "data_shaved_colors.bmx"
Import "data_substitution_rules.bmx"
Import "data_goals_probability.bmx"
Import "data_pitch_and_sun.bmx"
Import "t_associations_flags.bmx"
Import "t_color_replacement_list.bmx"
Import "t_location_settings.bmx"
Import "t_sound.bmx"
Import "t_channel.bmx"
Import "t_pitch.bmx"
Import "t_sprite.bmx"

'Function GCMemAlloced()
'	Return MemAlloced()
'End Function

Function flushmem()
	GCCollect()
End Function



'--- --- --- CONSTANTS --- --- ---'


'ball prediction for the next 2 seconds
Const BALL_PREDICTION:Int	= 2*REFRATE

'control mode
Const CM_UNDEFINED:Int	= 0
Const CM_COMPUTER:Int	= 1
Const CM_PLAYER:Int		= 2
Const CM_COACH:Int		= 3

''teams
Const NONE:Int	= -1
Const HOME:Int	= 0
Const AWAY:Int	= 1

'kits
Const MIN_KITS:Int = 3
Const MAX_KITS:Int = 5

'coach status
Const CS_BENCH:Int		= 0
Const CS_STAND:Int		= 1
Const CS_DOWN:Int		= 2
Const CS_SPEAK:Int		= 3
Const CS_CALL:Int		= 4

Const GRAVITY:Float = 350.0/SECOND

''ball radius
Const BALL_R:Int	= 4

''player's height
Const PLAYER_H:Int	= 22

'width and height of each ball zone
Const BALL_ZONE_DX:Int	= 206
Const BALL_ZONE_DY:Int	= 184

'field limits
Const FIELD_XMIN:Int	= -565
Const FIELD_XMAX:Int	= +572
Const FIELD_YMIN:Int	= -700
Const FIELD_YMAX:Int	= +698

'flagpost's height
Const FLAGPOST_H:Int	= 21

'jumpers
Const JUMPER_X:Int		= 92
Const JUMPER_Y:Int		= 684
Const JUMPER_H:Int		= 42

'goals
Const GOAL_DEPTH:Int	= +22
Const POST_X:Int		= 71
Const POST_R:Int		= 2			'posts and crossbar radius
Const CROSSBAR_H:Int	= 33		'+-6 z pass

'competition types
Const CT_LEAGUE:Int	= 0
Const CT_CUP:Int	= 1

'cup settings
Const CS_EXTRA_TIME_OFF:Int			= 0
Const CS_EXTRA_TIME_ON:Int			= 1
Const CS_EXTRA_TIME_IF_REPLAY:Int	= 2

Const CS_PENALTIES_OFF:Int			= 0
Const CS_PENALTIES_ON:Int			= 1
Const CS_PENALTIES_IF_REPLAY:Int	= 2

Const CS_AWAY_GOALS_OFF:Int					= 0
Const CS_AWAY_GOALS_AFTER_90_MINS:Int		= 1
Const CS_AWAY_GOALS_AFTER_EXTRA_TIME:Int	= 2

'result types
Const RT_AFTER_90_MINS:Int		= 0
Const RT_AFTER_EXTRA_TIME:Int	= 1
Const RT_PENALTIES:Int			= 2

''highlights
Const HL_BANKSIZE:Int	= 165 * 2 * VFRAMES
Const HL_MAXNUMBER:Int	= 30

''menu status
Const MS_NONE:Int        = 0
Const MS_FRIENDLY:Int    = 1
Const MS_COMPETITION:Int = 2
Const MS_EDIT:Int        = 3	
Const MS_TRAINING:Int    = 4

''match status
Global match_status:t_match_status

 
'--- --- --- GLOBALS --- --- ---'


Global debug:Int

Global frame:Int

Global match_settings:t_match_settings

''menu
Global menu:t_menu

''mouse
Global mouse:t_mouse

''input devices
Global input_devices:t_input_devices

''competition
Global competition:t_competition

'ball
Global ball:t_ball

Global player_roles:String[8]

Global img_player:TImage[2, 10]
Global img_keeper:TImage[2, 10]
Global img_player_shadows:TImage[4]
Global img_keeper_shadows:TImage[4]

Global img_keeper_cd:TPixmap
Global img_number:TImage
Global img_tiny_num:TImage

'teams
Global team:t_team[2]		'0=home, 1=away

''tactics
Global tactics_array:t_tactics[18]
Global tactics_name:String[18]


'' calendars (calendars for leagues up to 24 teams)
Global calendar:Int[4600]

''match statistics
Global stats:t_stats[2]	''0=HOME, 1=AWAY

Global joysticks:Int

'sounds
Global audio_drivers:TList

Global sprites:TList

Global light:Int

''highlights
Global bnk_highlights:TBank
Global hl_recorded:Int
Global current_highlight:Int

''menu Unicode fonts
Global img_ucode10:TImage		
Global img_ucode10g:TImage
Global img_ucode14:TImage

''mouse pointer
Global img_arrow:TImage

Global g_goal:t_goal

'--- --- --- TYPES --- --- ---'

'menu globals
Type t_menu
	Field status:Int        ''MS_FRIENDLY, MS_COMPETITION, MS_EDIT
	Field teams:Int         ''teams to select
	Field division:Int      ''division showed in a menu
	Field divisions:Int     ''divisions in a league
	Field no_league:Int     ''true if some teams without league are present in a country
	Field extension:String  ''extension of the team file
	Field confederation:String
	Field modified:Int      ''flag (edit menus)
	Field team_to_set:Int   ''used in MenuSetTeam
	Field show_result:Int   ''used in competitions
	
	''used in tactics editor
	Field tactics_to_edit:Int
	Field edited_tactics:t_tactics
	Field tactics_flip:Int
	Field tactics_undo:t_tactics_stack
	Field team_to_show:t_team
	
	Method New()
		Self.show_result = False
		Self.tactics_flip = True
	End Method
	
End Type


Type t_match_settings
	Field commentary:Int
	Field radar:Int
	Field auto_replay:Int
	Field substitutions:Int
	Field bench_side:Int ''1 = home upside, -1 = home downside
	Field bench_size:Int
End Type


'competition
Type t_competition Abstract
	Field name:String
	Field typ:Int				'CT_LEAGUE, CT_CUP
	Field byseason:Int			'true = by season, false = by pitch type
	Field season_start:Int
	Field season_end:Int
	Field current_month:Int
	Field pitch_type:Int		'PT.FROZEN, PT.MUDDY, ..., PT.RANDOM
	Field substitutions:Int
	Field bench_size:Int
	Field time:Int				''TI.DAY, TI.NIGHT
	Field ended:Int
	Field user_prefers_result:Int		'user preference: true = view result, false = view match
	Field teams:TList
	Field current_round:Int
	Field current_match:Int
	Field team_to_view:Int

	Method New()
		Self.name			= ""
		Self.byseason		= True	'by season
		Self.season_start	= 7 	'august
		Self.season_end 	= 4 	'may
		Self.pitch_type		= PT.RANDOM
		Self.substitutions	= 3
		Self.bench_size		= 5
		Self.time			= TI.DAY
		Self.ended			= False
		Self.user_prefers_result	= False
		Self.teams			= Null
		Self.current_round	= 0
		Self.current_match	= 0
		Self.team_to_view	= 0
	End Method
	
	Method initialize(list:TList)
		Self.teams = list
	End Method
	
	Method restart()
		Self.ended				= False
		Self.current_round		= 0
		Self.current_match		= 0
	End Method

	Method get_pitch_type:Int()
		Local p:Int
	
		If (Self.byseason)
			p = -1
			Local n:Int = Rand(0, 99)
			Local tot:Int = 0
			Repeat
				p = p + 1
				tot = tot + pitch_and_sun[Self.current_month, p]
			Until (tot > n)

		Else ''by pitch type
			If (Self.pitch_type = PT.RANDOM)
				p = Rand(0,8)
			Else
				p = Self.pitch_type
			EndIf
		EndIf
		
		Return p
	End Method
	
	Method get_byseason_label:String()
		If Self.byseason = True
			Return dictionary.gettext("SEASON")
		Else
			Return dictionary.gettext("PITCH TYPE")
		EndIf
	End Method
	
	Method get_time_label:String()
		Select Self.time 
			Case TI.DAY
				Return dictionary.gettext("DAY")
			Case TI.NIGHT
				Return dictionary.gettext("NIGHT")
		End Select
	End Method
	
	Method get_menu_title:String() Abstract

End Type

''league
Type t_league Extends t_competition
	Field weather_effect:Int	''WE.WIND, WE.RAIN, WE.SNOW, WE.FOG, WE.RANDOM
	Field weather_intensity:Int	''WI.NONE, WI.LIGHT, WI.STRONG
	Field sky:Int				''SK.CLEAR,  SK.CLOUDY
	Field number_of_teams:Int
	Field total_matches:Int
	Field rounds:Int
	Field points_for_a_win:Int
	Field calendar_current:TList

	Method New()
		Self.typ				= CT_LEAGUE
		Self.number_of_teams	= 2
		Self.rounds				= 1
		Self.points_for_a_win	= 3
		Self.calendar_current	= New TList
	End Method

	Method initialize(list:TList)
		Super.initialize(list)
		shuffle_list(Self.teams)
		Self.calendar_generate()
	End Method
	
	Method restart()
		Super.restart()
		Self.calendar_generate()
	End Method
	
	Method get_match:t_match()
		Return t_match(Self.calendar_current.ValueAtIndex(Self.current_match))
	End Method
	
	Method next_match()
		Self.current_match		= Self.current_match + 1
		If 2*Self.current_match = (Self.current_round+1)*Self.number_of_teams*(Self.number_of_teams-1)
			Self.next_round()
		EndIf
	End Method
	
	Method next_round()
		Self.current_round = Self.current_round +1
		Self.update_month()
	End Method
	
	Method update_month() 
		Local season_length:Int = ((Self.season_end - Self.season_start +12) Mod 12)
		Self.current_month = (Self.season_start + season_length*Self.current_round/Self.rounds) Mod 12
	End Method

	Method calendar_generate()
		Self.calendar_current.Clear()
		While (Self.current_round < Self.rounds)
			Local match:t_match = New t_match
		
			'' search position of current match in league calendar
			Local pos:Int = 0
			For Local i:Int = 2 To Self.number_of_teams-1
				pos = pos + i*(i-1)
			Next
			pos = pos + 2 * Self.current_match -Self.current_round * Self.number_of_teams * (Self.number_of_teams -1)
		
			'' get teams
			Local team_a:t_team
			Local team_b:t_team
			If (Self.current_round Mod 2) = 0
				team_a = t_team(Self.teams.valueatindex(calendar[pos]))
				team_b = t_team(Self.teams.valueatindex(calendar[pos+1]))
			Else
				team_a = t_team(Self.teams.valueatindex(calendar[pos+1]))
				team_b = t_team(Self.teams.valueatindex(calendar[pos]))
			EndIf
			
			match.teams = [team_a, team_b]
			ListAddLast(Self.calendar_current, match)
			Self.next_match()
		Wend
		
		Self.current_match = 0
		Self.current_round = 0
		Self.update_month()
	End Method
			
	Method set_result(home_goals:Int, away_goals:Int)
		Local match:t_match = Self.get_match()
		match.result = [home_goals, away_goals]
		match.teams[HOME].update_statistics(home_goals, away_goals, Self.points_for_a_win)
		match.teams[AWAY].update_statistics(away_goals, home_goals, Self.points_for_a_win)
	End Method
			
	Method get_menu_title:String()
		Return Self.name
	End Method
	
End Type	

''cup
Type t_cup Extends t_competition
	Field away_goals:Int		'0=off, 1=after 90 mins, 2=after extra time
	Field weather_effect:Int	''WE.WIND, WE.RAIN, WE.SNOW, WE.FOG, WE.RANDOM
	Field weather_intensity:Int	''WI.NONE, WI.LIGHT, WI.STRONG
	Field sky:Int				''SK.CLEAR, SK.CLOUDY
	Field rounds:Int			''1 to 6
	Field round:t_round[6]
	Field current_leg:Int
	Field total_legs:Int
	Field qualified_teams:TList
	Field calendar_current:TList
	
	Method New()
		Self.typ				= CT_CUP
		Self.away_goals			= CS_AWAY_GOALS_OFF
		Self.weather_effect		= WE.RANDOM
		Self.weather_intensity	= 0
		Self.sky				= SK.CLEAR
		For Local i:Int = 0 To 5
			Self.round[i] = New t_round
		Next
		Self.set_rounds(4)
		Self.current_leg		= 0
		Self.total_legs			= 0
		Self.calendar_current	= Null
		Self.qualified_teams	= New TList
	End Method
	
	Method initialize(list:TList)
		Super.initialize(list)
		For Local t:t_team = EachIn Self.teams
			ListAddLast(Self.qualified_teams, t)
		Next
		Self.calendar_generate()
	End Method
	
	Method set_rounds(n:Int)
		Self.rounds = n
		For Local r:Int = 0 To 5
			Self.round[r].teams = 2^(Self.rounds-r)
		Next
		Self.update_round_names()
	End Method
	
	Method restart()
		Super.restart()
		Self.current_leg		= 0
		ClearList(Self.qualified_teams)
		For Local t:t_team = EachIn Self.teams
			ListAddLast(Self.qualified_teams, t)
		Next
		Self.calendar_generate()
	End Method
	
	Method get_match:t_match()
		Return t_match(Self.calendar_current.valueatindex(Self.current_match))
	End Method

	Method next_match()
		Self.current_match = Self.current_match +1
		If Self.current_match = CountList(Self.calendar_current)
			Self.next_leg()
		EndIf
		Print "Next match: " + Self.current_match
	End Method
	
	Method next_leg()
		Self.calendar_print()
		Self.current_leg = Self.current_leg +1
		Self.current_match = 0
		Self.calendar_generate()
		If CountList(Self.calendar_current) = 0
			Self.next_round()
		EndIf
		Print "Next leg: " + Self.current_leg
		Self.calendar_print()
	End Method

	Method next_round()
		Self.current_round = Self.current_round +1
		Print "Next round: " + Self.current_round
		Self.current_leg = 0
		Self.current_match = 0
		Self.calendar_generate()
	End Method
	
	Method calendar_generate()
		'first leg
		If (Self.current_leg = 0)
			shuffle_list(Self.qualified_teams)
			Self.calendar_current = New TList
			Local t:t_team = t_team(Self.qualified_teams.valueatindex(0))
			For Local i:Int = 0 To CountList(Self.qualified_teams)/2-1
				Local match:t_match = New t_match
				match.teams = [t_team(Self.qualified_teams.valueatindex(2*i)), t_team(Self.qualified_teams.valueatindex(2*i+1))]
				match.result = Null
				match.old_result = Null
				ListAddLast(Self.calendar_current, match) 
			Next
			ClearList(Self.qualified_teams)
		
		'second leg
		Else If (Self.current_leg = 1) And (Self.round[Self.current_round].legs = 2)
			For Local match:t_match = EachIn Self.calendar_current
				Local tmp:t_team[] = match.teams
				match.teams = [tmp[1], tmp[0]]
				match.old_result = match.result
				match.result = Null
				match.includes_extra_time = False
				match.result_after90 = Null
				match.status = dictionary.gettext("1ST LEG") + " " + match.old_result[1] + "-" +  match.old_result[0]
			Next
		
		'replays
		Else 
			For Local match:t_match = EachIn Self.calendar_current
				If (match.qualified = Null)
					Local tmp:t_team[] = match.teams
					match.teams = [tmp[1], tmp[0]]
					match.result = Null
					match.includes_extra_time = False
					match.result_after90 = Null
					match.old_result = Null
					match.status = ""
				Else
					Self.calendar_current.remove(match)
				EndIf				
			Next
		EndIf

		'update month
		Local season_length:Int = ((Self.season_end - Self.season_start +12) Mod 12)
		Self.current_month = (Self.season_start + season_length*Self.current_round/Self.rounds) Mod 12

	End Method 

	Method calendar_print()
		Local s:String = "=== ROUND " + Self.current_round
		
		'first/second leg
		If (Self.current_leg = 0) Or (Self.current_leg = 1 And Self.round[Self.current_round].legs = 2)
			s = s + ", LEG " + Self.current_leg
			
		'replays
		Else 
			s = s + ", REPLAY " + (Self.current_leg -Self.round[Self.current_round].legs)
		EndIf
		
		Print s + " ==="
		
		For Local i:Int = 0 To CountList(Self.calendar_current)-1
			Local match:t_match = t_match(Self.calendar_current.valueatindex(i))
			Print "match " + i + ":" + match.ToString()
		Next
		
		s = "qualified teams:"
		If CountList(Self.qualified_teams)
			For Local t:t_team = EachIn Self.qualified_teams
				s = s + " " + t.name
			Next
		Else
			s = s + " none"
		EndIf
		Print s
	End Method 

	Method set_result(home_goals:Int, away_goals:Int, result_type:Int = RT_AFTER_90_MINS)
		Local match:t_match = Self.get_match()
		Select result_type
			Case RT_AFTER_90_MINS
				Print "Setting result after 90 mins for match " + Self.current_match + " to " + home_goals + ":" + away_goals
			Case RT_AFTER_EXTRA_TIME
				Print "Setting result after extra time for match " + Self.current_match + " to " + home_goals + ":" + away_goals
			Case RT_PENALTIES
				Print "Setting penalties result for match " + Self.current_match + " to " + home_goals + ":" + away_goals
		End Select
		If (result_type = RT_PENALTIES)
			match.result_penalties = [home_goals, away_goals]
		Else
			match.result = [home_goals, away_goals]
			If (result_type = RT_AFTER_EXTRA_TIME)
				match.includes_extra_time = True
			Else
				match.result_after90 = [home_goals, away_goals]
			EndIf
		EndIf
		match.qualified = get_qualified(match)
		match.status = get_match_status(match)
		If (match.qualified <> Null)
			ListAddLast(Self.qualified_teams, match.qualified)
			If (Self.current_round = Self.rounds -1)
				Self.ended = True
			EndIf
		EndIf
		Print "STATUS: " + match.status
	End Method
	
	'decide if extra time are to be played depending on current result, leg's type and settings  
	Method play_extra_time:Int()
		
		Local match:t_match = Self.get_match()
		
		'first leg
		If (Self.current_leg = 0)
		
			'two legs round
			If Self.round[Self.current_round].legs = 2
				Return False
			EndIf
			
			'result
			If (match.result[0] <> match.result[1])
				Return False
			EndIf
			
			'settings
			Select (Self.round[Self.current_round].extra_time)
				Case CS_EXTRA_TIME_OFF
					Return False
				Case CS_EXTRA_TIME_ON
					Return True
				Case CS_EXTRA_TIME_IF_REPLAY
					Return False
			End Select
			
		'second leg
		ElseIf (Self.current_leg = 1 And Self.round[Self.current_round].legs = 2)
		
			'aggregate goals
			Local agg_goals_a:Int = match.result[0] + match.old_result[1]
			Local agg_goals_b:Int = match.result[1] + match.old_result[0]
			If (agg_goals_a <> agg_goals_b)
				Return False
			EndIf
			
			'away goals
			If (match.old_result[1] <> match.result[1]) And (Self.away_goals = CS_AWAY_GOALS_AFTER_90_MINS)
				Return False
			EndIf
			
			'settings
			Select (Self.round[Self.current_round].extra_time)
				Case CS_EXTRA_TIME_OFF
					Return False
				Case CS_EXTRA_TIME_ON
					Return True
				Case CS_EXTRA_TIME_IF_REPLAY
					Return False
			End Select
						
		'replays
		Else
			'result
			If (match.result[0] <> match.result[1])
				Return False
			EndIf
			
			'settings
			Select (Self.round[Self.current_round].extra_time)
				Case CS_EXTRA_TIME_OFF
					Return False
				Case CS_EXTRA_TIME_ON
					Return True
				Case CS_EXTRA_TIME_IF_REPLAY
					Return True
			End Select
		EndIf		
	End Method

	'decide if penalties are to be played depending on current result, leg's type and settings  
	Method play_penalties:Int()
		Local match:t_match = Self.get_match()
		'first leg
		If (Self.current_leg = 0)
		
			'two legs round
			If Self.round[Self.current_round].legs = 2
				Return False
			EndIf
			
			'result
			If (match.result[0] <> match.result[1])
				Return False
			EndIf
			
			'settings
			Select (Self.round[Self.current_round].penalties)
				Case CS_PENALTIES_OFF
					Return False
				Case CS_PENALTIES_ON
					Return True
				Case CS_PENALTIES_IF_REPLAY
					Return False
			End Select

		'second leg		
		ElseIf (Self.current_leg = 1 And Self.round[Self.current_round].legs = 2)
			
			'aggregate goals
			Local agg_goals_a:Int = match.result[0] + match.old_result[1]
			Local agg_goals_b:Int = match.result[1] + match.old_result[0]
			If (agg_goals_a <> agg_goals_b)
				Return False
			EndIf
			
			'away goals
			If (match.old_result[1] <> match.result[1]) And (Self.away_goals <> CS_AWAY_GOALS_OFF)
				Return False
			EndIf
			
			'settings
			Select (Self.round[Self.current_round].penalties)
				Case CS_PENALTIES_OFF
					Return False
				Case CS_PENALTIES_ON
					Return True
				Case CS_PENALTIES_IF_REPLAY
					Return False
			End Select
					
		'replays
		Else
			'result
			If (match.result[0] <> match.result[1])
				Return False
			EndIf
			
			'settings
			Select (Self.round[Self.current_round].penalties)
				Case CS_PENALTIES_OFF
					Return False
				Case CS_PENALTIES_ON
					'this should never happen
					RuntimeError("Invalid state in cup")
				Case CS_PENALTIES_IF_REPLAY
					Return True
			End Select
		EndIf
	End Method
	
	Method get_qualified:t_team(match:t_match)
		If (match.result_penalties)
			If (match.result_penalties[0] > match.result_penalties[1])
				Return match.teams[0]
			Else If (match.result_penalties[0] < match.result_penalties[1])
				Return match.teams[1]
			Else
				RuntimeError("Invalid state in cup")
			EndIf			
		EndIf
				
		'first leg
		If (Self.current_leg = 0)
			Select (Self.round[Self.current_round].legs)
				Case 1
					If (match.result[0] > match.result[1])
						Return match.teams[0]
					Else If (match.result[0] < match.result[1])
						Return match.teams[1]
					Else
						Return Null
					EndIf
				Case 2
					Return Null
			End Select
		
		'second leg
		ElseIf (Self.current_leg = 1 And Self.round[Self.current_round].legs = 2)
			Local agg_score_a:Int = match.result[0] + match.old_result[1]
			Local agg_score_b:Int = match.result[1] + match.old_result[0]
			If (agg_score_a > agg_score_b)
				Return match.teams[0]
			Else If (agg_score_a < agg_score_b)
				Return match.teams[1]
			Else
				If (Self.away_goals = CS_AWAY_GOALS_AFTER_90_MINS) Or (Self.away_goals = CS_AWAY_GOALS_AFTER_EXTRA_TIME And match.includes_extra_time = 1)
					If (match.old_result[1] > match.result[1])
						Return match.teams[0]
					Else If (match.old_result[1] < match.result[1])
						Return match.teams[1]
					Else
						Return Null
					EndIf
				Else
					Return Null
				EndIf
			EndIf

		'replays
		Else
			If (match.result[0] > match.result[1])
				Return match.teams[0]
			Else If (match.result[0] < match.result[1])
				Return match.teams[1]
			Else
				Return Null
			EndIf
		EndIf
	End Method

	Method get_match_status:String(match:t_match)
		Local s:String = ""

		'first leg
		If (Self.current_leg = 0)
			If (match.qualified)
				If match.result_penalties
					s = match.qualified.name + " " + dictionary.gettext("WIN") + " "
					s = s + Max(match.result_penalties[0], match.result_penalties[1])
					s = s + "-"
					s = s + Min(match.result_penalties[0], match.result_penalties[1])
					s = s + " " + dictionary.gettext("ON PENALTIES")
					If (match.includes_extra_time)
						s = s + " " + dictionary.gettext("AFTER EXTRA TIME")
						If (match.result[0] <> match.result_after90[0]) Or (match.result[1] <> match.result_after90[1])
							s = s + " " + dictionary.gettext("90 MINUTES")
							s = s + " " + match.result_after90[0] + "-" + match.result_after90[1]
						EndIf
					EndIf
				Else If (match.includes_extra_time)
					s = dictionary.gettext("AFTER EXTRA TIME") + " " + dictionary.gettext("90 MINUTES")
					s = s + " " + match.result_after90[0] + "-" + match.result_after90[1]
				EndIf
			EndIf

		'second leg
		ElseIf (Self.current_leg = 1 And Self.round[Self.current_round].legs = 2)
			If (match.qualified)
				'penalties
				If match.result_penalties
					s = match.qualified.name + " " + dictionary.gettext("WIN") + " "
					s = s + Max(match.result_penalties[0], match.result_penalties[1])
					s = s + "-"
					s = s + Min(match.result_penalties[0], match.result_penalties[1])
					s = s + " " + dictionary.gettext("ON PENALTIES")
					If (match.includes_extra_time)
						s = s + " " + dictionary.gettext("AFTER EXTRA TIME")
						If (match.result[0] <> match.result_after90[0]) Or (match.result[1] <> match.result_after90[1])
							s = s + " " + dictionary.gettext("90 MINUTES")
							s = s + " " + match.result_after90[0] + "-" + match.result_after90[1]
						EndIf
					EndIf
				Else
					Local agg_score_a:Int = match.result[0] + match.old_result[1]
					Local agg_score_b:Int = match.result[1] + match.old_result[0]
					'away goals
					If (agg_score_a = agg_score_b)
						s = s + agg_score_a + "-" + agg_score_b + " " + dictionary.gettext("ON AGGREGATE") + " "
						s = s + match.qualified.name + " " + dictionary.gettext("WIN") + " " + dictionary.gettext("ON AWAY GOALS")
					Else 'on aggregate
						s = match.qualified.name + " " + dictionary.gettext("WIN") + " "
						s = s + Max(agg_score_a, agg_score_b)
						s = s + "-"
						s = s + Min(agg_score_a, agg_score_b)
						s = s + " " + dictionary.gettext("ON AGGREGATE")
					EndIf
					If (match.includes_extra_time)
						s = s + " " + dictionary.gettext("AFTER EXTRA TIME")
					EndIf
				EndIf
			Else
				s = dictionary.gettext("1ST LEG") + " " + match.old_result[0] + "-" + match.old_result[1]
			EndIf
			
		'replays
		Else
			If (match.qualified)
				If match.result_penalties
					s = match.qualified.name + " " + dictionary.gettext("WIN") + " "
					s = s + Max(match.result_penalties[0], match.result_penalties[1])
					s = s + "-"
					s = s + Min(match.result_penalties[0], match.result_penalties[1])
					s = s + " " + dictionary.gettext("ON PENALTIES")
					If (match.includes_extra_time)
						s = s + " " + dictionary.gettext("AFTER EXTRA TIME")
					EndIf
				Else If (match.includes_extra_time)
					s = dictionary.gettext("AFTER EXTRA TIME") + " " + dictionary.gettext("90 MINUTES")
					s = s + " " + match.result_after90[0] + "-" + match.result_after90[1]
				EndIf
			EndIf
		EndIf
		
		Return s
	End Method	
	
	Method update_round_names()
		Self.round[Self.rounds-1].name = dictionary.gettext("FINAL")
		If(Self.rounds) > 1
			Self.round[Self.rounds-2].name = dictionary.gettext("SEMI-FINAL")
		EndIf
		If(Self.rounds) > 2
			Self.round[Self.rounds-3].name = dictionary.gettext("QUARTER-FINAL")
		EndIf
		If(Self.rounds) > 3
			Self.round[0].name = dictionary.gettext("FIRST ROUND")
		EndIf
		If(Self.rounds) > 4
			Self.round[1].name = dictionary.gettext("SECOND ROUND")
		EndIf
		If(Self.rounds) > 5
			Self.round[2].name = dictionary.gettext("THIRD ROUND")
		EndIf
	End Method

	Method get_menu_title:String()
		
		Self.update_round_names()
		
		Local title:String = Self.name + " " + Self.round[Self.current_round].name
		Local matches:Int = CountList(Self.calendar_current)
		Select (Self.round[Self.current_round].legs)
			Case 1
				Select (Self.current_leg)
					Case 0
						'
					Case 1
						If matches = 1
							title = title + " " + dictionary.gettext("REPLAY")
						Else
							title = title + " " + dictionary.gettext("REPLAYS")
						EndIf
					Case 2
						If matches = 1
							title = title + " " + dictionary.gettext("2ND REPLAY")
						Else
							title = title + " " + dictionary.gettext("2ND REPLAYS")
						EndIf
					Case 3
						If matches = 1
							title = title + " " + dictionary.gettext("3RD REPLAY")
						Else
							title = title + " " + dictionary.gettext("3RD REPLAYS")
						EndIf
					Default
						If matches = 1
							title = title + " " + dictionary.gettext("REPLAY")
						Else
							title = title + " " + dictionary.gettext("REPLAYS")
						EndIf
				End Select
			Case 2
				Select (Self.current_leg)
					Case 0
						title = title + " " + dictionary.gettext("1ST LEG")
					Case 1
						title = title + " " + dictionary.gettext("2ND LEG") 
					Default
						If matches = 1
							title = title + " " + dictionary.gettext("REPLAY")
						Else
							title = title + " " + dictionary.gettext("REPLAYS")
						EndIf
				End Select
		End Select
		
		Return title
	End Method
	
	Method get_away_goals_label:String()
		Select Self.away_goals
			Case CS_AWAY_GOALS_OFF
				Return dictionary.gettext("OFF")
			Case CS_AWAY_GOALS_AFTER_90_MINS
				Return dictionary.gettext("AFTER 90 MINS")
			Case CS_AWAY_GOALS_AFTER_EXTRA_TIME
				Return dictionary.gettext("AFTER EXTRA TIME")
		End Select
	End Method

	Method get_legs_label:String(r:Int)
		Select Self.round[r].legs
			Case 1
				''// NOTE: max 6 characters
				Return dictionary.gettext("ONE LEG")
			Case 2
				''// NOTE: max 6 characters
				Return dictionary.gettext("TWO LEGS")
		End Select
	End Method
	
	Method get_extra_time_label:String(r:Int)
		Select Self.round[r].extra_time
			Case CS_EXTRA_TIME_OFF
				''// NOTE: max 15 characters
				Return dictionary.gettext("NO EXTRA TIME")
			Case CS_EXTRA_TIME_ON
				''// NOTE: max 15 characters
				Return dictionary.gettext("EXTRA TIME")
			Case CS_EXTRA_TIME_IF_REPLAY
				''// NOTE: max 15 characters
				Return dictionary.gettext("EXTRA TIME IF REPLAY")
		End Select
	End Method
	
	Method get_penalties_label:String(r:Int)
		Select Self.round[r].penalties
			Case CS_PENALTIES_OFF
				''// NOTE: max 15 characters
				Return dictionary.gettext("NO PENALTIES")
			Case CS_PENALTIES_ON
				''// NOTE: max 15 characters
				Return dictionary.gettext("PENALTIES")
			Case CS_PENALTIES_IF_REPLAY
				''// NOTE: max 15 characters
				Return dictionary.gettext("PENALTIES IF REPLAY")
		End Select
	End Method
	
End Type

'round
Type t_round
	Field name:String
	Field teams:Int
	Field legs:Int			'1 or 2
	Field extra_time:Int	'0=no extra time, 1=extra time, 2=extra time if replay
	Field penalties:Int		'0=no penalties, 1=penalties, 2=penalties if replay
	
	Method New()
		Self.name		= ""
		Self.teams		= 0
		Self.legs		= 1
		Self.extra_time	= CS_EXTRA_TIME_ON
		Self.penalties	= CS_PENALTIES_OFF
	End Method
End Type

Type t_match
	Field teams:t_team[2]
	Field result:Int[2]
	Field includes_extra_time:Int
	Field result_after90:Int[2]
	Field result_penalties:Int[2]
	Field old_result:Int[2]
	Field qualified:t_team
	Field status:String
	
	Method New()
		Self.teams					= Null
		Self.qualified				= Null
		Self.result					= Null
		Self.includes_extra_time	= 0
		Self.result_after90			= Null
		Self.result_penalties		= Null
		Self.old_result				= Null
		Self.status					= ""
	End Method	
	
	Method ToString:String()
		Local s:String = ""
		If Self.teams
			s = s + "team " + Self.teams[0].name + " vs team " + Self.teams[1].name
		EndIf
		If Self.result s = s + " RESULT: "+ Self.result[0] + ":" + Self.result[1]
		s = s + " STATUS: " + Self.status
		Return s
	End Method
End Type


'menu input
Type t_menu_input
	'values
	Field x:Int
	Field y:Int
	Field fa:Int
	Field fb:Int
	'old values
	Field x0:Int
	Field y0:Int
	Field fa0:Int
	Field fb0:Int
	'timers
	Field tx:Int
	Field ty:Int
	Field tfa:Int
	Field tfb:Int
End Type

'mouse
Type t_mouse
	Field x:Int
	Field x0:Int
	Field y:Int
	Field y0:Int
	Field moved:Int
	Field button1:Int
	Field button10:Int
	Field button2:Int
	Field button20:Int
End Type

'teams
Type t_team
	Field confederation:String
	Field national:Int		'0 -> club team, 1-> national team
	Field country:String
	Field number:Int
	Field ext:String
	Field name:String
	Field tactics:Int
	Field division:Int
	Field coach_name:String
	Field city:String
	Field stadium:String

	Field players:TList
	Field lineup:TList
	Field subs_count:Int	'substitutions made

	Field kit:Int			'selected kit: 0=first, 1=second, 2=third kit, 3=change1, 4=change2
	Field kit_count:Int		'number of available kits (min = 3, max = 5)
	Field kits:TList

	Field index:Int			'0=HOME, 1=AWAY
	Field control:Int		'1=computer, 2=player/coach, 3=coach
	Field input_device:t_input
	Field side:Int			'-1=upside, 1=downside	
	
	Field near1:t_player    ''nearest to the ball
	Field best_defender:t_player
	
	Field clnf:TImage		'club logo / national flag
	Field clnf_sh:TImage	'shadow

	Field won:Int
	Field drawn:Int
	Field lost:Int
	
	Field goals_for:Int
	Field goals_against:Int
	Field points:Int
	
	Method New()
		Self.players = New TList
		Self.lineup = New TList
		Self.kits = New TList
		Self.number = 0
		Self.ext = ""
		Self.control = CM_UNDEFINED
		Self._init()
	End Method
	
	Method _init()
		Self.confederation = ""
		Self.national = False
		Self.country = ""
		Self.name = ""
		Self.tactics = 0
		Self.division = 0
		Self.coach_name = ""
		Self.city = ""
		Self.stadium = ""
	
		Self.input_device = Null
		
		Self.players.Clear()
		Self.subs_count = 0
	
		Self.kit = 0
		Self.kit_count = 0
		Self.kits.Clear()
	
		Self.index = 0
		Self.side = 0
		
		Self.clnf = Null
		Self.clnf_sh = Null
	
		Self.won = 0
		Self.drawn = 0
		Self.lost = 0
		
		Self.goals_for = 0
		Self.goals_against = 0
		Self.points = 0
	
	End Method	
	
	Method set_input_device(d:t_input)
		Self.input_device = d
	End Method
	
	Method load_from_file()
		
		Local file_name:String = "data/team_" + Self.ext + ".yst"
		Local file_in:TStream = ReadFile(file_name)
		If (file_in = Null)
			RuntimeError("Invalid team file: " + file_name)
		EndIf
		
		Self._init()
		
		Local ln:String = ""
		Local b:Int
		While Not(Eof(file_in)) 
	
			b = ReadByte(file_in)		
	
			If (b = 35) ''Asc(#)
	
				ln = ReadLine(file_in)
	
				''compare the number
				If (Int(Mid(ln, 10, 3)) = Self.number)
	
					''country
					ln = ReadLine(file_in)
					Self.country = Mid(ln, 11, 3)
			
					Self.national = False
					If (Self.country = "UEF") Then Self.national = True	''UEFA
					If (Self.country = "CNC") Then Self.national = True	''CONCACAF
					If (Self.country = "CNM") Then Self.national = True	''CONMEBOL
					If (Self.country = "CAF") Then Self.national = True	''CAF
					If (Self.country = "AFC") Then Self.national = True	''AFC
					If (Self.country = "OFC") Then Self.national = True	''OFC
					
					''name
					ln = ReadLine(file_in)
					Self.name = Trim(Right(ln, Len(ln) -10))
					
					''tactics
					ln = ReadLine(file_in)
					Self.tactics = Int(Mid(ln, 11, 1))
			
					''division
					ln = ReadLine(file_in)
					Self.division = Int(Trim(Mid(ln, 11, 2)))
			
					''coach name
					ln = ReadLine(file_in)
					Self.coach_name	= Trim(Right(ln, Len(ln) -10))
	
					''coach type
					ln = ReadLine(file_in)
	
					''city
					ln = ReadLine(file_in)
					Self.city = Trim(Right(ln, Len(ln) -10))
	
					''stadium
					ln = ReadLine(file_in)
					Self.stadium = Trim(Right(ln, Len(ln) -10))
					
					''stadiumid
					ln = ReadLine(file_in)
	
					''void line
					ln = ReadLine(file_in)
	
					''kits headings
					ln = ReadLine(file_in)
	
					''main kit
					For Local k:Int = 0 To 4
						ln = ReadLine(file_in)
						Local style:String = Trim(Mid(ln, 11, 14))
						If (style <> "")
							Self.kit_count = Self.kit_count +1
							Local kit:t_kit	= New t_kit
							ListAddLast(Self.kits, kit)
							kit.style	= Int(style)
							kit.shirt1	= Int(Trim(Mid(ln, 18, 2)))
							kit.shirt2	= Int(Trim(Mid(ln, 25, 2)))
							kit.shorts	= Int(Trim(Mid(ln, 32, 2)))
							kit.socks	= Int(Trim(Mid(ln, 39, 2)))
						EndIf
					Next
			
					''goalie kits
					For Local k:Int = 0 To 1
						ln = ReadLine(file_in)
					Next
			
					''void line
					ln = ReadLine(file_in)
	
					''player headings
					ln = ReadLine(file_in)
	
					''read players
					ln = ReadLine(file_in)
					While (ln.length > 0)
			
						Local player:t_player = Self.new_player()
						
						player.index       = Int(Trim(Mid(ln, 1, 2)))-1
						player.number      = Int(Trim(Mid(ln, 11, 3)))
						player.name		   = Trim(Mid(ln, 19, 14))
						player.surname	   = Trim(Mid(ln, 33, 14))
						player.nationality = Trim(Mid(ln, 47, 3))
						player.role        = Int(Trim(Mid(ln, 53, 2)))
						
						player.hair_type  = hair_type(Int(Trim(Mid(ln, 59, 3))))
						player.hair_color = Int(Trim(Mid(ln, 65, 2)))
						player.skin_color = Int(Trim(Mid(ln, 71, 2)))
						player.price      = Int(Trim(Mid(ln, 77, 2)))
	
						''skills
						player.skill_passing   = Int(Mid(ln, 83, 1))
						player.skill_shooting  = Int(Mid(ln, 87, 1))
						player.skill_heading   = Int(Mid(ln, 91, 1))
						player.skill_tackling  = Int(Mid(ln, 95, 1))
						player.skill_control   = Int(Mid(ln, 99, 1))
						player.skill_speed     = Int(Mid(ln, 103, 1))
						player.skill_finishing = Int(Mid(ln, 107, 1))
						
						If (player.role = PR.GOALKEEPER)
							player.skill_keeper = round(player.price / 7)
						EndIf
				
						''order string with skills
						player.order_skills()
						
						''create ai
						player.set_ai(t_ai.Create(player))
						
						''set ai as default input_device
						player.set_input_device(player.get_ai())
						
						ln = ReadLine(file_in)
	
					Wend	
	
				EndIf
			EndIf
		Wend
		
		CloseFile(file_in)
	
	End Method
	
	Method save_to_file()
		
		Local file_name:String = "data/team_" + Lower(Self.ext) + ".ys0"
		Local file_in:TStream = ReadFile(file_name)
		If (file_in = Null)
			RuntimeError("Function save_team: cannot access file " + file_name)
		EndIf
		
		file_name = "data/team_" + Lower(Self.ext) + ".yst"
		Local file_out:TStream = WriteFile(file_name)
		If (file_out = Null)
			RuntimeError("Function save_team: cannot save file " + file_name)
		EndIf
		
		Local ln:String = ""
		Local b:Int
		While Not(Eof(file_in)) 
	
			b = ReadByte(file_in)
			WriteByte(file_out,b)		
			
			If (b = 35) ''Asc(#)
	
				''number
				ln = ReadLine(file_in)
				WriteLine(file_out, ln)
	
				''compare the number
				If (Int(Mid(ln,10,3)) = Self.number)
	
					''general info
					WriteLine(file_out, "Country  :" + Self.country)
					WriteLine(file_out, "Name     :" + Self.name)
					WriteLine(file_out, "Tactics  :" + Self.tactics)
					WriteLine(file_out, "Division :" + Self.division)
					WriteLine(file_out, "CoachName:" + Self.coach_name)
					WriteLine(file_out, "CoachType:" + "0")
					WriteLine(file_out, "City     :" + Self.city)
					WriteLine(file_out, "Stadium  :" + Self.stadium)
					WriteLine(file_out, "StadiumID:" + 0)
	
					''void line
					WriteLine(file_out, "")
	
					''kits headings
					WriteLine(file_out, "KITS______Type___First__Second_Shorts_Socks")
	
					''kits
					Local kit:t_kit
	
					''home
					kit = Self.kit_at_index(0)
					WriteLine(file_out, "Home      " + LSet(kit.style, 7) + LSet(kit.shirt1, 7) + LSet(kit.shirt2, 7) + LSet(kit.shorts, 7) + LSet(kit.socks, 7))
	
					''away
					kit = Self.kit_at_index(1)
					WriteLine(file_out, "Away      " + LSet(kit.style, 7) + LSet(kit.shirt1, 7) + LSet(kit.shirt2, 7) + LSet(kit.shorts, 7) + LSet(kit.socks, 7))
	
					''third
					kit = Self.kit_at_index(2)
					WriteLine(file_out, "Third     " + LSet(kit.style, 7) + LSet(kit.shirt1, 7) + LSet(kit.shirt2, 7) + LSet(kit.shorts, 7) + LSet(kit.socks, 7))
	
					''change 1
					If Self.kits.count() => 4
						kit = Self.kit_at_index(3)
						WriteLine(file_out, "Change1   " + LSet(kit.style, 7) + LSet(kit.shirt1, 7) + LSet(kit.shirt2, 7) + LSet(kit.shorts, 7) + LSet(kit.socks, 7))
					Else
						WriteLine(file_out, "Change1   ")
					EndIf
	
					''change 2
					If Self.kits.count() = 5
						kit = Self.kit_at_index(4)
						WriteLine(file_out, "Change2   " + LSet(kit.style, 7) + LSet(kit.shirt1, 7) + LSet(kit.shirt2, 7) + LSet(kit.shorts, 7) + LSet(kit.socks, 7))
					Else
						WriteLine(file_out, "Change2   ")
					EndIf
					
					''goalie
					WriteLine(file_out, "Goalie1   0      0      0      0      0")
					WriteLine(file_out, "Goalie2   0      0      0      0      0")
			
					''void line
					WriteLine(file_out, "")
	
					''players
					WriteLine(file_out, "PLAYERS___Number__Name__________Surname_______Nat___Role__HType_HCol__Skin__Price_PA__SH__HE__TA__BC__SP__FI")
	
					Local ply:Int = 0
					For Local player:t_player = EachIn Self.players
					
						ply = ply + 1
	
						ln = Replace(RSet(ply, 2)," ","0") + ":       "
						
						ln = ln + LSet(player.number, 8)
						ln = ln + LSet(player.name, 14)
						ln = ln + LSet(player.surname, 14)
						ln = ln + LSet(player.nationality, 6)
						ln = ln + LSet(player.role, 6)
						
						ln = ln + LSet(hair_cut[player.hair_type], 6)
						ln = ln + LSet(player.hair_color, 6)
						ln = ln + LSet(player.skin_color, 6)
						ln = ln + LSet(player.price, 6)
	
						''player skills
						If (player.role = PR.GOALKEEPER)
							player.skill_passing   = 0
							player.skill_shooting  = 0
							player.skill_heading   = 0
							player.skill_tackling  = 0
							player.skill_control   = 0
							player.skill_speed     = 0
							player.skill_finishing = 0
						EndIf
	
						ln = ln + player.skill_passing   + "   "
						ln = ln + player.skill_shooting  + "   "
						ln = ln + player.skill_heading   + "   "
						ln = ln + player.skill_tackling  + "   "
						ln = ln + player.skill_control   + "   "
						ln = ln + player.skill_speed     + "   "
						ln = ln + player.skill_finishing + "   "
						
						WriteLine(file_out, ln)
						
					Next
					
					''ending void lines				
					WriteLine(file_out, "")
					WriteLine(file_out, "")
	
					''move ahead input file handle
					Repeat 
						b = ReadByte(file_in)
	
						If (b = 35) Then WriteByte(file_out, b); Exit
	
						If Eof(file_in) Then Exit
					Forever
	
				EndIf
			EndIf
		Wend
		
		CloseFile(file_in)
		CloseFile(file_out)
	
	End Method
	
	Method new_player:t_player()
		If (Self.players.Count() = FULL_TEAM)
			Return Null
		EndIf
		Local player:t_player = New t_player
		ListAddLast(Self.players, player)
		player.team = Self
		Return player
	End Method
	
	Method delete_player:Int(player:t_player)
		If (Self.players.Count() <= BASE_TEAM)
			Return False
		EndIf
		ListRemove(Self.players, player)
		Return True
	End Method
	
	Method new_kit:t_kit()
		If (Self.kit_count >= MAX_KITS) Return Null
		Local kit:t_kit = New t_kit
		ListAddLast(Self.kits, kit)
		Self.kit_count = Self.kit_count +1
		Return kit
	End Method

	Method delete_kit:Int()
		If (Self.kit_count <= MIN_KITS) Return False
		Self.kits.removelast()
		Self.kit_count = Self.kit_count -1
		Return True
	End Method
	
	Method kit_at_index:t_kit(i:Int)
		Return t_kit(Self.kits.ValueAtIndex(i))
	End Method
	
	Method generate_scorers(goals:Int) 
		Local p:Int
		Local team_fi:Int
		p = 1
		team_fi = 0
		For Local ply:t_player = EachIn Self.players
			team_fi = team_fi +ply.skill_heading +ply.skill_shooting +ply.skill_finishing
		
			If (ply.role = PR.RIGHT_WINGER) Or (ply.role = PR.LEFT_WINGER)
				team_fi = team_fi + 10
			Else If (ply.role = PR.MIDFIELDER)
				team_fi = team_fi + 5
			Else If (ply.role = PR.ATTACKER)
				team_fi = team_fi + 30
			End If
		
			If (p = 11) Then Exit
			p = p + 1
		Next
		For Local g:Int = 1 To goals
			Local target:Int = Rand(1, team_fi)
			Local sum:Int = team_fi
			For Local ply:t_player = EachIn Self.players
				sum = sum -ply.skill_heading -ply.skill_shooting -ply.skill_finishing -7*(ply.role = PR.ATTACKER)
		
				If (ply.role = PR.RIGHT_WINGER) Or (ply.role = PR.LEFT_WINGER)
					sum = sum - 10
				Else If (ply.role = PR.MIDFIELDER)
					sum = sum - 5
				Else If (ply.role = PR.ATTACKER)
					sum = sum - 30
				End If
		
				If (sum < target) 
					ply.goals = ply.goals + 1
					Exit
				EndIf
			Next
		Next
	End Method
	
	Method save(frame:Int)
		For Local ply:t_player = EachIn Self.players
			ply.save(frame)
		Next
	End Method
	
	Method find_nearest()
		
		''''find nearest player
		''step 1: search using frame_distance,
		''which takes into account both the speed of the player and the speed and direction of the ball
		Self.near1 = Null
		For Local i:Int = 0 To TEAM_SIZE -1
			Local ply:t_player = Self.lineup_at_index(i)
			
			''discard those players which cannot reach the ball in less than BALL_PREDICTION frames
			If (ply.frame_distance = BALL_PREDICTION) Continue
			
			If ((Self.near1 = Null) Or (ply.frame_distance < Self.near1.frame_distance))
				Self.near1 = ply
			EndIf
		Next
		
		''step 2: if not found, repeat using pixel distance
		If (Self.near1 = Null)
			Self.near1 = Self.lineup_at_index(0)
			For Local i:Int = 1 To TEAM_SIZE -1
				Local ply:t_player = Self.lineup_at_index(i)
				
				If (ply.ball_distance < Self.near1.ball_distance)
					Self.near1 = ply
				EndIf
			Next
		EndIf
		
	End Method
	
	Method find_best_defender()
		Self.best_defender = Null
		
		If (ball.owner And ball.owner.team <> Self)
			Local attacker_goal_distance:Int = dist(ball.owner.x, ball.owner.y, 0, -GOAL_LINE * ball.owner.team.side)
			
			Local best_distance:Int = 2 * GOAL_LINE
			For Local i:Int = 1 To TEAM_SIZE -1
				Local ply:t_player = Self.lineup_at_index(i)
				ply.defend_distance = dist(ply.x, ply.y, ball.owner.x, ball.owner.y)
				
				Local ply_goal_distance:Int = dist(ply.x, ply.y, 0, -GOAL_LINE * ball.owner.team.side)
				If ((ply_goal_distance < 0.95*attacker_goal_distance) And (ply.defend_distance < best_distance))
					Self.best_defender = ply
					best_distance = ply.defend_distance
				EndIf
			Next
		EndIf
	End Method
	
	Method update_statistics(g_for:Int, g_against:Int, points_for_a_win:Int)
		Self.goals_for		= Self.goals_for + g_for
		Self.goals_against	= Self.goals_against + g_against
		If (g_for > g_against)
			Self.won		= Self.won + 1
			Self.points		= Self.points + points_for_a_win
		Else If (g_for = g_against)
			Self.drawn		= Self.drawn + 1
			Self.points		= Self.points + 1
		Else
			Self.lost		= Self.lost + 1
		EndIf
	End Method
	
	Method player_at_index:t_player(i:Int)
		Return t_player(Self.players.ValueAtIndex(i))
	End Method
	
	Method lineup_at_index:t_player(i:Int)
		Return t_player(Self.lineup.ValueAtIndex(i))
	End Method
	
	Method player_at_position:t_player(pos:Int, t:t_tactics = Null)
		If (t = Null)
			t = tactics_array[Self.tactics]
		EndIf
		If (pos < Self.players.Count())
			Local base_tactics:Int = t.based_on
			Local ply:Int = tactics_order[base_tactics, pos]
			Return Self.player_at_index(ply)
		Else
			Return Null
		EndIf
	End Method
	
	Method lineup_at_position:t_player(pos:Int, t:t_tactics = Null)
		If (t = Null)
			t = tactics_array[Self.tactics]
		EndIf
		If (pos < Self.lineup.Count())
			Local base_tactics:Int = t.based_on
			Local ply:Int = tactics_order[base_tactics, pos]
			Return Self.lineup_at_index(ply)
		Else
			Return Null
		EndIf
	End Method
	
	''club logo / national flag
	Method load_clnf(create_shadow:Int)
	
		Local folder:String, img:TImage
		If Self.national
			folder = "images/flags/"
			If Self.country = "UEF"
				folder = folder + "uefa"
			Else If Self.country = "CNC"
				folder = folder + "concacaf"
			Else If Self.country = "CNM"
				folder = folder + "conmebol"
			Else
				''CAF, AFC, OFC
				folder = folder + Lower(Self.country)
			EndIf
		Else
			folder = "images/logos/" + Lower(Self.country)
		EndIf
		
		Local myDir:Int = ReadDir(folder)
		
		If mydir <> 0
			Repeat
			Local file:String = NextFile(myDir)
			
			If file = "" Then Exit
			
			''if it is a file (and not a folder)
			If FileType(folder + "/" + file) = 1
				If Left(file,3) = Replace(RSet(Self.number, 3)," ",0) 
					img = Load_Image(folder, file, MASKEDIMAGE|DYNAMICIMAGE, $FF3399)
				EndIf
			EndIf
			Forever
		EndIf
			
		''load standard logo
		If img = Null
			Local file:String
			Local first_kit:t_kit = t_kit(Self.kits.first())
			
			Select first_kit.style
				Case 0
					file = "plain"
				Case 1
					file = "col_sleeves"
				Case 2
					file = "vertical"
				Case 3
					file = "horizontal"
				Case 4
					file = "check"
				Case 5
					file = "vert_halves"	
				Case 6
					file = "strip"
				Case 7
					file = "spice"
				Case 8
					file = "armband"
				Case 9
					file = "large_strips"
				Case 10
					file = "diagonal"
				Case 11
					file = "band"
				Case 12
					file = "line"
				Case 13
					file = "two_stripes"
				Case 14
					file = "double_stripe"
				Case 15
					file = "big_check"
				Case 16
					file = "big_v"
				Case 17
					file = "cross"
				Case 18
					file = "diagonal_half"
				Case 19
					file = "vert_strip"
				Case 20
					file = "side_lines"
			End Select
			img = load_image("images/logos/std", file+".png", MASKEDIMAGE|DYNAMICIMAGE, $FF3399)
			first_kit.render_image(img)
		EndIf
		
		Self.clnf = img
	
		''create shadow
		If (create_shadow = 1) And (Self.national = False)
			If Self.clnf <> Null
				Self.clnf_sh = copy_image(Self.clnf)
				image2shadow(Self.clnf_sh, $002400)
			EndIf
		EndIf
		
	End Method

	''image_type: "player" or "kit"
	Method get_image_path:String(image_type:String)
	
		Local folder:String
	
		''club
		If (Not Self.national)
			folder = "images/custom/clubs/" + Lower(Self.country)
		''national
		Else
			folder = "images/custom/nationals/"
			If Self.country = "UEF"
				folder = folder + "uefa"
			Else If Self.country = "CNC"
				folder = folder + "concacaf"
			Else If Self.country = "CNM"
				folder = folder + "conmebol"
			Else
				'CAF, AFC, OFC
				folder = folder + Lower(Self.country)
			EndIf
		EndIf
		
		''search team folder
		Local file:String
		Local myDir:Int = ReadDir(folder)
		If (mydir <> 0)
			Repeat
				file = NextFile(myDir)
				
				If (file = "") Then Exit
				
				''if it is a folder
				If (FileType(folder + "/" + file) = 2)
					If (Left(file,3) = Replace(RSet(Self.number,3)," ",0))
						folder = folder + "/" + file
						Exit
					EndIf
				EndIf
			Forever
		EndIf
		
		''search for the specific player/kit
		myDir = ReadDir(folder)
		If (mydir <> 0)
			Repeat
				file = NextFile(myDir)
				
				If (file = "") Then Exit
				
				''if it is a file (and not a folder)
				If (FileType(folder + "/" + file) = 1)
					If (Left(file,Len(image_type)+2) = image_type + "_" + (Self.kit+1))
						Return folder + "/" + file
					EndIf
				EndIf
			Forever
		EndIf
	
		''search for the universal player/kit	
		myDir = ReadDir(folder)
		If (mydir <> 0)
			Repeat
				file = NextFile(myDir)
				
				If (file="") Then Exit
				
				''if it is a file (and not a folder)
				If (FileType(folder + "/" + file) = 1)
					If (Left(file, Len(image_type)+1) = image_type + ".")
						Return folder + "/" + file
					EndIf
				EndIf
			Forever
		EndIf
	
		''generic
		Local kit:t_kit = Self.kit_at_index(Self.kit)
		Select kit.style
			Case 0
				file = "plain"
			Case 1
				file = "col_sleeves"
			Case 2
				file = "vertical"
			Case 3
				file = "horizontal"
			Case 4
				file = "check"
			Case 5
				file = "vert_halves"	
			Case 6
				file = "strip"
			Case 7
				file = "spice"
			Case 8
				file = "armband"
			Case 9
				file = "large_strips"
			Case 10
				file = "diagonal"
			Case 11
				file = "band"
			Case 12
				file = "line"
			Case 13
				file = "two_stripes"
			Case 14
				file = "double_stripe"
			Case 15
				file = "big_check"
			Case 16
				file = "big_v"
			Case 17
				file = "cross"
			Case 18
				file = "diagonal_half"
			Case 19
				file = "vert_strip"
			Case 20
				file = "side_lines"
		End Select
		
		Return "images/" + image_type + "/" + file + ".png"
		
	End Method
	
	''search nearest role in substitutions
	Method easy_subs:Int(role:Int)
		
		Local level:Int = -1
		
		''first search for the same player role
		Local target:Int = role
		
		Repeat
			For Local pos:Int = 0 To TEAM_SIZE -1
				If (Self.lineup_at_position(pos).role = target)
					Return pos
				EndIf
			Next
			
			''move the target
			level = level +1
			
			''not found, return first position
			If (level = 2)
				Return 0
			EndIf
			
			target = substitution_rules[role, level]
		Forever
		
	End Method
	
	Method defense_rating:Int()
		Local i:Int = 1
		Local defense:Int = 0
		For Local ply:t_player = EachIn Self.players
			If ((i => 1) And (i <= 11))
				If ((ply.role = PR.RIGHT_BACK) Or ..
					(ply.role = PR.LEFT_BACK) Or ..
					(ply.role = PR.DEFENDER) Or ..
					(ply.role = PR.MIDFIELDER))
					defense :+ ply.skill_tackling
					defense :+ ply.skill_heading
					defense :+ ply.skill_passing
					defense :+ ply.skill_speed
					defense :+ ply.skill_control
				EndIf
			EndIf
			i = i + 1
		Next
		Return defense
	End Method 
	
	Method offense_rating:Int()
		Local i:Int = 1
		Local offense:Int = 0
		For Local ply:t_player = EachIn Self.players
			If ((i => 1) And (i <= 11))
				If ((ply.role = PR.RIGHT_WINGER) Or ..
					(ply.role = PR.LEFT_WINGER) Or ..
					(ply.role = PR.MIDFIELDER) Or ..
					(ply.role = PR.ATTACKER))
					offense :+ ply.skill_heading
					offense :+ ply.skill_finishing
					offense :+ ply.skill_speed
					offense :+ ply.skill_shooting
					offense :+ ply.skill_control
				EndIf
			EndIf
			i = i + 1
		Next
		Return offense
	End Method
	
	''absolute ranking from 0 to 10
	Method ranking:Float()
		Local i:Int = 1
		Local r:Float = 0
		For Local ply:t_player = EachIn Self.players
			If ((i => 1) And (i <= 11))
				r = r + ply.price / 5.0
			EndIf
			i = i + 1
		Next
		r = r / 11.0
		Return r
	End Method 
	
	Method update_tactics(relative_to_center:Int = False)
		
		Local ball_zone:Int = 17 - Self.side * ball.zone_x - 5 * Self.side * ball.zone_y
		
		If (relative_to_center)
			ball_zone = 17
		EndIf
		
		For Local i:Int = 1 To TEAM_SIZE -1
			
			Local ply:t_player = Self.lineup_at_index(i)
			
			Local t:t_vec2d = tactics_array[Self.tactics].target[i, ball_zone]
			
			ply.tx = (1 - Abs(ball.mx)) * t.x + Abs(ball.mx) * t.x
			ply.ty = (1 - Abs(ball.my)) * t.y + Abs(ball.my) * t.y
			
			ply.tx = - Self.side * ply.tx
			ply.ty = - Self.side * (ply.ty -4)
		Next
		
	End Method
	
	Method update_frame_distance()
		For Local i:Int = 0 To TEAM_SIZE -1
			Self.lineup_at_index(i).update_frame_distance()
		Next
	End Method
	
	Method update_players:Int(limit:Int)
		
		Self.find_nearest()
		
		Self.find_best_defender()
		
		Local move:Int = Self.update_lineup(limit)
		
		Return move
		
	End Method
	
	Method update_lineup:Int(limit:Int)
		
		Local move:Int = False
		For Local ply:t_player = EachIn Self.lineup
			If (ply.update(limit))
				move = True
			EndIf
			ply.think()
		Next
		Return move
		
	End Method
	
	Method draw_controlled_players_numbers()
		For Local ply:t_player = EachIn Self.lineup
			If (ply.input_device <> ply.ai And ply.is_visible)
				ply.draw_number()
			EndIf
		Next
	End Method
	
	Method update_lineup_ai()
		For Local player:t_player = EachIn Self.lineup
			If (player.input_device = player.ai)
				player.update_ai()
			EndIf
		Next
	End Method
	
	Method non_ai_input_devices_count:Int()
		Local n:Int = 0
		For Local p:t_player = EachIn Self.players
			If (p.input_device <> p.ai)
				n :+ 1
			EndIf
		Next
		Return n
	End Method
	
	Method release_non_ai_input_devices()
		For Local p:t_player = EachIn Self.players
			If (p.input_device <> p.ai)
				p.input_device.set_is_available(True)
				p.set_input_device(p.ai)
			EndIf
		Next
	End Method
	
	Method uses_automatic_input_device:Int()
		Return (Self.control = CM_PLAYER) And (Self.input_device <> Null)
	End Method
	
	Method fire1_down:t_input()
		If (Self.uses_automatic_input_device:Int())
			If (Self.input_device.fire1_down())
				Return Self.input_device
			EndIf
		Else
			For Local p:t_player = EachIn Self.lineup
				If ((p.input_device <> p.ai) And p.input_device.fire1_down())
					Return p.input_device
				EndIf
			Next
		EndIf
	End Method
	
	Method fire1_up:t_input()
		If (Self.uses_automatic_input_device:Int())
			If (Self.input_device.fire1_up())
				Return Self.input_device
			EndIf
		Else
			For Local p:t_player = EachIn Self.lineup
				If ((p.input_device <> p.ai) And p.input_device.fire1_up())
					Return p.input_device
				EndIf
			Next
		EndIf
	End Method
	
	Method fire2_down:t_input()
		If (Self.uses_automatic_input_device:Int())
			If (Self.input_device.fire2_down())
				Return Self.input_device
			EndIf
		Else
			For Local p:t_player = EachIn Self.lineup
				If ((p.input_device <> p.ai) And p.input_device.fire2_down())
					Return p.input_device
				EndIf
			Next
		EndIf
	End Method
	
	Method automatic_input_device_selection()
		
		''search controlled player
		Local controlled:t_player
		For Local p:t_player = EachIn Self.lineup
			If (p.input_device <> p.ai)
				controlled = p
			EndIf
		Next
		
		If (controlled = Null)
			
			''assign input device
			If (Self.near1.get_active_state().check_name("state_stand_run"))
				Self.near1.set_input_device(Self.input_device)
			EndIf
			
		ElseIf (ball.owner = Null)
			
			''move input_device to nearest
			If (controlled <> Self.near1) And (controlled.frame_distance = BALL_PREDICTION)
				
				If (controlled.get_active_state().check_name("state_stand_run") And Self.near1.get_active_state().check_name("state_stand_run"))
					Self.near1.set_input_device(Self.input_device)
					controlled.set_input_device(controlled.get_ai())
				EndIf
				
			EndIf
			
		ElseIf (ball.owner.team.index = Self.index)
			
			''move input_device to ball owner
			If ((controlled <> ball.owner) And controlled.get_active_state().check_name("state_stand_run") And Self.near1.get_active_state().check_name("state_stand_run"))
				ball.owner.set_input_device(Self.input_device)
				controlled.set_input_device(controlled.get_ai())
			EndIf
			
		ElseIf (ball.owner.team.index <> Self.index)
			
			If (Self.best_defender And ..
				Self.best_defender <> controlled ..
				And Self.best_defender.defend_distance < 0.95 * controlled.defend_distance ..
				And controlled.get_active_state().check_name("state_stand_run") ..
				And Self.best_defender.get_active_state().check_name("state_stand_run"))
				Self.best_defender.set_input_device(Self.input_device)
				controlled.set_input_device(controlled.get_ai())
			EndIf
			
		EndIf
		
	End Method
	
End Type

Type t_player Extends t_sprite
	
	''keeper collision types
	Const CT_NONE:Int = 0
	Const CT_REBOUND:Int = 1
	Const CT_CATCH:Int = 2
	Const CT_DEFLECT:Int = 3
	
	Field name:String
	Field surname:String
	Field team:t_team
	Field nationality:String
	Field index:Int
	Field role:Int
	Field number:Int
	
	Field hair_type:Int
	Field hair_color:Int
	Field skin_color:Int
	
	
	Field skill_passing:Int
	Field skill_shooting:Int
	Field skill_heading:Int
	Field skill_tackling:Int
	Field skill_control:Int
	Field skill_speed:Int
	Field skill_finishing:Int
	Field skill_keeper:Int

	Field price:Int		'value 0..49

	Field goals:Int	
	
	Field input_device:t_input
	Field ai:t_ai
	
	Field kick_angle:Float
	Field defend_distance:Float
	
	Field facing_player:t_player
	Field facing_angle:Float
	
	Field fsm:t_state_machine
	Field state_idle:t_player_state_idle
	Field state_outside:t_player_state_outside
	Field state_bench_sitting:t_player_state_bench_sitting
	Field state_bench_standing:t_player_state_bench_standing
	Field state_bench_out:t_player_state_bench_out
	Field state_photo:t_player_state_photo
	Field state_stand_run:t_player_state_stand_run
	Field state_kick:t_player_state_kick
	Field state_head:t_player_state_head
	Field state_tackle:t_player_state_tackle
	Field state_reach_target:t_player_state_reach_target
	Field state_kick_off:t_player_state_kick_off
	Field state_goal_kick:t_player_state_goal_kick
	Field state_throw_in_angle:t_player_state_throw_in_angle
	Field state_throw_in_speed:t_player_state_throw_in_speed
	Field state_corner_kick_angle:t_player_state_corner_kick_angle
	Field state_corner_kick_speed:t_player_state_corner_kick_speed
	Field state_goal_scorer:t_player_state_goal_scorer
	Field state_goal_mate:t_player_state_goal_mate
	Field state_own_goal_scorer:t_player_state_own_goal_scorer
	
	Field state_keeper_positioning:t_player_state_keeper_positioning
	Field state_keeper_diving_low_single:t_player_state_keeper_diving_low_single
	Field state_keeper_diving_low_double:t_player_state_keeper_diving_low_double
	Field state_keeper_diving_middle_one:t_player_state_keeper_diving_middle_one
	Field state_keeper_diving_middle_two:t_player_state_keeper_diving_middle_two
	Field state_keeper_diving_high_one:t_player_state_keeper_diving_high_one
	Field state_keeper_catching_high:t_player_state_keeper_catching_high
	Field state_keeper_catching_low:t_player_state_keeper_catching_low
	Field state_keeper_kick_angle:t_player_state_keeper_kick_angle
	
	Method New()
		
		For Local i:Int = 0 To VSIZE-1
			Self.data[i] = New t_data
		Next
		
		Self.fsm = New t_state_machine
		
		Self.state_idle = t_player_state_idle.Create(Self)
		Self.fsm.add_state(Self.state_idle)
		
		Self.state_outside = t_player_state_outside.Create(Self)
		Self.fsm.add_state(Self.state_outside)
		
		Self.state_bench_sitting = t_player_state_bench_sitting.Create(Self)
		Self.fsm.add_state(Self.state_bench_sitting)
		
		Self.state_bench_standing = t_player_state_bench_standing.Create(Self)
		Self.fsm.add_state(Self.state_bench_standing)
		
		Self.state_bench_out = t_player_state_bench_out.Create(Self)
		Self.fsm.add_state(Self.state_bench_out)
		
		Self.state_photo = t_player_state_photo.Create(Self)
		Self.fsm.add_state(Self.state_photo)
		
		Self.state_stand_run = t_player_state_stand_run.Create(Self)
		Self.fsm.add_state(Self.state_stand_run)
		
		Self.state_kick = t_player_state_kick.Create(Self)
		Self.fsm.add_state(Self.state_kick)
		
		Self.state_head = t_player_state_head.Create(Self)
		Self.fsm.add_state(Self.state_head)
		
		Self.state_tackle = t_player_state_tackle.Create(Self)
		Self.fsm.add_state(Self.state_tackle)
		
		Self.state_reach_target = t_player_state_reach_target.Create(Self)
		Self.fsm.add_state(Self.state_reach_target)
		
		Self.state_kick_off = t_player_state_kick_off.Create(Self)
		Self.fsm.add_state(Self.state_kick_off)
		
		Self.state_goal_kick = t_player_state_goal_kick.Create(Self)
		Self.fsm.add_state(Self.state_goal_kick)
		
		Self.state_throw_in_angle = t_player_state_throw_in_angle.Create(Self)
		Self.fsm.add_state(Self.state_throw_in_angle)
		
		Self.state_throw_in_speed = t_player_state_throw_in_speed.Create(Self)
		Self.fsm.add_state(Self.state_throw_in_speed)
		
		Self.state_corner_kick_angle = t_player_state_corner_kick_angle.Create(Self)
		Self.fsm.add_state(Self.state_corner_kick_angle)
		
		Self.state_corner_kick_speed = t_player_state_corner_kick_speed.Create(Self)
		Self.fsm.add_state(Self.state_corner_kick_speed)
		
		Self.state_goal_scorer = t_player_state_goal_scorer.Create(Self)
		Self.fsm.add_state(Self.state_goal_scorer)
		
		Self.state_goal_mate = t_player_state_goal_mate.Create(Self)
		Self.fsm.add_state(Self.state_goal_mate)
		
		Self.state_own_goal_scorer = t_player_state_own_goal_scorer.Create(Self)
		Self.fsm.add_state(Self.state_own_goal_scorer)
		
		Self.state_keeper_positioning = t_player_state_keeper_positioning.Create(Self)
		Self.fsm.add_state(state_keeper_positioning)
		
		Self.state_keeper_diving_low_single = t_player_state_keeper_diving_low_single.Create(Self)
		Self.fsm.add_state(Self.state_keeper_diving_low_single)
		
		Self.state_keeper_diving_low_double = t_player_state_keeper_diving_low_double.Create(Self)
		Self.fsm.add_state(Self.state_keeper_diving_low_double)
		
		Self.state_keeper_diving_middle_one = t_player_state_keeper_diving_middle_one.Create(Self)
		Self.fsm.add_state(Self.state_keeper_diving_middle_one)
		
		Self.state_keeper_diving_middle_two = t_player_state_keeper_diving_middle_two.Create(Self)
		Self.fsm.add_state(Self.state_keeper_diving_middle_two)
		
		Self.state_keeper_diving_high_one = t_player_state_keeper_diving_high_one.Create(Self)
		Self.fsm.add_state(Self.state_keeper_diving_high_one)
		
		Self.state_keeper_catching_high = t_player_state_keeper_catching_high.Create(Self)
		Self.fsm.add_state(Self.state_keeper_catching_high)
		
		Self.state_keeper_catching_low = t_player_state_keeper_catching_low.Create(Self)
		Self.fsm.add_state(Self.state_keeper_catching_low)
		
		Self.state_keeper_kick_angle = t_player_state_keeper_kick_angle.Create(Self)
		Self.fsm.add_state(Self.state_keeper_kick_angle)
		
	End Method
	
	'menu
	Field skills:String		'string containing ordered skills 
	Field face:TImage		'face
	Field facesh:TImage		'face shadow

	'match	
	Field speed:Float
	
	Field hair:TImage		'hair image
	Field shadow:TImage[4]

	Field data:t_data[VSIZE]
	
	Field health:Int	'0 = good, 1 = injuried, 2 = sick

	Field x:Float		'x position
	Field y:Float		'y position
	Field z:Float		'z position
	Field x0:Float      ''old x position
	Field y0:Float      ''old y position
	Field z0:Float      ''old z position
	Field v:Float		'speed
	Field vz:Float		'vertical speed
	Field a:Float		'angle
	Field thrust_x:Float	'horiz.speed in keeper saves (min=0, max=1)
	Field thrust_z:Float	'vert.speed in keeper saves (min=0, max=1)

	Field tx:Float		'x position (target)
	Field ty:Float		'y position (target)

	Field fmx:Float		'0..7 direction
	Field fmy:Float		'1 = standing, 0 and 2 = running
	Field fmy_sweep:Float
	
	Field ball_distance:Float
	
    'from 0 to BALL_PREDICTION-1: frames required to reach the ball
    'equal to BALL_PREDICTION: ball too far to be reached
    'shoud be updated every frame
    Field frame_distance:Int;
	
	Method set_x(_x:Int)
		Self.x0 = Self.x
		Self.x = _x
	End Method
	
	Method set_y(_y:Int)
		Self.y0 = Self.y
		Self.y = _y
	End Method
	
	Method set_z(_z:Int)
		Self.z0 = Self.z
		Self.z = _z
	End Method
	
	Method set_tx(_tx:Int)
		Self.tx = _tx
	End Method
		
	Method set_ty(_ty:Int)
		Self.ty = _ty
	End Method
	
	Method set_target(_tx:Int, _ty:Int)
		Self.set_tx(_tx)
		Self.set_ty(_ty)
	End Method
	
	Method set_fmx(_fmx:Int)
		Self.fmx = _fmx
	End Method
	
	Method set_fmy(_fmy:Int)
		Self.fmy = _fmy
	End Method
	
	Method get_active_state:t_state()
		Return Self.fsm.get_active_state()
	End Method
	
	Method set_state(name:String)
		Self.fsm.set_state(name)
	End Method
	
	Method think()
		Self.fsm.think()
	End Method
	
	Method set_input_device(t:t_input)
		Self.input_device = t
	End Method
	
	Method update_ai()
		Self.ai.read_input()
	End Method
	
	Method get_ai:t_ai()
		Return Self.ai
	End Method
	
	Method set_ai(t:t_ai)
		Self.ai = t
	End Method
	
	Method is_using_ai:Int()
		Return (Self.input_device = Self.ai)
	End Method
	
	Method draw(frame:Int)
		If (Not data[frame].is_visible)
			Return
		EndIf
		
		If (Self.role = PR.GOALKEEPER)
			draw_sub_image_rect(img, data[frame].x -24, data[frame].y -34 -data[frame].z, 50, 50, 50*data[frame].fmx, 50*data[frame].fmy, 50, 50)
			
			''add an offset of 8
			Local fmx:Int = data[frame].fmx +8
			Local fmy:Int = data[frame].fmy
			If hair_map[fmx,fmy,2] Or hair_map[fmx,fmy,3]
				draw_sub_image_rect(hair, data[frame].x -24 +hair_map[fmx,fmy,2], data[frame].y -34 -data[frame].z +hair_map[fmx,fmy,3], 20, 20, hair_map[fmx,fmy,0] * 21, hair_map[fmx,fmy,1] * 21, 20, 20)
			EndIf
		Else
			Local fmx:Int = data[frame].fmx
			Local fmy:Int = data[frame].fmy
			
			Local x_offset:Int = player_map[fmx, fmy, 0]
			Local y_offset:Int = player_map[fmx, fmy, 1]
			
			draw_sub_image_rect(img, data[frame].x -x_offset, data[frame].y -y_offset -data[frame].z, 32, 32, 32*data[frame].fmx, 32*data[frame].fmy, 32, 32)
			
			If hair_map[fmx,fmy,2] Or hair_map[fmx,fmy,3]
				draw_sub_image_rect(hair, data[frame].x -x_offset -9 +hair_map[fmx,fmy,2], data[frame].y -y_offset -data[frame].z -9 +hair_map[fmx,fmy,3], 20, 20, hair_map[fmx,fmy,0] * 21, hair_map[fmx,fmy,1] * 21, 20, 20)
			EndIf
		EndIf
		
		If (debug)
			SetBlend(ALPHABLEND)
			SetScale(0.71, 0.71)
			DrawText(Mid(Upper(Self.get_active_state().name), 7), Self.x, Self.y -PLAYER_H -15)
			DrawText(Mid(Upper(Self.ai.fsm.active_state.name),7), Self.x, Self.y-PLAYER_H-5)
			
			SetScale(1, 1)
			SetBlend(MASKBLEND)
		EndIf
		
	End Method
	
	Method draw_shadow(frame:Int)
		If (Not data[frame].is_visible)
			Return
		EndIf
		
		If (Self.role = PR.GOALKEEPER)
			draw_sub_image_rect(img_keeper_shadows[0], Self.data[frame].x -24 +0.65 * Self.data[frame].z, Self.data[frame].y -34 +0.46 * Self.data[frame].z, 50, 50, 50*Self.data[frame].fmx, 50*Self.data[frame].fmy, 50, 50)
		Else
			Local x_offset:Int = player_map[data[frame].fmx, data[frame].fmy, 0]
			Local y_offset:Int = player_map[data[frame].fmx, data[frame].fmy, 1]
			draw_sub_image_rect(img_player_shadows[0], Self.data[frame].x -x_offset -9 +0.65 * Self.data[frame].z, Self.data[frame].y -y_offset -9 +0.46 * Self.data[frame].z, 50, 50, 50*Self.data[frame].fmx, 50*Self.data[frame].fmy, 50, 50)
		EndIf

		If (location_settings.time = TI.NIGHT)
			draw_sub_image_rect(img_player_shadows[1], Self.data[frame].x -24 -0.65 * Self.data[frame].z, Self.data[frame].y -34 +0.46 * Self.data[frame].z, 50, 50, 50*Self.data[frame].fmx, 50*Self.data[frame].fmy, 50, 50)
			draw_sub_image_rect(img_player_shadows[2], Self.data[frame].x -24 -0.65 * Self.data[frame].z, Self.data[frame].y -34 -0.46 * Self.data[frame].z, 50, 50, 50*Self.data[frame].fmx, 50*Self.data[frame].fmy, 50, 50)
			draw_sub_image_rect(img_player_shadows[3], Self.data[frame].x -24 +0.65 * Self.data[frame].z, Self.data[frame].y -34 -0.46 * Self.data[frame].z, 50, 50, 50*Self.data[frame].fmx, 50*Self.data[frame].fmy, 50, 50)
		EndIf
	End Method
	
	Method draw_number_and_name()
		text10u(Self.number + " " + Self.get_single_name(), 10, 1, img_ucode10g, 1)
	End Method
	
	Method animation_stand_run()
		Self.fmx = round(((Self.a +360) Mod 360)/45) Mod 8
		If (Self.v > 0)
			Self.fmy_sweep = (Self.fmy_sweep +0.16 * Self.v / 1000) Mod 4
			If (Self.fmy_sweep > 3)
				Self.fmy = Self.fmy_sweep -2
			Else
				Self.fmy = Self.fmy_sweep
			EndIf
		Else
			Self.fmy = 1
		EndIf
	End Method
	
	Method animation_scorer()
		Self.fmx = round(((Self.a +360) Mod 360)/45) Mod 8
		If (Self.v > 0)
			Self.fmy_sweep = (Self.fmy_sweep +0.16 * Self.v / 1000) Mod 4
			If (Self.fmy_sweep > 3)
				Self.fmy = 12
			Else
				Self.fmy = 11 +Self.fmy_sweep
			EndIf
		Else
			Self.fmy = 1
		EndIf
	End Method
	
	Method get_single_name:String()
		If (Self.surname)
			Return Self.surname
		Else
			Return Self.name
		EndIf
	End Method

	Method get_y:Int(frame:Int)
		Return data[frame].y
	End Method

	Method auto_number()
		Self.number = 0
		Self.change_number(+1)
	End Method

	Method change_number(direction:Int)
		
		Local used:Int
		Repeat
			Self.number = rotate(Self.number, 1, 99, direction)
			
			used = False
			For Local ply:t_player = EachIn Self.team.players
				If (ply <> Self)
					If (ply.number = Self.number)
						used = True
					EndIf
				EndIf
			Next
		Until Not used
		
	End Method

	Method update_price()
		
		''goalkeeper: price is not based on skills
		If (Self.role = PR.GOALKEEPER)
			Return
		EndIf
		
		Self.price = Self.skill_passing +..
		             Self.skill_shooting +..
		             Self.skill_heading +..
		             Self.skill_tackling +..
		             Self.skill_control +..
		             Self.skill_speed +..
		             Self.skill_finishing
	End Method

	Method update_frame_distance()
		frame_distance = BALL_PREDICTION
		For Local f:Int = BALL_PREDICTION -1 To 0 Step -1
			If (dist(x, y, ball.prediction[f].x, ball.prediction[f].y) < Self.speed * f / REFRATE)
				frame_distance = f
			EndIf
		Next
	End Method
	
	Method draw_number()
	
		Local f0:Int = Self.number Mod 10
		Local f1:Int = (Self.number - f0) / 10 Mod 10 
		
		Local dx:Int = x +1
		Local dy:Int = y -40 -z
	
		Local w0:Int = 6 - 2*(f0=1)
		Local w1:Int = 6 - 2*(f1=1)
	
		If (f1 > 0)
			dx = dx - (w0 + 2 + w1)/2
			draw_sub_image_rect(img_number, dx, dy, 8, 10, f1 * 8, 0, 8, 10)
			dx = dx + w1 + 2
			draw_sub_image_rect(img_number, dx, dy, 8, 10, f0 * 8, 0, 8, 10)
		Else
			draw_sub_image_rect(img_number, dx-w0/2, dy, 8, 10, f0 * 8, 0, 8, 10)
		EndIf
	
	End Method
	
	Method get_possession()
		If ((Self.ball_distance <= 8) ..
			And dist(Self.x0, Self.y0, ball.x0, ball.y0) > 8 ..
			And (ball.z < (PLAYER_H + BALL_R)))
			
			Local smoothed_ball_v:Float = ball.v * 0.5
			Local ball_vec:t_vec2d = t_vec2d.Create(smoothed_ball_v, ball.a, True)
			Local player_vec:t_vec2d = t_vec2d.Create(Self.v, Self.a, True)
			
			Local difference_vec:t_vec2d = player_vec.diff(ball_vec)
			
			If (difference_vec.v < 220 + 7*Self.skill_control)
				ball.set_owner(Self)
				ball.x = Self.x +(BALL_R -1) * Cos(Self.a)
				ball.y = Self.y +(BALL_R -1) * Sin(Self.a)
				ball.v = Self.v
				ball.a = Self.a
			Else
				ball.set_owner(Self)
				ball.set_owner(Null)
				ball.collision_player(Self, 0.5*difference_vec.v)
			EndIf
			
			ball.vz = ball.vz / (2 + Self.skill_control)
		EndIf
	End Method
	
	Method keeper_collision:Int()
		
		Local collision_type:Int = Self.CT_NONE
		
		If (Abs(ball.y0 -y) >= 1 And Abs(ball.y -y) < 1)
			
			''collision detection
			
			''keeper frame
			Local fmx:Int = round(fmx)
			Local fmy:Int = Abs(Floor(fmy))
			
			''offset
			Local offx:Int = +24 +round(ball.x -x)
			Local offy:Int = +34 +round(-ball.z -BALL_R +z)
			
			''verify if the pixel is inside the frame
			If (offx < 0) Or (offx => 50) Then Return 0
			If (offy < 0) Or (offy => 50) Then Return 0
			
			Local det_x:Int = round(50 * (fmx Mod 24) + offx)
			Local det_y:Int = round(50 * (fmy Mod 24) + offy)
			
			Local rgb:Int
			rgb = ReadPixel(img_keeper_cd,det_x,det_y) & $FFFFFF
			
			Select rgb
				Case $C0C000
					collision_type = Self.CT_REBOUND
					
				Case $C00000
					collision_type = Self.CT_CATCH
					
				Case $0000C0
					If (ball.v > 180)
						collision_type = Self.CT_DEFLECT
					Else
						collision_type = Self.CT_CATCH
					EndIf
					
			End Select
			
			Select collision_type
				Case Self.CT_REBOUND
					If (ball.v > 180)
						If (Not ChannelPlaying(channel.deflect))
							SetChannelVolume(channel.deflect, 0.1 * location_settings.sound_vol * 0.5)
							PlaySound(sound.deflect, channel.deflect)
						EndIf
					EndIf
					ball.v = ball.v / 4
					ball.a = (- ball.a) Mod 360
					ball.s = -ball.s
					ball.set_owner(Self, False)
					ball.set_owner(Null)
					
				Case Self.CT_CATCH
					If (ball.v > 180)
						If (Not ChannelPlaying(channel.hold))
							SetChannelVolume(channel.hold, 0.1 * location_settings.sound_vol * 0.5)
							PlaySound(sound.hold, channel.hold)
						EndIf
					EndIf
					ball.v = 0
					ball.vz = 0
					ball.s = 0
					ball.set_owner(Self)
					ball.set_holder(Self)
					
				Case Self.CT_DEFLECT
					If (ball.v > 180)
						If (Not ChannelPlaying(channel.deflect))
							SetChannelVolume(channel.deflect, 0.1 * location_settings.sound_vol * 0.5)
							PlaySound(sound.deflect, channel.deflect)
						EndIf
					EndIf
					''real ball x-y angle (when spinned, it is different from ball.a)
					Local ball_axy:Float = ATan2(ball.y -ball.y0, ball.x -ball.x0)
					
					Local ball_vx:Float = ball.v * Cos(ball_axy)
					Local ball_vy:Float = ball.v * Sin(ball_axy)
					
					ball_vx = Sgn(ball_vx)*(0.5*Abs(ball_vx)+0.25*Abs(ball_vy))+v*Cos(a)
					ball_vy = 0.7*ball_vy
					
					ball.v = Sqr(ball_vx^2+ball_vy^2)
					ball.a = ATan2(ball_vy, ball_vx)
					ball.vz = 1.5*vz
					
					ball.set_owner(Self, False)
					ball.set_owner(Null)
					
			End Select
			
		EndIf
		
		Return (collision_type = Self.CT_CATCH)
		
	End Method
	
	Method hold_ball(off_x:Int, off_z:Int)

		If ((ball.holder = Self))
			
			ball.x = x + off_x
			ball.y = y
			ball.z = z + off_z
			ball.v = v
			ball.vz = vz
		
		EndIf

	End Method
	
	Method save(frame:Int)
	
		data[frame].x = round(Self.x)
		data[frame].y = round(Self.y)
		data[frame].z = round(Self.z)
		data[frame].fmx = round(Self.fmx)
		data[frame].fmy = Abs(Floor(Self.fmy))
		data[frame].is_visible = Self.is_visible
	
	End Method
	
	''sets and orders skills in function of the role of the player
	Method order_skills()
		
		Self.skills = ""
		
		If (Self.role = PR.GOALKEEPER)
			Return
		EndIf
		
		''set starting order
		Local skill:Int[7]
		
		If (Self.role = PR.RIGHT_BACK) Or (Self.role = PR.LEFT_BACK)
			skill[0] = Self.skill_tackling
			skill[1] = Self.skill_speed
			skill[2] = Self.skill_passing
			skill[3] = Self.skill_shooting
			skill[4] = Self.skill_heading
			skill[5] = Self.skill_control
			skill[6] = Self.skill_finishing
			Self.skills = "TSPVHCF"
		
		ElseIf (Self.role = PR.DEFENDER)
			skill[0] = Self.skill_tackling
			skill[1] = Self.skill_heading
			skill[2] = Self.skill_passing
			skill[3] = Self.skill_speed
			skill[4] = Self.skill_shooting
			skill[5] = Self.skill_control
			skill[6] = Self.skill_finishing
			Self.skills = "THPSVCF"
		
		ElseIf (Self.role = PR.RIGHT_WINGER) Or (Self.role = PR.LEFT_WINGER)
			skill[0] = Self.skill_control
			skill[1] = Self.skill_speed
			skill[2] = Self.skill_passing
			skill[3] = Self.skill_tackling
			skill[4] = Self.skill_heading
			skill[5] = Self.skill_finishing
			skill[6] = Self.skill_shooting
			Self.skills = "CSPTHFV"
		
		ElseIf (Self.role = PR.MIDFIELDER)
			skill[0] = Self.skill_passing
			skill[1] = Self.skill_tackling
			skill[2] = Self.skill_control
			skill[3] = Self.skill_heading
			skill[4] = Self.skill_shooting
			skill[5] = Self.skill_speed
			skill[6] = Self.skill_finishing
			Self.skills = "PTCHVSF"
		
		ElseIf (Self.role = PR.ATTACKER)
			skill[0] = Self.skill_heading
			skill[1] = Self.skill_finishing
			skill[2] = Self.skill_speed
			skill[3] = Self.skill_shooting
			skill[4] = Self.skill_control
			skill[5] = Self.skill_passing
			skill[6] = Self.skill_tackling
			Self.skills = "HFSVCPT"
		EndIf
		
		Local ordered:Int, int_tmp:Int, chr_tmp1:String, chr_tmp2:String
		ordered = False
		While (Not ordered)
			ordered = True
			For Local i:Int = 0 To 5
				If skill[i] < skill[i+1]
					int_tmp = skill[i]
					skill[i] = skill[i+1]
					skill[i+1] = int_tmp
					
					chr_tmp1 = Mid(Self.skills, i+1, 1)
					chr_tmp2 = Mid(Self.skills, i+2, 1)
					Self.skills = Left(Self.skills, i) + chr_tmp2 + chr_tmp1 + Right(Self.skills, 7-(i+2))
					
					ordered = False
				EndIf
			Next
		Wend
		
	End Method
	
	''i = skill position, 1 to 3
	Method get_best_skill:String(i:Int)
		Local c:Int = Asc(Mid(Self.skills, i, 1))
		Select c
			Case 84 '"T"
				Return Left(dictionary.gettext("T // 1 letter code for: TACKLING"), 1)
			Case 72 '"H"
				Return Left(dictionary.gettext("H // 1 letter code for: HEADING"), 1)
			Case 80 '"P"
				Return Left(dictionary.gettext("P // 1 letter code for: PASSING"), 1)
			Case 83 '"S"
				Return Left(dictionary.gettext("S // 1 letter code for: SPEED"), 1)
			Case 67 '"C"
				Return Left(dictionary.gettext("C // 1 letter code for: BALL CONTROL"), 1)
			Case 70 '"F "
				Return Left(dictionary.gettext("F // 1 letter code for: FINISHING"), 1)
			Case 86 '"V"
				Return Left(dictionary.gettext("V // 1 letter code for: SHOOTING"), 1)
			Case -1
				''goalkeeper has no skills
				Return ""
		End Select
	End Method
	
	Method load_image_by_skin()
		If (Self.role = PR.GOALKEEPER)
			If (img_keeper[Self.team.index, Self.skin_color] = Null)
				img_keeper[Self.team.index, Self.skin_color] = Self.load_image()
			EndIf
			Self.img = img_keeper[Self.team.index, Self.skin_color]
		Else
			If (img_player[Self.team.index, Self.skin_color] = Null)
				img_player[Self.team.index, Self.skin_color] = Self.load_image()
			EndIf
			Self.img = img_player[Self.team.index, Self.skin_color]
		EndIf
	End Method
	
	Method load_image:TImage()
		Local img:TImage
		
		Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
		Self.team.kit_at_index(Self.team.kit).add_kit_colors(rgb_pairs)
		Self.add_skin_colors(rgb_pairs)
		
		If (Self.role = PR.GOALKEEPER)
			img = load_and_edit_png("images/player/keeper.png", rgb_pairs, MASKEDIMAGE)
		Else
			img = load_and_edit_png(Self.team.get_image_path("player"), rgb_pairs, MASKEDIMAGE)
		EndIf
		
		Return img
	End Method
	
	Method load_hair()
		Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
		Self.add_hair_colors(rgb_pairs)
		
		Self.hair = load_and_edit_png("images/player/hair/" + hair_cut[Self.hair_type] + ".png", rgb_pairs, MASKEDIMAGE)
	End Method
		
	Method create_face()
		
		Local filename:String = "images/player/menu/" + hair_cut[Self.hair_type] + ".png"
		Self.face = LoadImage(filename, MASKEDIMAGE|DYNAMICIMAGE)
		If (Self.face = Null) Then RuntimeError(" cannot find image: " + filename)
		
		''hair color
		If (hair_cut[Self.hair_type] = 201) ''shaved
			Local sc:Int
			sc = shaved_color[shaved_table[Self.hair_color, Self.skin_color], 0]
			replace_image_color(Self.face, $FF907130, set_alpha(sc, $FF))
	
			sc = shaved_color[shaved_table[Self.hair_color, Self.skin_color], 1]
			replace_image_color(Self.face, $FF715930, set_alpha(sc, $FF))
		Else
			replace_image_color(Self.face, $FF907130, set_alpha(hair_colors[Self.hair_color, 0], $FF))
			replace_image_color(Self.face, $FF715930, set_alpha(hair_colors[Self.hair_color, 1], $FF))
			replace_image_color(Self.face, $FF514030, set_alpha(hair_colors[Self.hair_color, 2], $FF))
		EndIf
	
		''skin color
		replace_image_color(Self.face, $FFFF6300, set_alpha(skin_colors[Self.skin_color, 0], $FF))
		replace_image_color(Self.face, $FFB54200, set_alpha(skin_colors[Self.skin_color, 1], $FF))
		replace_image_color(Self.face, $FF631800, set_alpha(skin_colors[Self.skin_color, 2], $FF))
	
		''shadow
		Self.facesh = copy_image(Self.face)
		image2shadow(Self.facesh, $242424)
	
	End Method
	
	Method add_skin_colors(rgb_pairs:t_color_replacement_list)
		
		rgb_pairs.add($FF6300, skin_colors[Self.skin_color, 0])
		rgb_pairs.add($B54200, skin_colors[Self.skin_color, 1])
		rgb_pairs.add($631800, skin_colors[Self.skin_color, 2])
		
	End Method
	
	Method add_hair_colors(rgb_pairs:t_color_replacement_list)
		''shaved
		If (hair_cut[Self.hair_type] = 201)
			rgb_pairs.add($907130, set_alpha(shaved_color[shaved_table[Self.hair_color, Self.skin_color], 0], $FF))
			rgb_pairs.add($715930, set_alpha(shaved_color[shaved_table[Self.hair_color, Self.skin_color], 1], $FF))
		''others
		Else
			rgb_pairs.add($907130, hair_colors[Self.hair_color, 0])
			rgb_pairs.add($715930, hair_colors[Self.hair_color, 1])
			rgb_pairs.add($514030, hair_colors[Self.hair_color, 2])
		EndIf
	End Method
	
	Method update:Int(limit:Int)
		
		''physical parameters
		''speeds are in pixel/s
		''TODO: change in function of time and stamina
		Self.speed = 130 + 4 * Self.skill_speed
		
		''store old values
		Self.x0 = Self.x
		Self.y0 = Self.y
		Self.z0 = Self.z
		
		''update position        
		Self.x :+ Self.v / SECOND * Cos(Self.a)
		Self.y :+ Self.v / SECOND * Sin(Self.a)
		Self.z :+ Self.vz / SECOND
		
		''gravity
		If (Self.z > 0)
			Self.vz :- GRAVITY
		EndIf
		
		''back to the ground
		If (Self.z < 0)
			Self.z = 0
			Self.vz = 0
		EndIf
		
		If (limit = True)
			Self.limit_inside_field()
		EndIf
		
		Self.ball_distance = dist(Self.x, Self.y, ball.x, ball.y)
		
		Return ((Self.v > 0) Or (Self.vz <> 0))
		
	End Method
	
	Method limit_inside_field()
		''left
		Self.x = Max(Self.x, -TOUCH_LINE -50)
		''right
		Self.x = Min(Self.x, +TOUCH_LINE +50)
		''top
		Self.y = Max(Self.y, -GOAL_LINE -50*(Abs(Self.x) > (POST_X + 10)))
		''bottom
		Self.y = Min(Self.y, +GOAL_LINE +50*(Abs(Self.x) > (POST_X + 10)))
	End Method
	
	Method target_distance:Float()
		Return dist(Self.tx, Self.ty, Self.x, Self.y)
	End Method
	
	Method target_angle:Float()
		Return ATan2(Self.ty -Self.y, Self.tx -Self.x)
	End Method
	
	Method watch_ball()
		Self.a = round((ATan2(Self.y -ball.y, Self.x -ball.x) +180) / 45.0) * 45.0
	End Method
	
	Method search_facing_player:Int(long_range:Int, in_action:Int = False)
		
		Local min_distance:Float = 0
		Local max_distance:Float = TOUCH_LINE / 2
		If (long_range)
			min_distance = TOUCH_LINE / 2
			max_distance = TOUCH_LINE
		EndIf
		
		Local max_angle:Float
		If(in_action)
			max_angle = 15.5 +Self.skill_passing
		Else
			max_angle = 22.5
		EndIf
		
		Local facing_delta:Float = max_distance * Sin(max_angle)
		
		Self.facing_player = Null
		Self.facing_angle = 0
		
		For Local i:Int = 0 To TEAM_SIZE -1
			Local ply:t_player = Self.team.lineup_at_index(i)
			If (ply = Self)
				Continue
			EndIf
			
			Local ply_distance:Float = dist(Self.x, Self.y, ply.x, ply.y)
			Local ply_angle:Float = ((ATan2(ply.y +5*Sin(ply.a) -Self.y, ply.x +5*Cos(ply.a) -Self.x) -Self.a + 540) Mod 360) -180
			Local ply_delta:Float = ply_distance * Sin(ply_angle)
			
			If (Abs(ply_angle) < max_angle And ply_distance > min_distance And ply_distance < max_distance And Abs(ply_delta) < Abs(facing_delta))
				Self.facing_player = ply
				Self.facing_angle = ply_angle
				facing_delta = ply_delta
			EndIf
		Next
		
        Return (Self.facing_player <> Null)
		
	End Method
	
End Type

Function compare_players_by_goals:Int(o1:Object, o2:Object)
	''by goals
	If t_player(o1).goals <> t_player(o2).goals
		Return t_player(o1).goals < t_player(o2).goals
	EndIf
	
	Local n1:String, n2:String
	If t_player(o1).surname <> ""
		n1 = t_player(o1).surname
	Else
		n1 = t_player(o1).name
	EndIf
	If t_player(o2).surname <> ""
		n2 = t_player(o2).surname
	Else
		n2 = t_player(o2).name
	EndIf

	''by names
	If n1 < n2
		Return -1
	Else
		Return n1 > n2
	EndIf
End Function


'coach
Type t_coach Extends t_sprite
	Field status:Int = CS_BENCH		'bench, up, speaking, calling substitution
	Field old_status:Int = CS_BENCH	'bench, up, speaking, calling substitution
	Field fmx:Int = 0
	Field fmy:Int = 0
	Field timer:Int	= 0

	Method update()
		If timer
			timer = timer -1
		EndIf

		Select status
			Case CS_BENCH
				fmx = 0; fmy = 0
			Case CS_STAND
				fmx = 0; fmy = 0

			'timed
			Case CS_DOWN
				fmx = 3; fmy = 0
				If timer = 0 Then status = CS_STAND
			Case CS_SPEAK
				fmx = 1 +((MilliSecs() Mod 400) > 200); fmy = 0
				If timer = 0 Then status = CS_STAND
			Case CS_CALL
				fmx = 4 +((MilliSecs() Mod 400) > 200); fmy = 0
				If timer = 0 Then status = CS_STAND
		End Select
	End Method
	
	Method draw(frame:Int)
		draw_sub_image_rect(img, x -13, y -25, 29, 29, 29*fmx, 29*fmy, 29, 29)
	End Method

End Type

'kits
Type t_kit
	Field style:Int
	Field shirt1:Int
	Field shirt2:Int
	Field shorts:Int
	Field socks:Int
	
	''apply kit colors to images (both for players, kit preview and standard logos)
	Method render_image(image:TImage)
	
		'shirt
		replace_image_color(image, $FFE20020, set_alpha(kit_color[Self.shirt1, 0], $FF))
		replace_image_color(image, $FFBF001B, set_alpha(kit_color[Self.shirt1, 1], $FF))
		replace_image_color(image, $FF9C0016, set_alpha(kit_color[Self.shirt1, 2], $FF))
		replace_image_color(image, $FF790011, set_alpha(kit_color[Self.shirt1, 3], $FF))	
	
		replace_image_color(image, $FF01FFC6, set_alpha(kit_color[Self.shirt2, 0], $FF))
		replace_image_color(image, $FF00C79B, set_alpha(kit_color[Self.shirt2, 1], $FF))
		replace_image_color(image, $FF008B6C, set_alpha(kit_color[Self.shirt2, 2], $FF))
		replace_image_color(image, $FF006A52, set_alpha(kit_color[Self.shirt2, 3], $FF))
	
		'short color
		replace_image_color(image, $FFF6FF00, set_alpha(kit_color[Self.shorts, 0], $FF))
		replace_image_color(image, $FFCDD400, set_alpha(kit_color[Self.shorts, 1], $FF))
		replace_image_color(image, $FFA3A900, set_alpha(kit_color[Self.shorts, 2], $FF))
		replace_image_color(image, $FF7A7E00, set_alpha(kit_color[Self.shorts, 3], $FF))
		
		'socks color
		replace_image_color(image, $FF0097EE, set_alpha(kit_color[Self.socks, 0], $FF))
		replace_image_color(image, $FF0088D6, set_alpha(kit_color[Self.socks, 1], $FF))
		replace_image_color(image, $FF0079BF, set_alpha(kit_color[Self.socks, 2], $FF))
		replace_image_color(image, $FF006AA7, set_alpha(kit_color[Self.socks, 3], $FF))
	
	End Method
	
	Method add_kit_colors(rgb_pairs:t_color_replacement_list)
		
		''shirt1
		rgb_pairs.add($E20020, kit_color[Self.shirt1, 0])
		rgb_pairs.add($BF001B, kit_color[Self.shirt1, 1])
		rgb_pairs.add($9C0016, kit_color[Self.shirt1, 2])
		rgb_pairs.add($790011, kit_color[Self.shirt1, 3])
		
		''shirt2
		rgb_pairs.add($01FFC6, kit_color[Self.shirt2, 0])
		rgb_pairs.add($00C79B, kit_color[Self.shirt2, 1])
		rgb_pairs.add($008B6C, kit_color[Self.shirt2, 2])
		rgb_pairs.add($006A52, kit_color[Self.shirt2, 3])
		
		''shorts
		rgb_pairs.add($F6FF00, kit_color[Self.shorts, 0])
		rgb_pairs.add($CDD400, kit_color[Self.shorts, 1])
		rgb_pairs.add($A3A900, kit_color[Self.shorts, 2])
		rgb_pairs.add($7A7E00, kit_color[Self.shorts, 3])
		
		''socks
		rgb_pairs.add($0097EE, kit_color[Self.socks, 0])
		rgb_pairs.add($0088D6, kit_color[Self.socks, 1])
		rgb_pairs.add($0079BF, kit_color[Self.socks, 2])
		rgb_pairs.add($006AA7, kit_color[Self.socks, 3])
			
	End Method
	
End Type	

Type t_match_status
	
	''periods
	Const UNDEFINED:Int         = 0
	Const FIRST_HALF:Int        = 1
	Const SECOND_HALF:Int       = 2
	Const FIRST_EXTRA_TIME:Int  = 3
	Const SECOND_EXTRA_TIME:Int = 4
	
	Field timer:Int          ''match timer in millisecs
	Field length:Int         ''length of the match in millisecs
	Field period:Int
	Field coin_toss:Int      ''0 = home begins, 1 = away begins
	Field kick_off_team:Int
	Field throw_in_x:Int
	Field throw_in_y:Int
	
	Method New()
		Self.timer  = 0
		Self.length = 0
		Self.period = Self.UNDEFINED
	End Method
	
	Method get_minute:Int()
		
		''virtual minutes : 90 = Self.timer : Self.length
		Local minute:Int = Self.timer * 90 / Self.length
		
		Select Self.period
			
			Case Self.FIRST_HALF
				minute = Min(minute, 45)
				
			Case Self.SECOND_HALF
				minute = Min(minute, 90)
				
			Case Self.FIRST_EXTRA_TIME
				minute = Min(minute, 105)
				
			Case Self.SECOND_EXTRA_TIME
				minute = Min(minute, 120)
				
		End Select
		
		Return minute
		
	End Method
	
End Type

Type t_ball Extends t_sprite
	'motion & graphics
	Field x:Float			'x position
	Field y:Float			'y position
	Field z:Float			'z position
	Field zmax:Float		'** debug only
	Field x0:Float			'old x position
	Field y0:Float			'old y position
	Field z0:Float			'old z position
	Field v:Float			'horiz. speed
	Field vz:Float			'vert.	speed
	Field a:Float			'horiz. angle
	Field s:Float			'spin
	Field f:Float			'frame index
	Field side:t_vec2d		'side.x (-1:left, 1:right) side.y (-1=up, 1=down)
	Field prediction:t_vec3d[BALL_PREDICTION]
	Field data:t_data[VSIZE]
	
	'gameplay
	Field zone_x:Int		'ball zone
	Field zone_y:Int
	Field mx:Float
	Field my:Float
	Field zone_x_next:Int	'next ball zone
	Field zone_y_next:Int		
	
	Field owner:t_player
	Field owner_last:t_player
	Field goal_owner:t_player
	Field holder:t_player
	
	Method New()

		For Local i:Int = 0 To VSIZE-1
			Self.data[i] = New t_data
		Next
		
		side = New t_vec2d
		
		For Local frm:Int = 0 To BALL_PREDICTION-1 Step 1
			prediction[frm] = New t_vec3d
		Next

	End Method

	Method set_position(x0:Int, y0:Int, z0:Int = 0)
		Self.x = x0
		Self.y = y0
		Self.z = z0
		Self.v = 0
		Self.vz = 0
		Self.s = 0
	End Method
	
	Method draw(frame:Int)
		draw_sub_image_rect(img, data[frame].x +1-BALL_R, data[frame].y -data[frame].z -2 -BALL_R, 8, 8, 8*data[frame].fmx, 0, 8, 8)
	End Method
	
	Method draw_shadow(frame:Int)
		draw_sub_image_rect(Self.img, Self.data[frame].x -1 +0.65 * Self.data[frame].z, Self.data[frame].y -3 +0.46 * Self.data[frame].z, 8, 8, 32, 0, 8, 8)
		If (location_settings.time = TI.NIGHT)
			draw_sub_image_rect(Self.img, Self.data[frame].x -5 -0.65 * Self.data[frame].z, Self.data[frame].y -3 +0.46 * Self.data[frame].z, 8, 8, 32, 0, 8, 8)
			draw_sub_image_rect(Self.img, Self.data[frame].x -5 -0.65 * Self.data[frame].z, Self.data[frame].y -7 -0.46 * Self.data[frame].z, 8, 8, 32, 0, 8, 8)
			draw_sub_image_rect(Self.img, Self.data[frame].x -1 +0.65 * Self.data[frame].z, Self.data[frame].y -7 -0.46 * Self.data[frame].z, 8, 8, 32, 0, 8, 8)
		EndIf
	End Method
	
	Method draw_shadow_on_crossbar(frame:Int)
		SetBlend(ALPHABLEND)
		location_settings.set_alpha()

		If (Self.data[frame].z > CROSSBAR_H)
	
			Local shadow_x:Int = Self.data[frame].x -1 +0.65*(Self.data[frame].z-CROSSBAR_H)
			Local shadow_y:Int = Self.data[frame].y -3 +0.46*(Self.data[frame].z-CROSSBAR_H) -CROSSBAR_H
	
			If is_in(shadow_x, -POST_X -POST_R -2*BALL_R, POST_X +POST_R)
	
				Local dest_rect_x:Int = Max(-POST_X -POST_R, shadow_x)
				Local dest_rect_y:Int = Max(Self.side.y*GOAL_LINE -CROSSBAR_H -3, shadow_y)
				Local dest_rect_w:Int = Min(8, POST_X +POST_R -shadow_x)
				Local dest_rect_h:Int = Max(0, (Min(shadow_y +8, Self.side.y*GOAL_LINE -CROSSBAR_H +1) -dest_rect_y))
				
				DrawSubImageRect(Self.img, dest_rect_x, dest_rect_y, dest_rect_w, dest_rect_h, dest_rect_x-(shadow_x -32), dest_rect_y -shadow_y, dest_rect_w, dest_rect_h)
			EndIf
		EndIf
		
		SetBlend(MASKBLEND)
		SetAlpha(1)
	End Method
	
	Method get_y:Int(frame:Int)
		Return data[frame].y
	End Method

	Method update()
		
		If (Self.owner <> Null)
			If ((Self.owner.ball_distance > 13) Or (Self.z > (PLAYER_H + BALL_R)))
				Self.set_owner(Null)
			EndIf
		EndIf
		
		''store old values
		Self.x0 = Self.x
		Self.y0 = Self.y
		Self.z0 = Self.z
		
        ''side
        Self.side.x = Sgn(Self.x)
        Self.side.y = Sgn(Self.y)
		
		Self.update_physics()
		
		''animation
		Self.f = (Self.f + 4 + Self.v / 2500) Mod 4
		
	End Method	
	
	Method update_physics(simulation:Int = False)
		
		''angle & spin
        Self.a :+ 4.0 / SECOND * Self.s
		Self.s :* 1 - 2.0 / SECOND
		
		''position & speed
		Self.x :+ Self.v / SECOND * Cos(Self.a)
		Self.y :+ Self.v / SECOND * Sin(Self.a)
		
		If (Self.z < 1)
			''grass friction
			Self.v :- location_settings.grass.friction / SECOND * Sqr(Abs(Self.v))
		Else
			''air friction
			Self.v :* 1 - 0.3 / SECOND
		EndIf
		
		''wind
		If (Self.v > 0 And Self.z > 0)
			Local ball_va:t_vec2d = t_vec2d.Create(Self.v, Self.a, True)
			Local wind_va:t_vec2d = t_vec2d.Create(0.025 * Log10(1 + Self.z) * location_settings.wind.speed, 45 * location_settings.wind.direction, True)
			Local result_va:t_vec2d = ball_va.sum(wind_va)
			Self.v = result_va.v
			Self.a = result_va.a
		EndIf
		
		''back to zero!
		If (Self.v <= 0)
			Self.v = 0
			Self.s = 0
		EndIf
		
		''z position
		Self.z :+ Self.vz / SECOND
		
		''z speed
		If (Self.z > 0)
			Self.vz :- GRAVITY
			
			''air friction
			Self.vz :* 1 - 0.3 / SECOND
		EndIf
		
		''bounce
		If (Self.z < 0 And Self.vz < 0)
			Self.z = 0
			If (Self.vz > -50)
				Self.vz = 0
			Else
				''bounce
				Self.v :* (1 + Self.vz / 1400.0)
				Self.vz :* -0.01 * location_settings.grass.bounce
			EndIf
			If (game_settings.sound_enabled And Not simulation)
				SetChannelVolume(channel.bounce, Min(2 * Self.vz / SECOND * 0.1 * location_settings.sound_vol, 1))
				PlaySound(sound.bounce, channel.bounce)
			EndIf
		EndIf
		
	End Method
	
	Method update_prediction()
		
		'save starting values
		Local old_x:Float = x
		Local old_y:Float = y
		Local old_z:Float = z
		Local old_v:Float = v
		Local old_vz:Float = vz
		Local old_a:Float = a
		Local old_s:Float = s
		
		For Local frm:Int = 0 To BALL_PREDICTION-1 Step 1
			prediction[frm].x = round(x)
			prediction[frm].y = round(y)
			prediction[frm].z = round(z)
			For Local subframe:Int = 0 To SUBFRAMES-1 Step 1
				update_physics(True)
			Next
		Next
		
		'restore starting values
		x = old_x
		y = old_y
		z = old_z
		v = old_v
		vz = old_vz
		a = old_a
		s = old_s
	
	End Method


	Method in_field_keep()
	
		'left
		If x <= FIELD_XMIN
			x = FIELD_XMIN
			v = 0.5 * v
			a = (180 - a) Mod 360
			s = -s 
		EndIf
		
		'right
		If x => FIELD_XMAX 
			x = FIELD_XMAX
			v = 0.5 * v
			a = (180 - a) Mod 360
			s = -s 
		EndIf
		
		'top
		If y <= FIELD_YMIN

			y = FIELD_YMIN
			v = 0.5 * v
			a = (- a) Mod 360
			s = -s 
		EndIf
		
		'bottom	
		If y => FIELD_YMAX
			y = FIELD_YMAX
			v = 0.5 * v
			a = (- a) Mod 360
			s = -s 
		EndIf
		
	End Method
	
	Method collision_goal:Int()
		
		If (Self.side.y = 0)
			Return False
		EndIf
		
		Local hit:Int = False
		
		'"sette" **
		If (Self.side.y * Self.y < GOAL_LINE) And ..
			(dist(Self.y0, Self.z0, Self.side.y * GOAL_LINE, CROSSBAR_H) > 6) And ..
			(dist(Self.y, Self.z, Self.side.y * GOAL_LINE, CROSSBAR_H) <= 6) And ..
			((dist(Self.x, Self.y, -POST_X, Self.side.y * (GOAL_LINE +1)) <= 6) Or (dist(Self.x, Self.y, POST_X, Self.side.y * (GOAL_LINE+1)) <= 6))
			
			''real ball x-y angle (when spinned, it is different from ball.a)
			Local ball_axy:Float = ATan2(Self.y -Self.y0, Self.x -Self.x0)
			
			Self.v = 0.5 * Self.v
			Self.a = (-ball_axy +360) Mod 360
			Self.s = 0
			Self.x = Self.x0
			Self.y = Self.y0
			
			hit = True
			
		''crossbar
		Else If (dist(Self.y0, Self.z0, Self.side.y * GOAL_LINE, CROSSBAR_H) > 5) And ..
			(dist(Self.y, Self.z, Self.side.y * GOAL_LINE, CROSSBAR_H) <= 5) And ..
			(-(POST_X +POST_R) < Self.x And Self.x < (POST_X +POST_R))
			
			''cartesian coordinates
			''real ball x-y angle (when spinned, it is different from ball.a)
			Local ball_axy:Float = ATan2(Self.y -Self.y0, Self.x -Self.x0)
			
			Local ball_vx:Float = Self.v * Cos(ball_axy)
			Local ball_vy:Float = Self.v * Sin(ball_axy)
			
			''collision y-z angle
			Local angle:Float = ATan2(Self.z -CROSSBAR_H, Self.y -Self.side.y * GOAL_LINE)
			
			''ball y-z speed and angle
			Local ball_vyz:Float = Sqr(ball_vy^2 +Self.vz^2)
			Local ball_ayz:Float = ATan2(Self.vz, ball_vy)
			
			''new angle
			ball_ayz = (2 * angle -ball_ayz +180)
			
			''new y-z speeds
			ball_vy = 0.5 * ball_vyz * Cos(ball_ayz)
			Self.vz = 0.5 * ball_vyz * Sin(ball_ayz)
			
			''new speed, angle & spin
			Self.v = Sqr(ball_vx^2 +ball_vy^2)
			Self.a = ATan2(ball_vy, ball_vx)
			Self.s = 0
			Self.y = Self.y0
			Self.z = Self.z0
			
			hit = True
			
		''posts
		Else If (dist(Self.x0, Self.y0, Self.side.x * POST_X, Self.side.y * (GOAL_LINE +1)) > 5) And ..
			(dist(Self.x, Self.y, Self.side.x * POST_X, Self.side.y * (GOAL_LINE +1)) <= 5) And ..
			(Self.z <= (CROSSBAR_H +POST_R))
			
			''real ball x-y angle (when spinned, it is different from ball.a)
			Local ball_axy:Float = ATan2(Self.y -Self.y0, Self.x -Self.x0)
			
			Local angle:Float = ATan2(Self.y -Self.side.y * (GOAL_LINE+1), Self.x -Self.side.x * POST_X)
			
			''new speed, angle & spin
			Self.v = 0.5 * Self.v
			Self.a = (2 * angle -ball_axy +180) Mod 360
			Self.s = 0
			Self.x = Self.x0
			Self.y = Self.y0
			
			hit = True
			
		EndIf
		
		''sound
		If (hit And game_settings.sound_enabled)
			If (Not ChannelPlaying(channel.post))
				SetChannelVolume(channel.post, 0.1 * location_settings.sound_vol)
				PlaySound(sound.post, channel.post)
			EndIf
		EndIf
		
		Return hit
		
	End Method
	
	Method update_zone(param_x:Int, param_y:Int, param_v:Float, param_a:Float)
		
		''zone
		Self.zone_x = round(param_x / BALL_ZONE_DX)
		Self.zone_x = bound(Self.zone_x, -2, +2)
		
		Self.zone_y = round(param_y / BALL_ZONE_DY)
		Self.zone_y = bound(Self.zone_y, -3, +3)
		
		''mx / my
		Self.mx = (((param_x Mod BALL_ZONE_DX) / BALL_ZONE_DX + 1.5) Mod 1 ) -0.5
		Self.my = (((param_y Mod BALL_ZONE_DY) / BALL_ZONE_DY + 1.5) Mod 1 ) -0.5
		
		''zone_next
		If (param_v > 0)
			Self.zone_x_next = Self.zone_x + round(Cos(param_a))
			Self.zone_x_next = bound(Self.zone_x_next, -2, +2)
			
			Self.zone_y_next = Self.zone_y + round(Sin(param_a))
			Self.zone_y_next = bound(Self.zone_y_next, -3, +3)
		EndIf
		
	End Method
	
	Method collision_flagposts()
		
		If (Self.side.x = 0) Or (Self.side.y = 0)
			Return
		EndIf
		
		If ((dist(Self.x, Self.y, Self.side.x * TOUCH_LINE, Self.side.y * GOAL_LINE) <= 5) And (Self.z <= FLAGPOST_H))
			''real ball x-y angle (when spinned, it is different from ball.a)
			Local ball_axy:Float = ATan2(Self.y -Self.y0, Self.x -Self.x0)
			
			Local angle:Float = ATan2(Self.y -Self.side.y * GOAL_LINE, Self.x -(Self.side.x * TOUCH_LINE))
			Self.v = 0.3 * Self.v
			Self.a = (2 * angle -ball_axy +180) Mod 360
			Self.s = 0
			Self.x = Self.x0
			Self.y = Self.y0
			
			If (game_settings.sound_enabled)
				If (Not ChannelPlaying(channel.post))
					SetChannelVolume(channel.post, 0.1 * location_settings.sound_vol * 0.5)
					PlaySound(sound.post, channel.post)
				EndIf
			EndIf
		EndIf
		
	End Method
	
	Method collision_net()
		
		Local sfx:Int
		
		If (side.y * y > GOAL_LINE)
			If (hypo(1.6*(y - side.y * GOAL_LINE), z) => CROSSBAR_H) And (hypo((1.6*(y0 - side.y * GOAL_LINE)), z0) < CROSSBAR_H)
				If (v > 200)
					sfx = True
				EndIf
				v = v/10
				vz = 0
				a = ATan2(-Sin(a)/4 , Cos(a))
				s = 0 
			EndIf

			'left/right
			If (x0 * side.x < POST_X) And (x * side.x => POST_X)
				If (v > 200)
					sfx = True
				EndIf
				v = v/10
				vz = 0
				a = ATan2(Sin(a), -Cos(a)/4)
				s = 0 
			EndIf

			If (sfx And game_settings.sound_enabled)
				If (Not ChannelPlaying(channel.net))
					SetChannelVolume(channel.net, 0.1 * location_settings.sound_vol * 0.5)
					PlaySound(sound.net, channel.net)
				EndIf
			EndIf
			
			''security' control **
'			If (hypo(1.6*(y - side.y*GOAL_LINE), z) > CROSSBAR_H+3)
'				v = 0	
'				z = 0
'				Print "back of the net detection error"				
'			EndIf
'			If x * side.x > (POST_X+1)
'				v = 0
'				z = 0
'				Print "left/right of the net detection error"				
'			EndIf
		EndIf
		
	End Method


	Method collision_net_out()

		'back-top
		If (dist(y0, z0, side.y * GOAL_LINE, 0) <= CROSSBAR_H) And ( -POST_X < x And x < POST_X)
			Local ball_vx:Float = v*Cos(a)	'cartesian coord.
			Local ball_vy:Float = v*Sin(a)
				
			Local angle:Float = ATan2 ( z, y - side.y * GOAL_LINE) ' y-z angle
			Local ball_vyz:Float = Sqr(ball_vy^2+vz^2) 'y-z speed
			Local ball_ayz:Float = ATan2 ( vz, - ball_vy) 'y-z angle
			
			ball_vyz = ball_vyz/10 'net friction
		
			ball_ayz = (2 * angle - ball_ayz + 180) Mod 360 'new angle
			ball_vy = ball_vyz * Cos(angle)	'new y-z speeds
			vz = ball_vyz * Sin(angle)
			
			v = Sqr(ball_vx^2+ball_vy^2) 'back to polar coord.
			a = ATan2(ball_vy, ball_vx)
			s = -s 
		
		EndIf
		
		'left/right
		If (x0 * side.x > POST_X) And (x * side.x <= POST_X)  And z < CROSSBAR_H + BALL_R
			If is_in( side.y * y, (GOAL_LINE+BALL_R), (GOAL_LINE + GOAL_DEPTH + BALL_R))
				v = Sqr(v)
				a = ATan2(Sin(a), -Cos(a)/4)
			EndIf		
		EndIf

	End Method

	'COLLISION - JUMPERS
	Method collision_jumpers()
	
		If dist(x, y, side.x * JUMPER_X, side.y * JUMPER_Y) <=5 And (z <= JUMPER_H)
			'real ball x-y angle (when spinned, it is different from ball.a)
			Local ball_axy:Float = ATan2(y - y0, x - x0)

			Local angle:Float = ATan2 ( y - side.y * JUMPER_Y, x - (side.x * JUMPER_X))
			v = 0.3 * v
			a = (2 * angle - ball_axy + 180 ) Mod 360 'new angle
			s = 0 '-ball.s 
			x = x0
			y = y0
			If (game_settings.sound_enabled)
				If (Not ChannelPlaying(channel.bounce))
					SetChannelVolume(channel.bounce, 0.1 * location_settings.sound_vol * 0.5)
					PlaySound(sound.bounce, channel.bounce)
				EndIf
			EndIf
		EndIf
	
	End Method

	Method collision_player(ply:t_player, new_v:Float)
		
		''real ball x-y angle (when spinned, it is different from ball.a)
		Local ball_axy:Float = ATan2(Self.y - Self.y0, Self.x - Self.x0)
		
		Local angle:Float = ATan2(Self.y -ply.y, Self.x -ply.x)
		Self.v = new_v
		Self.a = (2 * angle - ball_axy + 180) Mod 360
		Self.s = 0
		Self.x = x0
		Self.y = y0
		
	End Method
	
	Method set_owner(new_owner:t_player, update_goal_owner:Int = True)
		Self.owner = new_owner
		If (new_owner <> Null)
			Self.owner_last = new_owner
			If (update_goal_owner)
				Self.goal_owner = new_owner
			EndIf
		EndIf
	End Method
	
	Method set_holder(p:t_player)
		Self.holder = p
	End Method
	
	Method save(frame:Int)
		data[frame].x = round(x)
		data[frame].y = round(y)
		data[frame].z = round(z)
		data[frame].fmx = Floor(f)
	End Method

End Type	

Type t_keeper_frame
	Field fmx:Int[4]    ''top-right, top-left, bottom-right, bottom-left
	Field fmy:Int
	Field offx:Int[4]	''top-right, top-left, bottom-right, bottom-left
	Field offz:Int
End Type

Type t_data
	Field x:Float
	Field y:Float
	Field z:Float
	Field fmx:Int
	Field fmy:Int
	Field is_visible:Int
End Type

''statistics
Type t_stats
	Field goal:Int
	Field poss:Int
	Field shot:Int
	Field cent:Int
	Field corn:Int
	Field foul:Int
	Field yell:Int
	Field reds:Int
	
	Method New()
		Self.goal = 0
		Self.poss = 1
		Self.shot = 0
		Self.cent = 0
		Self.corn = 0
		Self.foul = 0
		Self.yell = 0
		Self.reds = 0
	End Method
End Type

Type t_goal
	''typ constants
	Const NORMAL:Int   = 0
	Const OWN_GOAL:Int = 1
	Const PENALTY:Int  = 2
	
	Field player:t_player
	Field minute:Int
	Field typ:Int
End Type

'vec3d
Type t_vec3d
	Field x:Int
	Field y:Int
	Field z:Int
End Type

Type t_player_state Extends t_state
	Field player:t_player
	
	Method set_player(_player:t_player)
		Self.player = _player
	End Method

End Type

Type t_player_state_idle Extends t_player_state
	
	Function Create:t_player_state_idle(p:t_player)
		Local s:t_player_state_idle = New t_player_state_idle
		s.set_name("state_idle")
		s.set_player(p)
		Return s
	End Function
	
	Method entry_actions()
		Super.entry_actions()
		Self.player.v = 0
		Self.player.animation_stand_run()
	End Method
	
End Type

Type t_player_state_outside Extends t_player_state
	
	Method New()
		Self.set_name("state_outside")
	End Method
	
	Function Create:t_player_state_outside(p:t_player)
		Local s:t_player_state_outside = New t_player_state_outside
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Self.player.is_visible And Self.player.target_distance() > 1)
			Self.player.v = 200
			Self.player.a = Self.player.target_angle()
		Else
			Self.player.v = 0
			Self.player.set_is_visible(False)
		EndIf
		
		''animation
		Self.player.fmx = round(((Self.player.a + 360) Mod 360)/45) Mod 8
		Self.player.animation_stand_run()
		
	End Method
	
	Method exit_actions()
		Super.exit_actions()
		Self.player.set_is_visible(True)
	End Method
	
End Type

Type t_player_state_bench_sitting Extends t_player_state
	
	Method New()
		Self.set_name("state_bench_sitting")
	End Method
	
	Function Create:t_player_state_bench_sitting(p:t_player)
		Local s:t_player_state_bench_sitting = New t_player_state_bench_sitting
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Self.player.target_distance() > 1)
			Self.player.v = 200
			Self.player.a = Self.player.target_angle()
		Else
			Self.player.v = 0
			Self.player.a = 0
		EndIf
		
		Self.player.animation_stand_run()
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		
		Self.player.tx = BENCH_X
		If ((1 -2*Self.player.team.index) = match_settings.bench_side)
			Self.player.set_ty(BENCH_Y_UP +14*(Self.player.index -TEAM_SIZE) +46)
		Else
			Self.player.set_ty(BENCH_Y_DOWN +14*(Self.player.index -TEAM_SIZE) +46)
		EndIf
		Self.player.v = 0
		Self.player.a = 0
		Self.player.animation_stand_run()
	End Method
	
End Type

Type t_player_state_bench_standing Extends t_player_state
	
	Method New()
		Self.set_name("state_bench_standing")
	End Method
	
	Function Create:t_player_state_bench_standing(p:t_player)
		Local s:t_player_state_bench_standing = New t_player_state_bench_standing
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Self.player.target_distance() > 1)
			Self.player.v = 200
			Self.player.a = Self.player.target_angle()
		Else
			Self.player.v = 0
			Self.player.a = 0
		EndIf
		
		Self.player.animation_stand_run()
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		
		Self.player.tx = BENCH_X +12
		If ((1 -2*Self.player.team.index) = match_settings.bench_side)
			Self.player.set_ty(BENCH_Y_UP +14*(Self.player.index -TEAM_SIZE) +46)
		Else
			Self.player.set_ty(BENCH_Y_DOWN +14*(Self.player.index -TEAM_SIZE) +46)
		EndIf
	End Method
	
End Type

Type t_player_state_bench_out Extends t_player_state
	
	Method New()
		Self.set_name("state_bench_out")
	End Method
	
	Function Create:t_player_state_bench_out(p:t_player)
		Local s:t_player_state_bench_out = New t_player_state_bench_out
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Self.player.target_distance() > 1)
			Self.player.v = 200
			Self.player.a = Self.player.target_angle()
		Else
			Self.player.v = 0
			Self.player.a = 0
		EndIf
		
		Self.player.animation_stand_run()
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		
		Self.player.set_tx(BENCH_X +16)
		Self.player.set_ty(12 * (2 * Self.player.team.index -1) * match_settings.bench_side)
	End Method
	
End Type

Type t_player_state_photo Extends t_player_state
	
	Method New()
		Self.set_name("state_photo")
	End Method
	
	Function Create:t_player_state_photo(p:t_player)
		Local s:t_player_state_photo = New t_player_state_photo
		s.set_player(p)
		Return s
	End Function
	
	Method entry_actions()
		Super.entry_actions()
		
		If (Self.player.role = PR.GOALKEEPER)
			Self.player.set_fmx(2)
			Self.player.set_fmy(16)
		Else
			If (Self.player.index = 9)
				Self.player.set_fmx(2)
			Else
				Self.player.set_fmx(Self.player.index Mod 2)
			EndIf
			Self.player.set_fmy(14)
		EndIf
		
	End Method
	
End Type

Type t_player_state_stand_run Extends t_player_state
	
	Method New()
		Self.set_name("state_stand_run")
	End Method
	
	Function Create:t_player_state_stand_run(p:t_player)
		Local s:t_player_state_stand_run = New t_player_state_stand_run
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.player.get_possession()
		
		''ball control
		If ((ball.owner = Self.player) And (ball.z < PLAYER_H))
			''ball2player angle
			Local angle:Float = ATan2(ball.y -Self.player.y, ball.x -Self.player.x)
			
			''player - ball angle difference
			Local angle_diff:Float = Abs(((angle -player.a +540) Mod 360) -180)
			
			''if angle diff = 0 then push the ball
			If ((angle_diff <= 22.5) And (player.ball_distance < 11))
				If (Self.player.input_device.get_value())
					ball.v = Max(ball.v, (1 +0.03*(Self.player.ball_distance < 11)) * Self.player.v)
					ball.a = Self.player.a
				EndIf
				
			''if changing direction keep control only if ball if near
			Else If ((angle_diff <= 67.5) And (Self.player.ball_distance < 11)) Or (Self.player.ball_distance < 6)
				ball.x = Self.player.x + (1.06 -0.01 * player.skill_control) * Self.player.ball_distance * Cos(Self.player.a)
				ball.y = Self.player.y + (1.06 -0.01 * player.skill_control) * Self.player.ball_distance * Sin(Self.player.a)
			EndIf
		EndIf
		
		''movement
		If (Self.player.input_device.get_value())
			Self.player.v = Self.player.speed * (1 -0.1*(Self.player = ball.owner))
			Self.player.a = Self.player.input_device.get_angle()
		Else
			Self.player.v = 0
		EndIf
		
		Self.player.animation_stand_run()
		
	End Method
	
	Method check_conditions:t_state()
		
		If (Self.player.role = PR.GOALKEEPER And ..
			Self.player = Self.player.team.lineup_at_index(0) And ..
			Self.player.team.near1 <> Self.player And ..
			Self.player.input_device = Self.player.get_ai())
			Return Self.player.state_keeper_positioning
		EndIf
		
		''player fired
		If (Self.player.input_device.fire1_down())
			
			''if in possession then kick
			If (ball.owner = Self.player)
				If (Self.player.v > 0 And ball.z < 8)
					Self.player.kick_angle = Self.player.a
					Return Self.player.state_kick
				EndIf
			''else head or tackle
			ElseIf (Self.player.frame_distance < BALL_PREDICTION)
				If (ball.prediction[Self.player.frame_distance].z > 0.6*PLAYER_H)
					Return Self.player.state_head
				ElseIf (ball.prediction[Self.player.frame_distance].z < 6)
					If ((Self.player.v > 0) And (Self.player.ball_distance < 100))
						Return Self.player.state_tackle
					EndIf
				EndIf
			Else
				''release input device
				If (Self.player.team.uses_automatic_input_device())
					Self.player.set_input_device(Self.player.get_ai())
				EndIf
			EndIf
		EndIf
		
		Return Null
		
	End Method
	
End Type

Type t_player_state_kick Extends t_player_state
	
	Field is_passing:Int
	
	Const IP_UNKNOWN:Int = -1
	Const IP_FALSE:Int = 0
	Const IP_TRUE:Int = 1
	
	Method New()
		Self.set_name("state_kick")
	End Method
	
	Function Create:t_player_state_kick(p:t_player)
		Local s:t_player_state_kick = New t_player_state_kick
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Local angle:Float
		If (Self.player.input_device.get_value())
			angle = Self.player.input_device.get_angle()
		Else
			angle = Self.player.kick_angle
		EndIf
		Local angle_diff:Float = ((angle -Self.player.kick_angle +540) Mod 360) -180
		
		''detect passing
		If (Self.timer > 0.1 * SECOND And Self.is_passing = Self.IP_UNKNOWN)
			If (Self.player.input_device.get_fire1() = True)
				Self.is_passing = Self.IP_FALSE
			Else
				Self.is_passing = Self.IP_TRUE
				
				''automatic angle correction
				If ((angle_diff = 0) And Self.player.search_facing_player(False))
					ball.a :+ Self.player.facing_angle
					Local d:Float = dist(Self.player.facing_player.x, Self.player.facing_player.y, Self.player.x, Self.player.y)
					ball.v :+ (0.035 + 0.005 * Self.player.skill_passing) * d
				EndIf
			EndIf
		EndIf
		
		''speed
		If (Self.timer < 0.2 * SECOND)
			''horizontal
			If (Self.player.input_device.get_fire1())
				ball.v :+ (960 + 2*Self.player.skill_shooting) / SECOND
			EndIf
			
			''vertical
			Local f:Float = 1.0
			If (Self.player.input_device.get_value())
				If (Abs(angle_diff) < 67.5)
					f = 0 + 0.5 * (Self.is_passing = Self.IP_FALSE)
				ElseIf (Abs(angle_diff) < 112.5)
					f = 1.0
				Else
					f = 1.33
				EndIf
			EndIf
			
			ball.vz =  f * (120 + ball.v * (1 - 0.05*Self.player.skill_shooting) * Self.timer / SECOND)
			
			''spin
			If (Self.player.input_device.get_value())
				If (Abs(angle_diff) > 22.5) And (Abs(angle_diff) < 157.5)
					ball.s :+ 130.0/SECOND * Sgn(angle_diff)
				EndIf
			EndIf
		EndIf
		
		Self.player.animation_stand_run()
		
	End Method
	
	Method check_conditions:t_state()
		If (Self.timer > 0.35 * SECOND)
			Return Self.player.state_stand_run
		EndIf
		
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		Self.is_passing = Self.IP_UNKNOWN
		
		ball.v = 190
		ball.a = Self.player.kick_angle
		
		''sound
		If (location_settings.sound_vol > 0)
			If Not ChannelPlaying(channel.kick)
				SetChannelVolume(channel.kick, 0.1*location_settings.sound_vol)
				PlaySound(sound.kick, channel.kick)
			EndIf
		EndIf
		
	End Method
	
	Method exit_actions()
		If (Self.player.team.uses_automatic_input_device())
			Self.player.set_input_device(Self.player.get_ai())
		EndIf
	End Method
	
End Type

Type t_player_state_head Extends t_player_state
	
	Field hit:Int
	
	Method New()
		Self.set_name("state_head")
	End Method
	
	Function Create:t_player_state_head(p:t_player)
		Local s:t_player_state_head = New t_player_state_head
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Not Self.hit)
			If (is_in(ball.z, Self.player.z +PLAYER_H -BALL_R, Self.player.z +PLAYER_H +BALL_R))
				If (Self.player.ball_distance < 2*BALL_R)
					
					ball.set_owner(Self.player)
					ball.v = Max(170 + 7*Self.player.skill_heading, 1.5*Self.player.v)
					ball.vz = 100 -8*Self.player.skill_heading +Self.player.vz
					Self.hit = True
					
					If (location_settings.sound_vol  > 0)
						If (Not ChannelPlaying(channel.kick))
							SetChannelVolume(channel.kick, 0.1*location_settings.sound_vol*0.1*(1+ 0.03*Self.timer))
							PlaySound(sound.kick, channel.kick)
						EndIf
					EndIf
					
					If (Self.player.input_device.get_value())
						ball.a = Self.player.input_device.get_angle()
					Else
						ball.a = Self.player.a
					EndIf
				EndIf
			EndIf
		EndIf
		
		''animation
		Self.player.fmx = round((((Self.player.a + 360) Mod 360)) / 45) Mod 8
		If (Abs(Self.player.vz) < 0.1)
			Self.player.fmy = 6
		Else
			Self.player.fmy = 5
		EndIf
		
	End Method
		
	Method check_conditions:t_state()
		If (Self.player.vz = 0)
			Return Self.player.state_stand_run
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		
		Self.hit = False
		
		If (Self.player.v > 0)
			Self.player.v = 0.5*(Self.player.speed) * (1 + 0.1*Self.player.skill_heading)
		EndIf
		
		Self.player.vz = 90 + 5*Self.player.skill_heading
	End Method
	
End Type

Type t_player_state_tackle Extends t_player_state
	
	Field hit:Int
	
	Method New()
		Self.set_name("state_tackle")
	End Method
	
	Function Create:t_player_state_tackle(p:t_player)
		Local s:t_player_state_tackle = New t_player_state_tackle
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Not Self.hit)
			If ((ball.z < 5) And (Self.player.ball_distance < 18))
				Local angle:Float = ATan2(ball.y -Self.player.y, ball.x -Self.player.x)
				Local angle_diff:Float = Abs((((angle -player.a +540) Mod 360)) -180)
				If ((angle_diff <= 90) And (Self.player.ball_distance * Sin(angle_diff) <= (8 +0.3*Self.player.skill_tackling)))
					
					ball.set_owner(Self.player)
					ball.v = Self.player.v * (1 + 0.02*Self.player.skill_tackling)
					Self.hit = True
					
					If ((player.input_device.get_value()) And (Abs((((Self.player.a -Self.player.input_device.get_angle() +540) Mod 360)) -180) < 67.5))
						ball.a = player.input_device.get_angle()
					Else
						ball.a = player.a
					EndIf
					
					''sound
					If ((location_settings.sound_vol  > 0) And (Not ChannelPlaying(channel.kick)))
						SetChannelVolume(channel.kick, 0.1*location_settings.sound_vol*0.1*(1+ 0.03*Self.timer))
						PlaySound(sound.kick, channel.kick)
					EndIf
				EndIf
			EndIf
		EndIf
		
		Self.player.v :- (20 + location_settings.grass.friction) / SECOND * Sqr(Abs(Self.player.v))

		''animation
		Self.player.fmx = round((((Self.player.a + 360) Mod 360)) / 45) Mod 8
		Self.player.fmy = 4
		
	End Method
		
	Method check_conditions:t_state()
		If (Self.player.v < 30)
			Return Self.player.state_stand_run
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		
		Self.hit = False
		
		Self.player.v = 1.4*(Self.player.speed) * (1 + 0.02*Self.player.skill_tackling)
	End Method
	
End Type

Type t_player_state_reach_target Extends t_player_state
	
	Method New()
		Self.set_name("state_reach_target")
	End Method
	
	Function Create:t_player_state_reach_target(p:t_player)
		Local s:t_player_state_reach_target = New t_player_state_reach_target
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.player.v = 200
		Self.player.a = Self.player.target_angle()
		
		Self.player.animation_stand_run()
	End Method
	
	Method check_conditions:t_state()
		If (Self.player.target_distance() < 1)
			Return Self.player.state_idle
		EndIf
	End Method
	
	Method exit_actions()
		Self.player.v = 0
		Self.player.watch_ball()
	End Method
	
End Type

Type t_player_state_kick_off Extends t_player_state
	
	Method New()
		Self.set_name("state_kick_off")
	End Method
	
	Function Create:t_player_state_kick_off(p:t_player)
		Local s:t_player_state_kick_off = New t_player_state_kick_off
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		ball.set_owner(Self.player)
		
		''prevent kicking backwards
		If (Self.player.input_device.get_y() <> Self.player.team.side)
			If (Self.player.input_device.get_value())
				Self.player.a = Self.player.input_device.get_angle()
				Self.player.x = ball.x -7 * Cos(Self.player.a) +1
				Self.player.y = ball.y -7 * Sin(Self.player.a) +1
			EndIf
		EndIf
		
		Self.player.animation_stand_run()
	End Method
	
	Method check_conditions:t_state()
		If (Self.player.input_device.fire1_down())
			Self.player.kick_angle = Self.player.a
			Return Self.player.state_kick
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		Self.player.x = ball.x -7 * Self.player.team.side +1
		Self.player.y = ball.y +1
		Self.player.v = 0
		Self.player.a = 90 * (1 -Self.player.team.side)
	End Method
	
End Type

Type t_player_state_goal_kick Extends t_player_state
	
	Method New()
		Self.set_name("state_goal_kick")
	End Method
	
	Function Create:t_player_state_goal_kick(p:t_player)
		Local s:t_player_state_goal_kick = New t_player_state_goal_kick
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		ball.set_owner(Self.player)
		
		''prevent kicking backwards
		If (Self.player.input_device.get_y() <> ball.side.y)
			If (Self.player.input_device.get_value())
				Self.player.a = Self.player.input_device.get_angle()
				Self.player.x = ball.x -7 * Cos(Self.player.a) +1
				Self.player.y = ball.y -7 * Sin(Self.player.a) +1
			EndIf
		EndIf
		
		Self.player.animation_stand_run()
		
	End Method
	
	Method check_conditions:t_state()
		If (Self.player.input_device.fire1_down())
			Self.player.kick_angle = Self.player.a
			Return Self.player.state_kick
		EndIf
		Return Null
	End Method
	
End Type

Type t_player_state_throw_in_angle Extends t_player_state
	
	Field animation_countdown:Int
	
	Method New()
		Self.set_name("state_throw_in_angle")
	End Method
	
	Function Create:t_player_state_throw_in_angle(p:t_player)
		Local s:t_player_state_throw_in_angle = New t_player_state_throw_in_angle
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		If (Self.animation_countdown > 0)
			Self.animation_countdown = Self.animation_countdown -1
		EndIf
		
		Local x:Int = Self.player.input_device.get_x()
		Local y:Int = Self.player.input_device.get_y()
		
		''prevent throwing outside
		If (x <> ball.side.x)
			Local value:Int = (x <> 0) Or (y <> 0)
			Local angle:Int = ATan2(y, x)
			
			If (value)
				''stop animation
				If (angle <> Self.player.a)
					Self.animation_countdown = SECOND
				EndIf
				Self.player.a = angle
				Self.player.fmx = (round(angle / 45.0) +8) Mod 8
			EndIf
		EndIf
		
		''do animation
		Local a:Int = 0
		If (Self.animation_countdown = 0)
			a = ((Self.timer Mod 200) > 100)
		EndIf
		
		Self.player.fmy = 8 + 1*a
		ball.x = Self.player.x +6 * Cos(player.a) * a
		ball.y = Self.player.y +6 * Sin(player.a) * a
		ball.z = PLAYER_H +2 +0*a
		
	End Method
	
	Method check_conditions:t_state()
		If (Self.player.input_device.fire1_down())
			Return Self.player.state_throw_in_speed
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		Self.player.a = 45 * (2 * ball.side.x +2)
		Self.player.x = ball.side.x * (TOUCH_LINE +6)
		Self.player.fmx = 2 * ball.side.x +2
		Self.player.fmy = 8
		ball.set_owner(Self.player)
		Self.animation_countdown = SECOND
	End Method
	
End Type

Type t_player_state_throw_in_speed Extends t_player_state
	
	Field thrown:Int
	
	Method New()
		Self.set_name("state_throw_in_speed")
	End Method
	
	Function Create:t_player_state_throw_in_speed(p:t_player)
		Local s:t_player_state_throw_in_speed = New t_player_state_throw_in_speed
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Not thrown)
			ball.x = Self.player.x
			ball.y = Self.player.y
			ball.z = PLAYER_H +2
			Self.player.fmy = 8
			
			If (Self.timer > 0.15 * SECOND And Self.player.input_device.get_fire1() = False)
				thrown = True
			EndIf
			If (Self.timer > 0.3*SECOND)
				thrown = True
			EndIf
			
			If (thrown)
				ball.x = Self.player.x +6 * Cos(player.a)
				ball.y = Self.player.y +6 * Sin(player.a)
				ball.v = 30 +1000 * Self.timer/SECOND
				ball.vz = 500 * Self.timer/SECOND
				Select Self.player.fmx
					Case 2
						ball.a = 90 +(10 +5*location_settings.wind.speed) * ball.side.x
					Case 6
						ball.a = 270 -(10 +5*location_settings.wind.speed) * ball.side.x
					Default
						ball.a = 45 * Self.player.fmx
				End Select
				If (Self.player.search_facing_player(Self.timer > 0.3 * SECOND))
					ball.a :+ Self.player.facing_angle
				EndIf
				
				Self.player.fmy = 9
			EndIf
		EndIf
		
	End Method
	
	Method check_conditions:t_state()
		If (Self.timer > 0.35*SECOND)
			Return Self.player.state_stand_run
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		Self.thrown = False
	End Method
	
End Type

Type t_player_state_corner_kick_angle Extends t_player_state
	
	Method New()
		Self.set_name("state_corner_kick_angle")
	End Method
	
	Function Create:t_player_state_corner_kick_angle(p:t_player)
		Local s:t_player_state_corner_kick_angle = New t_player_state_corner_kick_angle
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Local x:Int = Self.player.input_device.get_x()
		Local y:Int = Self.player.input_device.get_y()
		
		''prevent kicking out
		If (x <> ball.side.x) And (y <> ball.side.y)
			Local value:Int = (x <> 0) Or (y <> 0)
			Local angle:Int = ATan2(y, x)
			
			If (value)
				Self.player.a = angle
			EndIf
		EndIf
		Self.player.x = ball.x -7*Cos(Self.player.a)
		Self.player.y = ball.y -7*Sin(Self.player.a)
		Self.player.animation_stand_run()
		
	End Method
	
	Method check_conditions:t_state()
		If (Self.player.input_device.fire1_down())
			Return Self.player.state_corner_kick_speed
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		ball.set_owner(Self.player)
		Self.player.a = 90*(1 +ball.side.x)
	End Method	

End Type

Type t_player_state_corner_kick_speed Extends t_player_state
	
	Field kick_angle:Int
	Field kick_spin:Float
	Field thrown:Int
	
	Method New()
		Self.set_name("state_corner_kick_speed")
	End Method
	
	Function Create:t_player_state_corner_kick_speed(p:t_player)
		Local s:t_player_state_corner_kick_speed = New t_player_state_corner_kick_speed
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		If (Not thrown)
			
			If (Self.timer > 0.15 * SECOND And Self.player.input_device.get_fire1() = False)
				thrown = True
			EndIf
			If (Self.timer > 0.3*SECOND)
				thrown = True
			EndIf
			
			Local angle_diff:Float
			If(Self.player.input_device.get_value())
				angle_diff = ((Self.player.input_device.get_angle() -Self.kick_angle +540) Mod 360) -180
			Else
				angle_diff = 90
			EndIf
			
			''spin
			If (Self.player.input_device.get_value())
				If (Abs(angle_diff) > 22.5) And (Abs(angle_diff) < 157.5)
					Self.kick_spin :+ 50.0/SECOND * Sgn(angle_diff)
				EndIf
			EndIf
			
			If (thrown)
				
				Local f:Float
				If (Abs(angle_diff) < 67.5)
					f = 0
				ElseIf (Abs(angle_diff) < 112.5)
					f = 1.0
				Else
					f = 1.3
				EndIf
				ball.v = 160 +160 * f +300 * Self.timer / SECOND
				ball.vz =  f * (100 + 400 * Self.timer / SECOND)
				
				Local long_range:Int = (Self.timer > 0.2 * SECOND)
				
				If (Self.player.search_facing_player(long_range) And angle_diff = 0)
					ball.a = 45 * Self.player.fmx +Self.player.facing_angle
				Else
					Select Self.player.fmx
						Case 0
							ball.a = 0 -5 * ball.side.y
						Case 2
							ball.a = 90 +5 * ball.side.x
						Case 4
							ball.a = 180 +5 * ball.side.y
						Case 6
							ball.a = 270 -5 * ball.side.x
						Default
							ball.a = 45 * Self.player.fmx
					End Select
				EndIf
				ball.s = Self.kick_spin
			EndIf
		EndIf
		
	End Method
	
	Method check_conditions:t_state()
		If (Self.timer > 0.35*SECOND)
			Return Self.player.state_stand_run
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		Self.kick_angle = 45 * Self.player.fmx
		Self.kick_spin = 0
		Self.thrown = False
	End Method
	
End Type

Type t_player_state_goal_scorer Extends t_player_state
	
	Field celebration_type:Int
	
	Method New()
		Self.set_name("state_goal_scorer")
	End Method
	
	Function Create:t_player_state_goal_scorer(p:t_player)
		Local s:t_player_state_goal_scorer = New t_player_state_goal_scorer
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		''stop and look the crowd
		If (Self.player.target_distance() < 1)
			Self.player.v = 0
		EndIf
		Self.player.animation_scorer()
	End Method
	
	Method entry_actions()
		Local disallowed:Int[8]
		
		''top
		If (Self.player.y +GOAL_LINE < 375)
			disallowed[6] = 1
			If (player.x < 0)
				disallowed[7] = 1
			Else
				disallowed[5] = 1
			EndIf
		EndIf
		If (Self.player.y +GOAL_LINE < 220)
			disallowed[5] = 1
			disallowed[6] = 1
			disallowed[7] = 1
			If (player.x < 0)
				disallowed[0] = 1
			Else
				disallowed[4] = 1
			EndIf
		EndIf
		
		''bottom
		If (GOAL_LINE -Self.player.y < 375)
			disallowed[2] = 1
			If (player.x < 0)
				disallowed[1] = 1
			Else
				disallowed[3] = 1
			EndIf
		EndIf
		If (GOAL_LINE -Self.player.y < 220)
			disallowed[1] = 1
			disallowed[2] = 1
			disallowed[3] = 1
			If (player.x < 0)
				disallowed[0] = 1
			Else
				disallowed[4] = 1
			EndIf
		EndIf
		
		''left
		If (Self.player.x +TOUCH_LINE < 340)
			disallowed[4] = 1
		EndIf
		If (Self.player.x +TOUCH_LINE < 200)
			disallowed[3] = 1
			disallowed[4] = 1
			disallowed[5] = 1
		EndIf
		
		''right
		If (TOUCH_LINE -Self.player.x < 340)
			disallowed[0] = 1
		EndIf
		If (TOUCH_LINE -Self.player.x < 200)
			disallowed[7] = 1
			disallowed[0] = 1
			disallowed[1] = 1
		EndIf
		
		Repeat
			Self.celebration_type = Rand(0, 7)
		Until Not(disallowed[Self.celebration_type])
		
		Self.player.v = Self.player.speed
		Self.player.a = 45 * Self.celebration_type
		
		Local d:Int = 500
		
		''check x limits
		If (Abs(Cos(Self.player.a)) > 0.002)
			Local tx:Int = Self.player.x +d * Cos(Self.player.a)
			tx = Sgn(tx) * Min(Abs(tx), TOUCH_LINE +20 -2*Self.player.number)
			d = Min(d, (tx -Self.player.x) / Cos(Self.player.a))
		EndIf
		
		''check y limits
		If (Abs(Sin(Self.player.a)) > 0.002)
			Local ty:Int = Self.player.y +d * Sin(Self.player.a)
			ty = Sgn(ty) * Min(Abs(ty), GOAL_LINE +10 -Self.player.number)
			d = Min(d, (ty -Self.player.y) / Sin(Self.player.a))
		EndIf
		
		Self.player.tx = Self.player.x +d * Cos(Self.player.a)
		Self.player.ty = Self.player.y +d * Sin(Self.player.a)
	End Method
	
End Type

Type t_player_state_goal_mate Extends t_player_state
	
	Method New()
		Self.set_name("state_goal_mate")
	End Method
	
	Function Create:t_player_state_goal_mate(p:t_player)
		Local s:t_player_state_goal_mate = New t_player_state_goal_mate
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.player.tx = g_goal.player.x + (10 +(Self.player.number Mod 20)) * Cos(30 * Self.player.number)
		Self.player.ty = g_goal.player.y + (10 +(Self.player.number Mod 20)) * Sin(30 * Self.player.number)
		
		If (Self.player.target_distance() < 1)
			Self.player.v = 0
		Else
			Self.player.v = Self.player.speed
			Self.player.a = Self.player.target_angle()
		EndIf
		Self.player.animation_stand_run()
	End Method
	
End Type

Type t_player_state_own_goal_scorer Extends t_player_state
	
	Method New()
		Self.set_name("state_own_goal_scorer")
	End Method
	
	Function Create:t_player_state_own_goal_scorer(p:t_player)
		Local s:t_player_state_own_goal_scorer = New t_player_state_own_goal_scorer
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
	End Method
	
	Method entry_actions()
		Self.player.v = 0
		If (Self.player.role = PR.GOALKEEPER)
			Self.player.fmx = 2 +2*(1 -ball.side.y)
			Self.player.fmy = 1
		Else
			Self.player.fmy = 14 +0.5*(1 -ball.side.y)
			Self.player.fmx = 3
		EndIf
	End Method
	
End Type

Type t_player_state_keeper_positioning Extends t_player_state
	
	Field danger_time:Int
	
	Method New()
		Self.set_name("state_keeper_positioning")
	End Method
	
	Function Create:t_player_state_keeper_positioning(p:t_player)
		Local s:t_player_state_keeper_positioning = New t_player_state_keeper_positioning
		s.set_player(p)
		Return s
	End Function
	
	Method update_target()
		
		''default value
		Local new_tx:Float = ball.x * 12 / Max((Abs(ball.y - Sgn(player.y)*GOAL_LINE)),1)
		new_tx = Sgn(new_tx)*Min(Abs(new_tx), POST_X +5)
		Local new_ty:Float = player.team.side * (GOAL_LINE - 8)
		
		''penalty area positioning
		If ((Abs(ball.x) < PENALTY_AREA_W/2) And is_in(ball.y, player.team.side * (GOAL_LINE - PENALTY_AREA_H), player.team.side * GOAL_LINE))
			
			''if ball is approaching
			If (dist(ball.x, ball.y, 0, player.team.side * GOAL_LINE) <	dist(ball.x0, ball.y0, 0, player.team.side * GOAL_LINE))
				
				''if ball is reachable reach the point where it will go
				If (player.frame_distance < BALL_PREDICTION)
					
					new_tx = ball.prediction[player.frame_distance].x
					
				Else ''try to reach it anyway
					
					new_tx = ball.x
					new_ty = player.team.side * (GOAL_LINE - 0.5 * Abs(GOAL_LINE - Abs(ball.y)))
					
				EndIf
			EndIf
		EndIf
		
		''goal area positioning: reach the ball!
		If ((Abs(ball.x) < GOAL_AREA_W/2) And is_in(ball.y, player.team.side * (GOAL_LINE - GOAL_AREA_H), player.team.side * GOAL_LINE))
			
			If (player.frame_distance < BALL_PREDICTION)
			
				new_tx = ball.prediction[player.frame_distance].x
				new_ty = ball.prediction[player.frame_distance].y
				
			EndIf
		
		EndIf
		
		If (dist(new_tx, new_ty, player.tx, player.ty) > 1)
			
			player.tx = new_tx
			player.ty = new_ty
			
		EndIf
		
	End Method
	
	Method do_actions()
		
		Super.do_actions()
		If (Not (timer Mod 100))
			update_target()
		EndIf
		
		''distance from target position
		Local dx:Float = round(player.tx -player.x)
		Local dy:Float = round(player.ty -player.y)
		
		''reach target position
		If (Abs(dx) > 1 Or Abs(dy) > 1)
			player.v = (0.25 + 0.60*(hypo(dx, dy) > 4)) * player.speed
			player.a = (ATan2(dy,dx)+360) Mod 360
		''position reached
		Else
			dx = ball.x -player.x
			dy = ball.y -player.y
			player.v = 0
			If (timer > 0.5*SECOND) player.a = ATan2(dy, dx)
		EndIf
		
		Self.player.animation_stand_run()
		
	End Method
	
	Method check_conditions:t_state()
		
		If (Self.player.input_device <> Self.player.get_ai())
			Return Self.player.state_stand_run
		EndIf
		
		''detect danger
		Local found:Int = False
		If ((Abs(ball.y) < GOAL_LINE) And (Abs(ball.y) > 0.5*GOAL_LINE) And (Sgn(ball.y) = Sgn(player.y)) And (ball.owner = Null))
			For Local frm:Int = 0 To BALL_PREDICTION-1
				Local x:Int = ball.prediction[frm].x
				Local y:Int = ball.prediction[frm].y
				Local z:Int = ball.prediction[frm].z
				If ((Abs(x) < GOAL_AREA_W/2) And (Abs(z) < 2*CROSSBAR_H) And ((Abs(y) > GOAL_LINE) And (Abs(y) < GOAL_LINE +15)))
					found = True
				EndIf
			Next
		EndIf
		If (found)
			danger_time = danger_time +1
		Else
			danger_time = 0
		EndIf
		
		If (danger_time > (0.3 - 0.03*player.skill_keeper) * SECOND)
			Local pred_x:Int = 0
			Local pred_z:Int = 0
			
			''intersection with keeper diving surface
			Local frm:Int
			Local found_2:Int = False
			For frm = 0 To BALL_PREDICTION -1
				Local x:Int = ball.prediction[frm].x
				Local y:Int = ball.prediction[frm].y
				Local z:Int = ball.prediction[frm].z
				If ((Abs(x -player.x) < GOAL_AREA_W/2) And (Abs(z) < 2*CROSSBAR_H) And ((Abs(y) > Abs(player.y)) And (Abs(y) < Abs(player.y) +15)))
					pred_x = x
					pred_z = z
					found_2 = True
				EndIf
				If found_2 Exit
			Next
			Local diff_x:Double = pred_x - player.x
			
			''kind of save
			If (pred_z < 2 * CROSSBAR_H)
				Local r:Float = hypo(diff_x, pred_z)
				
				If (r < 88)
					If (Abs(diff_x) < 4)
						If (pred_z > 30)
							''CATCH HIGH
							If (frm * SUBFRAMES < 0.6 * SECOND)
								Return player.state_keeper_catching_high
							EndIf
						Else
							''CATCH LOW
							If (frm * SUBFRAMES < 0.6 * SECOND)
								Return player.state_keeper_catching_low
							EndIf
						EndIf
					Else If (pred_z < 7)
						If (Abs(diff_x) > POST_X)
							''LOW - ONE HAND
							If ((frm * SUBFRAMES < 0.7 * SECOND) And (frm * SUBFRAMES > 0.25 * SECOND))
								player.thrust_x = (Abs(diff_x) - POST_X) / (GOAL_AREA_W/2 - POST_X)
								player.a = (diff_x < 0)*180
								Return player.state_keeper_diving_low_single
							EndIf
						Else
							''LOW - TWO HANDS
							If (frm * SUBFRAMES < 0.5 * SECOND)
								player.thrust_x = (Abs(diff_x) - 8) / (POST_X - 8)
								player.a = (diff_x < 0)*180
								Return player.state_keeper_diving_low_double
							EndIf
						EndIf
					Else If (pred_z < 21)
						''MIDDLE - TWO HANDS
						If ((frm * SUBFRAMES < 0.7 * SECOND) And (frm * SUBFRAMES > 0.25 * SECOND))
							player.thrust_x = (Abs(diff_x) - 8) / (POST_X - 8)
							player.thrust_z = (pred_z -7) / 14.0
							player.a = (diff_x < 0)*180
							Return player.state_keeper_diving_middle_two
						EndIf
					Else If ((pred_z < 27) And (Abs(diff_x) < POST_X+16))
						''MIDDLE - ONE HAND
						If ((frm * SUBFRAMES < 0.7 * SECOND) And (frm * SUBFRAMES > 0.25 * SECOND))
							player.thrust_x = (Abs(diff_x) - 8) / (POST_X+8)
							player.thrust_z = (pred_z -17) / 6.0
							player.a = (diff_x < 0)*180
							Return player.state_keeper_diving_middle_one
						EndIf
					Else If ((pred_z < 44) And (Abs(diff_x) < POST_X+16))
						''HIGH - ONE HAND
						If ((frm * SUBFRAMES < 0.7 * SECOND) And (frm * SUBFRAMES > 0.25 * SECOND))
							player.thrust_x = (Abs(diff_x) - 8) / (POST_X+8)
							player.thrust_z = Min((pred_z -27) / 8.0, 1)
							player.a = (diff_x < 0)*180
							Return player.state_keeper_diving_high_one
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		danger_time = 0
	End Method
	
End Type

Type t_player_state_keeper_diving_low_single Extends t_player_state
	
	Field active:t_keeper_frame
	Field frames:t_keeper_frame[3]
	
	Method New()
		Self.set_name("state_keeper_diving_low_single")
		
		active = frames[0]
		
		frames[0] = New t_keeper_frame
		frames[0].fmx = [0, 1, 2, 3]
		frames[0].fmy = 8
		frames[0].offx = [13, -10, 17, -13]
		frames[0].offz = 0
		
		frames[1] = New t_keeper_frame
		frames[1].fmx = [4, 5, 6, 7]
		frames[1].fmy = 8 +4
		frames[1].offx = [13, -10, 17, -13]
		frames[1].offz = 0
		
		frames[2] = New t_keeper_frame
		frames[2].fmx = [4, 5, 6, 7]
		frames[2].fmy = 8 +6
		frames[2].offx = [16, -15, 14, -10]
		frames[2].offz = 0
	End Method
	
	Function Create:t_player_state_keeper_diving_low_single(p:t_player)
		Local s:t_player_state_keeper_diving_low_single = New t_player_state_keeper_diving_low_single
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		
		Super.do_actions()
		
		''collision detection
		If (ball.holder <> Self.player)
			Self.player.keeper_collision()
		EndIf
		
		''animation
		If (timer < 0.05*SECOND)
			player.v = 120
			active = frames[0] 
		Else If (timer < 0.4*SECOND)
			player.v = 100 + 20*player.thrust_x
			active = frames[1]
		Else If (timer < 0.6*SECOND)
			player.v = 100 + 20*player.thrust_x
			active = frames[2]
		Else
			player.v = player.v * (1 - 50.0/SECOND)
		EndIf
		
		Local p:Int = (player.a = 180) + 2*(player.y > 0)
		player.fmx = active.fmx[p]
		player.fmy = active.fmy
		player.hold_ball(active.offx[p], active.offz)
		
	End Method
	
	Method check_conditions:t_state()
		If (timer >= 1.0*SECOND)
			If (ball.holder = Self.player)
				Return Self.player.state_keeper_kick_angle
			Else
				Return player.state_keeper_positioning
			EndIf
		EndIf
		Return Null
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
	
End Type

Type t_player_state_keeper_diving_low_double Extends t_player_state
	
	Field active:t_keeper_frame
	Field frames:t_keeper_frame[3]
	
	Method New()
		Self.set_name("state_keeper_diving_low_double")
		
		active = frames[0]
		
		frames[0] = New t_keeper_frame
		frames[0].fmx = [0, 1, 2, 3]
		frames[0].fmy = 8
		frames[0].offx = [13, -10, 17, -13]
		frames[0].offz = 0
		
		frames[1] = New t_keeper_frame
		frames[1].fmx = [4, 5, 6, 7]
		frames[1].fmy = 8 +4
		frames[1].offx = [13, -10, 17, -13]
		frames[1].offz = 0
		
		frames[2] = New t_keeper_frame
		frames[2].fmx = [4, 5, 6, 7]
		frames[2].fmy = 8 +5
		frames[2].offx = [12, -9, 17, -13]
		frames[2].offz = 0
		
	End Method
	
	Function Create:t_player_state_keeper_diving_low_double(p:t_player)
		Local s:t_player_state_keeper_diving_low_double = New t_player_state_keeper_diving_low_double
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		
		Super.do_actions()
		
		''collision detection
		If (ball.holder <> Self.player)
			Self.player.keeper_collision()
		EndIf
		
		''animation
		If (timer < 0.1*SECOND)
			player.v = 10
			active = frames[0]
		Else If (timer < 0.5*SECOND)
			player.v = 20+(140+player.skill_keeper)*player.thrust_x
			active = frames[1]
		Else If (timer < 0.55*SECOND)
			player.v = 20
			active = frames[2]
		Else
			player.v = player.v * (1 - 50.0/SECOND)
		EndIf
		
		Local p:Int = (player.a = 180) + 2*(player.y > 0)
		player.fmx = active.fmx[p]
		player.fmy = active.fmy
		player.hold_ball(active.offx[p], active.offz)
		
	End Method
	
	Method check_conditions:t_state()
		If (timer >= 0.75*SECOND)
			If (ball.holder = Self.player)
				Return Self.player.state_keeper_kick_angle
			Else
				Return player.state_keeper_positioning
			EndIf
		EndIf
		Return Null
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
	
End Type

Type t_player_state_keeper_diving_middle_one Extends t_player_state
	
	Field active:t_keeper_frame
	Field frames:t_keeper_frame[5]
	
	Method New()
		Self.set_name("state_keeper_diving_middle_one")
		
		active = frames[0]
		
		frames[0] = New t_keeper_frame
		frames[0].fmx = [0, 1, 2, 3]
		frames[0].fmy = 8
		frames[0].offx = [10, -8, 13, -9]
		frames[0].offz = 24
		
		frames[1] = New t_keeper_frame
		frames[1].fmx = [0, 1, 2, 3]
		frames[1].fmy = 8 +1
		frames[1].offx = [10, -8, 13, -9]
		frames[1].offz = 24
		
		frames[2] = New t_keeper_frame
		frames[2].fmx = [0, 1, 2, 3]
		frames[2].fmy = 8 +2
		frames[2].offx = [10, -8, 13, -9]
		frames[2].offz = 24
		
		frames[3] = New t_keeper_frame
		frames[3].fmx = [0, 1, 2, 3]
		frames[3].fmy = 8 +3
		frames[3].offx = [8, -5, 10, -8]
		frames[3].offz = 10
		
		frames[4] = New t_keeper_frame
		frames[4].fmx = [0, 1, 2, 3]
		frames[4].fmy = 8 +4
		frames[4].offx = [8, -5, 15, -11]
		frames[4].offz = 0
	End Method
	
	Function Create:t_player_state_keeper_diving_middle_one(p:t_player)
		Local s:t_player_state_keeper_diving_middle_one = New t_player_state_keeper_diving_middle_one
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		
		Super.do_actions()
		
		''collision detection
		If (ball.holder <> Self.player)
			Self.player.keeper_collision()
		EndIf
		
		''animation
		If (timer < 0.1*SECOND)
			player.v = 20
			active = frames[0]
		Else If (timer < 0.2*SECOND)
			player.v = 120*player.thrust_x
			active = frames[1]
		Else If (timer < 0.75*SECOND)
			player.v = (163+player.skill_keeper)*player.thrust_x
			active = frames[2]
		Else If (timer < 0.9*SECOND)
			player.v = 40*player.thrust_x
			active = frames[3]
		Else If (timer < 1.0*SECOND)
			player.v = 40*player.thrust_x
			active = frames[4]
		Else
			player.v = player.v * (1 - 50.0/SECOND)
		EndIf
		
		Local p:Int = (player.a = 180) + 2*(player.y > 0)
		player.fmx = active.fmx[p]
		player.fmy = active.fmy
		player.hold_ball(active.offx[p], active.offz)
		
	End Method
	
	Method check_conditions:t_state()
		If (timer >= 1.2*SECOND)
			If (ball.holder = Self.player)
				Return Self.player.state_keeper_kick_angle
			Else
				Return player.state_keeper_positioning
			EndIf
		EndIf
		Return Null
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
	
End Type

Type t_player_state_keeper_diving_middle_two Extends t_player_state
	
	Field active:t_keeper_frame
	Field frames:t_keeper_frame[6]
	
	Method New()
		Self.set_name("state_keeper_diving_middle_two")
		
		active = frames[0]
		
		frames[0] = New t_keeper_frame
		frames[0].fmx = [4, 5, 6, 7]
		frames[0].fmy = 8
		frames[0].offx = [16, -13, 16, -12]
		frames[0].offz = 18
		
		frames[1] = New t_keeper_frame
		frames[1].fmx = [4, 5, 6, 7]
		frames[1].fmy = 8 +1
		frames[1].offx = [16, -13, 16, -12]
		frames[1].offz = 18
		
		frames[2] = New t_keeper_frame
		frames[2].fmx = [4, 5, 6, 7]
		frames[2].fmy = 8 +2
		frames[2].offx = [16, -13, 16, -12]
		frames[2].offz = 18
		
		frames[3] = New t_keeper_frame
		frames[3].fmx = [4, 5, 6, 7]
		frames[3].fmy = 8 +3
		frames[3].offx = [16, -13, 18, -14]
		frames[3].offz = 15
		
		frames[4] = New t_keeper_frame
		frames[4].fmx = [4, 5, 6, 7]
		frames[4].fmy = 8 +4
		frames[4].offx = [13, -10, 17, -13]
		frames[4].offz = 0
		
		frames[5] = New t_keeper_frame
		frames[5].fmx = [4, 5, 6, 7]
		frames[5].fmy = 8 +5
		frames[5].offx = [12, -9, 17, -13]
		frames[5].offz = 0
	End Method
	
	Function Create:t_player_state_keeper_diving_middle_two(p:t_player)
		Local s:t_player_state_keeper_diving_middle_two = New t_player_state_keeper_diving_middle_two
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		
		Super.do_actions()
		
		''collision detection
		If (ball.holder <> Self.player)
			Self.player.keeper_collision()
		EndIf
		
		''animation
		If (timer < 0.05*SECOND)
			player.v = 40*player.thrust_x
			active = frames[0] 
		Else If (timer < 0.1*SECOND)
			player.v = 40*player.thrust_x
			player.vz = 70+30*player.thrust_z
			active = frames[1]
		Else If (timer < 0.5*SECOND)
			player.v = (128+player.skill_keeper)*player.thrust_x
			active = frames[2]
		Else If (timer < 0.7*SECOND)
			player.v = 50*player.thrust_x
			active = frames[3]
		Else If (timer < 0.8*SECOND)
			player.v = 20*player.thrust_x
			active = frames[4]
		Else If (timer < 0.9*SECOND)
			player.v = 20*player.thrust_x
			active = frames[5]
		Else
			player.v = player.v * (1 - 50.0/SECOND)
		EndIf
		
		Local p:Int = (player.a = 180) + 2*(player.y > 0)
		player.fmx = active.fmx[p]
		player.fmy = active.fmy
		player.hold_ball(active.offx[p], active.offz)
		
	End Method
	
	Method check_conditions:t_state()
		If (timer >= 1.1*SECOND)
			If (ball.holder = Self.player)
				Return Self.player.state_keeper_kick_angle
			Else
				Return player.state_keeper_positioning
			EndIf
		EndIf
		Return Null
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
	
End Type

Type t_player_state_keeper_diving_high_one Extends t_player_state
	
	Field active:t_keeper_frame
	Field frames:t_keeper_frame[5]
	
	Method New()
		Self.set_name("state_keeper_diving_high_one")
		
		active = frames[0]
		
		frames[0] = New t_keeper_frame
		frames[0].fmx = [4, 5, 6, 7]
		frames[0].fmy = 8
		frames[0].offx = [16, -13, 16, -12]
		frames[0].offz = 18
		
		frames[1] = New t_keeper_frame
		frames[1].fmx = [0, 1, 2, 3]
		frames[1].fmy = 8 +5
		frames[1].offx = [13, -11, 10, -10]
		frames[1].offz = 26
		
		frames[2] = New t_keeper_frame
		frames[2].fmx = [0, 1, 2, 3]
		frames[2].fmy = 8 +6
		frames[2].offx = [13, -11, 10, -10]
		frames[2].offz = 26
		
		frames[3] = New t_keeper_frame
		frames[3].fmx = [0, 1, 2, 3]
		frames[3].fmy = 8 +7
		frames[3].offx = [14, -12, 10, -10]
		frames[3].offz = 20
		
		frames[4] = New t_keeper_frame
		frames[4].fmx = [0, 1, 2, 3]
		frames[4].fmy = 8 +4
		frames[4].offx = [8, -5, 15, -11]
		frames[4].offz = 0
	End Method
	
	Function Create:t_player_state_keeper_diving_high_one(p:t_player)
		Local s:t_player_state_keeper_diving_high_one = New t_player_state_keeper_diving_high_one
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		
		Super.do_actions()
		
		''collision detection
		If (ball.holder <> Self.player)
			Self.player.keeper_collision()
		EndIf
		
		''animation
		If (timer < 0.05*SECOND)
			player.v = 10
			active = frames[0]
		Else If (timer < 0.2*SECOND)
			player.v = 80*player.thrust_x
			active = frames[1]
		Else If (timer < 0.7*SECOND)
			player.v = (163+player.skill_keeper)*player.thrust_x
			player.vz = 13+player.skill_keeper+10*player.thrust_z
			active = frames[2]
		Else If (timer < 1.1*SECOND)
			player.v = (50+player.skill_keeper)*player.thrust_x
			active = frames[3]
		Else If (timer < 1.35*SECOND)
			player.v = 20
			active = frames[4]
		Else
			player.v = player.v * (1 - 50.0/SECOND)
		EndIf
		
		Local p:Int = (player.a = 180) + 2*(player.y > 0)
		player.fmx = active.fmx[p]
		player.fmy = active.fmy
		player.hold_ball(active.offx[p], active.offz)
		
	End Method
	
	Method check_conditions:t_state()
		If (timer >= 1.55*SECOND)
			If (ball.holder = Self.player)
				Return Self.player.state_keeper_kick_angle
			Else
				Return player.state_keeper_positioning
			EndIf
		EndIf
		Return Null
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
	
End Type

Type t_player_state_keeper_catching_high Extends t_player_state
	
	Field active:t_keeper_frame
	Field frames:t_keeper_frame[4]
	
	Method New()
		Self.set_name("state_keeper_catching_high")
		
		active = frames[0]
		
		frames[0] = New t_keeper_frame
		frames[0].fmx = [0, 0, 1, 1]
		frames[0].fmy = 8
		frames[0].offx = [2, 0, 0, 0]
		frames[0].offz = 30
		
		frames[1] = New t_keeper_frame
		frames[1].fmx = [0, 0, 1, 1]
		frames[1].fmy = 8 +9
		frames[1].offx = [2, 0, 0, 0]
		frames[1].offz = 30
		
		frames[2] = New t_keeper_frame
		frames[2].fmx = [0, 0, 1, 1]
		frames[2].fmy = 8 +10
		frames[2].offx = [2, 0, 0, 0]
		frames[2].offz = 30
		
		frames[3] = New t_keeper_frame
		frames[3].fmx = [0, 0, 1, 1]
		frames[3].fmy = 8 +8
		frames[3].offx = [2, 0, 0, 0]
		frames[3].offz = 15
	End Method
	
	Function Create:t_player_state_keeper_catching_high(p:t_player)
		Local s:t_player_state_keeper_catching_high = New t_player_state_keeper_catching_high
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		
		Super.do_actions()
		
		''collision detection
		If (ball.holder <> Self.player)
			Self.player.keeper_collision()
		EndIf
		
		''animation
		If (timer < 0.05*SECOND)
			player.v = 0
			active = frames[0]
		Else If (timer < 0.2*SECOND)
			player.vz = 35
			active = frames[1]
		Else If (timer < 0.6*SECOND)
			player.vz = 30
			active = frames[2]
		Else If (timer < 0.9*SECOND)
			If (ball.owner <> player) active = frames[3]
		EndIf
		
		Local p:Int = (player.a = 180) + 2*(player.y > 0)
		player.fmx = active.fmx[p]
		player.fmy = active.fmy
		player.hold_ball(active.offx[p], active.offz)
		
	End Method
	
	Method check_conditions:t_state()
		If (timer >= 1.0*SECOND)
			If (ball.holder = Self.player)
				Return Self.player.state_keeper_kick_angle
			Else
				Return player.state_keeper_positioning
			EndIf
		EndIf
		Return Null
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
	
End Type

Type t_player_state_keeper_catching_low Extends t_player_state
	
	Field active:t_keeper_frame
	Field frames:t_keeper_frame[1]
	
	Method New()
		Self.set_name("state_keeper_catching_low")
		
		active = frames[0]
		
		frames[0] = New t_keeper_frame
		frames[0].fmx = [2, 2, 6, 6]
		frames[0].fmy = 1
		frames[0].offx = [0, 0, 0, 0]
		frames[0].offz = 10
	End Method
	
	Function Create:t_player_state_keeper_catching_low(p:t_player)
		Local s:t_player_state_keeper_catching_low = New t_player_state_keeper_catching_low
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		
		Super.do_actions()
		
		''collision detection
		If (ball.holder <> Self.player)
			Self.player.keeper_collision()
		EndIf
		
		''animation
		player.v = 0
		active = frames[0]
		
		Local p:Int = (player.a = 180) + 2*(player.y > 0)
		player.fmx = active.fmx[p]
		player.fmy = active.fmy
		player.hold_ball(active.offx[p], active.offz)
		
	End Method
	
	Method check_conditions:t_state()
		If (timer >= 1.0*SECOND)
			If (ball.holder = Self.player)
				Return Self.player.state_keeper_kick_angle
			Else
				Return player.state_keeper_positioning
			EndIf
		EndIf
		Return Null
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
	
End Type

Type t_player_state_keeper_kick_angle Extends t_player_state
	
	Method New()
		Self.set_name("state_keeper_kick_angle")
	End Method
	
	Function Create:t_player_state_keeper_kick_angle(p:t_player)
		Local s:t_player_state_keeper_kick_angle = New t_player_state_keeper_kick_angle
		s.set_player(p)
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		''prevent kicking backwards
		If (Self.player.input_device.get_y() <> ball.side.y)
			If (Self.player.input_device.get_value())
				Self.player.a =  Self.player.input_device.get_angle()
			EndIf
		EndIf
		
		ball.set_position(Self.player.x +5*Cos(player.a), Self.player.y +5*Sin(player.a), 12)
		ball.update_prediction()
		
		Self.player.animation_stand_run()
		
	End Method
	
	Method check_conditions:t_state()
		If (Self.player.input_device.fire1_down())
			Self.player.kick_angle = Self.player.a
			Return Self.player.state_kick
		EndIf
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		ball.set_holder(Self.player)
		Self.player.v = 0
		Self.player.a = (90*ball.side.y +180) Mod 360
	End Method
	
	Method exit_actions()
		ball.set_holder(Null)
	End Method
End Type

Type t_ai Extends t_input
	
	Field player:t_player
	Field fsm:t_state_machine
	
	Field state_idle:t_state
	Field state_kicking_off:t_state
	Field state_positioning:t_state
	Field state_seeking:t_state
	Field state_defending:t_state
	Field state_attacking:t_state
	Field state_passing:t_state
	Field state_kicking:t_state
	Field state_goal_kicking:t_state
	Field state_throwing_in:t_state
	Field state_corner_kicking:t_state
	Field state_keeper_kicking:t_state
	
	Function Create:t_ai(p:t_player)
		Local ai:t_ai = New t_ai
		ai.set_player(p)
		ai.set_type(Self.ID_COMPUTER)
		ai.fsm = New t_state_machine
		
		ai.state_idle = t_ai_state_idle.Create(ai)
		ai.fsm.add_state(ai.state_idle)
		
		ai.state_kicking_off = t_ai_state_kicking_off.Create(ai)
		ai.fsm.add_state(ai.state_kicking_off)
		
		ai.state_positioning = t_ai_state_positioning.Create(ai)
		ai.fsm.add_state(ai.state_positioning)
		
		ai.state_seeking = t_ai_state_seeking.Create(ai)
		ai.fsm.add_state(ai.state_seeking)
		
		ai.state_defending = t_ai_state_defending.Create(ai)
		ai.fsm.add_state(ai.state_defending)
		
		ai.state_attacking = t_ai_state_attacking.Create(ai)
		ai.fsm.add_state(ai.state_attacking)
		
		ai.state_passing = t_ai_state_passing.Create(ai)
		ai.fsm.add_state(ai.state_passing)
		
		ai.state_kicking = t_ai_state_kicking.Create(ai)
		ai.fsm.add_state(ai.state_kicking)
		
		ai.state_goal_kicking = t_ai_state_goal_kicking.Create(ai)
		ai.fsm.add_state(ai.state_goal_kicking)
		
		ai.state_throwing_in = t_ai_state_throwing_in.Create(ai)
		ai.fsm.add_state(ai.state_throwing_in)
		
		ai.state_corner_kicking = t_ai_state_corner_kicking.Create(ai)
		ai.fsm.add_state(ai.state_corner_kicking)
		
		ai.state_keeper_kicking = t_ai_state_keeper_kicking.Create(ai)
		ai.fsm.add_state(ai.state_keeper_kicking)
		
		ai.fsm.set_state("state_idle")
		
		Return ai
	End Function
	
	Method set_player(p:t_player)
		Self.player = p
	End Method
	
	Method get_player:t_player()
		Return Self.player
	End Method
	
	Method _read()
		If (Not Self.player)
			Return
		EndIf
		Self.fsm.think()
	End Method
	
End Type

Type t_ai_state Extends t_state
	
	Field ai:t_ai
	Field player:t_player
	
End Type

Type t_ai_state_idle Extends t_ai_state
	
	Method New()
		Self.set_name("state_idle")
	End Method
	
	Function Create:t_ai_state_idle(ai:t_ai)
		Local s:t_ai_state_idle = New t_ai_state_idle
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		Self.ai.set_x(0)
		Self.ai.set_y(0)
		Self.ai.set_fire1(False)
		Self.ai.set_fire2(False)
	End Method
	
	Method check_conditions:t_state()
		Local player_state:t_state = Self.player.get_active_state()
		If (player_state)
			Select (player_state.get_name())
				Case "state_kick_off"
					Return Self.ai.state_kicking_off
					
				Case "state_stand_run"
					Return Self.ai.state_positioning
					
				Case "state_goal_kick"
					Return Self.ai.state_goal_kicking
					
				Case "state_throw_in_angle"
					Return Self.ai.state_throwing_in
					
				Case "state_corner_kick_angle"
					Return Self.ai.state_corner_kicking
					
				Case "state_keeper_kick_angle"
					Return Self.ai.state_keeper_kicking
			End Select
		EndIf
		Return Null
	End Method
	
End Type

Type t_ai_state_kicking_off Extends t_ai_state
	
	Method New()
		Self.set_name("state_kicking_off")
	End Method
	
	Function Create:t_ai_state_kicking_off(ai:t_ai)
		Local s:t_ai_state_kicking_off = New t_ai_state_kicking_off
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		Self.ai.set_x(Self.player.team.side)
		Self.ai.set_fire1(is_in(Self.timer, 1.0*REFRATE, 1.05*REFRATE))
	End Method
	
	Method check_conditions:t_state()
		If (Self.timer > 1.2*REFRATE)
			Return Self.ai.state_idle
		EndIf
		Return Null
	End Method
	
End Type

Type t_ai_state_positioning Extends t_ai_state
	
	Method New()
		Self.set_name("state_positioning")
	End Method
	
	Function Create:t_ai_state_positioning(ai:t_ai)
		Local s:t_ai_state_positioning = New t_ai_state_positioning
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		If (Self.player.target_distance() > 20)
			Local a:Float = player.target_angle()
			Self.ai.set_x(round(Cos(a)))
			Self.ai.set_y(round(Sin(a)))
		Else
			Self.ai.set_x(0)
			Self.ai.set_y(0)
		EndIf
	End Method
	
	Method check_conditions:t_state()
		Local player_state:t_state = Self.player.get_active_state()
		If (player_state And Not player_state.check_name("state_stand_run"))
			Return Self.ai.state_idle
		EndIf
		
		If (Self.player = player.team.near1)
			If (ball.owner = Null)
				Return Self.ai.state_seeking
			ElseIf (ball.owner = Self.player)
				Return Self.ai.state_attacking
			EndIf
		EndIf
		
		If (Self.player.team.best_defender = Self.player)
			Return Self.ai.state_defending
		EndIf
		
		Return Null
	End Method
	
End Type

Type t_ai_state_seeking Extends t_ai_state
	
	Method New()
		Self.set_name("state_seeking")
	End Method
	
	Function Create:t_ai_state_seeking(ai:t_ai)
		Local s:t_ai_state_seeking = New t_ai_state_seeking
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		Local d:Int = player.frame_distance
		If (d < BALL_PREDICTION)
			Local b:t_vec3d = ball.prediction[d]
			Local a:Float = ATan2(b.y -Self.player.y, b.x -Self.player.x)
			Self.ai.set_x(round(Cos(a)))
			Self.ai.set_y(round(Sin(a)))
		EndIf
	End Method
	
	Method check_conditions:t_state()
		''player has changed its state
		Local player_state:t_state = Self.player.get_active_state()
		If (player_state And Not player_state.check_name("state_stand_run"))
			Return Self.ai.state_idle
		EndIf
		
		''someone has got the ball
		If (ball.owner <> Null)
			''player
			If (ball.owner = Self.player)
				Return Self.ai.state_attacking
			EndIf
			''mate
			If (ball.owner.team = Self.player.team)
				Return Self.ai.state_positioning
			EndIf
		EndIf
		
		''no longer the nearest
		If (Self.player.team.near1 <> Self.player)
			Return Self.ai.state_positioning
		EndIf
		
		Return Null
	End Method
	
End Type

Type t_ai_state_defending Extends t_ai_state
	
	Method New()
		Self.set_name("state_defending")
	End Method
	
	Function Create:t_ai_state_defending(ai:t_ai)
		Local s:t_ai_state_defending = New t_ai_state_defending
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		Local d:Int = player.frame_distance
		If (d < BALL_PREDICTION)
			Local b:t_vec3d = ball.prediction[d]
			Local a:Float = ATan2(b.y -Self.player.y, b.x -Self.player.x)
			Self.ai.set_x(round(Cos(a)))
			Self.ai.set_y(round(Sin(a)))
		EndIf
	End Method
	
	Method check_conditions:t_state()
		''player has changed its state
		Local player_state:t_state = Self.player.get_active_state()
		If (player_state And Not player_state.check_name("state_stand_run"))
			Return Self.ai.state_idle
		EndIf
		
		''got the ball
		If (ball.owner <> Null)
			''self
			If (ball.owner = Self.player)
				Return Self.ai.state_attacking
			EndIf
			''mate
			If (ball.owner.team = Self.player.team)
				Return Self.ai.state_positioning
			EndIf
		EndIf
		
		''no longer the best defender
		If (Self.player.team.best_defender <> Self.player)
			Return Self.ai.state_positioning
		EndIf
		
		Return Null
	End Method
	
End Type

Type t_ai_state_attacking Extends t_ai_state
	
	Method New()
		Self.set_name("state_attacking")
	End Method
	
	Function Create:t_ai_state_attacking(ai:t_ai)
		Local s:t_ai_state_attacking = New t_ai_state_attacking
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.ai.set_x(-Sgn(round(ball.x/(POST_X + POST_R + BALL_R))))
		Self.ai.set_y(-Self.player.team.side)
		
		Self.player.search_facing_player(False)
	End Method
	
	Method check_conditions:t_state()
		''player has changed its state
		Local player_state:t_state = Self.player.get_active_state()
		If (player_state And Not player_state.check_name("state_stand_run"))
			Return Self.ai.state_idle
		EndIf
		
		''lost the ball
		If (ball.owner <> Self.player)
			Return Self.ai.state_seeking
		EndIf
		
		''passing
		If (Self.player.facing_player And (Self.timer > 0.25*REFRATE))
			Return Self.ai.state_passing
		EndIf
		
		''near the goal
		If (is_in(ball.y, -Self.player.team.side * (GOAL_LINE -1.5*PENALTY_AREA_H), -Self.player.team.side * GOAL_LINE) And (ball.z < 4))
			Return Self.ai.state_kicking
		EndIf
		
		Return Null
	End Method
	
End Type

Type t_ai_state_passing Extends t_ai_state
	
	Field duration:Int
	
	Method New()
		Self.set_name("state_passing")
	End Method
	
	Function Create:t_ai_state_passing(ai:t_ai)
		Local s:t_ai_state_passing = New t_ai_state_passing
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.ai.set_x(Self.ai.get_x())
		Self.ai.set_y(Self.ai.get_y())
		Self.ai.set_fire1(is_in(Self.timer, 2, Self.duration))
	End Method
	
	Method check_conditions:t_state()
		If (Self.timer > 0.5*REFRATE)
			Return Self.ai.state_idle
		EndIf
		
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		Local d:Float = dist(Self.player.x, Self.player.y, 0, Sgn(Self.player.y) * GOAL_LINE)
		Self.duration = 0.01 * (4 + d / 50) * REFRATE
	End Method
	
End Type

Type t_ai_state_kicking Extends t_ai_state
	
	Field duration:Int
	
	Method New()
		Self.set_name("state_kicking")
	End Method
	
	Function Create:t_ai_state_kicking(ai:t_ai)
		Local s:t_ai_state_kicking = New t_ai_state_kicking
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.ai.set_x(-Sgn(round(ball.x/(POST_X + POST_R + BALL_R))))
		Self.ai.set_y(-Self.player.team.side)
		Self.ai.set_fire1(True)
	End Method
	
	Method check_conditions:t_state()
		If (Self.timer > Self.duration)
			Return Self.ai.state_idle
		EndIf
		
		Return Null
	End Method
	
	Method entry_actions()
		Super.entry_actions()
		
		Local d:Float = dist(Self.player.x, Self.player.y, 0, Sgn(Self.player.y) * GOAL_LINE)
		Self.duration = 0.01 * (Rand(0, 8) + d / 50) * REFRATE
	End Method
	
End Type

Type t_ai_state_goal_kicking Extends t_ai_state
	
	Method New()
		Self.set_name("state_goal_kicking")
	End Method
	
	Function Create:t_ai_state_goal_kicking(ai:t_ai)
		Local s:t_ai_state_goal_kicking = New t_ai_state_goal_kicking
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.ai.set_x(0)
		Self.ai.set_y(0)
		Self.ai.set_fire1(is_in(Self.timer, 1.0*REFRATE, 1.05*REFRATE))
	End Method
	
	Method check_conditions:t_state()
		If (Self.timer > 1.5*REFRATE)
			Return Self.ai.state_idle
		EndIf
		Return Null
	End Method
	
End Type

Type t_ai_state_throwing_in Extends t_ai_state
	
	Method New()
		Self.set_name("state_throwing_in")
	End Method
	
	Function Create:t_ai_state_throwing_in(ai:t_ai)
		Local s:t_ai_state_throwing_in = New t_ai_state_throwing_in
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		If (Self.timer > 1.5*REFRATE)
			Self.ai.set_x(Self.player.team.side)
		EndIf
		Self.ai.set_fire1(is_in(Self.timer, 2.0*REFRATE, 2.31*REFRATE))
	End Method
	
	Method check_conditions:t_state()
		Select (Self.player.get_active_state())
			Case Self.player.state_throw_in_angle
				Return Null
			Case Self.player.state_throw_in_speed
				Return Null
			Default
				Return Self.ai.state_idle
		End Select
		Return Null
	End Method
	
End Type

Type t_ai_state_corner_kicking Extends t_ai_state
	
	Method New()
		Self.set_name("state_corner_kicking")
	End Method
	
	Function Create:t_ai_state_corner_kicking(ai:t_ai)
		Local s:t_ai_state_corner_kicking = New t_ai_state_corner_kicking
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.ai.set_x(is_in(Self.timer, 0.5*REFRATE, 0.55*REFRATE) * Self.player.team.side)
		Self.ai.set_y(0)
		Self.ai.set_fire1(is_in(Self.timer, 1.0*REFRATE, 1.05*REFRATE))
	End Method
	
	Method check_conditions:t_state()
		Select (Self.player.get_active_state())
			Case Self.player.state_corner_kick_angle
				Return Null
			Case Self.player.state_corner_kick_speed
				Return Null
			Default
				Return Self.ai.state_idle
		End Select
		Return Null
	End Method
End Type

Type t_ai_state_keeper_kicking Extends t_ai_state
	
	Method New()
		Self.set_name("state_keeper_kicking")
	End Method
	
	Function Create:t_ai_state_keeper_kicking(ai:t_ai)
		Local s:t_ai_state_keeper_kicking = New t_ai_state_keeper_kicking
		s.ai = ai
		s.player = ai.get_player()
		Return s
	End Function
	
	Method do_actions()
		Super.do_actions()
		
		Self.ai.set_x(0)
		Self.ai.set_y((2*(Self.timer > 0.5*REFRATE) -1) * Self.player.team.side)
		Self.ai.set_fire1(is_in(Self.timer, 1.0*REFRATE, 1.05*REFRATE))
	End Method
	
	Method check_conditions:t_state()
		Select (Self.player.get_active_state())
			Case Self.player.state_keeper_kick_angle
				Return Null
			Case Self.player.state_kick
				Return Null
			Default
				Return Self.ai.state_idle
		End Select
		Return Null
	End Method
	
End Type


'EDIT PNG PALETTE: change 'count' colors in a png image stored in a bankstream
Function edit_png_palette(bank_stream:TBankStream, bank:TBank, rgb_pairs:t_color_replacement_list) 


	'--- READ PALETTE INSIDE THE IMAGE ---

	'search for the palette 'chunk'
	SeekStream(bank_stream, 0)
	Local i0:Byte = ReadByte(bank_stream)
	Local i1:Byte = ReadByte(bank_stream)
	Local i2:Byte = ReadByte(bank_stream)
	Local i3:Byte = ReadByte(bank_stream)

	Local palette_found:Int = False

	While Not Eof(bank_stream)

		If Chr(i0)+Chr(i1)+Chr(i2)+Chr(i3) = "PLTE"
			palette_found = True
			Exit
		EndIf

		i0 = i1 ; i1 = i2 ; i2 = i3 ; i3 = ReadByte(bank_stream)
	
	Wend

	If Not palette_found
		RuntimeError("Palette not found inside image!")
	EndIf

	Local palette_offset:Int = StreamPos(bank_stream) - 8

	'read data size
	SeekStream(bank_stream, palette_offset)
	Local data_size:Int = 0
	For Local i:Int = 0 To 3
		data_size = 256*data_size + ReadByte(bank_stream) 
	Next

	'--- CHANGE COLORS INSIDE PALETTE ---
	For Local i:Int = 0 To (data_size/3)-1

		Local r:Int	= PeekByte(bank, palette_offset + 8 +3*i)
		Local g:Int	= PeekByte(bank, palette_offset + 8 +3*i +1)
		Local b:Int	= PeekByte(bank, palette_offset + 8 +3*i +2)

		For Local pair:t_rgb_pair = EachIn rgb_pairs
			If (rgb(r, g, b) = pair.rgb_old)
				PokeByte(bank, palette_offset + 8 +3*i, red(pair.rgb_new))
				PokeByte(bank, palette_offset + 8 +3*i +1, green(pair.rgb_new))
				PokeByte(bank, palette_offset + 8 +3*i +2, blue(pair.rgb_new))
			EndIf
		Next
	Next

	'calculate crc		
	Local crc32:Int = crc_bank(bank, palette_offset + 4, data_size + 4, crc_table)

	'write crc
	For Local i:Int = 0 To 3
		PokeByte(bank, palette_offset + 8 + data_size + i, (crc32 Shr (8*(3-i))) & $FF)
	Next

End Function

Function load_and_edit_png:TImage(filename:String, rgb_pairs:t_color_replacement_list, flags:Int = -1, mask_color:Int = 0)
	
	''load image into a bankstream
	Local file_image:TStream = ReadStream(filename)
	
	''create bank & bankstream
	Local bank:TBank = CreateBank()
	Local bank_stream:TBankStream = CreateBankStream(bank)

	''copy file_image to bankstream
	CopyStream(file_image, bank_stream)
	CloseStream(file_image)
	
	edit_png_palette(bank_stream, bank, rgb_pairs)
	
	SeekStream(bank_stream, 0)

	set_mask_color(mask_color)
	
	Local image:TImage = LoadImage(bank_stream, flags)
	
	Return image

End Function
