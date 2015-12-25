SuperStrict

Import "t_game_mode.bmx"

Type t_menu_view_selected_teams Extends t_game_mode

	Field w_play:t_widget

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
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
		Local s:String
		Select menu.status
			Case MS_FRIENDLY	
				w.set_colors($2D855D, $3DB37D, $1E5027)
				s = dictionary.gettext("FRIENDLY")
			Case MS_COMPETITION
				w.set_colors($415600, $5E7D00, $243000)
				s = competition.name
		End Select
		w.set_text(dictionary.gettext("SELECTED TEAMS FOR") + " " + s, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''computer
		w = New t_button
		w.set_geometry(512 -1.5*300 -20, 86, 300, 30)
		w.set_colors($981E1E, $C72929, $640000)
		w.set_text(dictionary.gettext("COMPUTER"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''player-coach
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
	
		Local teams:Int = team_list.count()
		Local tm:Int = 0
		Local col1:Int, col2:Int
		For Local team:t_team = EachIn team_list
			tm = tm +1
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
				w.y = 380 -32*0.5*teams +32*tm
			Else
				col1 = Floor(teams/3.0) + 1 * (teams Mod 3 = 2) 
				col2 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
				If (tm <= col1)
					w.x = 0.5*1024 -1.5*w.w -20
					w.y = 380 -32*0.5*col2 +32*tm
				Else If (tm <= col1 + col2)
					w.x = 0.5*1024 -0.5*w.w
					w.y = 380 -32*0.5*col2 +32*(tm -col1)
				Else
					w.x = 0.5*1024 +0.5*w.w +20
					w.y = 380 -32*0.5*col2 +32*(tm -col1 -col2)
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
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_SELECT_TEAMS)])
		Self.widgets.AddLast(w)
	
		''play friendly / nr. of selected teams
		w = New t_button
		w.set_geometry(512 +100 +20, 708, 360, 36)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_play")
		Self.w_play:t_widget = w
		Self.update_play_widget()
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
		Self.update_play_widget()
	End Method
	
	Method update_play_widget()
		Local diff:Int = team_list_target - team_list.count()
		If (diff = 0)
			If (menu.status = MS_FRIENDLY)
				Self.w_play.set_text(dictionary.gettext("FRIENDLY"))
			Else
				Select competition.typ
					Case CT_LEAGUE
						Self.w_play.set_text(dictionary.gettext("DIY LEAGUE"))
					Case CT_CUP
						Self.w_play.set_text(dictionary.gettext("DIY CUP"))
				End Select
			EndIf
			Self.w_play.set_colors($138B21, $1BC12F, $004814)
			Self.w_play.active = True
		Else
			If (diff > 1)
				''// NOTE: %n is replaced automatically by the game
				Self.w_play.set_text(Replace(dictionary.gettext("SELECT %n MORE TEAMS"), "%n", diff))
			Else If (diff = 1)
				Self.w_play.set_text(dictionary.gettext("SELECT 1 MORE TEAM"))
			Else If (diff = -1)
				Self.w_play.set_text(dictionary.gettext("SELECT 1 LESS TEAM"))
			Else
				''// NOTE: %n is replaced automatically by the game
				Self.w_play.set_text(Replace(dictionary.gettext("SELECT %n LESS TEAMS"), "%n", -diff))
			EndIf
			Self.w_play.set_colors($000000, $000000, $000000)
			Self.w_play.active = False
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
	
End Type
