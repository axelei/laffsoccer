SuperStrict

Import "t_game_mode.bmx"

Type t_menu_clubs_division Extends t_game_mode
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Local filename:String
		Select menu.status
			Case MS_FRIENDLY	
				filename 	= "menu_friendly.jpg"
			Case MS_COMPETITION
				filename 	= "menu_competition.jpg"
			Case MS_EDIT
				filename 	= "menu_edit.jpg"
			Case MS_TRAINING
				filename 	= "menu_training.jpg"
		End Select
		Self.img_background = backgrounds.get(filename)
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*660, 30, 660, 40)
		Select menu.status
			Case MS_FRIENDLY
				w.set_colors($2D855D, $3DB37D, $1E5027)
			Case MS_COMPETITION
				w.set_colors($415600, $5E7D00, $243000)
			Case MS_EDIT
				w.set_colors($89421B, $BB5A25, $3D1E0D)
			Case MS_TRAINING
				w.set_colors($1B8A7F, $25BDAE, $115750)
		End Select
		w.set_text(dictionary.gettext("CLUB TEAMS") + " - " + associations_files.find(menu.extension).name, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''divisions
		Local divisions:TList = New TList
		For Local div:Int = 1 To menu.divisions
			divisions.AddLast(String(div))
		Next
		If (menu.no_league = 1)
			divisions.AddLast(String(0))
		EndIf
		
		Local col1:Int, col2:Int
		For Local div:Int = 0 To divisions.Count() -1
			w = New t_button
			w.set_size(260, 30)
			w.set_colors($1B4D85, $256AB7, $001D3E)
			Local i:Int = Int(String(divisions.ValueAtIndex(div)))
			Select i
				Case 0
					w.set_text("NO LEAGUE", 0, 14)
				Case 1
					w.set_text("PREMIER DIVISION", 0, 14)
				Default
					w.set_text("DIVISION " + (i -1), 0, 14)
			End Select
			w.bind("fire1_down", "bc_division", [String(i)])
			
			If (divisions.Count() <= 8)
				w.x = 0.5*1024 -0.5*w.w
				w.y = 386 - 32*0.5*divisions.Count() + 32*(div +1)
			Else
				col1 = Floor(divisions.Count()/3.0) + 1 * (divisions.Count() Mod 3 = 2) 
				col2 = Floor(divisions.Count()/3.0) + 1 * (divisions.Count() Mod 3 > 0) 
				If ((div +1) <= col1)
					w.x = 0.5*1024 -1.5*w.w - 20
					w.y = 386 - 32*0.5*col2 + 32*(div +1)
				Else If ((div +1) <= col1 + col2)
					w.x = 0.5*1024 -0.5*w.w
					w.y = 386 - 32*0.5*col2 + 32*((div +1) -col1)
				Else
					w.x = 0.5*1024 +0.5*w.w + 20
					w.y = 386 - 32*0.5*col2 + 32*((div +1) -col1 -col2)
				EndIf					
			EndIf
			
			If (Self.selected_widget = Null)
				Self.selected_widget = w
			EndIf
			
			Self.widgets.AddLast(w)
		Next
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_CLUBS_COUNTRY)])
		Self.widgets.AddLast(w)
		
	End Method
	
	Method bc_division(n:Int)
		menu.division = n
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
	
End Type
