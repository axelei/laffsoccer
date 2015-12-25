SuperStrict

Import "t_game_mode.bmx"

Type t_menu_competition_warning Extends t_game_mode

	Method New()
	
		Self.type_id = TTypeId.ForObject(Self)
		
		Local msg1:String = dictionary.gettext("YOU ARE ABOUT TO LOSE YOUR CURRENT DIY COMPETITION")
		Local cut:Int = msg1.find(" ", msg1.length/2)
			
		''background
		Self.img_background = backgrounds.get("menu_competition.jpg")
	
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		w.set_colors($415600, $5E7D00, $243000)
		w.set_text(dictionary.gettext("DIY COMPETITION"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''warning
		w = New t_button
		w.set_geometry(512 -0.5*580, 240, 580, 240)
		w.set_colors($DC0000, $FF4141, $8C0000)
		w.active = False
		Self.widgets.AddLast(w)
		
		''warning msg1
		w = New t_label
		w.set_text(Left(msg1, cut), 0, 14)
		w.set_position(512, 340)
		Self.widgets.AddLast(w)
		
		''warning msg2
		w = New t_label
		w.set_text(Right(msg1, msg1.length -cut), 0, 14)
		w.set_position(512, 380)
		Self.widgets.AddLast(w)
		
		''continue
		w = New t_button
		w.set_geometry(512 -0.5*180, 630, 180, 36)
		w.set_colors($568200, $77B400, $243E00)
		w.set_text(dictionary.gettext("CONTINUE"), 0, 14)
		w.bind("fire1_down", "bc_continue")
		Self.widgets.AddLast(w)
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("ABORT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MAIN)])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
	End Method

	Method bc_continue()
		Select competition.typ
			Case CT_LEAGUE
				league = Null
			Case CT_CUP
				cup = Null
		End Select
		competition = Null
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_DIY_COMPETITION)
	End Method
		
End Type
