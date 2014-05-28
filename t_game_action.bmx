SuperStrict

''action types
Const AT_NONE:Int               = 0
Const AT_NEW_FOREGROUND:Int     = 1
Const AT_FADE_IN:Int            = 2
Const AT_FADE_OUT:Int           = 3
Const AT_HOLD_FOREGROUND:Int    = 6
Const AT_RESTORE_FOREGROUND:Int = 7

Type t_game_action
	Field typ:Int
	Field mode:Int
	Field timer:Int
	
	Method New()
		Self.typ = AT_NONE
		Self.timer = 0
	End Method
	
	Method set_type(t:Int, m:Int = 0)
		Self.typ = t
		Self.mode = m
	End Method
	
	Method update()
		If (Self.timer > 0)
			Self.timer = Self.timer -1
		EndIf
	End Method					

	Method ToString:String()
		Return "Type: " + Self.typ + "	Mode: " + Self.mode
	End Method
End Type
