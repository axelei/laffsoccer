SuperStrict

Import "t_game_mode.bmx"

Type t_menu_tactics_save_warning Extends t_game_mode

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_set_team.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -280, 30, 560, 40)
		w.set_colors($BA9206, $E9B607, $6A5304)
		w.set_text(dictionary.gettext("SAVE TACTICS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''warning
		w = New t_button
		w.set_geometry(512 -0.5*620, 260, 620, 180)
		w.set_colors($DC0000, $FF4141, $8C0000)
		w.set_text(dictionary.gettext("TACTICS HAVE BEEN CHANGED"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''save
		w = New t_button
		w.set_geometry(512 -90, 630, 180, 36)
		w.set_colors($10A000, $15E000, $096000)
		w.set_text(dictionary.gettext("SAVE"), 0, 14)
		w.bind("fire1_down", "bc_save")
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
		''exit
		w = New t_button
		w.set_geometry(512 -90, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
	
	End Method

	Method bc_save()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SAVE_TACTICS)
	End Method
	
	Method bc_exit()
		''copy edited tactics back
		tactics_array[menu.tactics_to_edit].copy(menu.edited_tactics)
		
 		If (menu.status = MS_NONE)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
		EndIf
	End Method

End Type
