SuperStrict

Import "t_game_mode.bmx"

Type t_menu_friendly Extends t_game_mode

	Field w_substitutes:t_widget
	Field w_bench_size:t_widget

	Method New()
	
		Self.type_id = TTypeId.ForObject(Self)
		
		menu.status = MS_FRIENDLY
		team_list_target = 2

		''background
		Self.img_background = backgrounds.get("menu_friendly.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		w.set_colors($2D855D, $3DB37D, $1E5027)
		w.set_text(dictionary.gettext("FRIENDLY"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''substitutes
		w = New t_button
		w.set_geometry(512 -440, 200, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("SUBSTITUTES"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_geometry(512 + 80, 200, 240, 36)
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
		w.set_geometry(512 -440, 250, 440, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("BENCH SIZE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 + 80, 250, 240, 36)
		w.set_colors($3C3C78, $5858AC, $202040)
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
		w.set_geometry(512 -0.5*340, 400, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CLUB TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_club_teams")
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

		''national teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 475, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("NATIONAL TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_national_teams")
		Self.widgets.AddLast(w)
	
		''custom teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 550, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CUSTOM TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_custom_teams")
		Self.widgets.AddLast(w)
	
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MAIN)])
		Self.widgets.AddLast(w)
	
	End Method

	Method bc_substitutes(n:Int)
		game_settings.substitutions = slide(game_settings.substitutions, 2, game_settings.bench_size, n)
		Self.update_substitutes_button()
	End Method
	
	Method bc_bench_size(n:Int)
		game_settings.bench_size = slide(game_settings.bench_size, 2, 5, n)
		game_settings.substitutions = Min(game_settings.substitutions, game_settings.bench_size)
		Self.update_bench_size_button()
		Self.update_substitutes_button()
	End Method
	
	Method bc_club_teams()
		team_list = New TList
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_CONFEDERATION)
	End Method
	
	Method bc_national_teams()
		team_list = New TList
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_NATIONALS_CONFEDERATION)
	End Method
	
	Method bc_custom_teams()
		team_list = New TList
		menu.confederation = t_confederation.CUSTOM
		menu.division = 1
		menu.extension = "cus"
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAMS)
	End Method	
	
	Method update_substitutes_button()
		Self.w_substitutes.set_text(game_settings.substitutions)
	End Method
	
	Method update_bench_size_button()
		Self.w_bench_size.set_text(game_settings.bench_size)
	End Method
	
End Type
