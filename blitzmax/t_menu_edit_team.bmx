SuperStrict

Import "t_game_mode.bmx"

Type t_menu_edit_team Extends t_game_mode
	
	Field edited_team:t_team
	Field selected_ply:Int
	
	Field w_face:t_widget[TEAM_SIZE]
	Field w_number:t_widget[TEAM_SIZE]
	Field w_name_surname:t_widget[TEAM_SIZE]
	Field w_type:t_widget[TEAM_SIZE]
	Field w_tactics:t_widget[18]
	Field w_kit:t_widget[5]
	Field player_kit:t_player_kit
	Field w_new_kit:t_widget
	Field w_delete_kit:t_widget
	Field w_save:t_widget
	Field w_clnf:t_widget
	
	Method New()

		Self.type_id = TTypeId.ForObject(Self)
		
		Self.edited_team = team[HOME]
		Self.selected_ply = 0 ''none

		''background
		Self.img_background = backgrounds.get("menu_edit_team.jpg")
		
		''load club logo / national flag
		Self.edited_team.load_clnf(0)
	
		''players
		Local w:t_widget
		For Local p:Int = 1 To TEAM_SIZE
			''face
			Local face_button:t_face_button
			face_button = New t_face_button
			face_button.set_geometry(630, 400 + 25 * (p -1), 24, 21)
			Self.w_face[p -1] = face_button
			Self.update_face_button(p)
			Self.widgets.AddLast(face_button)
	
			''number
			w = New t_button
			w.set_geometry(654, 400 +25*(p -1), 30, 21)
			w.set_text("", 0, 10)
			Self.w_number[p -1] = w
			Self.update_number_button(p)
			Self.widgets.AddLast(w)
	
			''name & surname
			w = New t_button
			w.set_geometry(684, 400 +25*(p -1), 280, 21)
			w.set_text("", 0, 10)
			Self.w_name_surname[p -1] = w
			Self.update_name_surname_button(p)
			Self.widgets.AddLast(w)
		
			''type
			w = New t_button
			w.set_geometry(964, 400 +25*(p -1), 30, 21)
			w.set_text("", 0, 10)
			Self.w_type[p -1] = w
			Self.update_type_button(p)
			Self.widgets.AddLast(w)
	
			''select
			w = New t_button
			w.set_geometry(630, 400 +25*(p -1), 24 +30 +280 +30, 21)
			w.bind("fire1_down", "bc_select_player", [String(p)])
			Self.widgets.AddLast(w)
		Next
	
		''tactics
		For Local tc:Int = 0 To 17
			w = New t_button
			w.set_geometry(490 +110*(Floor(tc/9)), 130 +26*(tc Mod 9), 90, 22)
			w.set_text(tactics_name[tc], 0, 10)
			w.bind("fire1_down", "bc_tactics", [String(tc)])
			Self.w_tactics[tc] = w
			Self.widgets.AddLast(w)
		Next
		Self.update_tactics_buttons()
	
		''team name
		Local input_button:t_input_button
		input_button = New t_input_button
		input_button.set_geometry(60, 30, 520, 40)
		input_button.set_colors($9C522A, $BB5A25, $69381D)
		input_button.set_text(Self.edited_team.name, 0, 14)
		input_button.set_entry_limit(16)
		input_button.bind("on_update", "bc_update_team_name")
		Self.widgets.AddLast(input_button)
	
		''city
		w = New t_button
		w.set_geometry(44, 110, 202, 30)
		w.set_colors($808080, $C0C0C0, $404040)
		w.set_text(dictionary.gettext("CITY") + ":", -1, 10)
		w.active = False
		Self.widgets.AddLast(w)
		
		input_button = New t_input_button
		input_button.set_geometry(202 +44 +8, 110, 202, 30)
		input_button.set_colors($10A000, $15E000, $096000)
		input_button.set_text(Self.edited_team.city, 1, 10)
		input_button.set_entry_limit(15)
		input_button.bind("on_update", "bc_update_city")
		Self.widgets.AddLast(input_button)
	
		''stadium
		w = New t_button
		w.set_geometry(44, 160, 202, 30)
		w.set_colors($808080, $C0C0C0, $404040)
		w.set_text(dictionary.gettext("STADIUM") + ":", -1, 10)
		w.active = False
		Self.widgets.AddLast(w)
		
		input_button = New t_input_button
		input_button.set_geometry(202 +44 +8, 160, 202, 30)
		input_button.set_colors($10A000, $15E000, $096000)
		input_button.set_text(Self.edited_team.stadium, 1, 10)
		input_button.set_entry_limit(15)
		input_button.bind("on_update", "bc_update_stadium")
		Self.widgets.AddLast(input_button)

		''confederation (nationals) / country & division (clubs)
		If (edited_team.national = True)
			w = New t_button
			w.set_geometry(44, 235, 202, 30)
			w.set_colors($808080, $C0C0C0, $404040)
			w.set_text(dictionary.gettext("CONFEDERATION") + ":", -1, 10)
			w.active = False
			Self.widgets.AddLast(w)
			
			w = New t_button
			w.set_geometry(202 +44 +8, 235, 202, 30)
			w.set_colors($666666, $8F8D8D, $404040)
			w.set_text(Self.edited_team.country, 1, 10)
			w.active = False
			Self.widgets.AddLast(w)
		Else
			w = New t_button
			w.set_geometry(44, 210, 202, 30)
			w.set_colors($808080, $C0C0C0, $404040)
			w.set_text(dictionary.gettext("COUNTRY") + ":", -1, 10)
			w.active = False
			Self.widgets.AddLast(w)
			
			w = New t_button
			w.set_geometry(202 +44 +8, 210, 202, 30)
			w.set_colors($666666, $8F8D8D, $404040)
			w.set_text(Self.edited_team.country, 1, 10)
			w.active = False
			Self.widgets.AddLast(w)
			
			w = New t_button
			w.set_geometry(44, 260, 202, 30)
			w.set_colors($808080, $C0C0C0, $404040)
			w.set_text(dictionary.gettext("DIVISION") + ":", -1, 10)
			w.active = False
			Self.widgets.AddLast(w)
			
			w = New t_button
			w.set_geometry(202 +44 +8, 260, 202, 30)
			w.set_colors($666666, $8F8D8D, $404040)
			w.set_text(Self.edited_team.division, 1, 10)
			w.active = False
			Self.widgets.AddLast(w)
		EndIf
	
		''coach name
		w = New t_button
		w.set_geometry(44, 325, 148, 30)
		w.set_colors($808080, $C0C0C0, $404040)
		w.set_text(dictionary.gettext("COACH") + ":", -1, 10)
		w.active = False
		Self.widgets.AddLast(w)
	
		input_button = New t_input_button
		input_button.set_geometry(148 +44 +8, 325, 266, 30)
		input_button.set_colors($10A000, $15E000, $096000)
		input_button.set_text(Self.edited_team.coach_name, 1, 10)
		input_button.set_entry_limit(22)
		input_button.bind("on_update", "bc_update_coach_name")
		Self.widgets.AddLast(input_button)

		''kit type
		w = New t_button
		w.set_geometry(42, 768 -95 -36*5 -24*4, 160, 36)
		w.set_colors($530DB3, $6F12EE, $380977)
		w.set_text(dictionary.gettext("STYLE"), 0, 10)
		w.bind("fire1_down", "bc_kit_style", ["+1"])
		w.bind("fire1_hold", "bc_kit_style", ["+1"])
		w.bind("fire2_down", "bc_kit_style", ["-1"])
		w.bind("fire2_hold", "bc_kit_style", ["-1"])
		Self.widgets.AddLast(w)
	
		''shirt 1
		w = New t_button
		w.set_geometry(42, 768 -95 -36*4 -24*3, 160, 36)
		w.set_colors($530DB3, $6F12EE, $380977)
		w.set_text(dictionary.gettext("SHIRT") + " 1", 0, 10)
		w.bind("fire1_down", "bc_shirt_1", ["+1"])
		w.bind("fire1_hold", "bc_shirt_1", ["+1"])
		w.bind("fire2_down", "bc_shirt_1", ["-1"])
		w.bind("fire2_hold", "bc_shirt_1", ["-1"])
		Self.widgets.AddLast(w)
	
		''shirt 2
		w = New t_button
		w.set_geometry(42, 768 -95 -36*3 -24*2, 160, 36)
		w.set_colors($530DB3, $6F12EE, $380977)
		w.set_text(dictionary.gettext("SHIRT") + " 2", 0, 10)
		w.bind("fire1_down", "bc_shirt_2", ["+1"])
		w.bind("fire1_hold", "bc_shirt_2", ["+1"])
		w.bind("fire2_down", "bc_shirt_2", ["-1"])
		w.bind("fire2_hold", "bc_shirt_2", ["-1"])
		Self.widgets.AddLast(w)
	
		''shorts
		w = New t_button
		w.set_geometry(42, 768 -95 -36*2 -24, 160, 36)
		w.set_colors($530DB3, $6F12EE, $380977)
		w.set_text(dictionary.gettext("SHORTS"), 0, 10)
		w.bind("fire1_down", "bc_shorts", ["+1"])
		w.bind("fire1_hold", "bc_shorts", ["+1"])
		w.bind("fire2_down", "bc_shorts", ["-1"])
		w.bind("fire2_hold", "bc_shorts", ["-1"])
		Self.widgets.AddLast(w)
	
		''socks
		w = New t_button
		w.set_geometry(42, 768 -95 -36, 160, 36)
		w.set_colors($530DB3, $6F12EE, $380977)
		w.set_text(dictionary.gettext("SOCKS"), 0, 10)
		w.bind("fire1_down", "bc_socks", ["+1"])
		w.bind("fire1_hold", "bc_socks", ["+1"])
		w.bind("fire2_down", "bc_socks", ["-1"])
		w.bind("fire2_hold", "bc_socks", ["-1"])
		Self.widgets.AddLast(w)
	
		''home
		w = New t_button
		w.set_geometry(430, 768 -95 -36*5 -24*4, 160, 36)
		''// NOTE: max 10 characters
		w.set_text(dictionary.gettext("HOME KIT"), 0, 10)
		w.bind("fire1_down", "bc_select_kit", ["0"])
		Self.w_kit[0] = w
		Self.widgets.AddLast(w)
	
		''away
		w = New t_button
		w.set_geometry(430, 768 -95 -36*4 -24*3, 160, 36)
		''// NOTE: max 10 characters
		w.set_text(dictionary.gettext("AWAY KIT"), 0, 10)
		w.bind("fire1_down", "bc_select_kit", ["1"])
		Self.w_kit[1] = w
		Self.widgets.AddLast(w)
	
		''third
		w = New t_button
		w.set_geometry(430, 768 -95 -36*3 -24*2, 160, 36)
		''// NOTE: max 10 characters
		w.set_text(dictionary.gettext("THIRD KIT"), 0, 10)
		w.bind("fire1_down", "bc_select_kit", ["2"])
		Self.w_kit[2] = w
		Self.widgets.AddLast(w)
	
		''change1
		w = New t_button
		w.set_geometry(430, 768 -95 -36*2 -24, 160, 36)
		''// NOTE: max 10 characters
		w.set_text(dictionary.gettext("1ST CHANGE KIT"), 0, 10)
		w.bind("fire1_down", "bc_select_kit", ["3"])
		Self.w_kit[3] = w
		Self.widgets.AddLast(w)
	
		''change2
		w = New t_button
		w.set_geometry(430, 768 -95 -36, 160, 36)
		''// NOTE: max 10 characters
		w.set_text(dictionary.gettext("2ND CHANGE KIT"), 0, 10)
		w.bind("fire1_down", "bc_select_kit", ["4"])
		Self.w_kit[4] = w
		Self.widgets.AddLast(w)
		
		Self.update_kit_buttons()
		
		''kit
		Self.player_kit = New t_player_kit
		Self.player_kit.set_position(233, 385)
		Self.player_kit.team = Self.edited_team
		Self.player_kit.reload()
		Self.widgets.AddLast(Self.player_kit)
	
		''club logo / national flag
		w = New t_picture
		w.image = Self.edited_team.clnf
		Local lgh:Int = ImageHeight(Self.edited_team.clnf)
		Local lgw:Int = ImageWidth(Self.edited_team.clnf)
		Local lgx:Int = 650 -0.5*lgw
		Local lgy:Int = 32
		w.set_geometry(lgx, lgy, lgw, lgh)
		Self.w_clnf = w
		Self.widgets.AddLast(w)
		
		''board
		Local board:t_board = New t_board
		board.set_position(720, 55)
		board.set_teams(Self.edited_team)
		Self.widgets.AddLast(board)
		
		''players
		w = New t_button
		w.set_geometry(40, 768 -60, 160, 36)
		w.set_colors($00825F, $00C28E, $00402F)
		w.set_text(dictionary.gettext("PLAYERS"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_EDIT_PLAYERS)])
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

		''new kit
		w = New t_button
		w.set_geometry(210, 768 -60, 210, 36)
		w.set_text(dictionary.gettext("NEW KIT"), 0, 14)
		w.bind("fire1_down", "bc_new_kit")
		Self.w_new_kit = w
		Self.update_new_kit_button()
		Self.widgets.AddLast(w)
	
		''delete kit
		w = New t_button
		w.set_geometry(430, 768 -60, 220, 36)
		w.set_text(dictionary.gettext("DELETE KIT"), 0, 14)
		w.bind("fire1_down", "bc_delete_kit")
		Self.w_delete_kit = w
		Self.update_delete_kit_button()
		Self.widgets.AddLast(w)
	
		''save
		w = New t_button
		w.set_geometry(660, 768 -60, 160, 36)
		w.set_text(dictionary.gettext("SAVE"), 0, 14)
		w.bind("fire1_down", "bc_save")
		Self.w_save = w
		Self.update_save_button()
		Self.widgets.AddLast(w)
	
		''exit
		w = New t_button
		w.set_geometry(830, 768 -60, 160, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_SELECT_TEAM)])
		Self.widgets.AddLast(w)
		
	End Method

	Method bc_select_player(p:Int)
		''select
		If (Self.selected_ply = 0)
			Self.selected_ply = p
			
		''deselect
		Else If (Self.selected_ply = p)
			selected_ply = 0
		
		''swap
		Else
			Local base_tactics:Int = tactics_array[edited_team.tactics].based_on
			Local ply1:Int = tactics_order[base_tactics, Self.selected_ply -1]
			Local ply2:Int = tactics_order[base_tactics, p -1]
			
			swap_elements(Self.edited_team.players, ply1, ply2)

			Local was_selected:Int = Self.selected_ply
			Self.selected_ply = 0
			
			Self.update_player_buttons(was_selected)
			Self.menu_modified()
		EndIf
		
		Self.update_player_buttons(p)
	End Method
	
	Method bc_tactics(n:Int)
		If (Self.edited_team.tactics <> n)
			Self.edited_team.tactics = n
			Self.update_tactics_buttons()
			Self.menu_modified()
			For Local p:Int = 1 To TEAM_SIZE
				Self.update_player_buttons(p)
			Next
		EndIf
	End Method
	
	Method bc_update_team_name()
		Self.edited_team.name = Self.selected_widget.text
		Self.menu_modified()
	End Method
	
	Method bc_update_city()
		Self.edited_team.city = Self.selected_widget.text
		Self.menu_modified()
	End Method
	
	Method bc_update_stadium()
		Self.edited_team.stadium = Self.selected_widget.text
		Self.menu_modified()
	End Method
	
	Method bc_update_coach_name()
		Self.edited_team.coach_name = Self.selected_widget.text
		Self.menu_modified()
	End Method
	
	Method bc_kit_style(n:Int)
		Local kit:t_kit = Self.edited_team.kit_at_index(Self.edited_team.kit)
		kit.style = rotate(kit.style, 0, 20, n)
		Self.player_kit.reload()
		
		If (Self.edited_team.kit = 0)
			Self.edited_team.load_clnf(0)
			Self.w_clnf.image = Self.edited_team.clnf
		EndIf
		Self.menu_modified()
	End Method
	
	Method bc_shirt_1(n:Int)	
		Local kit:t_kit = Self.edited_team.kit_at_index(Self.edited_team.kit)
		kit.shirt1 = rotate(kit.shirt1, 0, 22, n)
		Self.player_kit.recolor()
		
		If (Self.edited_team.kit = 0)
			Self.edited_team.load_clnf(0)
			Self.w_clnf.image = Self.edited_team.clnf
		EndIf
		Self.menu_modified()
	End Method
	
	Method bc_shirt_2(n:Int)
		Local kit:t_kit = Self.edited_team.kit_at_index(Self.edited_team.kit)
		kit.shirt2 = rotate(kit.shirt2, 0, 22, n)
		Self.player_kit.recolor()
		
		If (Self.edited_team.kit = 0)
			Self.edited_team.load_clnf(0)
			Self.w_clnf.image = Self.edited_team.clnf
		EndIf
		Self.menu_modified()
	End Method
	
	Method bc_shorts(n:Int)
		Local kit:t_kit = Self.edited_team.kit_at_index(Self.edited_team.kit)
		kit.shorts = rotate(kit.shorts, 0, 22, n)
		Self.player_kit.recolor()
		
		If (Self.edited_team.kit = 0)
			Self.edited_team.load_clnf(0)
			Self.w_clnf.image = Self.edited_team.clnf
		EndIf
		Self.menu_modified()
	End Method
	
	Method bc_socks(n:Int)
		Local kit:t_kit = Self.edited_team.kit_at_index(Self.edited_team.kit)
		kit.socks = rotate(kit.socks, 0, 22, n)
		Self.player_kit.recolor()
		
		If (Self.edited_team.kit = 0)
			Self.edited_team.load_clnf(0)
			Self.w_clnf.image = Self.edited_team.clnf
		EndIf
		Self.menu_modified()
	End Method
	
	Method bc_select_kit(n:Int)
		Self.edited_team.kit = n
		Self.player_kit.reload()
		Self.update_kit_buttons()
	End Method
	
	Method bc_new_kit()
		''add a change kit (kit_count is 3 or 4)
		Local kit:t_kit = Self.edited_team.new_kit()
		
		If (kit = Null) Return
	
		''copy style, shirt1 & shirt2 colors from kit 0 or 1
		Local other:t_kit = Self.edited_team.kit_at_index(Self.edited_team.kit_count-4)
		kit.style	= other.style
		kit.shirt1	= other.shirt1
		kit.shirt2	= other.shirt2
	
		''copy shorts & socks colors from kit 1 or 0
		other = Self.edited_team.kit_at_index(5 -Self.edited_team.kit_count)
		kit.shorts	= other.shorts
		kit.socks	= other.socks
					
		Self.edited_team.kit = edited_team.kit_count-1
		Self.player_kit.reload()
		Self.update_kit_buttons()
		Self.update_new_kit_button()
		Self.update_delete_kit_button()
		
		If (Self.w_new_kit.active = False)
			Self.selected_widget = Self.w_delete_kit
		EndIf
		
		Self.menu_modified()
	End Method
	
	Method bc_delete_kit()
		Local deleted:Int = edited_team.delete_kit()
		
		If (deleted = False) Return
		
		If (edited_team.kit => edited_team.kit_count-1)
			Self.edited_team.kit = Self.edited_team.kit_count-1
			Self.player_kit.reload()
		EndIf
		Self.update_kit_buttons()
		Self.update_new_kit_button()
		Self.update_delete_kit_button()
		
		If (Self.w_delete_kit.active = False)
			Self.selected_widget = Self.w_new_kit
		EndIf
		
		Self.menu_modified()
	End Method
	
	Method bc_save()
		Self.edited_team.save_to_file()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAM)
	End Method
		
	Method menu_modified()
		menu.modified = True
		Self.update_save_button()
	End Method
	
	Method update_player_buttons(p:Int)
		Self.update_face_button(p)
		Self.update_number_button(p)
		Self.update_name_surname_button(p)
		Self.update_type_button(p)
	End Method
	
	Method update_face_button(p:Int)
		Self.set_player_widget_color(Self.w_face[p -1], p)
		t_face_button(Self.w_face[p -1]).player = Self.edited_team.player_at_position(p -1)
	End Method
	
	Method update_number_button(p:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		Self.w_number[p -1].text = player.number
	End Method
	
	Method update_name_surname_button(p:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		Local ns:String = player.name
		If (Len(player.surname) > 0) And (Len(player.name) > 0)
			ns = ns + " "
		EndIf
		ns = ns + player.surname
		Self.w_name_surname[p -1].set_text(ns, 1)
		Self.set_player_widget_color(Self.w_name_surname[p -1], p)
	End Method
	
	Method update_type_button(p:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		Self.w_type[p -1].text = player_roles[player.role]
	End Method
	
	Method update_tactics_buttons()
		For Local tc:Int = 0 To 17
			If (Self.edited_team.tactics = tc)
				Self.w_tactics[tc].set_colors($9D7B03, $E2B004, $675103)
			Else
				Self.w_tactics[tc].set_colors($E2B004, $FCCE30, $9D7B03)
			EndIf
		Next
	End Method
	
	Method update_kit_buttons()
		For Local k:Int = 1 To 5
			If (k > Self.edited_team.kit_count)
				Self.w_kit[k-1].set_colors($666666, $8F8D8D, $404040)
				Self.w_kit[k-1].active = False
			Else
				If (k = Self.edited_team.kit +1)
					Self.w_kit[k-1].set_colors($881845, $DC246E, $510F29)
				Else
					Self.w_kit[k-1].set_colors($DA2A70, $E45C92, $A41C52)
				EndIf
				Self.w_kit[k-1].active = True
			EndIf
		Next
	End Method
	
	Method update_new_kit_button()
		If (Self.edited_team.kit_count < MAX_KITS)
			Self.w_new_kit.set_colors($1769BD, $3A90E8, $10447A)
			Self.w_new_kit.active = True
		Else
			Self.w_new_kit.set_colors($666666, $8F8D8D, $404040)
			Self.w_new_kit.active = False
		EndIf
	End Method
	
	Method update_delete_kit_button()
		If (Self.edited_team.kit_count > MIN_KITS)
			Self.w_delete_kit.set_colors($3217BD, $5639E7, $221080)
			Self.w_delete_kit.active = True
		Else
			Self.w_delete_kit.set_colors($666666, $8F8D8D, $404040)
			Self.w_delete_kit.active = False
		EndIf
	End Method
	
	Method update_save_button()
		If (menu.modified = True)
			Self.w_save.set_colors($DC0000, $FF4141, $8C0000)
			Self.w_save.active = True
		Else
			Self.w_save.set_colors($666666, $8F8D8D, $404040)
			Self.w_save.active = False
		EndIf
	End Method
	
	Method set_player_widget_color(b:t_widget, ply:Int)
		''selected
		If (selected_ply = ply)
			b.set_colors($993333, $C24242, $5A1E1E)
		''goalkeeper		
		Else If (ply = 1)
			b.set_colors($4AC058, $81D38B, $308C3B)
		''other player
		Else If (ply <= 11)
			b.set_colors($308C3B, $4AC058, $1F5926)
		EndIf
	End Method
	
End Type

Type t_player_kit Extends t_widget
	Field img_kit_raw:TImage
	Field img_kit:TImage
	Field img_kit_shad:TImage
	Field team:t_team

	Method render()
		draw_image(Self.img_kit_shad, Self.x+2, Self.y+2)
		draw_image(Self.img_kit, Self.x, Self.y)
	End Method

	Method recolor()	
		Self.img_kit = copy_image(Self.img_kit_raw)
		Self.team.kit_at_index(Self.team.kit).render_image(Self.img_kit)
	End Method		
	
	Method reload()
		Self.img_kit_raw = LoadImage(Self.team.get_image_path("kit"), MASKEDIMAGE|DYNAMICIMAGE)
		Self.recolor()
		
		Self.img_kit_shad = copy_image(Self.img_kit)
		image2shadow(Self.img_kit_shad, $242424)
	End Method
End Type

