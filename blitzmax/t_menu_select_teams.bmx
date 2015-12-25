SuperStrict

Import "t_game_mode.bmx"

Type t_menu_select_teams Extends t_game_mode
	
	Field w_view_selected_teams:t_widget
	Field w_play:t_widget
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		music_mute = False
		
		''load teams
		Local file_team:TStream
		file_team = ReadStream("data/team_" + menu.extension + ".yst")
		If (file_team = Null) Then RuntimeError("Invalid team file!")
		
		Local teams_list:TList = New TList
		While Not(Eof(file_team)) 
			Local b:Int = ReadByte(file_team)		
			If (b = 35)	''Asc(#)
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
		Local filename:String
		Select menu.status
			Case MS_FRIENDLY	
				filename = "menu_friendly.jpg"
			Case MS_COMPETITION
				filename = "menu_competition.jpg"
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
		End Select
		Local s:String		
		Select menu.division
			Case 0
				s = "NO LEAGUE"
			Case 1
				s = "PREMIER DIVISION"
			Default
				s = "DIVISION " + (menu.division-1)
		End Select
		If (menu.confederation = t_confederation.NATIONAL)
			s = dictionary.gettext("NATIONAL TEAMS") + " - " + associations_files.find(menu.extension).name
		EndIf
		w.set_text(s, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''computer
		w = New t_button
		w.set_geometry(512 -1.5*300 -20, 86, 300, 30)
		w.set_colors($981E1E, $C72929, $640000)
		w.set_text(dictionary.gettext("COMPUTER"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''player - coach
		w = New t_button
		w.set_geometry(512 -0.5*300, 86, 300, 30)
		w.set_colors($0000C8, $1919FF, $000078)
		w.set_text(dictionary.gettext("PLAYER") + "-" + dictionary.gettext("COACH"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''coach
		w = New t_button
		w.set_geometry(512 +0.5*300 +20, 86, 300, 30)
		w.set_colors($009BDC, $19BBFF, $0071A0)
		w.set_text(dictionary.gettext("COACH"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		Local teams:Int = teams_list.count()
		Local tm:Int = 0
		Local col1:Int, col2:Int, col3:Int
		For Local team:t_team = EachIn teams_list
			tm = tm + 1
			w = New t_button
			w.set_size(270, 30)
			w.set_text(team.name, 0, 14)
			w.bind("fire1_down", "bc_team", [team.name, team.ext, String(team.number)])
			Select inlist_team(team.ext, team.number)
				Case CM_UNDEFINED
					w.set_colors($98691E, $C88B28, $3E2600)
				Case CM_COMPUTER
					w.set_colors($981E1E, $C72929, $640000)
				Case CM_PLAYER
					w.set_colors($0000C8, $1919FF, $000078)
				Case CM_COACH
					w.set_colors($009BDC, $19BBFF, $0071A0)
			End Select
			If (teams <= 8)
				w.x = 0.5*1024 -0.5*w.w
				w.y = 380 - 32*0.5*teams + 32*tm
			Else
				col1 = Floor(teams/3.0) + 1 * (teams Mod 3 = 2) 
				col2 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
				col3 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
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
		
		''view selected teams
		w = New t_button
		w.set_geometry(512 -360 -100 -20, 708, 360, 36)
		w.set_colors($9A6C9C, $BA99BB, $4F294F)
		w.set_text(dictionary.gettext("VIEW SELECTED TEAMS"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_VIEW_SELECTED_TEAMS)])
		Self.w_view_selected_teams = w
		Self.update_view_selected_teams_button()
		Self.widgets.AddLast(w)
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
		
		''play friendly/competition / nr. of teams selected
		w = New t_button
		w.set_geometry(512 +100 +20, 708, 360, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_play")
		Self.w_play:t_widget = w
		Self.update_play_button()
		Self.widgets.AddLast(w)
		
	End Method
	
	Method bc_team(name:String, ext:String, n:Int)
		add_team(name, ext, n)
		Select inlist_team(ext, n)
			Case CM_UNDEFINED
				Self.selected_widget.set_colors($98691E, $C88B28, $3E2600)
			Case CM_COMPUTER
				Self.selected_widget.set_colors($981E1E, $C72929, $640000)
			Case CM_PLAYER
				Self.selected_widget.set_colors($0000C8, $1919FF, $000078)
			Case CM_COACH
				Self.selected_widget.set_colors($009BDC, $19BBFF, $0071A0)
		End Select
		Self.update_view_selected_teams_button()
		Self.update_play_button()
	End Method
	
	Method bc_exit()
		If (menu.confederation = t_confederation.CUSTOM)
			If (menu.status = MS_COMPETITION)
				team_list = Null
				Select competition.typ
					Case CT_LEAGUE
						league = Null
					Case CT_CUP
						cup = Null
				End Select
				competition = Null
			EndIf
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
		Else If (menu.confederation = t_confederation.NATIONAL)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_NATIONALS_CONFEDERATION)
		Else If (menu.divisions = 1)
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_COUNTRY)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_DIVISION)
		EndIf
	End Method
	
	Method bc_play()
		''load teams in the list
		For Local team:t_team = EachIn team_list
			team.load_from_file()
		Next
		
		Select menu.status
			
			Case MS_FRIENDLY
				
				''take teams from list
				Local t:Int = HOME
				For Local tmp:t_team = EachIn team_list
					team[t] = tmp
					t = AWAY
				Next
				
				''choose the menu to set
				input_devices.set_availability(True)
				If (team[HOME].control <> CM_COMPUTER) 
					menu.team_to_set = HOME
					game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
				ElseIf (team[AWAY].control <> CM_COMPUTER)
					menu.team_to_set = AWAY
					game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
				Else
					game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MATCH_PRESENTATION)
				EndIf
				
			Case MS_COMPETITION
				competition.initialize(team_list)
				team_list = Null
				Select competition.typ
					Case CT_LEAGUE
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
					Case CT_CUP
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
				End Select							
		End Select
		
	End Method
	
	Method update_play_button()
		Local diff:Int = team_list_target - team_list.count()
		If (diff = 0)
			If (menu.status = MS_FRIENDLY)
				Self.w_play.text = dictionary.gettext("FRIENDLY")
			Else
				Select competition.typ
					Case CT_LEAGUE
						Self.w_play.text = dictionary.gettext("DIY LEAGUE")
					Case CT_CUP
						Self.w_play.text = dictionary.gettext("DIY CUP")
				End Select
			EndIf
			Self.w_play.set_colors($138B21, $1BC12F, $004814)
			Self.w_play.active = True
		Else
			If (diff > 1)
				''// NOTE: %n is replaced automatically by the game
				Self.w_play.text = Replace(dictionary.gettext("SELECT %n MORE TEAMS"), "%n", diff)
			Else If (diff = 1)
				Self.w_play.text = dictionary.gettext("SELECT 1 MORE TEAM")
			Else If (diff = -1)
				Self.w_play.text = dictionary.gettext("SELECT 1 LESS TEAM")
			Else
				''// NOTE: %n is replaced automatically by the game
				Self.w_play.text = Replace(dictionary.gettext("SELECT %n LESS TEAMS"), "%n", -diff)
			EndIf
			Self.w_play.set_colors($000000, $000000, $000000)
			Self.w_play.active = False
		EndIf
	End Method
	
	Method update_view_selected_teams_button()
		Self.w_view_selected_teams.visible = (team_list.count() > 0)
	End Method
	
End Type
