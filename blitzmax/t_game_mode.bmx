SuperStrict

Import "t_backgrounds.bmx"
Import "lib_data.bmx"
Import "data_prices.bmx"
Import "data_weather_cap.bmx"
Import "t_label.bmx"
Import "t_input_button.bmx"
Import "t_face_button.bmx"
Import "t_flag_button.bmx"
Import "t_picture.bmx"
Import "t_fader.bmx"
Import "t_board.bmx"
Import "t_game_action_queue.bmx"
Import "t_menu_music.bmx"
Import "t_crowd_list.bmx"
Import "t_associations_files.bmx"


''--- --- GLOBALS --- ---''

Global memline:Int, memqty:Int

Global backgrounds:t_backgrounds

''graphics modes
Global graphics_mode_index:Int
Global graphics_modes:TList

'menu music volume & channel
Global music_volume:Float
Global music_mute:Int
Global menu_music:t_menu_music

'menu input
Global menu_input:t_menu_input

'league
Global league:t_league

'cup
Global cup:t_cup

'images
Global img_kit:TImage[2]		'home/away kits	(rendered)
Global img_kit_raw:TImage[2]	'same as above	(raw)
Global img_kit_shad:TImage[2]	'same as above	(shadow)

'vector of match lengths
Global match_length:Int[4]

'months' names
Global month_name:String[12]

Global team_list_target:Int

'temp player for copy&paste
Global ply_temp:t_player


''--- --- TYPES --- ---''
Type t_game_mode
	Field img_background:TImage
	Field selected_widget:t_widget
	Field widgets:TList
	Field event_name:String
	Field type_id:TTypeId
	
	Method New()
		music_mute = False
		Self.selected_widget = Null
		Self.widgets = New TList
		Self.reset_menu_input()
	End Method
	
	Method update()
			
		If (music_volume > game_settings.music_vol * (Not music_mute))
			music_volume = music_volume -5
		EndIf
		If (music_volume < game_settings.music_vol * (Not music_mute))
			music_volume = music_volume +2
		EndIf
		menu_music.update()
		menu_music.set_volume(0.01*music_volume)
		
		If (game_settings.mouse_enabled)

			read_mouse()

			If Not(Self.selected_widget And Self.selected_widget.entry_mode = True)
				For Local w:t_widget = EachIn Self.widgets
					If w.mouse_over() And w.visible And w.active And mouse.moved
						Self.selected_widget = w
					EndIf
				Next
			EndIf

		EndIf
		
		For Local d:t_input = EachIn input_devices
			d.read_input()
		Next
		
		Self.selected_widget = Self.read_menu_input()
		
		For Local w:t_widget = EachIn Self.widgets
			w.update()
			If (w.get_changed())
				For Local binding:t_binding = EachIn w.bindings
					If binding.event_name = "on_update"
						Local m:TMethod = Self.type_id.FindMethod(binding.method_name)
						If (m)
							m.Invoke(Self, binding.method_arguments)
						Else
							RuntimeError("Method " + binding.method_name + " is not defined!")
						EndIf
					EndIf
				Next
				w.set_changed(False)
			EndIf
		Next
		
		If (game_settings.mouse_enabled And Self.selected_widget)
			If (Not Self.selected_widget.mouse_over() And mouse.moved)
				Return
			EndIf
		EndIf
		
		Self.event_name = ""
		If (Not menu_input.fa0) And (menu_input.fa)
			Self.event_name = "fire1_down"
		EndIf
		If (menu_input.fa And menu_input.fa0 And menu_input.tfa = 0)
			Self.event_name = "fire1_hold"
		EndIf
		If (menu_input.fa0) And (Not menu_input.fa)
			Self.event_name = "fire1_up"
		EndIf
			
		If (Not menu_input.fb0) And (menu_input.fb)
			Self.event_name = "fire2_down"
		EndIf
		If (menu_input.fb And menu_input.fb0 And menu_input.tfb = 0)
			Self.event_name = "fire2_hold"
		EndIf
		If (menu_input.fb0) And (Not menu_input.fb)
			Self.event_name = "fire2_up"
		EndIf
		
		If (Self.selected_widget And (Self.event_name <> ""))
			For Local binding:t_binding = EachIn Self.selected_widget.bindings
				If binding.event_name = Self.event_name
					Local m:TMethod = Self.type_id.FindMethod(binding.method_name)
					If (m)
						m.Invoke(Self, binding.method_arguments)
					Else
						RuntimeError("Method " + binding.method_name + " is not defined!")
					EndIf
				EndIf
			Next
		EndIf
		
	End Method
	
	Method read_menu_input:t_widget()
		
		For Local w:t_widget = EachIn Self.widgets
			w.selected = False
		Next
		
		'fire a delay
		If menu_input.fa = 1
			If menu_input.fa0 = 0
				menu_input.tfa = 10
			Else If menu_input.tfa = 0
				menu_input.tfa = 4
			EndIf
		Else
			If menu_input.fa0 = 0
				menu_input.tfa = 0
			EndIf
		EndIf

		If menu_input.tfa > 0 Then menu_input.tfa = menu_input.tfa - 1

		'fire b delay
		If menu_input.fb = 1
			If menu_input.fb0 = 0
				menu_input.tfb = 10
			Else If menu_input.tfb = 0
				menu_input.tfb = 4
			EndIf
		Else
			If menu_input.fb0 = 0
				menu_input.tfb = 0
			EndIf
		EndIf

		If menu_input.tfb > 0 Then menu_input.tfb = menu_input.tfb - 1

		'old values
		menu_input.x0 = menu_input.x
		menu_input.y0 = menu_input.y
		menu_input.fa0 = menu_input.fa
		menu_input.fb0 = menu_input.fb

		menu_input.x = 0
		menu_input.y = 0
		menu_input.fa = 0
		menu_input.fb = 0
		For Local i:t_input = EachIn input_devices
			''x movement
			Local x:Int = i.get_x()
			If (x <> 0)
				menu_input.x = x
			EndIf
			
			''y movement
			Local y:Int = i.get_y()
			If (y <> 0)
				menu_input.y = y
			EndIf
			
			''fire 1
			Local f1:Int = i.get_fire1()
			If (f1 <> 0)
				menu_input.fa = 1
			EndIf
			
			''fire 2
			Local f2:Int = i.get_fire2()
			If (f2 <> 0)
				menu_input.fb = 1
			EndIf
		Next
		
		If (game_settings.mouse_enabled)
			If (mouse.button1 = 1)
				menu_input.fa = 1
			EndIf
			If (mouse.button2 = 1)
				menu_input.fb = 1
			EndIf
		EndIf
		
		''entry mode
		If (Self.selected_widget <> Null)
			If (Self.selected_widget.entry_mode = True)
				Self.selected_widget.selected = True
				Return Self.selected_widget
			EndIf
		EndIf

		Local bias:Int = 7

		'up/down
		If (menu_input.y = -1 And menu_input.ty = 0)
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If ((w.y + w.h) <= button.y)
								distance = hypo(bias * ((w.x +0.5*w.w) -(button.x +0.5*button.w)), (w.y +0.5*w.h) -(button.y +0.5*button.h))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		Else If (menu_input.y = 1 And menu_input.ty = 0)
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If (w.y => (button.y + button.h))
								distance = hypo(bias * ((w.x +0.5*w.w) -(button.x +0.5*button.w)), (w.y +0.5*w.h) -(button.y +0.5*button.h))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		EndIf
		
		'left/right
		If (menu_input.x = -1 And menu_input.tx = 0) 'And menu_input.f = 0
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If ((w.x + w.w) <= button.x)
								distance = hypo((w.x +0.5*w.w) -(button.x +0.5*button.w), bias * ((w.y +0.5*w.h) -(button.y +0.5*button.h)))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		Else If (menu_input.x = 1 And menu_input.tx = 0) 'And menu_input.f = 0
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If (w.x => (button.x + button.w))
								distance = hypo((w.x +0.5*w.w) -(button.x +0.5*button.w), bias * ((w.y +0.5*w.h) -(button.y +0.5*button.h)))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		EndIf
	
		'x-y delays
		If menu_input.x <> 0
			If menu_input.x0 = 0
				menu_input.tx = 8
			Else If menu_input.tx = 0
				menu_input.tx = 2
			EndIf
		Else
			menu_input.tx = 0
		EndIf
		If menu_input.y <> 0
			If menu_input.y0 = 0
				menu_input.ty = 8
			Else If menu_input.ty = 0
				menu_input.ty = 2
			EndIf
		Else
			menu_input.ty = 0
		EndIf
	
		If menu_input.tx > 0 Then menu_input.tx = menu_input.tx - 1
		If menu_input.ty > 0 Then menu_input.ty = menu_input.ty - 1
		
		If (Self.selected_widget)
			Self.selected_widget.selected = True
		EndIf

		Return Self.selected_widget
	End Method

	Method bc_set_menu(menu_id:Int)
		game_action_queue.push(AT_NEW_FOREGROUND, menu_id)
	End Method

	Method bc_widget_edit()
		Local widget:t_widget = Self.selected_widget
		widget.set_entry_mode(True)
	End Method

	Method reset_menu_input()
		menu_input.x 	= 0
		menu_input.y 	= 0
	'	menu_input.fa 	= 0
	'	menu_input.fb 	= 0
	
		menu_input.x0 	= 0
		menu_input.y0 	= 0
	'	menu_input.fa0 	= 0
	'	menu_input.fb0 	= 1
	
		menu_input.tx 	= 0
		menu_input.ty 	= 0
		menu_input.tfa 	= 10
		menu_input.tfb 	= 10
	End Method
	
	Method render()
		SetColor(light, light, light)
		
		draw_image Self.img_background, 0, 0
		
		'draw buttons
		For Local w:t_widget = EachIn Self.widgets
			w.render()
		Next

		If (game_settings.mouse_enabled)
			draw_image img_arrow, mouse.x, mouse.y
		EndIf
	End Method
	
	Method on_resume()
	End Method
	
	Method on_pause()
	End Method
	
End Type


''--- --- INITIALIZATION --- ---''

'menu input
menu_input = New t_menu_input



''--- --- FUNCTIONS --- ---''

Function set_tactics_names()
	tactics_name[0] = "4-4-2"
	tactics_name[1] = "5-4-1"
	tactics_name[2] = "4-5-1"
	tactics_name[3] = "5-3-2"
	tactics_name[4] = "3-5-2"
	tactics_name[5] = "4-3-3"
	tactics_name[6] = "4-2-4"
	tactics_name[7] = "3-4-3"
	tactics_name[8] = dictionary.gettext("SWEEP")
	tactics_name[9] = "5-2-3"
	tactics_name[10] = dictionary.gettext("ATTACK")
	tactics_name[11] = dictionary.gettext("DEFEND")
	tactics_name[12] = dictionary.gettext("USER") + " 1"
	tactics_name[13] = dictionary.gettext("USER") + " 2"
	tactics_name[14] = dictionary.gettext("USER") + " 3"
	tactics_name[15] = dictionary.gettext("USER") + " 4"
	tactics_name[16] = dictionary.gettext("USER") + " 5"
	tactics_name[17] = dictionary.gettext("USER") + " 6"
End Function


Function set_player_types()
	''// NOTE: max 2 characters
	player_roles[0] = first_word(dictionary.gettext("G // code for: GOALKEEPER"))

	''// NOTE: max 2 characters
	player_roles[1] = first_word(dictionary.gettext("RB // code for: RIGHT BACK"))

	''// NOTE: max 2 characters
	player_roles[2] = first_word(dictionary.gettext("LB // code for: LEFT BACK"))

	''// NOTE: max 2 characters
	player_roles[3] = first_word(dictionary.gettext("D // code for: DEFENDER"))

	''// NOTE: max 2 characters
	player_roles[4] = first_word(dictionary.gettext("RW // code for: RIGHT WING"))

	''// NOTE: max 2 characters
	player_roles[5] = first_word(dictionary.gettext("LW // code for: LEFT WING"))

	''// NOTE: max 2 characters
	player_roles[6] = first_word(dictionary.gettext("M // code for: MIDFIELD"))

	''// NOTE: max 2 characters
	player_roles[7] = first_word(dictionary.gettext("A // code for: ATTACK"))
End Function


Function set_pitch_types()
	pitch_types[0] = dictionary.gettext("FROZEN")
	pitch_types[1] = dictionary.gettext("MUDDY")
	pitch_types[2] = dictionary.gettext("WET")
	pitch_types[3] = dictionary.gettext("SOFT")
	pitch_types[4] = dictionary.gettext("NORMAL")
	pitch_types[5] = dictionary.gettext("DRY")
	pitch_types[6] = dictionary.gettext("HARD")
	pitch_types[7] = dictionary.gettext("SNOWED")
	pitch_types[8] = dictionary.gettext("WHITE")
	pitch_types[9] = dictionary.gettext("RANDOM")
End Function


Function set_months_names()
	month_name[0] = dictionary.gettext("JANUARY")
	month_name[1] = dictionary.gettext("FEBRUARY")
	month_name[2] = dictionary.gettext("MARCH")
	month_name[3] = dictionary.gettext("APRIL")
	month_name[4] = dictionary.gettext("MAY")
	month_name[5] = dictionary.gettext("JUNE")
	month_name[6] = dictionary.gettext("JULY")
	month_name[7] = dictionary.gettext("AUGUST")
	month_name[8] = dictionary.gettext("SEPTEMBER")
	month_name[9] = dictionary.gettext("OCTOBER")
	month_name[10] = dictionary.gettext("NOVEMBER")
	month_name[11] = dictionary.gettext("DECEMBER")
End Function


'MEMORY
Function memory(s:String)
	Flip
	DrawText s, 10, 10+12*memline
	DrawText GCMemAlloced(), 300, 10+12*memline
	DrawText (GCMemAlloced()-memqty), 500, 10+12*memline
	memqty = GCMemAlloced()
	Flip
	memline:+1
End Function
