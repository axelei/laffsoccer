SuperStrict

Import "t_game_mode.bmx"
Import "t_team_input_device_button.bmx"
Import "t_player_input_device_button.bmx"

Type t_menu_set_team Extends t_game_mode

	Field own_team:t_team
	Field opponent_team:t_team
	Field current_team:t_team
	Field reserved_input_devices:Int
	
	Field img_hflag:TImage
	Field img_aflag:TImage
	Field img_stars:TImage
	Field img_controls:TImage

	Field img_ucode10y:TImage
	Field selected_ply:Int
	Field compare_tactics:Int
	Field board:t_board
	
	Field w_player_input_devices:t_widget[FULL_TEAM]
	Field w_face:t_widget[FULL_TEAM]
	Field w_number:t_widget[FULL_TEAM]
	Field w_name:t_widget[FULL_TEAM]
	Field w_nat_flag:t_widget[FULL_TEAM]
	Field w_nat_code:t_widget[FULL_TEAM]
	Field w_type:t_widget[FULL_TEAM]
	Field w_skill:t_widget[FULL_TEAM, 3]
	Field w_stars:t_widget[FULL_TEAM]
	Field w_tactics:t_widget[18]
	Field w_compare_tactics:t_widget
	Field w_opponent_team:t_widget
	Field w_edit_tactics:t_widget
	Field w_control_mode:t_widget
	Field w_team_input_device:t_widget
	Field w_play_match:t_widget
	Field w_exit:t_widget
	Field w_team_name:t_widget
	Field w_clnf:t_widget
	Field w_cnnc:t_widget
	Field w_coach_name:t_widget
	
	Method New()
	
		Self.type_id = TTypeId.ForObject(Self)
		
		Self.selected_ply = 0 ''none
		Self.compare_tactics = False
		
		music_mute = True

		''create faces for the team menu
		For Local t:Int = HOME To AWAY
			For Local player:t_player = EachIn team[t].players
				player.create_face()
			Next
		Next
		
		If (menu.team_to_set = HOME)
			Self.own_team = team[HOME]
			Self.opponent_team = team[AWAY]
			Self.reserved_input_devices = (Self.own_team.control <> CM_COMPUTER) And (Self.opponent_team.control <> CM_COMPUTER)
		Else
			Self.own_team = team[AWAY]
			Self.opponent_team = team[HOME]
			Self.reserved_input_devices = 0
		EndIf
		
		''default input device
		If (Self.own_team.input_device = Null And Self.own_team.non_ai_input_devices_count() = 0)
			Self.own_team.set_input_device(input_devices.assign_first_available())
		EndIf
		
		Self.current_team = Self.own_team
		
		''background
		Self.img_background = backgrounds.get("menu_set_team.jpg")
	
		''small national flags
		Self.img_hflag	= load_image("images/flags/small", Lower(Self.own_team.country) +".png", 0, 0)
		Self.img_aflag	= load_image("images/flags/small", Lower(Self.opponent_team.country) +".png", 0, 0)
		
		''load club logo / national flag
		Self.own_team.load_clnf(0)
		Self.opponent_team.load_clnf(0)
	
		''load other images
		Self.img_stars		= load_image("images", "stars.png", MASKEDIMAGE, $0000CC)
		Self.img_controls	= load_image("images", "controls.png")
	
		''yellow charset
		Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
		rgb_pairs.add($FCFCFC, $FCFC00)

		Self.img_ucode10y = load_and_edit_png("images/ucode_10.png", rgb_pairs, MASKEDIMAGE, $00F700)
	
		''players
		Local w:t_widget
		For Local p:Int = 1 To FULL_TEAM
			
			''face
			w = New t_face_button
			w.set_geometry(30, 126 + 19 * (p -1), 24, 17)
			w.active = False
			Self.w_face[p -1] = w
			Self.widgets.AddLast(w)

			''number
			w = New t_button
			w.set_geometry(54, 126 +19*(p -1), 30, 17)
			w.set_text("", 0, 10)
			w.active = False
			Self.w_number[p -1] = w
			Self.widgets.AddLast(w)
			
			''name (select player)
			w = New t_button
			w.set_geometry(84, 126 +19*(p -1), 276, 17)
			w.set_text("", 0, 10)
			w.bind("fire1_down", "bc_select_player", [String(p)])
			Self.w_name[p -1] = w
			Self.widgets.AddLast(w)
			
			''nationality flag
			Local flag_button:t_flag_button
			flag_button = New t_flag_button
			flag_button.set_geometry(360, 126 + 19 * (p -1), 24, 17)
			flag_button.active = False
			Self.w_nat_flag[p -1] = flag_button
			Self.widgets.AddLast(flag_button)
			
			''nationality code
			w = New t_button
			w.set_geometry(363, 126 +19*(p -1), 54, 17)
			w.set_text("", 0, 10)
			w.active = False
			Self.w_nat_code[p -1] = w
			Self.widgets.AddLast(w)
			
			''type
			w = New t_button
			w.set_geometry(0, 126 +19*(p -1), 30, 17)
			w.set_text("", 0, 10)
			w.active = False
			Self.w_type[p -1] = w
			Self.widgets.AddLast(w)
			
			''skills
			For Local j:Int = 1 To 3
				w = New t_button
				w.set_geometry(0, 126 +19*(p -1), 12, 17)
				w.set_text("", 0, 10)
				w.active = False
				w.img_ucode = Self.img_ucode10y
				Self.w_skill[p -1, j -1] = w
				Self.widgets.AddLast(w)
			Next
			
			''stars
			w = New t_picture
			w.set_geometry(0, 126 + 19 * (p -1), 64, 16)
			w.set_frame(64, 16, 0, 0)
			w.image = Self.img_stars
			Self.w_stars[p -1] = w
			Self.widgets.AddLast(w)
			
			''input device type
			w = New t_player_input_device_button
			w.set_geometry(0, 126 + 19 * (p -1), 52, 17)
			w.set_image(Self.img_controls)
			w.bind("fire1_down", "bc_select_player_input_device", [String(p -1)])
			Self.w_player_input_devices[p -1] = w
			Self.widgets.AddLast(w)
			
		Next
		
		Self.update_player_buttons()
	
		''tactics
		For Local tc:Int = 0 To 17
			w = New t_button
			w.set_geometry(1024 -30 -90, 126 +20*tc, 90, 18)
			w.set_text(tactics_name[tc], 0, 10)
			w.bind("fire1_down", "bc_tactics", [String(tc)])
			Self.w_tactics[tc] = w
			Self.widgets.AddLast(w)
		Next
		Self.update_tactics_buttons()
	
		''team tactics / compare tactics
		w = New t_button
		w.set_geometry(512 +115, 450, 264, 34)
		w.set_colors($824200, $B46A00, $4C2600)
		w.set_text("", 0, 10)
		w.bind("fire1_down", "bc_compare_tactics")
		Self.w_compare_tactics = w
		Self.update_compare_tactics_button()
		Self.widgets.AddLast(w)
	
		''opponent team
		w = New t_button
		w.set_geometry(512 +115, 500, 175, 34)
		w.set_colors($8B2323, $BF4531, $571717)
		w.set_text(dictionary.gettext("OPPONENT TEAM"), 0, 10)
		w.bind("fire1_down", "bc_view_opponent")
		Self.w_opponent_team = w
		Self.widgets.AddLast(w)
	
		''edit tactics
		w = New t_button
		w.set_geometry(1024 -30 -175, 500, 175, 34)
		w.set_colors($BA9206, $E9B607, $6A5304)
		w.set_text(dictionary.gettext("EDIT TACTICS"), 0, 10)
		w.bind("fire1_down", "bc_edit_tactics")
		Self.w_edit_tactics = w
		Self.widgets.AddLast(w)
	
		''control mode (player/coach)
		w = New t_button
		w.set_geometry(512 +115, 562, 150, 40)
		w.set_text("", 0, 10)
		w.bind("fire1_down", "bc_control_mode")
		Self.w_control_mode = w
		update_control_mode_button()
		Self.widgets.AddLast(w)
	
		''input device
		w = New t_team_input_device_button
		w.set_geometry(512 +115, 618, 212, 40)
		w.set_image(Self.img_controls)
		w.bind("fire1_down", "bc_input_device", ["+1"])
		w.bind("fire2_down", "bc_input_device", ["-1"])

		Self.w_team_input_device = w
		Self.update_team_input_device_button()
		Self.widgets.AddLast(w)
	
		''play match
		w = New t_button
		w.set_geometry(512 +115, 768 -0.5*44 -60, 200, 44)
		w.set_colors($DC0000, $FF4141, $8C0000)
		w.set_text(dictionary.gettext("PLAY MATCH"), 0, 14)
		Self.w_play_match = w
		w.bind("fire1_down", "bc_play_match")
		Self.widgets.AddLast(w)
	
		Self.selected_widget = w

		''exit
		w = New t_button
		w.set_geometry(1024 -145 -30, 768 -0.5*40 -60, 145, 40)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		Self.w_exit = w
		w.bind("fire1_down", "bc_exit")
		Self.widgets.AddLast(w)
	
		''board
		Self.board:t_board = New t_board
		Self.board.set_position(0.5*1024 +115, 126)
		Self.board.set_teams(Self.own_team, Self.opponent_team)
		Self.widgets.AddLast(Self.board)
		
		''team name
		w = New t_button
		w.set_geometry(0.5*1024 -300, 45, 601, 41)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text("", 0, 14)
		w.active = False
		Self.w_team_name = w
		Self.update_team_name_button()
		Self.widgets.AddLast(w)
	
		''club logo / national flag
		w = New t_picture
		Self.w_clnf = w
		Self.update_clnf_picture()
		Self.widgets.AddLast(w)
		
		''club national / national confederation flag
		w = New t_button
		w.set_position(1024 -45 -62, 45)
		w.set_colors($000000, $606060, $606060)
		w.active = False
		Self.w_cnnc = w
		Self.update_cnnc_button()
		Self.widgets.AddLast(w)
		
		''coach name			
		w = New t_label
		w.set_position(512 +115 +150 +10, 562 +20)
		Self.w_coach_name = w
		Self.update_coach_name_label()
		Self.widgets.AddLast(w)
		
	End Method

	Method bc_select_player(n:Int)
		''select
		If (Self.selected_ply = 0)
			Self.selected_ply = n
		''deselect
		Else If (Self.selected_ply = n)
			Self.selected_ply = 0
		''swap
		Else 
			Local base_tactics:Int = tactics_array[Self.current_team.tactics].based_on
			Local ply1:Int = tactics_order[base_tactics, Self.selected_ply -1]
			Local ply2:Int = tactics_order[base_tactics, n -1]
			swap_elements(Self.current_team.players, ply1, ply2)
			Self.selected_ply = 0
		EndIf
		Self.update_player_buttons()
	End Method
	
	Method bc_select_player_input_device(pos:Int)
		Local player:t_player = t_player_input_device_button(Self.w_player_input_devices[pos]).player
		Select player.input_device.typ
			Case t_input.ID_COMPUTER
				''move from team to player
				If (Self.own_team.input_device <> Null)
					player.set_input_device(Self.own_team.input_device)
					Self.own_team.set_input_device(Null)
				''get first available
				ElseIf (input_devices.get_availability_count() > Self.reserved_input_devices)
					player.set_input_device(input_devices.assign_first_available())
				EndIf
			Default
				Local d:t_input = input_devices.assign_next_available(player.input_device)
				If (d <> Null)
					player.set_input_device(d)
				Else
					''back to ai
					player.input_device.set_is_available(True)
					player.set_input_device(player.ai)
					If (Self.own_team.non_ai_input_devices_count() = 0)
						Self.own_team.set_input_device(input_devices.assign_first_available())
					EndIf
				EndIf
		End Select
		Self.update_player_buttons()
		Self.update_team_input_device_button()
	End Method
	
	Method bc_tactics(n:Int)
		Self.current_team.tactics = n
		Self.update_tactics_buttons()
		Self.update_player_buttons()
	End Method
	
	Method bc_compare_tactics()
		Self.compare_tactics = Not Self.compare_tactics 
		Self.board.set_compare_tactics(Self.compare_tactics)
		Self.update_compare_tactics_button()
	End Method
	
	Method bc_view_opponent()
		If (Self.current_team = Self.own_team)
			Self.current_team = Self.opponent_team
		Else
			Self.current_team = Self.own_team
		EndIf
		Local view_opponent:Int = (Self.current_team = Self.opponent_team)
		Self.board.set_view_opponent(view_opponent)
		Self.update_player_buttons()
		Self.update_tactics_buttons()
		Self.update_control_mode_button()
		Self.update_team_input_device_button()
		Self.update_team_name_button()
		Self.update_clnf_picture()
		Self.update_cnnc_button()
		Self.update_coach_name_label()
		
		Self.w_compare_tactics.visible = Not view_opponent
		Self.w_opponent_team.visible = Not view_opponent
		Self.w_edit_tactics.visible = Not view_opponent
		Self.w_play_match.visible = Not view_opponent
		Self.w_exit.visible = Not view_opponent
	End Method
	
	Method bc_edit_tactics()
		menu.team_to_show = Self.own_team
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TACTICS_TO_EDIT)
	End Method
	
	Method bc_control_mode()
		Self.own_team.control = rotate(Self.own_team.control, CM_PLAYER, CM_COACH, 1)
		Select Self.own_team.control
			Case CM_PLAYER
				''nothing to do
			Case CM_COACH
				If (Self.own_team.input_device = Null)
					Self.own_team.release_non_ai_input_devices()
					Self.own_team.set_input_device(input_devices.assign_first_available())
				EndIf
		End Select
		Self.update_control_mode_button()
		Self.update_player_buttons()
	End Method
	
	Method bc_input_device(n:Int)
		Self.own_team.input_device = input_devices.rotate_available(Self.own_team.input_device, n)
		Self.update_team_input_device_button()
	End Method

	Method bc_play_match()	
		If (menu.team_to_set = HOME) And (Self.opponent_team.control <> CM_COMPUTER)
			menu.team_to_set = AWAY
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SET_TEAM)
		Else
			game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MATCH_PRESENTATION)
		EndIf
	End Method
	
	Method bc_exit()
 		Select menu.status
			Case MS_FRIENDLY
				game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_SELECT_TEAMS)
			Case MS_COMPETITION
				Select competition.typ
					Case CT_LEAGUE
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_LEAGUE)
					Case CT_CUP
						game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_PLAY_CUP)
				End Select							
		End Select
	End Method
	
	Method update_player_buttons()
		For Local p:Int = 1 To FULL_TEAM
			Local player:t_player = Self.current_team.player_at_position(p -1)
			
			''face
			Self.set_player_widget_color(Self.w_face[p -1], p)
			t_face_button(Self.w_face[p -1]).player = player
		
			''number
			If (player)
				Self.w_number[p -1].text = player.number
			Else
				Self.w_number[p -1].text = ""
			EndIf
		
			''name		
			If (player)
				Local ns:String = player.name
				If (Len(player.surname) > 0) And (Len(player.name) > 0)
					ns = ns + " "
				EndIf
				Self.w_name[p -1].set_text(ns + player.surname, 1)
			Else
				Self.w_name[p -1].set_text("")
			EndIf
			Self.w_name[p -1].active = (Self.current_team = Self.own_team) And (p <= Self.current_team.players.Count())
			Self.set_player_widget_color(Self.w_name[p -1], p)
			
			''nationality
			Local x:Int = 363
			If (Self.current_team.national = False)
				If (game_settings.use_flags)
					t_flag_button(Self.w_nat_flag[p -1]).player = player
					x = 391
				Else
					If (player)
						Self.w_nat_code[p -1].set_text("(" + player.nationality + ")")
					Else
						Self.w_nat_code[p -1].set_text("")
					EndIf
					x = 417
				EndIf
			EndIf
			
			''type
			Self.w_type[p -1].x = x
			If (player)
				Self.w_type[p -1].set_text(player_roles[player.role])
			Else
				Self.w_type[p -1].set_text("")
			EndIf
			x = x +31
			
			''skills
			For Local j:Int = 1 To 3
				Self.w_skill[p -1, j -1].x = x
				If (player)
					Self.w_skill[p -1, j -1].set_text(player.get_best_skill(j))
				Else
					Self.w_skill[p -1, j -1].set_text("")
				EndIf
				x = x +12
			Next
			x = x +4
			
			''stars
			Self.w_stars[p -1].x = x
			If (player)
				Self.w_stars[p -1].frame_y = price[player.price].stars
				Self.w_stars[p -1].visible = True
			Else
				Self.w_stars[p -1].visible = False
			EndIf
			x = x +64
			
			''input devices
			Self.w_player_input_devices[p -1].x = x
			Self.w_player_input_devices[p -1].visible = (p <= TEAM_SIZE +game_settings.bench_size)
			Self.w_player_input_devices[p -1].active = (Self.current_team = Self.own_team) And (Self.own_team.control = CM_PLAYER)
			t_player_input_device_button(Self.w_player_input_devices[p -1]).player = player
			
		Next
	End Method
	
	Method update_tactics_buttons()
		For Local tc:Int = 0 To 17
			If (Self.current_team.tactics = tc)
				Self.w_tactics[tc].set_colors($9D7B03, $E2B004, $675103)
			Else
				Self.w_tactics[tc].set_colors($E2B004, $FCCE30, $9D7B03)
			EndIf
			Self.w_tactics[tc].active = (Self.current_team = Self.own_team)
		Next
	End Method
	
	Method update_control_mode_button()
		Select Self.current_team.control
			Case CM_COMPUTER
				Self.w_control_mode.text = dictionary.gettext("COMPUTER") + ":"
				Self.w_control_mode.set_colors($981E1E, $C72929, $640000)
			Case CM_PLAYER
				Self.w_control_mode.text = dictionary.gettext("PLAYER") + ":"
				Self.w_control_mode.set_colors($0000C8, $1919FF, $000078)
			Case CM_COACH
				Self.w_control_mode.text = dictionary.gettext("COACH") + ":"
				Self.w_control_mode.set_colors($009BDC, $19BBFF, $0071A0)
		End Select
		Self.w_control_mode.active = (Self.current_team = Self.own_team)
	End Method
	
	Method update_team_input_device_button()
		t_team_input_device_button(Self.w_team_input_device).set_team(Self.current_team)
	End Method
	
	Method update_compare_tactics_button()
		If (Self.compare_tactics = True)
			Self.w_compare_tactics.text = dictionary.gettext("TACTICS COMPARISON")
		Else
			Self.w_compare_tactics.text = dictionary.gettext("TEAM TACTICS")
		EndIf
	End Method
	
	Method update_team_name_button()
		Self.w_team_name.set_text(Self.current_team.name)
		If (Self.current_team = Self.own_team)
			Self.w_team_name.set_colors($6A5ACD, $8F83D7, $372989)
		Else
			Self.w_team_name.set_colors($C14531, $DF897B, $8E3324)
		EndIf
	End Method
	
	Method update_clnf_picture()
		Self.w_clnf.image = Self.current_team.clnf
		Local lgh:Int = ImageHeight(Self.current_team.clnf)
		Local lgw:Int = ImageWidth(Self.current_team.clnf)
		Local lgx:Int = 35
		Local lgy:Int = 65 -0.5 * lgh
		Self.w_clnf.set_geometry(lgx, lgy, lgw, lgh)
	End Method
	
	Method update_cnnc_button()
		If (Self.current_team = Self.own_team)
			Local lgw:Int = ImageWidth(Self.img_hflag)
			Local lgh:Int = ImageHeight(Self.img_hflag)
			Self.w_cnnc.set_size(lgw +4, lgh +4) 
			Self.w_cnnc.image = Self.img_hflag
			Self.w_cnnc.set_frame(lgw, lgh, 0, 0)
		EndIf
		If (Self.current_team = Self.opponent_team)
			Local lgw:Int = ImageWidth(Self.img_aflag)
			Local lgh:Int = ImageHeight(Self.img_aflag)
			Self.w_cnnc.set_size(lgw +4, lgh +4) 
			Self.w_cnnc.image = Self.img_aflag
			Self.w_cnnc.set_frame(lgw, lgh, 0, 0)
		EndIf
	End Method
	
	Method update_coach_name_label()
		Self.w_coach_name.set_text(Self.current_team.coach_name, 1, 10)
	EndMethod
	
	Method set_player_widget_color(b:t_widget, p:Int)
		If (Self.current_team = Self.own_team)
			''goalkeeper		
			If (p = 1)
				b.set_colors($00A7DE, $33CCFF, $005F7E)
			''other player
			Else If (p <= TEAM_SIZE)
				b.set_colors($003FDE, $255EFF, $00247E)
			''bench
			Else If (p <= TEAM_SIZE +game_settings.bench_size)
				b.set_colors($111188, $2D2DB3, $001140)
			''reserve
			Else If (p <= Self.current_team.players.Count())
				b.set_colors($404040, $606060, $202020)
			''void
			Else 			
				b.set_colors($202020, $404040, $101010)
			EndIf
			
			''selected
			If (selected_ply = p)
				b.set_colors($993333, $C24242, $5A1E1E)
			EndIf
			
		''opponent
		Else
			''goalkeeper		
			If (p = 1)
				b.set_colors($E60000, $FF4141, $B40000)
			''other player
			Else If (p <= TEAM_SIZE)
				b.set_colors($B40000, $E60000, $780000)
			''bench
			Else If (p <= TEAM_SIZE + game_settings.bench_size)
				b.set_colors($780000, $B40000, $3C0000)
			''reserve
			Else If (p <= Self.current_team.players.Count())
				b.set_colors($404040, $606060, $202020)
			''void
			Else
				b.set_colors($202020, $404040, $101010)
			EndIf
		EndIf
	End Method
	
End Type

