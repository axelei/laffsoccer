SuperStrict

Import "t_game_mode.bmx"

Type t_menu_tactics_import Extends t_game_mode

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_set_team.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -280, 30, 560, 40)
		w.set_colors($BA9206, $E9B607, $6A5304)
		w.set_text(dictionary.gettext("IMPORT TACTICS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''preset tactics
		For Local i:Int = 0 To 11
			w = New t_button
			Local dx:Int = -1.5*120 -6 +(i Mod 3)*(120 +6)
			Local dy:Int = Int(i/3)*(36 +6)
			w.set_geometry(512 +dx, 220 +dy, 120, 36)
			w.set_colors($98691E, $C88B28, $3E2600)
			w.set_text(tactics_name[i], 0, 14)
			w.bind("fire1_down", "bc_import", [String(i)])
			Self.widgets.AddLast(w)
			If (i = 1)
				Self.selected_widget = w
			EndIf
		Next
		
		''custom tactics
		For Local i:Int = 12 To 17
			w = New t_button
			Local dx:Int = -183 -3 +((i -12) Mod 2)*(183 +6)
			Local dy:Int = Int((i-12)/2)*(36 +6)
			w.set_geometry(512 +dx, 228 +4*(34 +6) +dy, 183, 36)
			w.set_colors($98691E, $C88B28, $3E2600)
			w.set_text(tactics_name[i], 0, 14)
			w.bind("fire1_down", "bc_import", [String(i)])
			Self.widgets.AddLast(w)
		Next
		
		''load tactics
		w = New t_button
		w.set_geometry(512 -183 -3, 234 +7*(34 +6), 372, 36)
		w.set_colors($AB148D, $DE1AB7, $780E63)
		w.set_text(dictionary.gettext("LOAD"), 0, 14)
		w.bind("fire1_down", "bc_load")
		Self.widgets.AddLast(w)

		
		''exit
		w = New t_button
		w.set_geometry(512 -90, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
	
	End Method

	Method bc_import(n:Int)
		menu.tactics_undo.push(menu.edited_tactics)
		menu.edited_tactics.load_file("tactics/preset/" + tactics_file[n] + ".TAC")
		
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_EDIT_TACTICS)
	End Method

	Method bc_load()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_TACTICS_LOAD)
	End Method
	
	Method bc_exit()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_EDIT_TACTICS)
	End Method
	
End Type
