SuperStrict

Import "t_game_mode.bmx"
Import "t_piece.bmx"

Type t_menu_edit_tactics Extends t_game_mode

	Field copy_mode:Int
	Field team:t_team
	Field selected_for_swap:Int
	Field selected_for_pair:Int
	Field ball_zone:t_vec2d
	Field ball_copy_zone:t_vec2d
	
	Field img_number:TImage
	Field img_ball:TImage
	Field img_copy_ball:TImage
	Field img_pieces:TImage
	Field img_stars:TImage
	Field img_ucode10y:TImage
	Field img_player_shadow:TImage
	
	Field w_tactics_board:t_picture
	Field w_ball:t_ball_piece
	Field w_ball_copy:t_ball_piece
	Field w_player:t_player_piece[TEAM_SIZE]
	Field w_face:t_widget[FULL_TEAM]
	Field w_number:t_widget[FULL_TEAM]
	Field w_name:t_widget[FULL_TEAM]
	Field w_nat_flag:t_widget[FULL_TEAM]
	Field w_nat_code:t_widget[FULL_TEAM]
	Field w_type:t_widget[FULL_TEAM]
	Field w_skill:t_widget[FULL_TEAM, 3]
	Field w_stars:t_widget[FULL_TEAM]
	
	Field w_copy:t_widget
	Field w_flip:t_widget
	Field w_undo:t_widget
	
	Method New()
		
		Self.type_id = TTypeId.ForObject(Self)
		
		Self.copy_mode = False
		Self.team = menu.team_to_show
		Self.selected_for_swap = -1
		Self.selected_for_pair = -1
		Self.ball_zone = New t_vec2d
		Self.ball_copy_zone = New t_vec2d
		
		''background
		Self.img_background = backgrounds.get("menu_set_team.jpg")
		
		Self.img_number    = load_image("images", "number.png", MASKEDIMAGE, 0)
		Self.img_ball      = load_image("images", "ball.png", MASKEDIMAGE, $00FF00)
		Self.img_copy_ball = load_image("images", "ballsnow.png", MASKEDIMAGE, $00FF00)
		Self.img_pieces    = load_image("images", "pieces.png", MASKEDIMAGE, $00FF00)
		Self.img_stars     = load_image("images", "stars.png", MASKEDIMAGE, $0000CC)
		
		''yellow charset
		Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
		rgb_pairs.add($FCFCFC, $FCFC00)

		Self.img_ucode10y = load_and_edit_png("images/ucode_10.png", rgb_pairs, MASKEDIMAGE, $00F700)
		
		''players
		rgb_pairs.Clear()
		rgb_pairs.add($404040, $242424)
		Local img_shadow:TImage = load_and_edit_png("images/player/shadows/player_0.png", rgb_pairs, MASKEDIMAGE, $00F700)
		For Local p:t_player = EachIn Self.team.players
			p.img = p.load_image()
			p.shadow[0] = img_shadow
			p.load_hair()
			p.fmx = 2
			p.fmy = 1
		Next
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -280, 30, 560, 40)
		w.set_colors($BA9206, $E9B607, $6A5304)
		w.set_text(dictionary.gettext("EDIT TACTICS") + " (" + tactics_name[menu.tactics_to_edit] + ")", 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''board
		Self.w_tactics_board:t_picture = New t_picture
		Self.w_tactics_board.image = load_image("images", "tactics_board.png")
		Self.w_tactics_board.set_geometry(40, 110, 396, 576)
		Self.widgets.AddLast(Self.w_tactics_board)
		
		''ball piece
		Self.w_ball = New t_ball_piece
		Self.w_ball.set_size(24, 14)
		Self.w_ball.img_ball = Self.img_ball
		Self.w_ball.set_ranges(0, 4, 0, 6)
		Self.w_ball.set_grid_geometry(64, 155, 324, 472)
		Self.w_ball.active = True
		Self.w_ball.bind("on_update", "bc_update_ball_zone")
		Self.w_ball.bind("fire1_down", "bc_ball")
		Self.w_ball.bind("fire2_down", "bc_ball")
		Self.widgets.AddLast(Self.w_ball)
		Self.update_ball_piece()
		
		''ball copy piece
		Self.w_ball_copy = New t_ball_piece
		Self.w_ball_copy.set_size(24, 14)
		Self.w_ball_copy.img_ball = Self.img_copy_ball
		Self.w_ball_copy.set_ranges(0, 4, 0, 6)
		Self.w_ball_copy.set_grid_geometry(64, 155, 324, 472)
		Self.w_ball_copy.bind("on_update", "bc_update_ball_copy_zone")
		Self.w_ball_copy.bind("fire1_down", "bc_ball_copy")
		Self.w_ball_copy.bind("fire2_down", "bc_ball_copy")
		Self.widgets.AddLast(Self.w_ball_copy)
		Self.update_ball_copy_piece()
		
		''players pieces
		For Local p:Int = 0 To TEAM_SIZE -1
			Self.w_player[p] = New t_player_piece
			Self.w_player[p].set_size(24, 14)
			Self.w_player[p].img_number = Self.img_number
			Self.w_player[p].set_frame(16, 18, 0, 0)
			Self.w_player[p].set_ranges(0, 14, 0, 15)
			If (p = 0)
				Self.w_player[p].set_grid_geometry(40, 110, 372, 562)
				Self.w_player[p].active = False
			Else
				Self.w_player[p].set_grid_geometry(44, 130, 364, 522)
				Self.w_player[p].active = True
				Self.w_player[p].bind("on_update", "bc_update_player_zone", [String(p)])
			EndIf
			Self.w_player[p].bind("fire1_down", "bc_player", [String(p)])
			Self.w_player[p].bind("fire2_down", "bc_player", [String(p)])
			Self.widgets.AddLast(Self.w_player[p])
		Next
		Self.update_player_pieces()
		
		''players
		For Local p:Int = 0 To FULL_TEAM -1
			
			''face
			w = New t_face_button
			w.set_geometry(460, 110 +18*p, 24, 18)
			w.bind("fire1_down", "bc_pair_player", [String(p)])
			w.active = False
			Self.w_face[p] = w
			Self.widgets.AddLast(w)
			
			''number
			w = New t_button
			w.set_geometry(484, 110 +18*p, 30, 18)
			w.set_text("", 0, 10)
			w.active = False
			Self.w_number[p] = w
			Self.widgets.AddLast(w)
			
			''name (select player)
			w = New t_button
			w.set_geometry(514, 110 +18*p, 276, 18)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_select_player", [String(p)])
			Self.w_name[p] = w
			Self.widgets.AddLast(w)
			
			''nationality flag
			Local flag_button:t_flag_button
			flag_button = New t_flag_button
			flag_button.set_geometry(790, 110 +18*p, 24, 18)
			flag_button.active = False
			Self.w_nat_flag[p] = flag_button
			Self.widgets.AddLast(flag_button)
			
			''nationality code
			w = New t_button
			w.set_geometry(793, 110 +18*p, 54, 18)
			w.set_text("", 0, 10)
			w.active = False
			Self.w_nat_code[p] = w
			Self.widgets.AddLast(w)
			
			''type
			w = New t_button
			w.set_geometry(0, 110 +18*p, 30, 18)
			w.set_text("", 0, 10)
			w.active = False
			Self.w_type[p] = w
			Self.widgets.AddLast(w)
			
			''skills
			For Local j:Int = 0 To 2
				w = New t_button
				w.set_geometry(0, 110 +18*p, 12, 18)
				w.set_text("", 0, 10)
				w.active = False
				w.img_ucode = Self.img_ucode10y
				Self.w_skill[p, j] = w
				Self.widgets.AddLast(w)
			Next
			
			''stars
			w = New t_picture
			w.set_geometry(0, 110 +18*p, 64, 18)
			w.set_frame(64, 16, 0, 0)
			w.image = Self.img_stars
			Self.w_stars[p] = w
			Self.widgets.AddLast(w)
			
		Next
		
		Self.update_player_buttons()
		
		''copy
		w = New t_button
		w.set_geometry(10, 708, 140, 36)
		w.set_text(dictionary.gettext("COPY"), 0, 14)
		w.bind("fire1_down", "bc_copy")
		Self.w_copy = w
		Self.update_copy_button()
		Self.widgets.AddLast(w)
	
		''flip
		w = New t_button
		w.set_geometry(154, 708, 150, 36)
		w.set_colors($536B90, $7090C2, $263142)
		w.set_text("", 0, 14)
		w.bind("fire1_down", "bc_flip")
		Self.w_flip = w
		Self.update_flip_button()
		Self.widgets.AddLast(w)
	
		''undo
		w = New t_button
		w.set_geometry(308, 708, 144, 36)
		w.set_text(dictionary.gettext("UNDO"), 0, 14)
		w.bind("fire1_down", "bc_undo")
		Self.w_undo = w
		Self.update_undo_button()
		Self.widgets.AddLast(w)
	
		''import
		w = New t_button
		w.set_geometry(456, 708, 140, 36)
		w.set_colors($AB148D, $DE1AB7, $780E63)
		w.set_text(dictionary.gettext("IMPORT"), 0, 14)
		w.bind("fire1_down", "bc_import")
		Self.widgets.AddLast(w)
	
		''save/exit
		w = New t_button
		w.set_geometry(600, 708, 240, 36)
		w.set_colors($10A000, $15E000, $096000)
		w.set_text(dictionary.gettext("SAVE") + "/" + dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_save_exit")
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w
		
		''abort
		w = New t_button
		w.set_geometry(844, 708, 170, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("ABORT"), 0, 14)
		w.bind("fire1_down", "bc_abort")
		Self.widgets.AddLast(w)
	
	End Method
	
	Method bc_update_ball_zone()
		Self.ball_zone.x = 2 -Self.w_ball.square.x
		Self.ball_zone.y = 3 -Self.w_ball.square.y
		Self.update_player_pieces()
	End Method
	
	Method bc_ball()
		Self.w_ball.toggle_entry_mode()
	End Method
	
	Method bc_update_ball_copy_zone()
		Self.ball_copy_zone.x = 2 -Self.w_ball_copy.square.x
		Self.ball_copy_zone.y = 3 -Self.w_ball_copy.square.y
	End Method
	
	Method bc_ball_copy()
		''copy tactics
		Self.push_undo_stack()
		Local to_zone:Int   = 17 +Self.ball_copy_zone.x +5*Self.ball_copy_zone.y
		Local from_zone:Int = 17 +Self.ball_zone.x      +5*Self.ball_zone.y
		For Local p1:Int = 1 To TEAM_SIZE -1
			
			''flip mode on
			If (menu.tactics_flip And Sgn(Self.ball_zone.x * Self.ball_copy_zone.x) = -1)
				''paired players
				If (menu.edited_tactics.is_paired(p1))
					Local p2:Int = menu.edited_tactics.get_paired(p1)
					menu.edited_tactics.target[p1, to_zone].x = -menu.edited_tactics.target[p2, from_zone].x
					menu.edited_tactics.target[p1, to_zone].y = +menu.edited_tactics.target[p2, from_zone].y
				Else
					menu.edited_tactics.target[p1, to_zone].x = -menu.edited_tactics.target[p1, from_zone].x
					menu.edited_tactics.target[p1, to_zone].y = +menu.edited_tactics.target[p1, from_zone].y
				EndIf
			Else
				menu.edited_tactics.target[p1, to_zone].x = menu.edited_tactics.target[p1, from_zone].x
				menu.edited_tactics.target[p1, to_zone].y = menu.edited_tactics.target[p1, from_zone].y
			EndIf
		Next
			
		''copy flipped zone
		If (menu.tactics_flip And (Sgn(Self.ball_zone.x * Self.ball_copy_zone.x) <> 0))
			Local flipped_to_zone:Int   = 17 -Self.ball_copy_zone.x +5*Self.ball_copy_zone.y
			For Local p1:Int = 1 To TEAM_SIZE -1
				
				''paired players
				If (menu.edited_tactics.is_paired(p1))
					Local p2:Int = menu.edited_tactics.get_paired(p1)
					menu.edited_tactics.target[p1, flipped_to_zone].x = -menu.edited_tactics.target[p2, to_zone].x
					menu.edited_tactics.target[p1, flipped_to_zone].y = +menu.edited_tactics.target[p2, to_zone].y
				Else
					menu.edited_tactics.target[p1, flipped_to_zone].x = -menu.edited_tactics.target[p1, to_zone].x
					menu.edited_tactics.target[p1, flipped_to_zone].y = +menu.edited_tactics.target[p1, to_zone].y
				EndIf
			Next
		EndIf
		
		''disable copy mode
		Self.copy_mode = False
		Self.selected_widget = Self.w_ball
		Self.w_ball_copy.set_entry_mode(False)
		Self.update_copy_button()
		Self.update_ball_copy_piece()
		
		''update ball zone
		Self.ball_zone.x = Self.ball_copy_zone.x
		Self.ball_zone.y = Self.ball_copy_zone.y
		Self.update_ball_piece()
		Self.update_player_pieces()
	End Method
	
	Method bc_update_player_zone(p1:Int)
		Self.push_undo_stack()

		Local target:t_vec2d = New t_vec2d
		target.x = (7 -Self.w_player[p1].square.x) * TACT_DX
		target.y = (15 -2*Self.w_player[p1].square.y) * TACT_DY
		Local current_zone:Int = 17 +Self.ball_zone.x +5*Self.ball_zone.y
		menu.edited_tactics.target[p1, current_zone] = target

		''flip mode on
		If (menu.tactics_flip And (Self.ball_zone.x <> 0))
			Local flipped_zone:Int = 17 -Self.ball_zone.x +5*Self.ball_zone.y
			For Local p1:Int = 1 To TEAM_SIZE -1
				
				''paired players
				If (menu.edited_tactics.is_paired(p1))
					Local p2:Int = menu.edited_tactics.get_paired(p1)
					menu.edited_tactics.target[p1, flipped_zone].x = -menu.edited_tactics.target[p2, current_zone].x
					menu.edited_tactics.target[p1, flipped_zone].y = +menu.edited_tactics.target[p2, current_zone].y
				Else
					menu.edited_tactics.target[p1, flipped_zone].x = -menu.edited_tactics.target[p1, current_zone].x
					menu.edited_tactics.target[p1, flipped_zone].y = +menu.edited_tactics.target[p1, current_zone].y
				EndIf
			Next
		EndIf
	End Method
	
	Method bc_player(p:Int)
		Self.w_player[p].toggle_entry_mode()
	End Method
	
	Method bc_pair_player(n:Int)
		''swap and pair are mutually exclusive
		If (Self.selected_for_swap <> -1)
			Return
		EndIf
	
		''select
		If (Self.selected_for_pair = -1)
			Self.selected_for_pair = n
		''deselect
		Else If (Self.selected_for_pair = n)
			Self.selected_for_pair = -1
		''add/delete pair
		Else
			Self.push_undo_stack()
			Local base_tactics:Int = menu.edited_tactics.based_on
			Local ply1:Int = tactics_order[base_tactics, Self.selected_for_pair]
			Local ply2:Int = tactics_order[base_tactics, n]
			menu.edited_tactics.add_delete_pair(ply1, ply2)
			Self.selected_for_pair = -1
		EndIf
		Self.update_player_buttons()
	End Method
	
	Method bc_select_player(n:Int)
		''swap and pair are mutually exclusive
		If (Self.selected_for_pair <> -1)
			Return
		EndIf
		
		''select
		If (Self.selected_for_swap = -1)
			Self.selected_for_swap = n
		''deselect
		Else If (Self.selected_for_swap = n)
			Self.selected_for_swap = -1
		''swap
		Else 
			Local base_tactics:Int = menu.edited_tactics.based_on
			Local ply1:Int = tactics_order[base_tactics, Self.selected_for_swap]
			Local ply2:Int = tactics_order[base_tactics, n]
			swap_elements(Self.team.players, ply1, ply2)
			Self.selected_for_swap = -1
		EndIf
		Self.update_player_buttons()
	End Method
	
	Method bc_copy()
		Self.copy_mode = True
		Self.selected_widget = Self.w_ball_copy
		Self.w_ball_copy.set_entry_mode(True)
		Self.ball_copy_zone.x = Self.ball_zone.x
		Self.ball_copy_zone.y = Self.ball_zone.y
		Self.update_copy_button()
		Self.update_ball_copy_piece()
	End Method
	
	Method bc_flip()
		menu.tactics_flip = Not menu.tactics_flip
		Self.update_flip_button()
		Self.update_player_buttons()
	End Method
	
	Method bc_undo()
		If (menu.tactics_undo.IsEmpty())
			Return
		EndIf
		menu.edited_tactics = menu.tactics_undo.pop()
		Self.update_player_pieces()
		Self.update_player_buttons()
		Self.update_undo_button()
	End Method
	
	Method bc_import()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_TACTICS_IMPORT)
	End Method
	
	Method bc_save_exit()
		If (Not menu.tactics_undo.IsEmpty())
			''save warning
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_TACTICS_SAVE_WARNING)
		Else
			''exit
	 		If (menu.status = MS_NONE)
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
			Else
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
			EndIf
		EndIf
	End Method
	
	Method bc_abort()
		If (Not menu.tactics_undo.IsEmpty())
			''abort warning
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_TACTICS_ABORT_WARNING)
		Else
			''exit
	 		If (menu.status = MS_NONE)
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MAIN)
			Else
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
			EndIf
		EndIf
	End Method
		
	Method update_ball_piece()
		Self.w_ball.set_square(2 -Self.ball_zone.x, 3 -Self.ball_zone.y)
	End Method
	
	Method update_ball_copy_piece()
		Self.w_ball_copy.visible = (Self.copy_mode = True)
		Self.w_ball_copy.set_square(2 -Self.ball_copy_zone.x, 3 -Self.ball_copy_zone.y)
	End Method
	
	Method update_player_pieces()
		For Local ply:Int = 0 To TEAM_SIZE -1
			''update link to player
			Local player:t_player = t_player(Self.team.players.valueatindex(ply))
			Self.w_player[ply].player = player
			
			''update position
			If (ply = 0)
				Self.w_player[ply].set_square(7, 15)
			Else
				Local target:t_vec2d = menu.edited_tactics.target[ply, 17 +Self.ball_zone.x +5*Self.ball_zone.y]
				Self.w_player[ply].set_square(14 -(target.x / TACT_DX +7), 15 -((target.y / TACT_DY +15) / 2))
			EndIf
		Next
	End Method
	
	Method update_player_buttons()
		For Local p:Int = 0 To FULL_TEAM -1
			Local player:t_player = Self.team.player_at_position(p, menu.edited_tactics)
			
			''face
			If (menu.tactics_flip)
				Self.w_face[p].active = (p > 0) And (p < TEAM_SIZE)
				Self.set_player_flip_color(Self.w_face[p], p)
			Else
				Self.w_face[p].active = False
				Self.set_player_widget_color(Self.w_face[p], p)
			EndIf
			t_face_button(Self.w_face[p]).player = player
			
			''number
			If (player)
				Self.w_number[p].text = player.number
			Else
				Self.w_number[p].text = ""
			EndIf
			
			''name
			If (player)
				Local ns:String = player.name
				If (Len(player.surname) > 0) And (Len(player.name) > 0)
					ns = ns + " "
				EndIf
				Self.w_name[p].set_text(ns + player.surname, 1)
			Else
				Self.w_name[p].set_text("")
			EndIf
			Self.w_name[p].active = (p < Self.team.players.Count())
			Self.set_player_widget_color(Self.w_name[p], p)
			
			''nationality
			Local x:Int = 793
			If (Self.team.national = False)
				If (game_settings.use_flags)
					t_flag_button(Self.w_nat_flag[p]).player = player
					x = 821
				Else
					If (player)
						Self.w_nat_code[p].set_text("(" + player.nationality + ")")
					Else
						Self.w_nat_code[p].set_text("")
					EndIf
					x = 847
				EndIf
			EndIf
			
			''type
			Self.w_type[p].x = x
			If (player)
				Self.w_type[p].set_text(player_roles[player.role])
			Else
				Self.w_type[p].set_text("")
			EndIf
			x = x +31
			
			''skills
			For Local j:Int = 0 To 2
				Self.w_skill[p, j].x = x
				If (player)
					Self.w_skill[p, j].set_text(player.get_best_skill(j +1))
				Else
					Self.w_skill[p, j].set_text("")
				EndIf
				x = x +12
			Next
			x = x +4
			
			''stars
			Self.w_stars[p].x = x
			If (player)
				Self.w_stars[p].frame_y = price[player.price].stars
				Self.w_stars[p].visible = True
			Else
				Self.w_stars[p].visible = False
			EndIf

		Next
		Self.update_player_pieces()
	End Method
	
	Method push_undo_stack()
		menu.tactics_undo.push(menu.edited_tactics)
		Self.update_undo_button()
	End Method
	
	Method update_copy_button()
		If (Self.copy_mode)
			Self.w_copy.set_colors($666666, $8F8D8D, $404040)
			Self.w_copy.active = False
		Else
			Self.w_copy.set_colors($1769BD, $3A90E8, $10447A)
			Self.w_copy.active = True
		EndIf		
	End Method
	
	Method update_flip_button()
		If (menu.tactics_flip)
			Self.w_flip.text = dictionary.gettext("FLIP ON")
		Else
			Self.w_flip.text = dictionary.gettext("FLIP OFF")
		EndIf
	End Method
	
	Method update_undo_button()
		If (Not menu.tactics_undo.IsEmpty())
			Self.w_undo.set_colors($BDBF2F, $F0F23C, $8B8C23)
			Self.w_undo.active = True
		Else
			Self.w_undo.set_colors($666666, $8F8D8D, $404040)
			Self.w_undo.active = False
		EndIf
	End Method
	
	Method set_player_widget_color(w:t_widget, pos:Int)
		''selected for swap
		If (selected_for_swap = pos)
			w.set_colors($993333, $C24242, $5A1E1E)
		''selected for pair
		Else If (selected_for_pair = pos)
			w.set_colors($339999, $42C2C2, $1E5A5A)
		''goalkeeper		
		Else If (pos = 0)
			w.set_colors($00A7DE, $33CCFF, $005F7E)
		''other player
		Else If (pos < TEAM_SIZE)
			w.set_colors($003FDE, $255EFF, $00247E)
		''bench
		Else If (pos < TEAM_SIZE + game_settings.bench_size)
			w.set_colors($111188, $2D2DB3, $001140)
		''reserve
		Else If (pos < Self.team.players.Count())
			w.set_colors($404040, $606060, $202020)
		''void
		Else 			
			w.set_colors($202020, $404040, $101010)
		EndIf
	End Method
	
	Method set_player_flip_color(w:t_widget, pos:Int)
		If (pos > 0) And (pos < TEAM_SIZE)
			Local base_tactics:Int = menu.edited_tactics.based_on
			Local ply:Int = tactics_order[base_tactics, pos]
			Select menu.edited_tactics.pairs[ply]
				Case 0
					w.set_colors($5FC24A, $78F55D, $468F36)
				Case 1
					w.set_colors($CC3E4C, $FF4E5F, $992F39)
				Case 2
					w.set_colors($9D9A98, $D1CDCA, $6B6968)
				Case 3
					w.set_colors($BE8445, $F2A858, $8C6133)
				Case 4
					w.set_colors($BD4DB8, $F062E9, $8A3886)
				Case 255
					w.set_colors($000000, $000000, $000000)
				Default
					RuntimeError("invalid pair value")
			End Select
		Else
			w.set_colors($000000, $000000, $000000)
		EndIf	
	End Method
	
End Type

Type t_ball_piece Extends t_piece
	Field img_ball:TImage
		
	Method draw_image()
		draw_sub_image_rect(Self.img_ball, Self.x +0.5*Self.w -2, Self.y +1, 8, 8, 32, 0, 8, 8)
		draw_sub_image_rect(Self.img_ball, Self.x +0.5*Self.w -4, Self.y -1, 8, 8, 0, 0, 8, 8)
	End Method
End Type

Type t_player_piece Extends t_piece
	Field player:t_player
	Field img_number:TImage
	
	Method draw_image()
		If (Self.player)
			Self.player.x = Self.x +0.5*Self.w -1
			Self.player.y = Self.y +0.5*Self.h
			
			draw_sub_image_rect(Self.player.shadow[0], Self.player.x -29, Self.player.y -34, 50, 50, 50*Self.player.fmx, 50*Self.player.fmy, 50, 50)
			
			Self.player.save(0)
			Self.player.draw(0)
			
			''number
			Local f0:Int = Self.player.number Mod 10
			Local f1:Int = (Self.player.number -f0) / 10 Mod 10 
			
			Local dx:Int = Self.x +0.5*Self.w
			Local dy:Int = Self.y -32
		
			Local w0:Int = 6 -2*(f0 = 1)
			Local w1:Int = 6 -2*(f1 = 1)
			
			If (f1 > 0)
		 		dx = dx -(w0 +2 +w1)/2
	 			draw_sub_image_rect(img_number, dx, dy, 8, 10, f1*8, 0, 8, 10)
 				dx = dx +w1 +2
				draw_sub_image_rect(img_number, dx, dy, 8, 10, f0*8, 0, 8, 10)
			Else
				draw_sub_image_rect(img_number, dx -w0/2, dy, 8, 10, f0*8, 0, 8, 10)
			EndIf

		EndIf
	End Method
End Type
