SuperStrict

Import "t_game_mode.bmx"

Type t_menu_tactics_abort_warning Extends t_game_mode

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_set_team.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -280, 30, 560, 40)
		w.set_colors($BA9206, $E9B607, $6A5304)
		w.set_text(dictionary.gettext("EDIT TACTICS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''warning
		w = New t_button
		w.set_geometry(512 -0.5*620, 260, 620, 180)
		w.set_colors($DC0000, $FF4141, $8C0000)
		w.set_text(dictionary.gettext("EDITED TACTICS WILL BE LOST"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''continue
		w = New t_button
		w.set_geometry(512 -90, 630, 180, 36)
		w.set_colors($10A000, $15E000, $096000)
		w.set_text(dictionary.gettext("CONTINUE"), 0, 14)
		w.bind("fire1_down", "bc_continue")
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
		''abort
		w = New t_button
		w.set_geometry(512 -90, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("ABORT"), 0, 14)
		w.bind("fire1_down", "bc_abort")
		Self.widgets.AddLast(w)
	
	End Method

	Method bc_continue()
 		If (menu.status = MS_NONE)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
		EndIf
	End Method

	Method bc_abort()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_EDIT_TACTICS)
	End Method
	
End Type
