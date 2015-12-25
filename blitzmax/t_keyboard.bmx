SuperStrict

Import "t_input.bmx"

Type t_keyboard Extends t_input
	
	Method New()
		Self.typ = Self.ID_KEYBOARD
	End Method
	
	Method _read()
		Self.x0		= Self.read_x()
		Self.y0		= Self.read_y()
		Self.fire10	= Self.read_fire1()
		Self.fire20	= Self.read_fire2()
	End Method
	
	Method read_x:Int()
		Local i:Int=0
		If (KeyDown(game_settings.key[Self.port].key_left))
			i = i - 1
		EndIf
		If (KeyDown(game_settings.key[Self.port].key_right))
			i = i + 1
		EndIf
		Return i
	End Method
	
	Method read_y:Int()
		Local i:Int=0
		If (KeyDown(game_settings.key[Self.port].key_up))
			i = i - 1
		EndIf
		If (KeyDown(game_settings.key[Self.port].key_down))
			i = i + 1
		EndIf
		Return i
	End Method
	
	Method read_fire1:Int()
		Return KeyDown(game_settings.key[Self.port].button_1)
	End Method
	
	Method read_fire2:Int()
		Return KeyDown(game_settings.key[Self.port].button_2)
	End Method
	
End Type
