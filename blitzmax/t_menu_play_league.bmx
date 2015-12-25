SuperStrict

Import "t_game_mode.bmx"

Type t_menu_play_league Extends t_game_mode

	Field match:t_match
	
	Field w_next_match:t_widget
	Field w_play_match:t_widget
	Field w_view_result:t_widget
	Field w_exit:t_widget

	Method New()
		
		If league.current_round = league.rounds
			league.ended = True
		EndIf
		
		Self.type_id = TTypeId.ForObject(Self)
		
		music_mute = False
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''table list
		Local table:TList = league.teams.copy()
	
		table.sort(True, compare_teams_by_stats)
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*520, 30, 520, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(league.get_menu_title(), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''table headers
		Local dy:Int = 100 + 11*(24-league.number_of_teams)

		w = New t_label
		w.set_geometry(460, dy, 62, 21)
		''// NOTE: max 4 characters
		w.set_text(first_word(dictionary.gettext("PL // code for: PLAYED MATCHES")), 0, 10)
		Self.widgets.AddLast(w)

		w = New t_label
		w.set_geometry(520, dy, 62, 21)
		''// NOTE: max 4 characters
		w.set_text(first_word(dictionary.gettext("W // code for: WON MATCHES")), 0, 10)
		Self.widgets.AddLast(w)

		w = New t_label
		w.set_geometry(580, dy, 62, 21)
		''// NOTE: max 4 characters
		w.set_text(first_word(dictionary.gettext("D // code for: DRAWN MATCHES")), 0, 10)
		Self.widgets.AddLast(w)

		w = New t_label
		w.set_geometry(640, dy, 62, 21)
		''// NOTE: max 4 characters
		w.set_text(first_word(dictionary.gettext("L // code for: LOST MATCHES")), 0, 10)
		Self.widgets.AddLast(w)

		w = New t_label
		w.set_geometry(700, dy, 62, 21)
		''// NOTE: max 4 characters
		w.set_text(first_word(dictionary.gettext("F // code for: GOALS FOR")), 0, 10)
		Self.widgets.AddLast(w)

		w = New t_label
		w.set_geometry(760, dy, 62, 21)
		''// NOTE: max 4 characters
		w.set_text(first_word(dictionary.gettext("A // code for: GOALS AGAINST")), 0, 10)
		Self.widgets.AddLast(w)

		w = New t_label
		w.set_geometry(820, dy, 62, 21)
		''// NOTE: max 4 characters
		w.set_text(first_word(dictionary.gettext("PTS // code for: POINTS")), 0, 10)
		Self.widgets.AddLast(w)
			
		''table
		Local tm:Int = 0
		For Local team:t_team = EachIn table
			w = New t_button
			w.set_geometry(100, dy +20 +22*tm, 36, 24)
			w.set_text(tm +1, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			w = New t_button
			w.set_geometry(140, dy +20 +22*tm, 322, 24)
			Select team.control
				Case CM_COMPUTER
					w.set_colors($981E1E, $000001, $000001)
				Case CM_PLAYER
					w.set_colors($0000C8, $000001, $000001)
				Case CM_COACH
					w.set_colors($009BDC, $000001, $000001)
			End Select
			w.set_text(team.name, 1, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			''played
			w = New t_button
			w.set_geometry(460, dy +20 +22*tm, 62, 24)
			w.set_colors($808080, $010001, $010001)
			w.set_text(team.won + team.drawn + team.lost, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			''won
			w = New t_button
			w.set_geometry(520, dy +20 +22*tm, 62, 24)
			w.set_colors($808080, $010001, $010001)
			w.set_text(team.won, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			''drawn
			w = New t_button
			w.set_geometry(580, dy +20 +22*tm, 62, 24)
			w.set_colors($808080, $010001, $010001)
			w.set_text(team.drawn, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			''lost
			w = New t_button
			w.set_geometry(640, dy +20 +22*tm, 62, 24)
			w.set_colors($808080, $010001, $010001)
			w.set_text(team.lost, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			''goals for
			w = New t_button
			w.set_geometry(700, dy +20 +22*tm, 62, 24)
			w.set_colors($808080, $010001, $010001)
			w.set_text(team.goals_for, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			''goals against
			w = New t_button
			w.set_geometry(760, dy +20 +22*tm, 62, 24)
			w.set_colors($808080, $010001, $010001)
			w.set_text(team.goals_against, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
	
			''points
			w = New t_button
			w.set_geometry(820, dy +20 +22*tm, 62, 24)
			w.set_colors($808080, $010001, $010001)
			w.set_text(team.points, 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
			
			tm = tm + 1
		Next
		
		If Not league.ended
		
			Self.match = league.get_match()
			
			''team A
			w = New t_label
			w.set_geometry(110, 660, 322, 36)
			w.set_text(match.teams[0].name, -1, 14)
			Self.widgets.AddLast(w)

			''result (home goals)
			w = New t_label
			w.set_geometry(512 -60, 660, 40, 36)
			w.set_text("", -1, 14)
			If (match.result <> Null)
				w.text = match.result[0]
			EndIf				
			Self.widgets.AddLast(w)

			''versus / -
			w = New t_label
			w.set_geometry(512 -20, 660, 40, 36)
			w.set_text("", 0, 14)
			If (match.result = Null)
				''// NOTE: max 2 characters
				w.text = first_word(dictionary.gettext("V // code for: VERSUS"))
			Else
				w.text = "-"
			EndIf				
			Self.widgets.AddLast(w)
			
			''result (away goals)
			w = New t_label
			w.set_geometry(512 +20, 660, 40, 36)
			w.set_text("", +1, 14)
			If (match.result <> Null)
				w.text = match.result[1]
			EndIf				
			Self.widgets.AddLast(w)

			''team B
			w = New t_label
			w.set_geometry(592, 660, 322, 36)
			w.set_text(match.teams[1].name, +1, 14)
			Self.widgets.AddLast(w)

			''next match
			w = New t_button
			w.set_geometry(512 -430, 708, 460, 36)
			w.set_colors($138B21, $1BC12F, $004814)
			w.set_text(dictionary.gettext("NEXT MATCH"), 0, 14)
			w.bind("fire1_down", "bc_next_match")
			w.bind("fire1_hold", "bc_next_match")
			w.visible = menu.show_result	
			Self.w_next_match = w
			Self.widgets.AddLast(w)
		
			''play match
			w = New t_button
			w.set_geometry(512 -430, 708, 220, 36)
			w.set_colors($138B21, $1BC12F, $004814)
			w.set_text("", 0, 14)
			If ((Self.match.teams[HOME].control <> CM_COMPUTER) Or (Self.match.teams[AWAY].control <> CM_COMPUTER))
				w.text = "- " + dictionary.gettext("MATCH") + " -"
			Else
				w.text = dictionary.gettext("VIEW MATCH")
			EndIf
			w.bind("fire1_down", "bc_play_view_match")
			w.visible = Not menu.show_result
			Self.w_play_match = w
			Self.widgets.AddLast(w)
	
			''view result	
			w = New t_button
			w.set_geometry(512 -190, 708, 220, 36)
			w.set_colors($138B21, $1BC12F, $004814)
			w.set_text("", 0, 14)
			If ((Self.match.teams[HOME].control <> CM_COMPUTER) Or (Self.match.teams[AWAY].control <> CM_COMPUTER))
				w.text = "- " + dictionary.gettext("RESULT") + " -"
				w.bind("fire1_down", "bc_view_result")
			Else
				w.text = dictionary.gettext("VIEW RESULT")
				w.bind("fire1_down", "bc_view_result")
				w.bind("fire1_hold", "bc_view_result")
			EndIf
			w.visible = Not menu.show_result
			Self.w_view_result = w
			Self.widgets.AddLast(w)
			
		EndIf
	
		''view statistics	
		w = New t_button
		w.set_geometry(512 +50, 708, 180, 36)
		w.set_colors($138B21, $1BC12F, $004814)
		w.set_text(dictionary.gettext("STATS"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_VIEW_STATISTICS)])
		Self.widgets.AddLast(w)
		
		''exit
		w = New t_button
		w.set_geometry(512 +250, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MAIN)])
		Self.w_exit = w
		Self.widgets.AddLast(w)
		
		If Not league.ended 
			If menu.show_result
				Self.selected_widget = Self.w_next_match
			ElseIf ((Self.match.teams[HOME].control <> CM_COMPUTER) Or (Self.match.teams[AWAY].control <> CM_COMPUTER)) And (Not league.user_prefers_result)
				Self.selected_widget = Self.w_play_match
			Else
				Self.selected_widget = Self.w_view_result
			EndIf
		Else
			Self.selected_widget = Self.w_exit
		EndIf
	
	End Method

	Method bc_next_match()
		league.next_match()
		menu.show_result = False
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
	End Method
	
	Method bc_view_result()
		If ((Self.match.teams[HOME].control <> CM_COMPUTER) Or (Self.match.teams[AWAY].control <> CM_COMPUTER))
			league.user_prefers_result = True
		EndIf
		
		''SET RESULT
		''goals
		Local goal_a:Int = generate_score(Self.match.teams[HOME], Self.match.teams[AWAY])
		Local goal_b:Int = generate_score(Self.match.teams[AWAY], Self.match.teams[HOME])
		
		league.set_result(goal_a, goal_b)
	
		Self.match.teams[HOME].generate_scorers(goal_a)
		Self.match.teams[AWAY].generate_scorers(goal_b)

		menu.show_result = True
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
	End Method	
	
	Method bc_play_view_match()
		league.user_prefers_result = False

		''take teams from list
		team[HOME] = Self.match.teams[HOME]
		team[AWAY] = Self.match.teams[AWAY]
		
		''choose the menu to set
		input_devices.set_availability(True)
		If (team[HOME].control <> CM_COMPUTER) 
			menu.team_to_set = HOME
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
		ElseIf (team[AWAY].control <> CM_COMPUTER)
			menu.team_to_set = AWAY
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MATCH_PRESENTATION)
		EndIf
	End Method
	
End Type
