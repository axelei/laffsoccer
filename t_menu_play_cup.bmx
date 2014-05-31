SuperStrict

Import "t_game_mode.bmx"

Type t_menu_play_cup Extends t_game_mode

	Field match:t_match
	Field matches:Int
	Field img_scroll:TImage
	Field offset:Int
	Field img_ucode10g:TImage
	Field result_widgets:TList

	Field w_next_match:t_widget
	Field w_play_match:t_widget
	Field w_view_result:t_widget
	Field w_exit:t_widget
	
	Method New()
		If (cup.current_round = cup.rounds)
			cup.ended = True
		EndIf
		
		Self.type_id = TTypeId.ForObject(Self)
		
		music_mute = False
		
		Self.matches = CountList(cup.calendar_current)
		Self.offset = 0
		If (Self.matches > 8) And (cup.current_match > 4)
			Self.offset = Min(cup.current_match -4, Self.matches -8)
		EndIf
		
		''load images	
		img_scroll = load_image("images", "scroll.png", 0, 0)

		''green charset
		Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
		rgb_pairs.add($FCFCFC, $21e337)

		Self.img_ucode10g = load_and_edit_png("images/ucode_10.png", rgb_pairs, MASKEDIMAGE, $00F700)

		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''table list
		Local table:TList = cup.teams.copy()
		table.sort()
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*840, 30, 840, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(cup.get_menu_title(), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		Local dy:Int = 120
		If (Self.matches < 8)
			dy = dy +64*(8 -Self.matches)/2
		EndIf
				
		''calendar
		Self.result_widgets = New TList
		For Local m:Int = 0 To CountList(cup.calendar_current) -1
			Local match:t_match = t_match(cup.calendar_current.valueatindex(m))
		
			w = New t_button
			w.set_geometry(207, dy +64*m, 240, 26)
			Select match.teams[0].control
				Case CM_COMPUTER
					w.set_colors($981E1E, $000001, $000001)
				Case CM_PLAYER
					w.set_colors($0000C8, $000001, $000001)
				Case CM_COACH
					w.set_colors($009BDC, $000001, $000001)
			End Select
			w.set_text(match.teams[0].name, -1, 10)
			w.active = False
			Self.result_widgets.AddLast(w)
			Self.widgets.AddLast(w)
	
			''result (home goals)
			w = New t_button
			w.set_geometry(512 -45, dy +64*m, 30, 26)
			w.set_text("", -1, 10)
			If (match.result <> Null)
				w.text = match.result[0]
			EndIf				
			w.active = False
			Self.result_widgets.AddLast(w)
			Self.widgets.AddLast(w)

			''versus / -
			w = New t_button
			w.set_geometry(512 -15, dy +64*m, 30, 26)
			''// NOTE: max 2 characters
			w.set_text(first_word(dictionary.gettext("V // code for: VERSUS")), 0, 10)
			If (match.result <> Null)
				w.text = "-"
			EndIf				
			w.active = False
			Self.result_widgets.AddLast(w)
			Self.widgets.AddLast(w)
	
			''result (away goals)
			w = New t_button
			w.set_geometry(512 +15, dy +64*m, 30, 26)
			w.set_text("", +1, 10)
			If (match.result <> Null)
				w.text = match.result[1]
			EndIf				
			w.active = False
			Self.result_widgets.AddLast(w)
			Self.widgets.AddLast(w)
	
			w = New t_button
			w.set_geometry(577, dy +64*m, 240, 26)
			Select match.teams[1].control
				Case CM_COMPUTER
					w.set_colors($981E1E, $000001, $000001)
				Case CM_PLAYER
					w.set_colors($0000C8, $000001, $000001)
				Case CM_COACH
					w.set_colors($009BDC, $000001, $000001)
			End Select
			w.set_text(match.teams[1].name, +1, 10)
			w.active = False
			Self.result_widgets.AddLast(w)
			Self.widgets.AddLast(w)
	
			''status
			w = New t_button
			w.set_geometry(512 -360, dy +26 +64*m, 720, 26)
			w.set_text(match.status, 0, 10)
			w.img_ucode = Self.img_ucode10g
			w.active = False
			Self.result_widgets.AddLast(w)
			Self.widgets.AddLast(w)
		Next
		Self.update_result_widgets()
		
		Self.match:t_match = cup.get_match()
		
		''home team
		w = New t_button
		w.set_geometry(110, 660, 322, 36)
		w.set_text(Self.match.teams[HOME].name, -1, 14)
		w.active = False
		Self.widgets.AddLast(w)

		''result (home goals)
		w = New t_button
		w.set_geometry(512 -60, 660, 40, 36)
		w.set_text("", -1, 14)
		If (Self.match.result <> Null)
			w.text = Self.match.result[HOME]
		EndIf				
		w.active = False
		Self.widgets.AddLast(w)

		''versus / -
		w = New t_button
		w.set_geometry(512 -20, 660, 40, 36)
		''// NOTE: max 2 characters
		w.set_text(first_word(dictionary.gettext("V // code for: VERSUS")), 0, 14)
		If (Self.match.result <> Null)
			w.text = "-"
		EndIf				
		w.active = False
		Self.widgets.AddLast(w)

		''result (away goals)
		w = New t_button
		w.set_geometry(512 +20, 660, 40, 36)
		w.set_text("", +1, 14)
		If (Self.match.result <> Null)
			w.text = Self.match.result[AWAY]
		EndIf				
		w.active = False
		Self.widgets.AddLast(w)

		''away team
		w = New t_button
		w.set_geometry(592, 660, 322, 36)
		w.set_text(Self.match.teams[AWAY].name, +1, 14)
		w.active = False
		Self.widgets.AddLast(w)

		If (Not cup.ended)
		
			''scroll
			If (Self.matches > 8)
				''scroll up
				w = New t_button
				w.set_geometry(100, 114, 20, 36)
				w.image = Self.img_scroll
				w.set_frame(16, 32, 0, 0)
				w.bind("fire1_down", "bc_scroll", ["-1"])
				w.bind("fire1_hold", "bc_scroll", ["-1"])
				Self.widgets.AddLast(w)
			
				''scroll down
				w = New t_button
				w.set_geometry(100, 568, 20, 36)
				w.image = Self.img_scroll
				w.set_frame(16, 32, 1, 0)
				w.bind("fire1_down", "bc_scroll", ["+1"])
				w.bind("fire1_hold", "bc_scroll", ["+1"])
				Self.widgets.AddLast(w)
			EndIf
			
			If (menu.show_result)
			
				''next_match
				w = New t_button
				w.set_geometry(512 -430, 708, 460, 36)
				w.set_colors($138B21, $1BC12F, $004814)
				w.set_text(dictionary.gettext("NEXT MATCH"), 0, 14)
				w.bind("fire1_down", "bc_next_match")
				w.bind("fire1_hold", "bc_next_match")
				Self.w_next_match = w
				Self.widgets.AddLast(w)
			
			Else

				''play_match
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
				Self.w_view_result = w
				Self.widgets.AddLast(w)
				
			EndIf
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
		
		If (Not cup.ended)
			If (menu.show_result)
				Self.selected_widget = Self.w_next_match
			ElseIf ((Self.match.teams[HOME].control <> CM_COMPUTER) Or (Self.match.teams[AWAY].control <> CM_COMPUTER)) And (Not cup.user_prefers_result)
				Self.selected_widget = Self.w_play_match
			Else
				Self.selected_widget = Self.w_view_result
			EndIf
		Else
			Self.selected_widget = Self.w_exit
		EndIf
	
	End Method

	Method bc_next_match()
		cup.next_match()
		menu.show_result = False
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
	End Method
	
	Method bc_view_result()
		If ((Self.match.teams[HOME].control <> CM_COMPUTER) Or (Self.match.teams[AWAY].control <> CM_COMPUTER))
			cup.user_prefers_result = True
		EndIf
		
		''set result
		Local goal_a:Int = generate_score(Self.match.teams[HOME], Self.match.teams[AWAY])
		Local goal_b:Int = generate_score(Self.match.teams[AWAY], Self.match.teams[HOME])
		cup.set_result(goal_a, goal_b, RT_AFTER_90_MINS)
		If (cup.play_extra_time())
			goal_a = goal_a +generate_score(Self.match.teams[HOME], Self.match.teams[AWAY], True)
			goal_b = goal_b +generate_score(Self.match.teams[AWAY], Self.match.teams[HOME], True)
			cup.set_result(goal_a, goal_b, RT_AFTER_EXTRA_TIME)
		EndIf
		Self.match.teams[HOME].generate_scorers(goal_a)
		Self.match.teams[AWAY].generate_scorers(goal_b)
		If (cup.play_penalties())
			Repeat 
				goal_a = Rand(0, 5)
				goal_b = Rand(0, 5)
			Until (goal_a <> goal_b)
			cup.set_result(goal_a, goal_b, RT_PENALTIES)
		EndIf
		menu.show_result = True
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
	End Method
	
	Method bc_scroll(n:Int)
		Self.offset = slide(Self.offset, 0, Self.matches -8, n)
		Self.update_result_widgets()
	End Method
	
	Method update_result_widgets()
		If (Self.matches > 8)
			Local m:Int = 0
			For Local w:t_widget = EachIn Self.result_widgets
				If (m >= 6*Self.offset) And (m < 6*(Self.offset +8))
					w.y = 120 +64*(m/6 -Self.offset) +26*((m Mod 6) = 5)
					w.visible = True
				Else
					w.visible = False
				EndIf
				m = m +1
			Next
		EndIf
	End Method
	
	Method bc_play_view_match()
		cup.user_prefers_result = False

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
