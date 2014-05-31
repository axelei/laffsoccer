SuperStrict

Import "t_game_mode.bmx"

Type t_menu_load_teams Extends t_game_mode

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Select menu.status
			Case MS_EDIT
				Self.img_background = backgrounds.get("menu_edit.jpg")
			Case MS_TRAINING
				Self.img_background = backgrounds.get("menu_training.jpg")
		End Select
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		Select menu.status
			Case MS_EDIT
				w.set_colors($89421B, $BB5A25, $3D1E0D)
				w.set_text(dictionary.gettext("EDIT TEAMS"), 0, 14)
			Case MS_TRAINING
				w.set_colors($1B8A7F, $25BDAE, $115750)
				w.set_text(dictionary.gettext("TRAINING"), 0, 14)
		End Select
		w.active = False
		Self.widgets.AddLast(w)
	
		''club teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 315, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CLUB TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_club_teams")
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

		''national teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 390, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("NATIONAL TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_national_teams")
		Self.widgets.AddLast(w)
	
		''custom teams
		w = New t_button
		w.set_geometry(512 -0.5*340, 465, 340, 40)
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
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAM)
	End Method
	
End Type
