SuperStrict

Import "t_game_mode.bmx"

Type t_menu_design_diy_cup Extends t_game_mode

	Field w_cup_name:t_input_button
	Field w_season_start:t_widget
	Field w_season_separator:t_widget
	Field w_season_end:t_widget
	Field w_pitch_type:t_widget
	Field w_away_goals:t_widget
	Field w_substitutes:t_widget
	Field w_round_name:t_widget[6]
	Field w_round_teams:t_widget[6]
	Field w_round_legs:t_widget[6]
	Field w_round_extra_time:t_widget[6]
	Field w_round_penalties:t_widget[6]
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''create new cup
		cup = New t_cup
		cup.name = dictionary.gettext("DIY CUP")

		menu.show_result = False
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*520, 30, 520, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(dictionary.gettext("DESIGN DIY CUP"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''name
		Self.w_cup_name = New t_input_button
		Self.w_cup_name.set_geometry(512 -470, 101, 930, 36)
		Self.w_cup_name.set_colors($1F1F95, $3030D4, $151563)
		Self.w_cup_name.set_text(cup.name, 0, 14)
		Self.w_cup_name.set_entry_limit(24)
		Self.w_cup_name.bind("on_update", "bc_update_cup_name")
		Self.widgets.AddLast(Self.w_cup_name)
		
		''season/pitch type
		w = New t_button
		w.set_geometry(512 -470, 144, 220, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(cup.get_byseason_label(), 0, 14)
		w.bind("fire1_down", "bc_season_pitch_type")
		Self.widgets.AddLast(w)
	
		''season start
		w = New t_button
		w.set_geometry(512 -240, 144, 70, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(Left(month_name[cup.season_start],3), 0, 14)
		w.bind("fire1_down", "bc_season_start", ["+1"])
		w.bind("fire1_hold", "bc_season_start", ["+1"])
		w.bind("fire2_down", "bc_season_start", ["-1"])
		w.bind("fire2_hold", "bc_season_start", ["-1"])
		Self.w_season_start = w
		Self.widgets.AddLast(w)
		
		''season separator
		w = New t_button
		w.set_geometry(512 -170, 144, 40, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text("-", 0, 14)
		w.active = False
		Self.w_season_separator = w	
		Self.widgets.AddLast(w)
	
		''season end
		w = New t_button
		w.set_geometry(512 -130, 144, 70, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(Left(month_name[cup.season_end],3), 0, 14)
		w.bind("fire1_down", "bc_season_end", ["+1"])
		w.bind("fire1_hold", "bc_season_end", ["+1"])
		w.bind("fire2_down", "bc_season_end", ["-1"])
		w.bind("fire2_hold", "bc_season_end", ["-1"])
		Self.w_season_end = w
		Self.widgets.AddLast(w)
		
		''pitch type
		w = New t_button
		w.set_geometry(512 -240, 144, 180, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(pitch_types[cup.pitch_type], 0, 14)
		w.visible = False
		w.bind("fire1_down", "bc_pitch_type", ["+1"])
		w.bind("fire1_hold", "bc_pitch_type", ["+1"])
		w.bind("fire2_down", "bc_pitch_type", ["-1"])
		w.bind("fire2_hold", "bc_pitch_type", ["-1"])
		Self.w_pitch_type = w	
		Self.widgets.AddLast(w)
	
		''away goals (off/after 90 mins/after extra time)
		w = New t_button
		w.set_geometry(512 -40, 144, 218, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("AWAY GOALS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +188, 144, 272, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(cup.get_away_goals_label(), 0, 14)
		w.bind("fire1_down", "bc_away_goals")
		Self.w_away_goals = w
		Self.update_away_goals_visibility()
		Self.widgets.AddLast(w)
	
		''time (day/night)
		w = New t_button
		w.set_geometry(512 -470, 187, 230, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("TIME"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 -230, 187, 180, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(cup.get_time_label(), 0, 14)
		w.bind("fire1_down", "bc_time")
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

		''rounds
		w = New t_button
		w.set_geometry(512 -470, 230, 230, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("ROUNDS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 -230, 230, 180, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(cup.rounds, 0, 14)
		w.bind("fire1_down", "bc_rounds", ["+1"])
		w.bind("fire1_hold", "bc_rounds", ["+1"])
		w.bind("fire2_down", "bc_rounds", ["-1"])
		w.bind("fire2_hold", "bc_rounds", ["-1"])
		Self.widgets.AddLast(w)
		
		''substitutes
		w = New t_button
		w.set_geometry(512 -30, 187, 320, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("SUBSTITUTES"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +300, 187, 160, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(cup.substitutions, 0, 14)
		w.bind("fire1_down", "bc_substitutes", ["+1"])
		w.bind("fire1_hold", "bc_substitutes", ["+1"])
		w.bind("fire2_down", "bc_substitutes", ["-1"])
		w.bind("fire2_hold", "bc_substitutes", ["-1"])
		Self.w_substitutes = w
		Self.widgets.AddLast(w)

		''bench size
		w = New t_button
		w.set_geometry(512 -30, 230, 320, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("BENCH SIZE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +300, 230, 160, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text(cup.bench_size, 0, 14)
		w.bind("fire1_down", "bc_bench_size", ["+1"])
		w.bind("fire1_hold", "bc_bench_size", ["+1"])
		w.bind("fire2_down", "bc_bench_size", ["-1"])
		w.bind("fire2_hold", "bc_bench_size", ["-1"])
		Self.widgets.AddLast(w)
		
		''teams
		w = New t_label
		w.set_text(dictionary.gettext("TEAMS"), 0, 14)
		w.set_position(512 -205, 296)
		Self.widgets.AddLast(w)

		''description
		w = New t_label
		w.set_text(dictionary.gettext("DESCRIPTION"), 0, 14)
		w.set_position(512 -10 +105, 296)
		Self.widgets.AddLast(w)

		''rounds
		For Local r:Int = 0 To 5
			Local visible:Int = (r < cup.rounds)
			
			''name
			w = New t_button
			w.set_geometry(512 -470, 315 +34*r, 230, 32)
			w.set_colors($800000, $B40000, $400000)
			w.set_text(cup.round[r].name, 0, 14)
			w.active = False
			w.visible = visible
			Self.w_round_name[r] = w
			Self.widgets.AddLast(w)

			''teams
			w = New t_button
			w.set_geometry(512 -230, 315 +34*r, 50, 32)
			w.set_colors($800000, $B40000, $400000)
			w.set_text(cup.round[r].teams, 0, 14)
			w.active = False
			w.visible = visible
			Self.w_round_teams[r] = w
			Self.widgets.AddLast(w)

			''legs
			w = New t_button
			w.set_geometry(512 -170, 315 +34*r, 130, 32)
			w.set_colors($1F1F95, $3030D4, $151563)
			w.set_text(cup.get_legs_label(r), 0, 14)
			w.visible = visible
			w.bind("fire1_down", "bc_legs", [String(r)])
			Self.w_round_legs[r] = w
			Self.widgets.AddLast(w)

			''extra time
			w = New t_button
			w.set_geometry(512 -30, 315 +34*r, 240, 32)
			w.set_colors($1F1F95, $3030D4, $151563)
			w.set_text(cup.get_extra_time_label(r), 0, 14)
			w.visible = visible
			w.bind("fire1_down", "bc_extra_time", [String(r)])
			Self.w_round_extra_time[r] = w
			Self.widgets.AddLast(w)

			''penalties
			w = New t_button
			w.set_geometry(512 +220, 315 +34*r, 240, 32)
			w.set_colors($1F1F95, $3030D4, $151563)
			w.set_text(cup.get_penalties_label(r), 0, 14)
			w.visible = visible
			w.bind("fire1_down", "bc_penalties", [String(r)])
			Self.w_round_penalties[r] = w
			Self.widgets.AddLast(w)
			
			'*** temporary
			w.active = False
			w.set_colors($666666, $8F8D8D, $404040)
			'***
		Next
			
		''club teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 535, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CLUB TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_club_teams")
		Self.widgets.AddLast(w)
	
		''national teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 585, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("NATIONAL TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_national_teams")
		Self.widgets.AddLast(w)
	
		''custom teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 635, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CUSTOM TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_custom_teams")
		Self.widgets.AddLast(w)
	
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
	
	End Method

	Method bc_update_cup_name()
		cup.name = Self.w_cup_name.text
	End Method
	
	Method bc_season_pitch_type()
		cup.byseason = Not cup.byseason
		
		Self.w_season_start.visible     = cup.byseason
		Self.w_season_separator.visible = cup.byseason
		Self.w_season_end.visible       = cup.byseason
		Self.w_pitch_type.visible       = Not cup.byseason
		
		Self.selected_widget.text = cup.get_byseason_label()
	End Method
	
	Method bc_season_start(n:Int)
		cup.season_start = rotate(cup.season_start, 0, 11, n)
		Self.selected_widget.text = Left(month_name[cup.season_start],3)
	End Method
	
	Method bc_season_end(n:Int)
		cup.season_end = rotate(cup.season_end, 0, 11, n)
		Self.selected_widget.text = Left(month_name[cup.season_end],3)
	End Method
	
	Method bc_pitch_type(n:Int)
		cup.pitch_type = rotate(cup.pitch_type, 0, 9, n)
		Self.selected_widget.text = pitch_types[cup.pitch_type]
	End Method
	
	Method bc_away_goals()
		cup.away_goals = rotate(cup.away_goals, 0, 2, 1)
		Self.selected_widget.text = cup.get_away_goals_label()
	End Method
	
	Method bc_time()
		cup.time = rotate(cup.time, TI.DAY, TI.NIGHT, 1)
		Self.selected_widget.text = cup.get_time_label()
	End Method
	
	Method bc_rounds(n:Int)
		cup.set_rounds(slide(cup.rounds, 1, 6, n))
		Self.selected_widget.text = cup.rounds
		
		For Local r:Int = 0 To 5 
			Self.w_round_name[r].text        = cup.round[r].name
			Self.w_round_teams[r].text       = cup.round[r].teams
			Self.w_round_legs[r].text        = cup.get_legs_label(r)
			Self.w_round_extra_time[r].text  = cup.get_extra_time_label(r)
			Self.w_round_penalties[r].text   = cup.get_penalties_label(r)
			
			Local visible:Int = (r < cup.rounds)
			Self.w_round_name[r].visible        = visible
			Self.w_round_teams[r].visible       = visible
			Self.w_round_legs[r].visible        = visible
			Self.w_round_extra_time[r].visible  = visible
			Self.w_round_penalties[r].visible   = visible
		Next
		Self.update_away_goals_visibility()
	End Method
	
	Method bc_substitutes(n:Int)
		cup.substitutions = slide(cup.substitutions, 2, cup.bench_size, n)
		Self.selected_widget.text = cup.substitutions
	End Method
	
	Method bc_bench_size(n:Int)
		cup.bench_size = slide(cup.bench_size, 2, 5, n)
		cup.substitutions = Min(cup.substitutions, cup.bench_size)
		Self.selected_widget.text = cup.bench_size
		Self.w_substitutes.text = cup.substitutions
	End Method
	
	Method bc_legs(r:Int) 
		cup.round[r].legs = rotate(cup.round[r].legs, 1, 2, 1)
		Self.w_round_legs[r].text = cup.get_legs_label(r)
		Self.update_away_goals_visibility()
	End Method
	
	Method bc_extra_time(r:Int)
		cup.round[r].extra_time = rotate(cup.round[r].extra_time, 0, 2, 1)
		Self.w_round_extra_time[r].text = cup.get_extra_time_label(r)
	End Method
	
	Method bc_penalties(r:Int)
		cup.round[r].penalties = rotate(cup.round[r].penalties, 0, 2, 1)
		Self.w_round_penalties[r].text = cup.get_penalties_label(r)
	End Method
	
	Method bc_club_teams()
		cup.total_legs = 0
		For Local r:Int = 0 To cup.rounds-1
			cup.total_legs = cup.total_legs +cup.round[r].legs
		Next

		competition = cup
		
		team_list = New TList
		team_list_target = cup.round[0].teams
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_CONFEDERATION)
	End Method
	
	Method bc_national_teams()
		cup.total_legs = 0
		For Local r:Int = 0 To cup.rounds-1
			cup.total_legs = cup.total_legs +cup.round[r].legs
		Next

		competition = cup
		
		team_list = New TList
		team_list_target = cup.round[0].teams
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_NATIONALS_CONFEDERATION)
	End Method
	
	Method bc_custom_teams()
		cup.total_legs = 0
		For Local r:Int = 0 To cup.rounds-1
			cup.total_legs = cup.total_legs +cup.round[r].legs
		Next

		competition = cup;
		
		team_list = New TList
		team_list_target = cup.round[0].teams
		menu.confederation = t_confederation.CUSTOM
		menu.division = 1
		menu.extension = "cus"
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAMS)
	End Method
	
	Method bc_exit()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
		cup = Null
	End Method
	
	Method update_away_goals_visibility()
		Local visible:Int = False
		For Local r:Int = 0 To cup.rounds-1	
			If (cup.round[r].legs = 2)
				visible = True
			EndIf
		Next
		Self.w_away_goals.visible = visible
	End Method
	
End Type
