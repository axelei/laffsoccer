SuperStrict

Import "t_game_mode.bmx"

Type t_menu_view_statistics Extends t_game_mode

	Method New()
	
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(dictionary.gettext("STATISTICS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''highest scorer list
		w = New t_button
		w.set_geometry(512 -0.5*340, 300, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("HIGHEST SCORER LIST"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_TOP_SCORERS)])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
		''competition info
		w = New t_button
		w.set_geometry(512 -0.5*340, 400, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("COMPETITION INFO"), 0, 14)
		w.bind("fire1_down", "bc_competition_info")
		Self.widgets.AddLast(w)
	
		''view squads
		w = New t_button
		w.set_geometry(512 -0.5*340, 500, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("VIEW SQUADS"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_SELECT_SQUAD_TO_VIEW)])
		Self.widgets.AddLast(w)
	
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)

	End Method

	Method bc_competition_info()
		Select competition.typ
			Case CT_LEAGUE
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_LEAGUE_INFO)
			Case CT_CUP
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CUP_INFO)
		End Select							
	End Method
	
	Method bc_exit()
		Select competition.typ
			Case CT_LEAGUE
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
			Case CT_CUP
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
		End Select							
	End Method
	
End Type
