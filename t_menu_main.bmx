SuperStrict

Import "t_game_mode.bmx"

Type t_menu_main Extends t_game_mode
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		menu.status = MS_NONE
		
		''background
		Self.img_background = backgrounds.get("menu_main.jpg")
	
		''release date
		Local w:t_widget = New t_label
		w.set_text("31 MAY 2014", +1, 10)
		w.set_position(20, 768-29)
		Self.widgets.AddLast(w)
		
		''homepage
		w = New t_label
		w.set_text("YSOCCER.SF.NET", -1, 10)
		w.set_position(1024 -20, 768 -29)
		Self.widgets.AddLast(w)
		
		''game options
		w = New t_button
		w.set_colors($536B90, $7090C2, $263142)
		w.set_geometry(512 -65 -320, 290, 320, 36)
		w.set_text(dictionary.gettext("GAME OPTIONS"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_GAME_OPTIONS)])
		Self.widgets.AddLast(w)
		
		Self.selected_widget = w
		
		''match options
		w = New t_button
		w.set_colors($6101D7, $7D1DFF, $3A0181)
		w.set_geometry(512 -65 -320, 340, 320, 36)
		w.set_text(dictionary.gettext("MATCH OPTIONS"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MATCH_OPTIONS)])
		Self.widgets.AddLast(w)
		
		''control
		w = New t_button
		w.set_colors($A905A3, $E808E0, $5A0259)
		w.set_geometry(512 -65 -320, 390, 320, 36)
		w.set_text(dictionary.gettext("CONTROL"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_CONTROL)])
		Self.widgets.AddLast(w)
		
		''edit tactics
		w = New t_button
		w.set_colors($BA9206, $E9B607, $6A5304)
		w.set_geometry(512 -65 -320, 440, 320, 36)
		w.set_text(dictionary.gettext("EDIT TACTICS"), 0, 14)
		w.bind("fire1_down", "bc_edit_tactics")
		Self.widgets.AddLast(w)
		
		''friendly
		w = New t_button
		w.set_colors($2D855D, $3DB37D, $1E5027)
		w.set_geometry(512 +65, 290, 320, 36)
		w.set_text(dictionary.gettext("FRIENDLY"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_FRIENDLY)])
		Self.widgets.AddLast(w)
		
		''diy competition
		w = New t_button
		w.set_colors($415600, $5E7D00, $243000)
		w.set_geometry(512 +65, 340, 320, 36)
		w.set_text(dictionary.gettext("DIY COMPETITION"), 0, 14)
		w.bind("fire1_down", "bc_set_diy_competition")
		Self.widgets.AddLast(w)
		
		''edit teams
		w = New t_button
		w.set_colors($89421B, $BB5A25, $3D1E0D)
		w.set_geometry(512 +65, 390, 320, 36)
		w.set_text(dictionary.gettext("EDIT TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_edit_teams")
		Self.widgets.AddLast(w)
		
		''training
		w = New t_button
		w.set_colors($1B8A7F, $25BDAE, $115750)
		w.set_geometry(512 +65, 440, 320, 36)
		w.set_text(dictionary.gettext("TRAINING"), 0, 14)
		w.bind("fire1_down", "bc_training")
		Self.widgets.AddLast(w)
		
		If (competition <> Null)
			w = New t_button
			w.set_colors($568200, $77B400, $243E00)
			w.set_geometry(512 -260, 510, 520, 36)
			Local s:String
			If (competition.ended)
				s = dictionary.gettext("REPLAY")
			Else
				s = dictionary.gettext("CONTINUE")
			EndIf
			w.set_text(s + " " + competition.name, 0, 14)
			w.bind("fire1_down", "bc_set_competition")
			Self.widgets.AddLast(w)
		EndIf
		
	End Method
	
	Method bc_edit_tactics()
		menu.team_to_show        = New t_team
		menu.team_to_show.ext    = "CUS"
		menu.team_to_show.number = 313
		menu.team_to_show.load_from_file()
		
		For Local ply:t_player = EachIn menu.team_to_show.players
			ply.create_face()
		Next
		
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TACTICS_TO_EDIT)
	End Method		
	
	Method bc_set_diy_competition()
		If (competition = Null)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_DIY_COMPETITION)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_COMPETITION_WARNING)
		EndIf
	End Method
	
	Method bc_set_competition()
		''replay
		If competition.ended
			competition.restart()
			menu.show_result = False
			
			''reload teams in the list
			For Local team:t_team = EachIn competition.teams
				team.load_from_file()
			Next
		EndIf
		menu.status = MS_COMPETITION
		Select competition.typ
			Case CT_LEAGUE
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
			Case CT_CUP
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
		End Select
	End Method
	
	Method bc_edit_teams()
		menu.status = MS_EDIT
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_LOAD_TEAMS)
	End Method
	
	Method bc_training()
		menu.status = MS_TRAINING
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_LOAD_TEAMS)
	End Method
	
End Type
