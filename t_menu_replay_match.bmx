SuperStrict

Import "t_game_mode.bmx"

Type t_menu_replay_match Extends t_game_mode

	Method New()

		Self.type_id = TTypeId.ForObject(Self)
		
		music_mute = True
		
		''background
		Self.img_background = backgrounds.get("menu_match_presentation.jpg")
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		w.set_colors($6101D7, $7D1DFF, $3A0181)
		w.set_text(dictionary.gettext("FRIENDLY"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''play match
		w = New t_button
		w.set_geometry(512 -0.5*260, 350, 260, 44)
		w.set_colors($DC0000, $FF4141, $8C0000)
		w.set_text(dictionary.gettext("PLAY MATCH"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MATCH_LOADING)])
		Self.widgets.AddLast(w)
		
		Self.selected_widget = w
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MAIN)])
		Self.widgets.AddLast(w)
		
	End Method

End Type
