SuperStrict

Import "t_game_mode.bmx"

Type t_menu_league_info Extends t_game_mode

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*520, 30, 520, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(league.name, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''season/pitch type
		w = New t_button
		w.set_geometry(512 -370, 200, 440, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(league.get_byseason_label(), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''season start
		w = New t_button
		w.set_geometry(512 +90, 200, 100, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(Left(month_name[league.season_start], 3), 0, 14)
		w.active = False
		w.visible = (league.byseason = True)
		Self.widgets.AddLast(w)
	
		''season separator
		w = New t_button
		w.set_geometry(512 +190, 200, 40, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text("-", 0, 14)
		w.active = False
		w.visible = (league.byseason = True)
		Self.widgets.AddLast(w)
	
		''season end
		w = New t_button
		w.set_geometry(512 +230, 200, 100, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(Left(month_name[league.season_end], 3), 0, 14)
		w.active = False
		w.visible = (league.byseason = True)
		Self.widgets.AddLast(w)
	
		''pitch type
		w = New t_button
		w.set_geometry(512 +90, 200, 240, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(pitch_types[league.pitch_type], 0, 14)
		w.active = False
		w.visible = (league.byseason = False)
		Self.widgets.AddLast(w)
	
		''time (day/night)
		w = New t_button
		w.set_geometry(512 -370, 245, 440, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("TIME"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 245, 240, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(league.get_time_label(), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''number of teams
		w = New t_button
		w.set_geometry(512 -370, 290, 440, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("NUMBER OF TEAMS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 290, 240, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(league.number_of_teams, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''play each team
		w = New t_button
		w.set_geometry(512 -370, 335, 440, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("PLAY EACH TEAM"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 335, 240, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(Chr(215) + league.rounds, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''points for a win
		w = New t_button
		w.set_geometry(512 -370, 380, 440, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("POINTS FOR A WIN"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 380, 240, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(league.points_for_a_win, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''substitutes
		w = New t_button
		w.set_geometry(512 -370, 425, 440, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("SUBSTITUTES"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 425, 240, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(league.substitutions, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''bench size
		w = New t_button
		w.set_geometry(512 -370, 470, 440, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("BENCH SIZE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +90, 470, 240, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(league.bench_size, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_VIEW_STATISTICS)])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
	End Method

End Type
