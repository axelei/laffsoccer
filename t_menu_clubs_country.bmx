SuperStrict

Import "t_game_mode.bmx"

Type t_menu_clubs_country Extends t_game_mode
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
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
		
		w.set_text(dictionary.gettext("CLUB TEAMS") + " - " + menu.confederation, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''countries
		Local col1:Int, col2:Int, col3:Int, tm:Int
		tm = 0
		For Local file:t_association_file = EachIn associations_files
			If (file.confederation = menu.confederation)
				tm = tm + 1
				w = New t_button
				w.set_size(290, 30)
				w.set_text(file.name, 0, 14)
				If (file.is_available)
					w.set_colors($568200, $77B400, $243E00)
					w.bind("fire1_down", "bc_country", [file.code])
				Else
					w.set_colors($6E6E6E, $969696, $3C3C3C)
					w.active = False
				EndIf	
				If (teams <= 8)
					w.x = 0.5*1024 -0.5*w.w
					w.y = 350 -34*0.5*teams + 34*tm
				Else
					col1 = Floor(teams/3.0) + 1 * (teams Mod 3 = 2) 
					col2 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
					col3 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
					If (tm <= col1)
						w.x = 0.5*1024 -1.5*w.w -20
						w.y = 360 -34*0.5*col2 +34*tm
					Else If (tm <= col1 + col2)
						w.x = 0.5*1024 -0.5*w.w
						w.y = 360 -34*0.5*col2 +34*(tm -col1)
					Else
						w.x = 0.5*1024 +0.5*w.w +20
						w.y = 360 -34*0.5*col2 +34*(tm -col1 -col2)
					EndIf					
				EndIf
				
				If (Self.selected_widget = Null) And (w.active = True)
					Self.selected_widget = w
				EndIf
			
				Self.widgets.AddLast(w)
			EndIf
		Next  
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_CLUBS_CONFEDERATION)])
		Self.widgets.AddLast(w)
		If (Self.selected_widget = Null)
			Self.selected_widget = w
		EndIf
		
	End Method
	
	Method bc_country(code:String)
		menu.extension = code
		
		Local file_team:TStream
		file_team = ReadStream("data/team_" + Lower(menu.extension) + ".yst")
		If (file_team = Null)
			RuntimeError("Invalid team file!")
		EndIf
		
		''find the number of teams and divisions
		menu.teams = 0
		menu.divisions = 1
		menu.no_league = 0
		While Not(Eof(file_team)) 
			
			Local b:Int
			
			b = ReadByte(file_team)
			
			If (b = 35) ''Asc(#)
				
				''read lines up to Division
				Local ln:String 
				
				For Local i:Int = 1 To 5		
					ln = ReadLine(file_team)
				Next
				
				''read and compare the division
				Local d:Int 	
				
				d = Int(Mid(ln,11,3)) 
				If (d > menu.divisions)
					menu.divisions = d
				EndIf
				
				''check availability of no-league teams
				If (d = 0)
					menu.no_league = 1
				EndIf
				
				menu.teams = menu.teams +1
				
			EndIf
			
		Wend		
		
		CloseStream(file_team)
		
		If (menu.teams = 0)
			RuntimeError("Cannot find teams in file: team_" + Lower(menu.extension) + ".yst")
		EndIf
		
		If (menu.divisions = 1)
			
			menu.division = 1
			
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
			
		Else 
			
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_DIVISION)
			
		EndIf
		
	End Method	
	
End Type
