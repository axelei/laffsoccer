SuperStrict

Import "t_game_mode.bmx"

Type t_menu_save_tactics Extends t_game_mode
	
	Field file_name:String

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		Self.file_name = tactics_name[menu.tactics_to_edit]
		
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
		
		''read tactics files
		Local tactics_files:TList = New TList
		Local directory:Int = ReadDir("tactics/")
		Local file:String = NextFile(directory)
		While (file <> "")
			''it is a file (and not a folder)
			If (FileType("tactics/" + file) = 1)
				If (Right(file, 4) = ".TAC")
					ListAddLast(tactics_files, Left(file, Len(file) -4))
				EndIf
			EndIf
			file = NextFile(directory)
		Wend
		CloseDir(directory)
		
		Local tactics_count:Int = tactics_files.count()
		Local t:Int = 0
		Local col1:Int, col2:Int
		For Local tactics_file:String = EachIn tactics_files
			t = t + 1
			w = New t_button
			w.set_size(160, 36)
			w.set_colors($98691E, $C88B28, $3E2600)
			w.set_text(tactics_file, 0, 14)
			w.bind("fire1_down", "bc_save", [tactics_file])
			If (tactics_count <= 8)
				w.x = 0.5*1024 -0.5*240
				w.y = 266 - 42*0.5*tactics_count + 42*t
			Else
				col1 = Floor(tactics_count/3.0) + 1 * ((tactics_count Mod 3) = 2) 
				col2 = Floor(tactics_count/3.0) + 1 * ((tactics_count Mod 3) > 0) 
				If (t <= col1)
					w.x = 0.5*1024 -1.5*240 -60
					w.y = 266 - 42*0.5*col2 +42*t
				Else If (t <= col1 + col2)
					w.x = 0.5*1024 -0.5*240
					w.y = 266 - 42*0.5*col2 +42*(t -col1)
				Else
					w.x = 0.5*1024 +0.5*240 +60
					w.y = 266 - 42*0.5*col2 +42*(t -col1 -col2)
				EndIf					
			EndIf
			
			Self.widgets.AddLast(w)
			
			Local w2:t_widget = New t_button
			w2.set_size(80, 36)
			w2.set_position(w.x +165, w.y)
			w2.set_colors($98691E, $C88B28, $3E2600)
			w2.set_text("TACT", 0, 14)
			w2.active = False
			Self.widgets.AddLast(w2)
			
		Next
		
		''filename
		w = New t_label
		w.set_geometry(512 -160, 500, 160, 36)
		w.set_colors($9C522A, $BB5A25, $69381D)
		w.set_text(dictionary.gettext("FILENAME")+":", -1, 14)
		Self.widgets.AddLast(w)

		Local input_button:t_input_button
		input_button = New t_input_button
		input_button.set_geometry(512, 500, 160, 36)
		input_button.set_colors($1769BD, $3A90E8, $10447A)
		input_button.set_text(Self.file_name, 0, 14)
		input_button.set_entry_limit(8)
		input_button.bind("on_update", "bc_update_file_name")
		Self.widgets.AddLast(input_button)
		
		
		''save
		w = New t_button
		w.set_geometry(512 -90, 610, 180, 36)
		w.set_colors($10A000, $15E000, $096000)
		w.set_text(dictionary.gettext("SAVE"), 0, 14)
		w.bind("fire1_down", "bc_save", [""])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
		''abort
		w = New t_button
		w.set_geometry(512 -90, 708, 180, 36)
		w.set_colors($DC0000, $FF4141, $8C0000)
		w.set_text(dictionary.gettext("ABORT"), 0, 14)
		w.bind("fire1_down", "bc_abort")
		Self.widgets.AddLast(w)
	
	End Method
	
	Method bc_update_file_name()
		Self.file_name = Self.selected_widget.text
	End Method
	
	Method bc_save(name:String)
		
		If (name = "")
			name = Self.file_name
		EndIf
		
		''rename
		menu.edited_tactics.name = name
		
		''save
		menu.edited_tactics.save_file("tactics/" + name + ".TAC")
		
		''copy edited tactics back
		tactics_array[menu.tactics_to_edit].copy(menu.edited_tactics)
		tactics_name[menu.tactics_to_edit] = menu.edited_tactics.name
		
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
