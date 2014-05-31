SuperStrict

Import "t_game_mode.bmx"

Type t_menu_select_squad_to_view Extends t_game_mode

	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
		
		''copy & sort team list
		Local table:TList = competition.teams.copy()
		Local teams:Int = table.count()
		sort_by_names(table)
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*660, 30, 660, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(dictionary.gettext("SELECT SQUAD TO VIEW"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''teams
		Local tm:Int, col1:Int, col2:Int, col3:Int
		tm = 0
		For Local team:t_team = EachIn table
			tm = tm + 1
			w = New t_button
			w.set_size(270, 30)
			w.set_colors($98691E, $C88B28, $3E2600)
			w.set_text(team.name, 0, 14)
			w.bind("fire1_down", "bc_team", [String(team.number), team.ext])
			If teams <= 8
				w.x = 0.5*1024 -0.5*w.w
				w.y = 386 - 36*0.5*teams + 36*tm
			Else
				col1 = Floor(teams/3.0) + 1 * (teams Mod 3 = 2) 
				col2 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
				col3 = Floor(teams/3.0) + 1 * (teams Mod 3 > 0) 
				If tm <= col1
					w.x = 0.5*1024 -1.5*w.w - 20
					w.y = 386 - 36*0.5*col2 + 36*tm
				Else If tm <= col1 + col2
					w.x = 0.5*1024 -0.5*w.w
					w.y = 386 - 36*0.5*col2 + 36*(tm -col1)
				Else
					w.x = 0.5*1024 +0.5*w.w + 20
					w.y = 386 - 36*0.5*col2 + 36*(tm -col1 -col2)
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
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_VIEW_STATISTICS)])
		Self.widgets.AddLast(w)
		
	End Method
	
	Method bc_team(n:Int, ext:String)
		Local i:Int = 0
		For Local team:t_team = EachIn competition.teams
			If team.number = n And team.ext = ext
				competition.team_to_view = i
			EndIf
			i = i + 1
		Next
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_VIEW_TEAM)
	End Method
	
End Type
