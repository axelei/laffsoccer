SuperStrict

Import "t_game_mode.bmx"

Type t_menu_diy_competition Extends t_game_mode

	Method New()
	
		Self.type_id = TTypeId.ForObject(Self)
		
		menu.status = MS_COMPETITION

		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*520, 30, 520, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(dictionary.gettext("SELECT DIY COMPETITION TYPE"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''league
		w = New t_button
		w.set_geometry(512 -0.5*340, 300, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("LEAGUE"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_DESIGN_DIY_LEAGUE)])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

		''cup
		w = New t_button
		w.set_geometry(512 -0.5*340, 380, 340, 40)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CUP"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_DESIGN_DIY_CUP)])
		Self.widgets.AddLast(w)
	
		''tournament
		w = New t_button
		w.set_geometry(512 -0.5*340, 460, 340, 40)
		w.set_colors($666666, $8F8D8D, $404040)
		w.set_text(dictionary.gettext("TOURNAMENT"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
	
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MAIN)])
		Self.widgets.AddLast(w)
	
	End Method

End Type	
