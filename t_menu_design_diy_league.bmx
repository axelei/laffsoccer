SuperStrict

Import "t_game_mode.bmx"

Type t_menu_design_diy_league Extends t_game_mode
	
	Field w_league_name:t_input_button
	Field w_season_pitch_type:t_widget
	Field w_season_start:t_widget
	Field w_season_separator:t_widget
	Field w_season_end:t_widget
	Field w_pitch_type:t_widget
	Field w_time:t_widget
	Field w_number_of_teams:t_widget
	Field w_play_each_time:t_widget
	Field w_points_for_a_win:t_widget
	Field w_substitutes:t_widget
	Field w_bench_size:t_widget
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
		
		''create new league
		league = New t_league
		league.name = dictionary.gettext("DIY LEAGUE")
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*520, 30, 520, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(dictionary.gettext("DESIGN DIY LEAGUE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''name
		Self.w_league_name = New t_input_button
		Self.w_league_name.set_geometry(512 -370, 120, 700, 36)
		Self.w_league_name.set_colors($1F1F95, $3030D4, $151563)
		Self.w_league_name.set_text(league.name, 0, 14)
		Self.w_league_name.set_entry_limit(24)
		Self.w_league_name.bind("on_update", "bc_update_league_name")
		Self.widgets.AddLast(Self.w_league_name)
		
		''season/pitch type
		w = New t_button
		w.set_geometry(512 -370, 165, 440, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_season_pitch_type")
		Self.w_season_pitch_type = w
		Self.update_season_pitch_type_button()
		Self.widgets.AddLast(w)
		
		''season start
		w = New t_button
		w.set_geometry(512 +90, 165, 100, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_season_start", ["+1"])
		w.bind("fire1_hold", "bc_season_start", ["+1"])
		w.bind("fire2_down", "bc_season_start", ["-1"])
		w.bind("fire2_hold", "bc_season_start", ["-1"])
		Self.w_season_start = w
		Self.update_season_start_button()
		Self.widgets.AddLast(w)
		
		''season separator
		w = New t_button
		w.set_geometry(512 +190, 165, 40, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text("-", 0, 14)
		w.active = False
		Self.w_season_separator = w
		Self.widgets.AddLast(w)
		
		''season end
		w = New t_button
		w.set_geometry(512 +230, 165, 100, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_season_end", ["+1"])
		w.bind("fire1_hold", "bc_season_end", ["+1"])
		w.bind("fire2_down", "bc_season_end", ["-1"])
		w.bind("fire2_hold", "bc_season_end", ["-1"])
		Self.w_season_end = w	
		Self.update_season_end_button()
		Self.widgets.AddLast(w)
		
		''pitch type
		w = New t_button
		w.set_geometry(512 +90, 165, 240, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.visible = False
		w.bind("fire1_down", "bc_pitch_type", ["+1"])
		w.bind("fire1_hold", "bc_pitch_type", ["+1"])
		w.bind("fire2_down", "bc_pitch_type", ["-1"])
		w.bind("fire2_hold", "bc_pitch_type", ["-1"])
		Self.w_pitch_type = w	
		Self.update_pitch_type_button()
		Self.widgets.AddLast(w)
	
		''time (day/night)
		w = New t_button
		w.set_geometry(512 -370, 210, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("TIME"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 210, 240, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_time")
		Self.w_time = w
		Self.update_time_button()
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

		''number of teams
		w = New t_button
		w.set_geometry(512 -370, 255, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("NUMBER OF TEAMS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 255, 240, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_number_of_teams", ["+1"])
		w.bind("fire1_hold", "bc_number_of_teams", ["+1"])
		w.bind("fire2_down", "bc_number_of_teams", ["-1"])
		w.bind("fire2_hold", "bc_number_of_teams", ["-1"])
		Self.w_number_of_teams = w
		Self.update_number_of_teams_button()
		Self.widgets.AddLast(w)
		
		''play each team
		w = New t_button
		w.set_geometry(512 -370, 300, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("PLAY EACH TEAM"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 300, 240, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_play_each_time", ["+1"])
		w.bind("fire1_hold", "bc_play_each_time", ["+1"])
		w.bind("fire2_down", "bc_play_each_time", ["-1"])
		w.bind("fire2_hold", "bc_play_each_time", ["-1"])
		Self.w_play_each_time = w
		Self.update_play_each_time_button()
		Self.widgets.AddLast(w)
		
		''points for a win
		w = New t_button
		w.set_geometry(512 -370, 345, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("POINTS FOR A WIN"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 345, 240, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_points_for_a_win")
		Self.w_points_for_a_win = w
		Self.update_points_for_a_win_button()
		Self.widgets.AddLast(w)
		
		''substitutes
		w = New t_button
		w.set_geometry(512 -370, 390, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("SUBSTITUTES"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 390, 240, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_substitutes", ["+1"])
		w.bind("fire1_hold", "bc_substitutes", ["+1"])
		w.bind("fire2_down", "bc_substitutes", ["-1"])
		w.bind("fire2_hold", "bc_substitutes", ["-1"])
		Self.w_substitutes = w
		Self.update_substitutes_button()
		Self.widgets.AddLast(w)
	
		''bench size
		w = New t_button
		w.set_geometry(512 -370, 435, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("BENCH SIZE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 435, 240, 36)
		w.set_colors($1F1F95, $3030D4, $151563)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_bench_size", ["+1"])
		w.bind("fire1_hold", "bc_bench_size", ["+1"])
		w.bind("fire2_down", "bc_bench_size", ["-1"])
		w.bind("fire2_hold", "bc_bench_size", ["-1"])
		Self.w_bench_size = w
		Self.update_bench_size_button()
		Self.widgets.AddLast(w)
	
		''club teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 505, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CLUB TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_club_teams")
		Self.widgets.AddLast(w)
	
		''national teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 565, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("NATIONAL TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_national_teams")
		Self.widgets.AddLast(w)
	
		''custom teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 625, 340, 40)
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

	Method bc_update_league_name()
		league.name = Self.w_league_name.text
	End Method
	
	Method bc_season_pitch_type()
		league.byseason = Not league.byseason
		
		Self.w_season_start.visible	    = (league.byseason = True)
		Self.w_season_start.active      = (league.byseason = True)
		Self.w_season_separator.visible = (league.byseason = True)
		Self.w_season_end.visible       = (league.byseason = True)
		Self.w_season_end.active        = (league.byseason = True)
		Self.w_pitch_type.visible       = (league.byseason = False)
		Self.w_pitch_type.active        = (league.byseason = False)
		
		Self.update_season_pitch_type_button()
	End Method
	
	Method bc_season_start(n:Int)
		league.season_start = rotate(league.season_start, 0, 11, n)
		Self.update_season_start_button()
	End Method
	
	Method bc_season_end(n:Int)
		league.season_end = rotate(league.season_end, 0, 11, n)
		Self.update_season_end_button()
	End Method
	
	Method bc_pitch_type(n:Int)
		league.pitch_type = rotate(league.pitch_type, 0, 9, n)
		Self.update_pitch_type_button()
	End Method
	
	Method bc_time()
		league.time = rotate(league.time, TI.DAY, TI.NIGHT, 1)
		Self.update_time_button()
	End Method
	
	Method bc_number_of_teams(n:Int)
		league.number_of_teams = slide(league.number_of_teams, 2, 24, n)
		Self.update_number_of_teams_button()
	End Method
	
	Method bc_play_each_time(n:Int)
		league.rounds = slide(league.rounds, 1, 10, n)
		Self.update_play_each_time_button()
	End Method
	
	Method bc_points_for_a_win()
		league.points_for_a_win = rotate(league.points_for_a_win, 2, 3, 1)
		Self.update_points_for_a_win_button()
	End Method
	
	Method bc_substitutes(n:Int)
		league.substitutions = slide(league.substitutions, 2, league.bench_size, n)
		Self.update_substitutes_button()
	End Method
	
	Method bc_bench_size(n:Int)
		league.bench_size = slide(league.bench_size, 2, 5, n)
		league.substitutions = Min(league.substitutions, league.bench_size)
		Self.update_bench_size_button()
		Self.update_substitutes_button()
	End Method
	
	Method bc_club_teams()
		league.total_matches = league.number_of_teams * (league.number_of_teams -1)
		league.total_matches = (league.total_matches * league.rounds) / 2

		team_list = New TList
		team_list_target = league.number_of_teams

		competition = league
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_CONFEDERATION)
	End Method
	
	Method bc_national_teams()
		league.total_matches = league.number_of_teams * (league.number_of_teams -1)
		league.total_matches = (league.total_matches * league.rounds) / 2

		team_list = New TList
		team_list_target = league.number_of_teams

		competition = league
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_NATIONALS_CONFEDERATION)
	End Method
	
	Method bc_custom_teams()
		league.total_matches = league.number_of_teams * (league.number_of_teams -1)
		league.total_matches = (league.total_matches * league.rounds) / 2
		
		team_list = New TList
		team_list_target = league.number_of_teams
		menu.confederation = t_confederation.CUSTOM
		menu.division = 1
		menu.extension = "cus"
		
		competition = league
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAMS)
	End Method
	
	Method bc_exit()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
		league = Null
	End Method
	
	Method update_season_pitch_type_button()
		Self.w_season_pitch_type.set_text(league.get_byseason_label())
	End Method
	
	Method update_season_start_button()
		Self.w_season_start.set_text(Left(month_name[league.season_start], 3))
	End Method
	
	Method update_season_end_button()
		Self.w_season_end.set_text(Left(month_name[league.season_end], 3))
	End Method
	
	Method update_pitch_type_button()
		Self.w_pitch_type.set_text(pitch_types[league.pitch_type])
	End Method
	
	Method update_time_button()
		Self.w_time.set_text(league.get_time_label())
	End Method
	
	Method update_number_of_teams_button()
		Self.w_number_of_teams.set_text(league.number_of_teams)
	End Method
	
	Method update_play_each_time_button()
		Self.w_play_each_time.set_text(Chr(215) + league.rounds)
	End Method
	
	Method update_points_for_a_win_button()
		Self.w_points_for_a_win.set_text(league.points_for_a_win)
	End Method
	
	Method update_substitutes_button()
		Self.w_substitutes.set_text(league.substitutions)
	End Method
	
	Method update_bench_size_button()
		Self.w_bench_size.set_text(league.bench_size)
	End Method
	
End Type
