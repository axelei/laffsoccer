SuperStrict

Import "t_game_mode.bmx"

Type t_menu_view_team Extends t_game_mode

	Field team:t_team
	Field img_hflag:TImage
	Field img_ucode10y:TImage
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		Self.team = t_team(competition.teams.ValueAtIndex(competition.team_to_view))
				
		''background
		Self.img_background = backgrounds.get("menu_set_team.jpg")
	
		''small national flags
		Self.img_hflag	= load_image("images/flags/small", Lower(team.country) +".png", 0, 0)
		
		''load faces
		For Local player:t_player = EachIn team.players
			player.create_face()
		Next
	
		''load club logo / national flag
		Self.team.load_clnf(0)
		
		''load other images
		Local img_stars:TImage	= load_image("images", "stars.png", MASKEDIMAGE, $0000CC)
	
		''yellow charset
		Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
		rgb_pairs.add($FCFCFC, $FCFC00)
		
		Self.img_ucode10y = load_and_edit_png("images/ucode_10.png", rgb_pairs, MASKEDIMAGE, $00F700)
	
		''players
		Local w:t_widget
		Local x:Int
		For Local p:Int = 1 To FULL_TEAM
		
			Local player:t_player = Self.team.player_at_position(p -1)
			
			''face (select player) 
			Local face_button:t_face_button
			face_button = New t_face_button
			face_button.set_geometry(30, 126 + 19 * (p -1), 24, 17)
			Self.set_player_widget_color(face_button, p)
			face_button.player = player
			face_button.active = False
			Self.widgets.AddLast(face_button)
		
			''number
			w = New t_label
			w.set_geometry(54, 126 +19*(p -1), 30, 17)
			w.set_text("", 0, 10)
			If (player)
				w.text = player.number
			EndIf
			Self.widgets.AddLast(w)
			
			''name
			w = New t_button
			w.set_geometry(84, 126 +19*(p -1), 276, 17)
			w.set_text("", 1, 10)
			Self.set_player_widget_color(w, p)
			If (player)
				Local ns:String = player.name
				If Len(player.surname) > 0 And Len(player.name) > 0
					ns = ns + " "
				EndIf
				w.text = ns + player.surname
			EndIf
			w.active = False
			Self.widgets.AddLast(w)
			
			''nationality
			x = 360
			If Self.team.national = False
				If (game_settings.use_flags)
					Local flag_button:t_flag_button
					flag_button = New t_flag_button
					flag_button.set_geometry(x, 126 + 19 * (p -1), 24, 17)
					flag_button.player = player
					flag_button.active = False
					Self.widgets.AddLast(flag_button)
					x = x +31
				Else
					w = New t_label
					w.set_geometry(x+3, 126 + 19 * (p -1), 54, 17)
					w.set_text("", 0, 10)
					If (player)
						w.set_text("(" + player.nationality + ")")
					EndIf
					Self.widgets.AddLast(w)
					x = x +57
				EndIf
			EndIf
			
			''type
			w = New t_label
			w.set_geometry(x, 126 +19*(p -1), 30, 17)
			w.set_text("", 0, 10)
			If (player)
				w.set_text(player_roles[player.role])
			EndIf
			Self.widgets.AddLast(w)
			x = x +31
			
			''skills
			For Local j:Int = 1 To 3
				w = New t_label
				w.set_geometry(x, 126 +19*(p -1), 12, 17)
				w.set_text("", 0, 10)
				w.img_ucode = Self.img_ucode10y
				If (player)
					w.text = player.get_best_skill(j)
				EndIf
				Self.widgets.AddLast(w)
				x = x +12
			Next
			x = x +31
			
			''goals
			w = New t_label
			w.set_geometry(x, 126 +19*(p -1), 30, 17)
			w.set_text("", 0, 10)
			If (player)
				w.text = player.goals
			EndIf
			Self.widgets.AddLast(w)
		Next
		
		''goals label
		w = New t_label
		w.set_text("GOALS", 0, 10)
		w.set_position(x +15, 116)
		Self.widgets.AddLast(w)
		
		''title
		w = New t_button
		w.set_geometry(0.5*1024 -300, 45, 601, 41)
		w.set_colors($6A5ACD, $8F83D7, $372989)
		w.set_text(Self.team.name, 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''club logo / national flag
		w = New t_picture
		w.image = team.clnf
		Local lgh:Int = ImageHeight(team.clnf)
		Local lgw:Int = ImageWidth(team.clnf)
		Local lgx:Int = 35
		Local lgy:Int = 65 -0.5 * lgh
		w.set_geometry(lgx, lgy, lgw, lgh)
		Self.widgets.AddLast(w)
		
		''national / confederation flag
		lgw = ImageWidth(Self.img_hflag)
		lgh = ImageHeight(Self.img_hflag)
		w = New t_button
		w.set_geometry(1024 -45 -62, 45, lgw +4, lgh +4)
		w.set_colors($000000, $8F8D8D, $8F8D8D)
		w.image = Self.img_hflag
		w.set_frame(lgw, lgh, 0, 0)
		w.active = False
		Self.widgets.AddLast(w)
			
		''board
		Local board:t_board = New t_board
		board.set_position(0.5*1024 +115, 126)
		board.set_teams(Self.team)
		Self.widgets.AddLast(board)
		
		''exit
		w = New t_button
		w.set_geometry(1024 -185 -30, 768 -0.5*40 -60, 145, 40)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_SELECT_SQUAD_TO_VIEW)])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

	End Method

	Method set_player_widget_color(w:t_widget, p:Int)
		''goalkeeper		
		If (p = 1)
			w.set_colors($00A7DE, $33CCFF, $005F7E)
		''other player
		Else If (p <= TEAM_SIZE)
			w.set_colors($003FDE, $255EFF, $00247E)
		''bench
		Else If (p <= TEAM_SIZE + game_settings.bench_size)
			w.set_colors($111188, $2D2DB3, $001140)
		''reserve
		Else If (p <= Self.team.players.Count())
			w.set_colors($404040, $606060, $202020)
		''void
		Else 			
			w.set_colors($202020, $404040, $101010)
		EndIf
	End Method
	
End Type
