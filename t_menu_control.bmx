SuperStrict

Import "t_game_mode.bmx"

Global keynames:String[256]

Type t_menu_control Extends t_game_mode
	
	Method New()
		
		Self.load_keynames(game_settings.language)
		
		Self.type_id = TTypeId.ForObject(Self)
		
		''background
		Self.img_background = backgrounds.get("menu_control.jpg")
		
		''title
		Local w:t_widget
		w = New t_button
		w.set_geometry(512 -0.5*400, 30, 400, 40)
		w.set_colors($A905A3, $E808E0, $5A0259)
		w.set_text(dictionary.gettext("CONTROL"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''keyboard set
		w = New t_button
		w.set_geometry(512 -280 -20 -120, 210, 280, 36)
		w.set_colors($548854, $70A870, $263E26)
		w.set_text(dictionary.gettext("KEYS"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_geometry(512 -0.5*220, 210, 220, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text("1", 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		w = New t_button
		w.set_geometry(512 +0.5*220 +20, 210, 220, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text("2", 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''up
		w = New t_button
		w.set_geometry(512 -280 -20 -120, 280, 280, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("UP"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''down
		w = New t_button
		w.set_geometry(512 -280 -20 -120, 330, 280, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("DOWN"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''left
		w = New t_button
		w.set_geometry(512 -280 -20 -120, 380, 280, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("LEFT"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''right
		w = New t_button
		w.set_geometry(512 -280 -20 -120, 430, 280, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("RIGHT"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''first button
		w = New t_button
		w.set_geometry(512 -280 -20 -120, 480, 280, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("BUTTON A"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''second button
		w = New t_button
		w.set_geometry(512 -280 -20 -120, 530, 280, 36)
		w.set_colors($800000, $B40000, $400000)
		w.set_text(dictionary.gettext("BUTTON B"), 0, 14)
		w.active = False
		Self.widgets.AddLast(w)
		
		''first set
		''up
		Local key_button:t_key_button
		key_button = New t_key_button
		key_button.set_geometry(512 -0.5*220, 280, 220, 36)
		key_button.set_colors($3C3C78, $5858AC, $202040)
		key_button.set_text(keynames[game_settings.key[0].key_up], 0, 14)
		key_button.set_key(0, "key_up")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		Self.selected_widget = key_button
		
		''down
		key_button = New t_key_button
		key_button.set_geometry(512 -0.5*220, 330, 220, 36)
		key_button.set_colors($1F1F95, $3030D4, $151563)
		key_button.set_text(keynames[game_settings.key[0].key_down], 0, 14)
		key_button.set_key(0, "key_down")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''left
		key_button = New t_key_button
		key_button.set_geometry(512 -0.5*220, 380, 220, 36)
		key_button.set_colors($3C3C78, $5858AC, $202040)
		key_button.set_text(keynames[game_settings.key[0].key_left], 0, 14)
		key_button.set_key(0, "key_left")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''right
		key_button = New t_key_button
		key_button.set_geometry(512 -0.5*220, 430, 220, 36)
		key_button.set_colors($1F1F95, $3030D4, $151563)
		key_button.set_text(keynames[game_settings.key[0].key_right], 0, 14)
		key_button.set_key(0, "key_right")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''button 1
		key_button = New t_key_button
		key_button.set_geometry(512 -0.5*220, 480, 220, 36)
		key_button.set_colors($3C3C78, $5858AC, $202040)
		key_button.set_text(keynames[game_settings.key[0].button_1], 0, 14)
		key_button.set_key(0, "button_1")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''button 2
		key_button = New t_key_button
		key_button.set_geometry(512 -0.5*220, 530, 220, 36)
		key_button.set_colors($1F1F95, $3030D4, $151563)
		key_button.set_text(keynames[game_settings.key[0].button_2], 0, 14)
		key_button.set_key(0, "button_2")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''second set
		''up
		key_button = New t_key_button
		key_button.set_geometry(512 +0.5*220 +20, 280, 220, 36)
		key_button.set_colors($3C3C78, $5858AC, $202040)
		key_button.set_text(keynames[game_settings.key[1].key_up], 0, 14)
		key_button.set_key(1, "key_up")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''down
		key_button = New t_key_button
		key_button.set_geometry(512 +0.5*220 +20, 330, 220, 36)
		key_button.set_colors($1F1F95, $3030D4, $151563)
		key_button.set_text(keynames[game_settings.key[1].key_down], 0, 14)
		key_button.set_key(1, "key_down")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''left
		key_button = New t_key_button
		key_button.set_geometry(512 +0.5*220 +20, 380, 220, 36)
		key_button.set_colors($3C3C78, $5858AC, $202040)
		key_button.set_text(keynames[game_settings.key[1].key_left], 0, 14)
		key_button.set_key(1, "key_left")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''right
		key_button = New t_key_button
		key_button.set_geometry(512 +0.5*220 +20, 430, 220, 36)
		key_button.set_colors($1F1F95, $3030D4, $151563)
		key_button.set_text(keynames[game_settings.key[1].key_right], 0, 14)
		key_button.set_key(1, "key_right")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''button 1
		key_button = New t_key_button
		key_button.set_geometry(512 +0.5*220 +20, 480, 220, 36)
		key_button.set_colors($3C3C78, $5858AC, $202040)
		key_button.set_text(keynames[game_settings.key[1].button_1], 0, 14)
		key_button.set_key(1, "button_1")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''button 2
		key_button = New t_key_button
		key_button.set_geometry(512 +0.5*220 +20, 530, 220, 36)
		key_button.set_colors($1F1F95, $3030D4, $151563)
		key_button.set_text(keynames[game_settings.key[1].button_2], 0, 14)
		key_button.set_key(1, "button_2")
		key_button.bind("fire1_down", "bc_key_button")
		Self.widgets.AddLast(key_button)
		
		''exit
		w = New t_button
		w.set_geometry(512 -0.5*180, 708, 180, 36)
		w.set_colors($C84200, $FF6519, $803300)
		w.set_text(dictionary.gettext("EXIT"), 0, 14)
		w.bind("fire1_down", "bc_set_menu", [String(GM.MENU_MAIN)])
		Self.widgets.AddLast(w)
		
	End Method
	
	Method bc_key_button()
		Local key_button:t_key_button = t_key_button(Self.selected_widget)
		key_button.set_entry_mode(Not key_button.entry_mode)
	End Method
	
	Method load_keynames(language:Int)
		keynames[KEY_BACKSPACE]    = dictionary.gettext("KEY_BACKSPACE")
		keynames[KEY_TAB]          = dictionary.gettext("KEY_TAB")
		keynames[KEY_CLEAR]        = dictionary.gettext("KEY_CLEAR")
		keynames[KEY_RETURN]       = dictionary.gettext("KEY_RETURN")
		keynames[KEY_PAGEUP]       = dictionary.gettext("KEY_PAGEUP")
		keynames[KEY_PAGEDOWN]     = dictionary.gettext("KEY_PAGEDOWN")
		keynames[KEY_END]          = dictionary.gettext("KEY_END")
		keynames[KEY_HOME]         = dictionary.gettext("KEY_HOME")
		keynames[KEY_LEFT]         = dictionary.gettext("KEY_LEFT")
		keynames[KEY_UP]           = dictionary.gettext("KEY_UP")
		keynames[KEY_RIGHT]        = dictionary.gettext("KEY_RIGHT")
		keynames[KEY_DOWN]         = dictionary.gettext("KEY_DOWN")
		keynames[KEY_INSERT]       = dictionary.gettext("KEY_INSERT")
		keynames[KEY_DELETE]       = dictionary.gettext("KEY_DELETE")
		keynames[KEY_0]            = dictionary.gettext("KEY_0")
		keynames[KEY_1]            = dictionary.gettext("KEY_1")
		keynames[KEY_2]            = dictionary.gettext("KEY_2")
		keynames[KEY_3]            = dictionary.gettext("KEY_3")
		keynames[KEY_4]            = dictionary.gettext("KEY_4")
		keynames[KEY_5]            = dictionary.gettext("KEY_5")
		keynames[KEY_6]            = dictionary.gettext("KEY_6")
		keynames[KEY_7]            = dictionary.gettext("KEY_7")
		keynames[KEY_8]            = dictionary.gettext("KEY_8")
		keynames[KEY_9]            = dictionary.gettext("KEY_9")
		keynames[KEY_A]            = dictionary.gettext("KEY_A")
		keynames[KEY_B]            = dictionary.gettext("KEY_B")
		keynames[KEY_C]            = dictionary.gettext("KEY_C")
		keynames[KEY_D]            = dictionary.gettext("KEY_D")
		keynames[KEY_E]            = dictionary.gettext("KEY_E")
		keynames[KEY_F]            = dictionary.gettext("KEY_F")
		keynames[KEY_G]            = dictionary.gettext("KEY_G")
		keynames[KEY_H]            = dictionary.gettext("KEY_H")
		keynames[KEY_I]            = dictionary.gettext("KEY_I")
		keynames[KEY_J]            = dictionary.gettext("KEY_J")
		keynames[KEY_K]            = dictionary.gettext("KEY_K")
		keynames[KEY_L]            = dictionary.gettext("KEY_L")
		keynames[KEY_M]            = dictionary.gettext("KEY_M")
		keynames[KEY_N]            = dictionary.gettext("KEY_N")
		keynames[KEY_O]            = dictionary.gettext("KEY_O")
		keynames[KEY_P]            = dictionary.gettext("KEY_P")
		keynames[KEY_Q]            = dictionary.gettext("KEY_Q")
		keynames[KEY_R]            = dictionary.gettext("KEY_R")
		keynames[KEY_S]            = dictionary.gettext("KEY_S")
		keynames[KEY_T]            = dictionary.gettext("KEY_T")
		keynames[KEY_U]            = dictionary.gettext("KEY_U")
		keynames[KEY_V]            = dictionary.gettext("KEY_V")
		keynames[KEY_W]            = dictionary.gettext("KEY_W")
		keynames[KEY_X]            = dictionary.gettext("KEY_X")
		keynames[KEY_Y]            = dictionary.gettext("KEY_Y")
		keynames[KEY_Z]            = dictionary.gettext("KEY_Z")
		keynames[KEY_NUM0]         = dictionary.gettext("KEY_NUM0")
		keynames[KEY_NUM1]         = dictionary.gettext("KEY_NUM1")
		keynames[KEY_NUM2]         = dictionary.gettext("KEY_NUM2")
		keynames[KEY_NUM3]         = dictionary.gettext("KEY_NUM3")
		keynames[KEY_NUM4]         = dictionary.gettext("KEY_NUM4")
		keynames[KEY_NUM5]         = dictionary.gettext("KEY_NUM5")
		keynames[KEY_NUM6]         = dictionary.gettext("KEY_NUM6")
		keynames[KEY_NUM7]         = dictionary.gettext("KEY_NUM7")
		keynames[KEY_NUM8]         = dictionary.gettext("KEY_NUM8")
		keynames[KEY_NUM9]         = dictionary.gettext("KEY_NUM9")
		keynames[KEY_LSHIFT]       = dictionary.gettext("KEY_LSHIFT")
		keynames[KEY_RSHIFT]       = dictionary.gettext("KEY_RSHIFT")
		keynames[KEY_LCONTROL]     = dictionary.gettext("KEY_LCONTROL")
		keynames[KEY_RCONTROL]     = dictionary.gettext("KEY_RCONTROL")
		keynames[KEY_LALT]         = dictionary.gettext("KEY_LALT")
		keynames[KEY_RALT]         = dictionary.gettext("KEY_RALT")
		keynames[KEY_SEMICOLON]    = dictionary.gettext("KEY_SEMICOLON")
		keynames[KEY_EQUALS]       = dictionary.gettext("KEY_EQUALS")
		keynames[KEY_COMMA]        = dictionary.gettext("KEY_COMMA")
		keynames[KEY_MINUS]        = dictionary.gettext("KEY_MINUS")
		keynames[KEY_PERIOD]       = dictionary.gettext("KEY_PERIOD")
		keynames[KEY_SLASH]        = dictionary.gettext("KEY_SLASH")
		keynames[KEY_TILDE]        = dictionary.gettext("KEY_TILDE")
		keynames[KEY_OPENBRACKET]  = dictionary.gettext("KEY_OPENBRACKET")
		keynames[KEY_CLOSEBRACKET] = dictionary.gettext("KEY_CLOSEBRACKET")
		keynames[KEY_QUOTES]       = dictionary.gettext("KEY_QUOTES")
		keynames[KEY_BACKSLASH]    = dictionary.gettext("KEY_BACKSLASH")
	End Method
	
End Type

Type t_key_button Extends t_button
	Field entry_mode:Int
	Field key_set:Int
	Field key_name:String
	
	Method New()
		Self.active     = True
		Self.entry_mode = False
	End Method
	
	Method set_entry_mode(n:Int)
		Self.entry_mode = n
		Select Self.key_name
			
			Case "key_up"
				Self.text = keynames[game_settings.key[Self.key_set].key_up]
				
			Case "key_down"
				Self.text = keynames[game_settings.key[Self.key_set].key_down]
				
			Case "key_left"
				Self.text = keynames[game_settings.key[Self.key_set].key_left]
				
			Case "key_right"
				Self.text = keynames[game_settings.key[Self.key_set].key_right]
				
			Case "button_1"
				Self.text = keynames[game_settings.key[Self.key_set].button_1]
				
			Case "button_2"
				Self.text = keynames[game_settings.key[Self.key_set].button_2]
				
		End Select
	End Method
	
	Method set_key(_key_set:Int, _key_name:String)
		Self.key_set  = _key_set
		Self.key_name = _key_name
	End Method
	
	Method update()
		If Self.selected = False
			Self.set_entry_mode(False)
		EndIf
		If Self.entry_mode = False
			Return
		EndIf
		
		''for each key
		For Local i:Int = 1 To 255
			''if pressed
			If (KeyDown(i) = 1)
				
				''if esc go back to normal mode
				If (i = KEY_ESCAPE)
					Self.set_entry_mode(False)
					Return
				EndIf
				
				If (keynames[i] <> "")
					
					Local isfree:Int = Self.key_isfree(i)
					
					Select Self.key_name
						
						Case "key_up"
							If (game_settings.key[Self.key_set].key_up = i Or isfree)
								game_settings.key[Self.key_set].key_up = i
								Self.set_entry_mode(False)
							EndIf
							
						Case "key_down"
							If (game_settings.key[Self.key_set].key_down = i Or isfree)
								game_settings.key[Self.key_set].key_down = i
								Self.set_entry_mode(False)
							EndIf
							
						Case "key_left"
							If (game_settings.key[Self.key_set].key_left = i Or isfree)
								game_settings.key[Self.key_set].key_left = i
								Self.set_entry_mode(False)
							EndIf
							
						Case "key_right"
							If (game_settings.key[Self.key_set].key_right = i Or isfree)
								game_settings.key[Self.key_set].key_right = i
								Self.set_entry_mode(False)
							EndIf
							
						Case "button_1"
							If (game_settings.key[Self.key_set].button_1 = i Or isfree)
								game_settings.key[Self.key_set].button_1 = i
								Self.set_entry_mode(False)
							EndIf
							
						Case "button_2"
							If (game_settings.key[Self.key_set].button_2 = i Or isfree)
								game_settings.key[Self.key_set].button_2 = i
								Self.set_entry_mode(False)
							EndIf
							
					End Select
				EndIf
			EndIf
		Next
	End Method
	
	Method get_text:String()
		Self.blinking = Self.entry_mode
		If (Self.entry_mode = True)
			Return "?"
		Else
			Return Self.text
		EndIf
	End Method
	
	Method key_isfree:Int(i:Int)
		If (game_settings.key[0].key_up    = i) Then Return False
		If (game_settings.key[0].key_down  = i) Then Return False
		If (game_settings.key[0].key_left  = i) Then Return False
		If (game_settings.key[0].key_right = i) Then Return False
		If (game_settings.key[0].button_1  = i) Then Return False
		If (game_settings.key[0].button_2  = i) Then Return False
		
		If (game_settings.key[1].key_up    = i) Then Return False
		If (game_settings.key[1].key_down  = i) Then Return False
		If (game_settings.key[1].key_left  = i) Then Return False
		If (game_settings.key[1].key_right = i) Then Return False
		If (game_settings.key[1].button_1  = i) Then Return False
		If (game_settings.key[1].button_2  = i) Then Return False
		Return True
	End Method
	
End Type
