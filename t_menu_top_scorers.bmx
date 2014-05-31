SuperStrict

Import "t_game_mode.bmx"

Type t_menu_top_scorers Extends t_game_mode
	
	Method New()

		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''top scorers
		Local top_scorers:TList = New TList
		For Local team:t_team = EachIn competition.teams
			For Local player:t_player = EachIn team.players
				If player.goals > 0 
					ListAddLast(top_scorers, player)
				EndIf
			Next
		Next
		
		top_scorers.sort(True, compare_players_by_goals)
		
		Local w:t_widget
		Local row:Int = 0
		Local goals:Int = 10000	
		For Local player:t_player = EachIn top_scorers
			''goals group
			If player.goals < goals
				row = row + 2
				w = New t_button
				w.set_geometry(512-120, 22*row, 240, 22)
				w.set_colors($00825F, $00C28E, $00402F)
				w.set_text(player.goals, 0, 10)
				w.active = False
				Self.widgets.AddLast(w)
				goals = player.goals
			EndIf
			
			row = row + 1
			
			''name
			w = New t_button
			w.set_geometry(512-120, 22*row, 240, 22)
			w.set_colors($1F1F95, $000000, $000000)
			w.set_text("", 0, 10)
			If player.surname <> ""
				w.text = player.surname
			Else
				w.text = player.name
			EndIf
			w.active = False
			Self.widgets.AddLast(w)

			''team
			w = New t_label
			w.set_geometry(512 +130, 22*row, 240, 22)
			w.set_text(" (" + player.team.name + ")", 1, 10)
			Self.widgets.AddLast(w)
	
			''goals
			If row > 24 Then Exit
		Next
		
		''center list
		Local y0:Int = 375 - 11*row
		For Local w:t_widget = EachIn Self.widgets
			w.y = w.y + y0
		Next
	
		''title
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(dictionary.gettext("HIGHEST SCORER LIST"), 0, 14)
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
