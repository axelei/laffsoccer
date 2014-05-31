SuperStrict

Import "t_game_mode.bmx"

Type t_menu_nationals_confederation Extends t_game_mode
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		menu.confederation = t_confederation.NATIONAL
		menu.division = 1
		
		Local teams:Int = 0
		For Local file:t_association_file = EachIn associations_files
			If (file.confederation = menu.confederation)
				teams :+ 1
			EndIf
		Next
		
		''background
		Local filename:String
		Select menu.status
			Case MS_FRIENDLY
				filename = "menu_friendly.jpg"
			Case MS_COMPETITION
				filename = "menu_competition.jpg"
			Case MS_EDIT
				filename = "menu_edit.jpg"
			Case MS_TRAINING
				filename 	= "menu_training.jpg"
		End Select
		Self.img_background = backgrounds.get(filename)
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*660, 30, 660, 40)
		Local s:String
		Select menu.status
			Case MS_FRIENDLY	
				w.set_colors($2D855D, $3DB37D, $1E5027)
				s = dictionary.gettext("FRIENDLY")
			Case MS_COMPETITION
				w.set_colors($415600, $5E7D00, $243000)
				s = competition.name
			Case MS_EDIT
				w.set_colors($89421B, $BB5A25, $3D1E0D)
				s = dictionary.gettext("EDIT TEAMS")
			Case MS_TRAINING
				s = dictionary.gettext("TRAINING")
				w.set_colors($1B8A7F, $25BDAE, $115750)
		End Select
		w.set_text(s + " - " + dictionary.gettext("NATIONAL TEAMS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''confederations
		Local col1:Int, col2:Int, tm:Int, k:Int
		tm = 0
		For Local file:t_association_file = EachIn associations_files
			If (file.confederation = menu.confederation)
				tm = tm +1
				w = New t_button
				w.set_size(300, 40)
				w.set_text(file.name, 0, 14)
				If (file.is_available)
					w.set_colors($568200, $77B400, $243E00)
					w.bind("fire1_down", "bc_confederation", [file.code])
				Else
					w.set_colors($6E6E6E, $969696, $3C3C3C)
					w.active = False
				EndIf					
				If (teams <= 8)
					w.x = 0.5*1024 -0.5*w.w
					w.y = 350 -60*0.5*teams +60*tm
				Else
					col1 = Floor(teams/3.0) + 1 * (teams Mod 3 = 2) 
					col2 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
					If (tm <= col1)
						w.x = 0.5*1024 -1.5*w.w -20
						w.y = 350 -36*0.5*col2 +36*tm
					Else If (tm <= col1 + col2)
						w.x = 0.5*1024 -0.5*w.w
						w.y = 350 -36*0.5*col2 +36*(tm -col1)
					Else
						w.x = 0.5*1024 +0.5*w.w +20
						w.y = 350 -36*0.5*col2 +36*(tm -col1 -col2)
					EndIf					
				EndIf
			
				If (Self.selected_widget = Null) And (w.active = True)
					Self.selected_widget = w
				EndIf
				
				Self.widgets.AddLast(w)
			EndIf
		Next
		
		''abort
		w = New t_button
		w.set_geometry(512 -0.5*200, 708, 200, 36)
		w.set_colors($C8000E, $FF1929, $74040C)
		w.set_text(dictionary.gettext("ABORT"), 0, 14)
		w.bind("fire1_down", "bc_abort")
		Self.widgets.AddLast(w)
		If (Self.selected_widget = Null)
			Self.selected_widget = w
		EndIf
			
	End Method
	
	Method bc_confederation(code:String)
		menu.extension = code
		Select menu.status
			Case MS_FRIENDLY
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAMS)
			Case MS_COMPETITION
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAMS)
			Case MS_EDIT
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAM)
			Case MS_TRAINING
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAM)
		End Select
	End Method
	
	Method bc_abort()
		Select menu.status
			Case MS_FRIENDLY
				team_list = Null
			Case MS_COMPETITION
				team_list = Null
				Select competition.typ
					Case CT_LEAGUE
						league = Null
					Case CT_CUP
						cup = Null
				End Select
				competition = Null
		End Select
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
	End Method
	
End Type
