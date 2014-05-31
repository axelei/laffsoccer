SuperStrict

Import "t_game_mode.bmx"

Type t_menu_select_tactics_to_edit Extends t_game_mode

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_set_team.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		w.set_colors($BA9206, $E9B607, $6A5304)
		w.set_text(dictionary.gettext("EDIT TACTICS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''tactics
		For Local t:Int = 12 To 17
			w = New t_button
			w.set_geometry(512 -0.5*340, 200 +75*(t -12), 340, 40)
			w.set_colors($568200, $77B400, $243E00)
			w.set_text(tactics_name[t], 0, 14)
			w.bind("fire1_down", "bc_edit_tactics", [String(t)])
			Self.widgets.AddLast(w)
			If (t = 12)
				Self.selected_widget = w
			EndIf
		Next
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
	
	End Method

	Method bc_edit_tactics(t:Int)
		menu.tactics_to_edit = t
		menu.edited_tactics = New t_tactics
		menu.edited_tactics.copy(tactics_array[menu.tactics_to_edit])
		menu.tactics_undo = New t_tactics_stack
		
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_EDIT_TACTICS)
	End Method
	
	Method bc_exit()
 		If (menu.status = MS_NONE)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
		EndIf
	End Method
	
End Type
