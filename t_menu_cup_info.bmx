SuperStrict

Import "t_game_mode.bmx"

Type t_menu_cup_info Extends t_game_mode

	Method New()
			
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*520, 30, 520, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(cup.name, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''season/pitch type
		w = New t_button
		w.set_geometry(512 -440, 195, 220, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(cup.get_byseason_label(), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		If (cup.byseason = True)
			
			''season start
			w = New t_button
			w.set_geometry(512 -210, 195, 70, 36)
			w.set_colors($666666, $8F8D8D, $404040)
			w.set_text(Left(month_name[cup.season_start], 3), 0, 14)
			w.active = False
			Self.widgets.AddLast(w)
	
			''season separator
			w = New t_button
			w.set_geometry(512 -140, 195, 40, 36)
			w.set_colors($666666, $8F8D8D, $404040)
			w.set_text("-", 0, 14)
			w.active = False
			Self.widgets.AddLast(w)
		
			''season end
			w = New t_button
			w.set_geometry(512 -100, 195, 70, 36)
			w.set_colors($666666, $8F8D8D, $404040)
			w.set_text(Left(month_name[cup.season_end], 3), 0, 14)
			w.active = False
			Self.widgets.AddLast(w)
		
		Else
		
			''pitch type
			w = New t_button
			w.set_geometry(512 -210, 195, 180, 36)
			w.set_colors($666666, $8F8D8D, $404040)
			w.set_text(pitch_types[cup.pitch_type], 0, 14)
			w.active = False
			Self.widgets.AddLast(w)
			
		EndIf
	
		''away goals (off/after 90 mins/after extra time)
		w = New t_button
		w.set_geometry(512 -10, 195, 180, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("AWAY GOALS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +180, 195, 250, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.visible	= False
		For Local r:Int = 0 To cup.rounds-1
			If (cup.round[r].legs = 2)
				w.visible = True
			EndIf
		Next
		w.set_text(cup.get_away_goals_label(), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''time (day/night)
		w = New t_button
		w.set_geometry(512 -440, 245, 230, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("TIME"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 -200, 245, 180, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(cup.get_time_label(), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''rounds
		w = New t_button
		w.set_geometry(512 -440, 295, 230, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("ROUNDS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 -200, 295, 180, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(cup.rounds, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''substitutes
		w = New t_button
		w.set_geometry(512, 245, 320, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("SUBSTITUTES"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +330, 245, 100, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(cup.substitutions, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''bench size
		w = New t_button
		w.set_geometry(512, 295, 320, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("BENCH SIZE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		w = New t_button
		w.set_geometry(512 +330, 295, 100, 36)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(cup.bench_size, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''teams
		w = New t_label
		w.set_text(dictionary.gettext("TEAMS"), 0, 14)
		w.set_position(512 -260 +75, 356)
		Self.widgets.AddLast(w)

		''description
		w = New t_label
		w.set_text(dictionary.gettext("DESCRIPTION"), 0, 14)
		w.set_position(512 -20 +105, 356)
		Self.widgets.AddLast(w)

		''rounds
		For Local r:Int = 0 To cup.rounds-1
			''name
			w = New t_button
			w.set_geometry(512 -440, 375 +34*r, 220, 32)
			If (cup.current_round = r)
				w.set_colors($663b5d, $8f5382, $40253a)
			Else
				w.set_colors($666666, $8F8D8D, $404040)
			EndIf
			w.set_text(cup.round[r].name, 0, 14)
			w.active = False
			Self.widgets.AddLast(w)

			''teams
			w = New t_button
			w.set_geometry(512 -210, 375 +34*r, 50, 32)
			If (cup.current_round = r)
				w.set_colors($663b5d, $8f5382, $40253a)
			Else
				w.set_colors($666666, $8F8D8D, $404040)
			EndIf
			w.set_text(cup.round[r].teams, 0, 14)
			w.active = False
			Self.widgets.AddLast(w)

			''legs
			w = New t_button
			w.set_geometry(512 -150, 375 +34*r, 120, 32)
			If (cup.current_round = r)
				w.set_colors($663b5d, $8f5382, $40253a)
			Else
				w.set_colors($666666, $8F8D8D, $404040)
			EndIf
			w.set_text(cup.get_legs_label(r), 0, 14)
			w.active = False
			Self.widgets.AddLast(w)

			''extra time
			w = New t_button
			w.set_geometry(512 -20, 375 +34*r, 210, 32)
			If (cup.current_round = r)
				w.set_colors($663b5d, $8f5382, $40253a)
			Else
				w.set_colors($666666, $8F8D8D, $404040)
			EndIf
			w.set_text(cup.get_extra_time_label(r), 0, 14)
			w.active = False
			Self.widgets.AddLast(w)

			''penalties
			w = New t_button
			w.set_geometry(512 +200, 375 +34*r, 230, 32)
			If (cup.current_round = r)
				w.set_colors($663b5d, $8f5382, $40253a)
			Else
				w.set_colors($666666, $8F8D8D, $404040)
			EndIf
			w.set_text(cup.get_penalties_label(r), 0, 14)
			w.active = False
			Self.widgets.AddLast(w)
		Next
			
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
