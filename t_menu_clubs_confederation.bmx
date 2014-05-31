SuperStrict

Import "t_game_mode.bmx"

Type t_menu_clubs_confederation Extends t_game_mode

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
		Local s:String
		Select menu.status
			Case MS_FRIENDLY
				s = dictionary.gettext("FRIENDLY")
				w.set_colors($2D855D, $3DB37D, $1E5027)
			Case MS_COMPETITION
				s = competition.name
				w.set_colors($415600, $5E7D00, $243000)
			Case MS_EDIT
				s = dictionary.gettext("EDIT TEAMS")
				w.set_colors($89421B, $BB5A25, $3D1E0D)
			Case MS_TRAINING
				s = dictionary.gettext("TRAINING")
				w.set_colors($1B8A7F, $25BDAE, $115750)
		End Select
		w.set_text(s + " - " + dictionary.gettext("CLUB TEAMS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''UEFA
		w = New t_button
		w.set_geometry(512 -0.5*300, 230, 300, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text("UEFA", 0, 14)
		w.bind("fire1_down", "bc_confederation", [t_confederation.UEFA])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
	
		''CONCACAF
		w = New t_button
		w.set_geometry(512 -0.5*300, 290, 300, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text("CONCACAF", 0, 14)
		w.bind("fire1_down", "bc_confederation", [t_confederation.CONCACAF])
		Self.widgets.AddLast(w)
	
		''CONMEBOL
		w = New t_button
		w.set_geometry(512 -0.5*300, 350, 300, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text("CONMEBOL", 0, 14)
		w.bind("fire1_down", "bc_confederation", [t_confederation.CONMEBOL])
		Self.widgets.AddLast(w)
	
		''CAF
		w = New t_button
		w.set_geometry(512 -0.5*300, 410, 300, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text("CAF", 0, 14)
		w.bind("fire1_down", "bc_confederation", [t_confederation.CAF])
		Self.widgets.AddLast(w)
		
		''AFC
		w = New t_button
		w.set_geometry(512 -0.5*300, 470, 300, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text("AFC", 0, 14)
		w.bind("fire1_down", "bc_confederation", [t_confederation.AFC])
		Self.widgets.AddLast(w)
		
		''OFC
		w = New t_button
		w.set_geometry(512 -0.5*300, 530, 300, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text("OFC", 0, 14)
		w.bind("fire1_down", "bc_confederation", [t_confederation.OFC])
		Self.widgets.AddLast(w)
		
		''abort
		w = New t_button
		w.set_geometry(512 -0.5*200, 708, 200, 36)
		w.set_colors($C8000E, $FF1929, $74040C)
		w.set_text(dictionary.gettext("ABORT"), 0, 14)
		w.bind("fire1_down", "bc_abort")
		Self.widgets.AddLast(w)
	
	End Method

	Method bc_confederation(n:String)
		menu.confederation = n
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_CLUBS_COUNTRY)
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
