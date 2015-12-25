SuperStrict

Import "t_game_mode.bmx"

Type t_menu_select_team Extends t_game_mode
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''load teams
		Local file_name:String, file_team:TStream
		file_name = "data/team_" + menu.extension + ".yst"
		file_team = ReadStream(file_name)
		If (file_team = Null)
			RuntimeError(" file not found: " + file_name)
		EndIf
		
		Local teams_list:TList = New TList
		While Not(Eof(file_team)) 
			Local b:Int = ReadByte(file_team)		
			If (b = 35) ''Asc(#)
				''number
				Local ln:String = ReadLine(file_team)
				Local num:Int = Int(Mid(ln, 10, 3))
				
				''skip Country
				ReadLine(file_team)
				
				''name
				ln = ReadLine(file_team)
				Local team_name:String = Right(ln, Len(ln) -10)
				
				''skip Tactics
				ReadLine(file_team)
				
				''Division
				ln = ReadLine(file_team)
				
				''compare the division
				If (Int(Mid(ln, 11, 3)) = menu.division)
					Local team:t_team	= New t_team
					team.name 			= team_name
					team.ext			= menu.extension
					team.number			= num
					teams_list.AddLast(team)
				EndIf
			EndIf
		Wend
		sort_by_names(teams_list)
		
		CloseStream(file_team)
		
		''background
		Select menu.status
			Case MS_EDIT
				Self.img_background = backgrounds.get("menu_edit.jpg")
			Case MS_TRAINING
				Self.img_background = backgrounds.get("menu_training.jpg")
		End Select
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*660, 30, 660, 40)
		Select menu.status
			Case MS_EDIT
				w.set_colors($89421B, $BB5A25, $3D1E0D)
			Case MS_TRAINING
				w.set_colors($1B8A7F, $25BDAE, $115750)
		End Select
		Select menu.division
			Case 0
				w.set_text("NO LEAGUE", 0, 14)
			Case 1
				w.set_text("PREMIER DIVISION", 0, 14)
			Default
				w.set_text("DIVISION " +(menu.division -1), 0, 14)
		End Select
		
		''national teams
		If (menu.confederation = t_confederation.NATIONAL)
			w.set_text(dictionary.gettext("NATIONAL TEAMS") + " - " + associations_files.find(menu.extension).name)
		EndIf
		w.active = False
		Self.widgets.AddLast(w)
		
		Local teams:Int = teams_list.count()
		Local tm:Int = 0
		Local col1:Int, col2:Int
		For Local team:t_team = EachIn teams_list
			tm = tm + 1
			w = New t_button
			w.set_size(270, 30)
			w.set_colors($98691E, $C88B28, $3E2600)
			w.set_text(team.name, 0, 14)
			w.bind("fire1_down", "bc_team", [String(team.number)])
			If (teams <= 8)
				w.x = 0.5*1024 -0.5*w.w
				w.y = 380 - 32*0.5*teams + 32*tm
			Else
				col1 = Floor(teams/3.0) + 1 * ((teams Mod 3) = 2) 
				col2 = Floor(teams/3.0) + 1 * ((teams Mod 3) > 0) 
				If (tm <= col1)
					w.x = 0.5*1024 -1.5*w.w - 20
					w.y = 380 - 32*0.5*col2 + 32*tm
				Else If (tm <= col1 + col2)
					w.x = 0.5*1024 -0.5*w.w
					w.y = 380 - 32*0.5*col2 + 32*(tm -col1)
				Else
					w.x = 0.5*1024 +0.5*w.w + 20
					w.y = 380 - 32*0.5*col2 + 32*(tm -col1 -col2)
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
		w.bind("fire1_down", "bc_exit")
		If (Self.selected_widget = Null)
			Self.selected_widget = w
		EndIf
		Self.widgets.AddLast(w)
		
	End Method
	
	Method bc_team(n:Int)
		team[HOME]			= New t_team
		team[HOME].ext		= menu.extension
		team[HOME].number	= n
		
		team[HOME].load_from_file()
		
		team[HOME].kit = 0
		
		team[AWAY] = Null
		
		Select menu.status
			Case MS_EDIT
				
				For Local ply:t_player = EachIn team[HOME].players
					ply.create_face()
				Next
				
				backup_file("data/team_" + Lower(team[HOME].ext) + ".yst", "data/team_" + Lower(team[HOME].ext) + ".ys0")
				
				menu.modified = False
				
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_EDIT_PLAYERS)
				
			Case MS_TRAINING
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_TRAINING_SETTINGS)
		End Select
		
	End Method
	
	Method bc_exit()
		If (menu.confederation = t_confederation.CUSTOM)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
		Else If (menu.confederation = t_confederation.NATIONAL)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_NATIONALS_CONFEDERATION)
		Else If (menu.divisions = 1)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_COUNTRY)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_DIVISION)
		EndIf
	End Method
	
End Type
