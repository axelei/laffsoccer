SuperStrict

Import "t_game_mode.bmx"

Type t_menu_edit_players Extends t_game_mode
	
	Field edited_team:t_team
	Field img_skill:TImage
	Field selected_ply:Int
	
	Field w_hair_color:t_widget[FULL_TEAM]
	Field w_hair_style:t_widget[FULL_TEAM]
	Field w_skin_color:t_widget[FULL_TEAM]
	Field w_face:t_widget[FULL_TEAM]
	Field w_number:t_widget[FULL_TEAM]
	Field w_name:t_widget[FULL_TEAM]
	Field w_surname:t_widget[FULL_TEAM]
	Field w_nationality:t_widget[FULL_TEAM]
	Field w_type:t_widget[FULL_TEAM]
	
	Field w_passing:t_widget[FULL_TEAM]
	Field w_shooting:t_widget[FULL_TEAM]
	Field w_heading:t_widget[FULL_TEAM]
	Field w_tackling:t_widget[FULL_TEAM]
	Field w_ball_control:t_widget[FULL_TEAM]
	Field w_speed:t_widget[FULL_TEAM]
	Field w_finishing:t_widget[FULL_TEAM]
	Field w_price:t_widget[FULL_TEAM]
	
	Field w_new_player:t_widget
	Field w_delete_player:t_widget
	Field w_save:t_widget
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		Self.edited_team = team[HOME]
		Self.selected_ply = 0 ''none
		
		''background
		Self.img_background = backgrounds.get("menu_edit_players.jpg")
		
		''load images	
		Self.img_skill 	= load_image("images", "skill.png", MASKEDIMAGE, $808080)
		
		''players
		Local w:t_widget
		Local input_button:t_input_button
		For Local p:Int = 1 To FULL_TEAM
			
			''hair color
			w = New t_button
			w.set_geometry(50, 86 +19*(p -1), 20, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_hair_color", [String(p), "+1"])
			w.bind("fire1_hold", "bc_hair_color", [String(p), "+1"])
			w.bind("fire2_down", "bc_hair_color", [String(p), "-1"])
			w.bind("fire2_hold", "bc_hair_color", [String(p), "-1"])
			Self.w_hair_color[p -1] = w
			Self.update_hair_color_button(p)
			Self.widgets.AddLast(w)
			
			''hair style
			w = New t_button
			w.set_geometry(74, 86 +19*(p -1), 20, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_hair_style", [String(p), "+1"])
			w.bind("fire1_hold", "bc_hair_style", [String(p), "+1"])
			w.bind("fire2_down", "bc_hair_style", [String(p), "-1"])
			w.bind("fire2_hold", "bc_hair_style", [String(p), "-1"])
			Self.w_hair_style[p -1] = w
			Self.update_hair_style_button(p)
			Self.widgets.AddLast(w)
			
			''skin color
			w = New t_button
			w.set_geometry(98, 86 +19*(p -1), 20, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_skin_color", [String(p), "+1"])
			w.bind("fire1_hold", "bc_skin_color", [String(p), "+1"])
			w.bind("fire2_down", "bc_skin_color", [String(p), "-1"])
			w.bind("fire2_hold", "bc_skin_color", [String(p), "-1"])
			Self.w_skin_color[p -1] = w
			Self.update_skin_color_button(p)
			Self.widgets.AddLast(w)
			
			''face (select player) 
			Local face_button:t_face_button
			face_button = New t_face_button
			face_button.set_geometry(122, 86 +19*(p -1), 24, 17)
			face_button.bind("fire1_down", "bc_select_player", [String(p)])
			Self.w_face[p -1] = face_button
			Self.update_face_button(p)
			Self.widgets.AddLast(face_button)
			
			''number
			w = New t_button
			w.set_geometry(146, 86 +19*(p -1), 30, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_number", [String(p), "+1"])
			w.bind("fire1_hold", "bc_number", [String(p), "+1"])
			w.bind("fire2_down", "bc_number", [String(p), "-1"])
			w.bind("fire2_hold", "bc_number", [String(p), "-1"])
			Self.w_number[p -1] = w
			Self.update_number_button(p)
			Self.widgets.AddLast(w)
			
			''name
			input_button = New t_input_button
			input_button.set_geometry(176, 86 +19*(p -1), 194, 17)
			input_button.set_text("", 1, 10)
			input_button.set_entry_limit(14)
			input_button.bind("on_update", "bc_update_name", [String(p)])
			Self.w_name[p -1] = input_button
			Self.update_name_button(p)
			Self.widgets.AddLast(input_button)
			
			''surname
			input_button = New t_input_button
			input_button.set_geometry(374, 86 +19*(p -1), 194, 17)
			input_button.set_text("", 1, 10)
			input_button.set_entry_limit(14)
			input_button.bind("on_update", "bc_update_surname", [String(p)])
			Self.w_surname[p -1] = input_button
			Self.update_surname_button(p)
			Self.widgets.AddLast(input_button)
			
			''nationality
			w = New t_button
			w.set_geometry(568, 86 +19*(p -1), 56, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_nationality", [String(p), "+1"])
			w.bind("fire1_hold", "bc_nationality", [String(p), "+1"])
			w.bind("fire2_down", "bc_nationality", [String(p), "-1"])
			w.bind("fire2_hold", "bc_nationality", [String(p), "-1"])
			Self.w_nationality[p -1] = w
			Self.update_nationality_button(p)
			Self.widgets.AddLast(w)
			
			''type
			w = New t_button
			w.set_geometry(624, 86 +19*(p -1), 30, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_type", [String(p), "+1"])
			w.bind("fire1_hold", "bc_type", [String(p), "+1"])
			w.bind("fire2_down", "bc_type", [String(p), "-1"])
			w.bind("fire2_hold", "bc_type", [String(p), "-1"])
			Self.w_type[p -1] = w
			Self.update_type_button(p)
			Self.widgets.AddLast(w)
			
			''passing
			w = New t_button
			w.set_geometry(654 +40*0, 86 +19*(p -1), 36, 17)
			w.bind("fire1_down", "bc_passing", [String(p), "+1"])
			w.bind("fire1_hold", "bc_passing", [String(p), "+1"])
			w.bind("fire2_down", "bc_passing", [String(p), "-1"])
			w.bind("fire2_hold", "bc_passing", [String(p), "-1"])
			Self.w_passing[p -1] = w
			Self.update_passing_button(p)
			Self.widgets.AddLast(w)
			
			''shooting
			w = New t_button
			w.set_geometry(654 +40*1, 86 +19*(p -1), 36, 17)
			w.bind("fire1_down", "bc_shooting", [String(p), "+1"])
			w.bind("fire1_hold", "bc_shooting", [String(p), "+1"])
			w.bind("fire2_down", "bc_shooting", [String(p), "-1"])
			w.bind("fire2_hold", "bc_shooting", [String(p), "-1"])
			Self.w_shooting[p -1] = w
			Self.update_shooting_button(p)
			Self.widgets.AddLast(w)
			
			''heading
			w = New t_button
			w.set_geometry(654 +40*2, 86 +19*(p -1), 36, 17)
			w.bind("fire1_down", "bc_heading", [String(p), "+1"])
			w.bind("fire1_hold", "bc_heading", [String(p), "+1"])
			w.bind("fire2_down", "bc_heading", [String(p), "-1"])
			w.bind("fire2_hold", "bc_heading", [String(p), "-1"])
			Self.w_heading[p -1] = w
			Self.update_heading_button(p)
			Self.widgets.AddLast(w)
			
			''tackling
			w = New t_button
			w.set_geometry(654 +40*3, 86 +19*(p -1), 36, 17)
			w.bind("fire1_down", "bc_tackling", [String(p), "+1"])
			w.bind("fire1_hold", "bc_tackling", [String(p), "+1"])
			w.bind("fire2_down", "bc_tackling", [String(p), "-1"])
			w.bind("fire2_hold", "bc_tackling", [String(p), "-1"])
			Self.w_tackling[p -1] = w
			Self.update_tackling_button(p)
			Self.widgets.AddLast(w)
			
			''ball control
			w = New t_button
			w.set_geometry(654 +40*4, 86 +19*(p -1), 36, 17)
			w.bind("fire1_down", "bc_ball_control", [String(p), "+1"])
			w.bind("fire1_hold", "bc_ball_control", [String(p), "+1"])
			w.bind("fire2_down", "bc_ball_control", [String(p), "-1"])
			w.bind("fire2_hold", "bc_ball_control", [String(p), "-1"])
			Self.w_ball_control[p -1] = w
			Self.update_ball_control_button(p)
			Self.widgets.AddLast(w)
			
			''speed
			w = New t_button
			w.set_geometry(654 +40*5, 86 +19*(p -1), 36, 17)
			w.bind("fire1_down", "bc_speed", [String(p), "+1"])
			w.bind("fire1_hold", "bc_speed", [String(p), "+1"])
			w.bind("fire2_down", "bc_speed", [String(p), "-1"])
			w.bind("fire2_hold", "bc_speed", [String(p), "-1"])
			Self.w_speed[p -1] = w
			Self.update_speed_button(p)
			Self.widgets.AddLast(w)
			
			''finishing
			w = New t_button
			w.set_geometry(654 +40*6, 86 +19*(p -1), 36, 17)
			w.bind("fire1_down", "bc_finishing", [String(p), "+1"])
			w.bind("fire1_hold", "bc_finishing", [String(p), "+1"])
			w.bind("fire2_down", "bc_finishing", [String(p), "-1"])
			w.bind("fire2_hold", "bc_finishing", [String(p), "-1"])
			Self.w_finishing[p -1] = w
			Self.update_finishing_button(p)
			Self.widgets.AddLast(w)
			
			''price
			w = New t_button
			w.set_geometry(930, 86 +19*(p -1), 66, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_price", [String(p), "+1"])
			w.bind("fire1_hold", "bc_price", [String(p), "+1"])
			w.bind("fire2_down", "bc_price", [String(p), "-1"])
			w.bind("fire2_hold", "bc_price", [String(p), "-1"])
			Self.w_price[p -1] = w
			Self.update_price_button(p)
			Self.widgets.AddLast(w)
		Next
		
		''SKILLS - column headers
		For Local k:Int = 0 To 6 
			w = New t_button
			w.set_geometry(654 +40*k, 86 -19, 36, 17)
			Local s:String
			Select k
				Case 0
					s = dictionary.gettext("P // 1 letter code for: PASSING")
				Case 1
					s = dictionary.gettext("V // 1 letter code for: SHOOTING")
				Case 2
					s = dictionary.gettext("H // 1 letter code for: HEADING")
				Case 3
					s = dictionary.gettext("T // 1 letter code for: TACKLING")
				Case 4
					s = dictionary.gettext("C // 1 letter code for: BALL CONTROL")
				Case 5
					s = dictionary.gettext("S // 1 letter code for: SPEED")
				Case 6
					s = dictionary.gettext("F // 1 letter code for: FINISHING")
			End Select
			w.set_text(Left(s, 1), 0, 10)
			w.active = False
			Self.widgets.AddLast(w)
		Next
		
		''team name
		input_button = New t_input_button
		input_button.set_geometry(60, 30, 520, 40)
		input_button.set_colors($9C522A, $BB5A25, $69381D)
		input_button.set_text(Self.edited_team.name, 0, 14)
		input_button.set_entry_limit(16)
		input_button.bind("on_update", "bc_update_team_name")
		Self.widgets.AddLast(input_button)
		
		''team
		w = New t_button
		w.set_geometry(40, 768 -60, 160, 36)
		w.set_colors($00825F, $00C28E, $00402F)
		w.set_text(dictionary.gettext("TEAM"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_EDIT_TEAM)])
		Self.widgets.AddLast(w)
		
		Self.selected_widget = w
		
		''new player
		w = New t_button
		w.set_geometry(210, 768 -60, 210, 36)
		w.set_text(dictionary.gettext("NEW PLAYER"), 0, 14)
		w.bind("fire1_down", "bc_new_player")
		Self.w_new_player = w
		Self.update_new_player_button()
		Self.widgets.AddLast(w)
		
		''delete player
		w = New t_button
		w.set_geometry(430, 768 -60, 220, 36)
		w.set_text(dictionary.gettext("DELETE PLAYER"), 0, 14)
		w.bind("fire1_down", "bc_delete_player")
		Self.w_delete_player = w
		Self.update_delete_player_button()
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
	
	Method bc_hair_color(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		player.hair_color = rotate(player.hair_color, 0, 8, n)
		player.create_face()
		Self.menu_modified()
	End Method
	
	Method bc_hair_style(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		player.hair_type = rotate(player.hair_type, 0, 41, n)
		player.create_face()
		Self.menu_modified()
	End Method
	
	Method bc_skin_color(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		player.skin_color = rotate(player.skin_color, 0, 6, n)
		player.create_face()
		Self.menu_modified()
	End Method
	
	Method bc_select_player(p:Int)
		''select
		If (Self.selected_ply = 0)
			Self.selected_ply = p
			Local player:t_player = Self.edited_team.player_at_position(selected_ply -1)
			copy_player(player)
			
		''deselect
		Else If (Self.selected_ply = p)
			Self.selected_ply = 0
			ply_temp = Null
			
		''swap
		Else 
			''find players
			Local base_tactics:Int = tactics_array[edited_team.tactics].based_on
			Local ply1:Int = tactics_order[base_tactics, Self.selected_ply -1]
			Local ply2:Int = tactics_order[base_tactics, p -1]
			
			swap_elements(edited_team.players, ply1, ply2)
			
			Local was_selected:Int = Self.selected_ply
			Self.selected_ply = 0
			
			Self.update_player_buttons(was_selected)
			Self.menu_modified()
		EndIf
		
		Self.update_player_buttons(p)
		Self.update_delete_player_button()
		
	End Method
	
	Method bc_number(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		player.change_number(n)
		Self.update_number_button(p)
		Self.menu_modified()
	End Method
	
	Method bc_update_name(p:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		player.name = Self.selected_widget.text
		Self.menu_modified()
	End Method
		
	Method bc_update_surname(p:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		player.surname = Self.selected_widget.text
		Self.menu_modified()
	End Method
		
	Method bc_nationality(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.nationality = associations_list.rotate(player.nationality, n)
		
		Self.update_nationality_button(p)
		Self.menu_modified()
	End Method
	
	Method bc_type(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		player.role = rotate(player.role, 0, 7, n)
		
		Self.update_type_button(p)
		Self.update_passing_button(p)
		Self.update_shooting_button(p)
		Self.update_heading_button(p)
		Self.update_tackling_button(p)
		Self.update_ball_control_button(p)
		Self.update_speed_button(p)
		Self.update_finishing_button(p)
		Self.update_price_button(p)
		
		Self.menu_modified()
	End Method
	
	Method bc_passing(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.skill_passing = slide(player.skill_passing, 0, 7, n)
		Self.menu_modified()
		
		Self.update_passing_button(p)
		Self.update_price_button(p)
	End Method
	
	Method bc_shooting(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.skill_shooting = slide(player.skill_shooting, 0, 7, n)
		Self.menu_modified()
		
		Self.update_shooting_button(p)
		Self.update_price_button(p)
	End Method
	
	Method bc_heading(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.skill_heading = slide(player.skill_heading, 0, 7, n)
		Self.menu_modified()
		
		Self.update_heading_button(p)
		Self.update_price_button(p)
	End Method
	
	Method bc_tackling(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.skill_tackling = slide(player.skill_tackling, 0, 7, n)
		Self.menu_modified()
		
		Self.update_tackling_button(p)
		Self.update_price_button(p)
	End Method
	
	Method bc_ball_control(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.skill_control = slide(player.skill_control, 0, 7, n)
		Self.menu_modified()
		
		Self.update_ball_control_button(p)
		Self.update_price_button(p)
	End Method
	
	Method bc_speed(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.skill_speed = slide(player.skill_speed, 0, 7, n)
		Self.menu_modified()
		
		Self.update_speed_button(p)
		Self.update_price_button(p)
	End Method
	
	Method bc_finishing(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.skill_finishing = slide(player.skill_finishing, 0, 7, n)
		Self.menu_modified()
		
		Self.update_finishing_button(p)
		Self.update_price_button(p)
	End Method
	
	Method bc_price(p:Int, n:Int)
		Local player:t_player = Self.edited_team.player_at_position(p -1)
		
		player.price = rotate(player.price, 0, 49, n)
		
		Self.update_price_button(p)
		Self.menu_modified()
	End Method
	
	Method bc_update_team_name()
		Self.edited_team.name = Self.selected_widget.text
		Self.menu_modified()
	End Method
	
	Method bc_new_player()
		Local player:t_player = edited_team.new_player()
		
		''team full		
		If (player = Null) Return
		
		''set default nationality & number
		player.nationality = edited_team.country		
		player.auto_number()
		
		''paste selected player
		If (ply_temp <> Null)
			paste_player(player)
			Local was_selected:Int = Self.selected_ply
			Self.selected_ply = 0
			If (was_selected <> 0)
				Self.update_player_buttons(was_selected)
			EndIf
		EndIf
		
		player.create_face()
		
		Self.update_player_buttons(edited_team.players.Count())
		Self.update_new_player_button()
		Self.update_delete_player_button()
		Self.menu_modified()
	End Method
	
	Method bc_delete_player()
		If (Self.selected_ply <> 0) And (Self.edited_team.players.Count() > BASE_TEAM)
			
			''swap 'selected' and 'last' player 
			Local base_tactics:Int = tactics_array[edited_team.tactics].based_on
			Local ply1:Int = tactics_order[base_tactics, Self.selected_ply -1]
			Local ply2:Int = tactics_order[base_tactics, Self.edited_team.players.Count() -1]
			
			swap_elements(Self.edited_team.players, ply1, ply2)
			
			Local was_selected:Int = Self.selected_ply
			Self.selected_ply = 0
			Self.update_player_buttons(was_selected)
			
			Local player:t_player = Self.edited_team.player_at_position(Self.edited_team.players.Count() -1)
			edited_team.delete_player(player)
			Self.update_player_buttons(Self.edited_team.players.Count() +1)
			
			Self.update_new_player_button()
			Self.update_delete_player_button()
			
			Self.menu_modified()
		EndIf
	End Method
	
	Method bc_save()
		Self.edited_team.save_to_file()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAM)
	End Method
	
	Method menu_modified()
		menu.modified = True
		Self.update_save_button()
	End Method
	
	Method copy_player(player:t_player)
		
		If (ply_temp = Null)
			ply_temp = New t_player
		EndIf
		
		ply_temp.name        = player.name
		ply_temp.surname     = player.surname
		ply_temp.nationality = player.nationality
		ply_temp.role        = player.role
		
		ply_temp.hair_type  = player.hair_type
		ply_temp.hair_color = player.hair_color
		ply_temp.skin_color = player.skin_color
		
		ply_temp.skill_passing   = player.skill_passing
		ply_temp.skill_shooting  = player.skill_shooting
		ply_temp.skill_heading   = player.skill_heading
		ply_temp.skill_tackling  = player.skill_tackling
		ply_temp.skill_control   = player.skill_control
		ply_temp.skill_speed     = player.skill_speed
		ply_temp.skill_finishing = player.skill_finishing
		
	End Method
	
	Method paste_player(player:t_player)
		
		player.name        = ply_temp.name
		player.surname     = ply_temp.surname
		player.nationality = ply_temp.nationality
		player.role        = ply_temp.role
		
		player.hair_type  = ply_temp.hair_type
		player.hair_color = ply_temp.hair_color
		player.skin_color = ply_temp.skin_color
		
		player.skill_passing   = ply_temp.skill_passing
		player.skill_shooting  = ply_temp.skill_shooting
		player.skill_heading   = ply_temp.skill_heading
		player.skill_tackling  = ply_temp.skill_tackling
		player.skill_control   = ply_temp.skill_control
		player.skill_speed     = ply_temp.skill_speed
		player.skill_finishing = ply_temp.skill_finishing
		
		ply_temp = Null
		
	End Method
	
	Method update_player_buttons(p:Int)
		Self.update_hair_color_button(p)
		Self.update_hair_style_button(p)
		Self.update_skin_color_button(p)
		Self.update_face_button(p)
		Self.update_number_button(p)
		
		Self.update_name_button(p)
		Self.update_surname_button(p)
		Self.update_nationality_button(p)
		Self.update_type_button(p)
		
		Self.update_passing_button(p)
		Self.update_shooting_button(p)
		Self.update_heading_button(p)
		Self.update_tackling_button(p)
		
		Self.update_ball_control_button(p)
		Self.update_speed_button(p)
		Self.update_finishing_button(p)
		Self.update_price_button(p)
	End Method
	
	Method update_hair_color_button(p:Int)
		Self.set_player_widget_color(Self.w_hair_color[p -1], p)
		If (p <= edited_team.players.Count())
			Self.w_hair_color[p -1].text = Left(dictionary.gettext("H // 1 letter code for: HAIR COLOR"), 1)
		Else
			Self.w_hair_color[p -1].text = ""
		EndIf
		Self.w_hair_color[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_hair_style_button(p:Int)
		Self.set_player_widget_color(Self.w_hair_style[p -1], p)
		If (p <= edited_team.players.Count())
			Self.w_hair_style[p -1].text = Left(dictionary.gettext("T // 1 letter code for: HAIR STYLE"), 1)
		Else
			Self.w_hair_style[p -1].text = ""
		EndIf
		Self.w_hair_style[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_skin_color_button(p:Int)
		Self.set_player_widget_color(Self.w_skin_color[p -1], p)
		If (p <= edited_team.players.Count())
			Self.w_skin_color[p -1].text = Left(dictionary.gettext("S // 1 letter code for: SKIN COLOR"), 1)
		Else
			Self.w_skin_color[p -1].text = ""
		EndIf
		Self.w_skin_color[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_face_button(p:Int)
		Self.set_player_widget_color(Self.w_face[p -1], p)
		If (p <= edited_team.players.Count())
			t_face_button(Self.w_face[p -1]).player = Self.edited_team.player_at_position(p -1)
		Else
			t_face_button(Self.w_face[p -1]).player = Null
		EndIf
		Self.w_face[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_number_button(p:Int)
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			Self.w_number[p -1].text = player.number
		Else
			Self.w_number[p -1].text = ""
		EndIf
		Self.w_number[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_name_button(p:Int)
		Self.set_player_widget_color(Self.w_name[p -1], p)
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			Self.w_name[p -1].text = player.name
		Else
			Self.w_name[p -1].text = ""
		EndIf
		Self.w_name[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_surname_button(p:Int)
		Self.set_player_widget_color(Self.w_surname[p -1], p)
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			Self.w_surname[p -1].text = player.surname
		Else
			Self.w_surname[p -1].text = ""
		EndIf
		Self.w_surname[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_nationality_button(p:Int)
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			Self.w_nationality[p -1].text = "(" + player.nationality + ")"
		Else
			Self.w_nationality[p -1].text = ""
		EndIf
		Self.w_nationality[p -1].set_active((p <= edited_team.players.Count()) And (edited_team.national = False))
	End Method
	
	Method update_type_button(p:Int)
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			Self.w_type[p -1].text = player_roles[player.role]
		Else
			Self.w_type[p -1].text = ""
		EndIf
		Self.w_type[p -1].active = (p <= edited_team.players.Count())
	End Method
	
	Method update_passing_button(p:Int)
		Self.set_player_widget_color(Self.w_passing[p -1], p)
		Self.w_passing[p -1].image = Null
		Self.w_passing[p -1].active = False
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			If (player.role <> PR.GOALKEEPER)
				Self.w_passing[p -1].image = Self.img_skill
				Self.w_passing[p -1].set_frame(32, 13, player.skill_passing, 0)
				Self.w_passing[p -1].active = True
			EndIf
		EndIf
	End Method
	
	Method update_shooting_button(p:Int)
		Self.set_player_widget_color(Self.w_shooting[p -1], p)
		Self.w_shooting[p -1].image = Null
		Self.w_shooting[p -1].active = False
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			If (player.role <> PR.GOALKEEPER)
				Self.w_shooting[p -1].image = Self.img_skill
				Self.w_shooting[p -1].set_frame(32, 13, player.skill_shooting, 0)
				Self.w_shooting[p -1].active = True
			EndIf
		EndIf
	End Method
	
	Method update_heading_button(p:Int)
		Self.set_player_widget_color(Self.w_heading[p -1], p)
		Self.w_heading[p -1].image = Null
		Self.w_heading[p -1].active = False
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			If (player.role <> PR.GOALKEEPER)
				Self.w_heading[p -1].image = Self.img_skill
				Self.w_heading[p -1].set_frame(32, 13, player.skill_heading, 0)
				Self.w_heading[p -1].active = True
			EndIf
		EndIf
	End Method
	
	Method update_tackling_button(p:Int)
		Self.set_player_widget_color(Self.w_tackling[p -1], p)
		Self.w_tackling[p -1].image = Null
		Self.w_tackling[p -1].active = False
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			If (player.role <> PR.GOALKEEPER)
				Self.w_tackling[p -1].image = Self.img_skill
				Self.w_tackling[p -1].set_frame(32, 13, player.skill_tackling, 0)
				Self.w_tackling[p -1].active = True
			EndIf
		EndIf
	End Method
	
	Method update_ball_control_button(p:Int)
		Self.set_player_widget_color(Self.w_ball_control[p -1], p)
		Self.w_ball_control[p -1].image = Null
		Self.w_ball_control[p -1].active = False
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			If (player.role <> PR.GOALKEEPER)
				Self.w_ball_control[p -1].image = Self.img_skill
				Self.w_ball_control[p -1].set_frame(32, 13, player.skill_control, 0)
				Self.w_ball_control[p -1].active = True
			EndIf
		EndIf
	End Method
	
	Method update_speed_button(p:Int)
		Self.set_player_widget_color(Self.w_speed[p -1], p)
		Self.w_speed[p -1].image = Null
		Self.w_speed[p -1].active = False
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			If (player.role <> PR.GOALKEEPER)
				Self.w_speed[p -1].image = Self.img_skill
				Self.w_speed[p -1].set_frame(32, 13, player.skill_speed, 0)
				Self.w_speed[p -1].active = True
			EndIf
		EndIf
	End Method
	
	Method update_finishing_button(p:Int)
		Self.set_player_widget_color(Self.w_finishing[p -1], p)
		Self.w_finishing[p -1].image = Null
		Self.w_finishing[p -1].active = False
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			If (player.role <> PR.GOALKEEPER)
				Self.w_finishing[p -1].image = Self.img_skill
				Self.w_finishing[p -1].set_frame(32, 13, player.skill_finishing, 0)
				Self.w_finishing[p -1].active = True
			EndIf
		EndIf
	End Method
	
	Method update_price_button(p:Int)
		If (p <= edited_team.players.Count())
			Local player:t_player = Self.edited_team.player_at_position(p -1)
			player.update_price()
			Self.w_price[p -1].text = price[player.price].figure
			Self.w_price[p -1].active = (player.role = PR.GOALKEEPER)
		Else
			Self.w_price[p -1].text = ""
			Self.w_price[p -1].active = False
		EndIf
	End Method
	
	Method update_new_player_button()
		If (Self.edited_team.players.Count() < full_team)
			Self.w_new_player.set_colors($1769BD, $3A90E8, $10447A)
			Self.w_new_player.active = 1
		Else
			Self.w_new_player.set_colors($666666, $8F8D8D, $404040)
			Self.w_new_player.active = 0
		EndIf
	End Method
	
	Method update_delete_player_button()
		If (Self.selected_ply <> 0) And (Self.edited_team.players.Count() > BASE_TEAM)
			Self.w_delete_player.set_colors($3217BD, $5639E7, $221080)
			Self.w_delete_player.active = 1
		Else
			Self.w_delete_player.set_colors($666666, $8F8D8D, $404040)
			Self.w_delete_player.active = 0
		EndIf
	End Method
	
	Method update_save_button()
		If (menu.modified = True)
			Self.w_save.set_colors($DC0000, $FF4141, $8C0000)
			Self.w_save.active = 1
		Else
			Self.w_save.set_colors($666666, $8F8D8D, $404040)
			Self.w_save.active = 0
		EndIf
	End Method
	
	Method set_player_widget_color(b:t_widget, ply:Int)
		''goalkeeper		
		If (ply = 1)
			b.set_colors($4AC058, $81D38B, $308C3B)
		''other player
		Else If (ply <= 11)
			b.set_colors($308C3B, $4AC058, $1F5926)
		''bench
		Else If (ply <= TEAM_SIZE + game_settings.bench_size)
			b.set_colors($2C7231, $40984A, $19431C)
		''reserve
		Else If (ply <= edited_team.players.Count())
			b.set_colors($404040, $606060, $202020)
		''void
		Else
			b.set_colors($202020, $404040, $101010)
		EndIf
		
		''selected
		If (selected_ply = ply)
			b.set_colors($993333, $C24242, $5A1E1E)
		EndIf
	End Method
	
End Type
