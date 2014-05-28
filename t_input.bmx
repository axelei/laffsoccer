SuperStrict

Import "t_game_settings.bmx"

Type t_input
	Const ID_COMPUTER:Int = 0
	Const ID_KEYBOARD:Int = 1
	Const ID_JOYSTICK:Int = 2
	
	Field typ:Int					''ID_COMPUTER, ID_KEYBOARD, ID_JOYSTICK
	Field port:Int
	Field x0:Int, x1:Int, x2:Int	''0 input values - 1,2 old values buffer
	Field y0:Int, y1:Int, y2:Int
	Field value:Int
	Field angle:Int
	Field fire10:Int, fire11:Int	''button 1: 10 input value - 11 old value
	Field fire20:Int, fire21:Int	''button 2: 20 input value - 21 old value
	Field is_available:Int
	
	Method read_input()
		
		''update input buffer
		Self.x2 = Self.x1
		Self.x1 = Self.x0
		Self.y2 = Self.y1
		Self.y1 = Self.y0
		Self.fire11 = Self.fire10
		Self.fire21 = Self.fire20
		
		''read new values
		Self._read()
		
		''clean spikes: safe values are x1, y1
		If ((Self.x1 <> Self.x0) And (Self.x1 <> Self.x2))
			Self.x1 = Self.x2
		EndIf
		If ((Self.y1 <> Self.y0) And (Self.y1 <> Self.y2))
			Self.y1 = Self.y2
		EndIf
		
		Self.value = (Self.x1 <> 0) Or (Self.y1 <> 0)
		Self.angle = ATan2(Self.y1, Self.x1)
		
	End Method
	
	Method _read() Abstract
	
	''x
	Method set_x(x:Int)
		Self.x0 = x
	End Method
	
	Method get_x:Int()
		Return Self.x1
	End Method
	
	Method x_moved:Int()
		Return Self.x1 And (Not Self.x0)
	End Method
	
	Method x_released:Int()
		Return (Not Self.x1) And Self.x0
	End Method
	
	''y
	Method set_y(y:Int)
		Self.y0 = y
	End Method
	
	Method get_y:Int()
		Return Self.y1
	End Method
	
	Method y_moved:Int()
		Return Self.y1 And (Not Self.y0)
	End Method
	
	Method y_released:Int()
		Return (Not Self.y1) And Self.y0
	End Method
	
	''fire1
	Method set_fire1(f:Int)
		Self.fire10 = f
	End Method
	
	Method get_fire1:Int()
		Return Self.fire11
	End Method
	
	Method fire1_down:Int()
		Return (Self.fire10 = 1) And (Self.fire11 = 0)
	End Method
	
	Method fire1_up:Int()
		Return (Self.fire10 = 0) And (Self.fire11 = 1)
	End Method
	
	''fire2
	Method set_fire2(f:Int)
		Self.fire20 = f
	End Method
	
	Method get_fire2:Int()
		Return Self.fire21
	End Method
	
	Method fire2_down:Int()
		Return (Self.fire20 = 1) And (Self.fire21 = 0)
	End Method
	
	Method fire2_up:Int()
		Return (Self.fire20 = 0) And (Self.fire21 = 1)
	End Method
	
	Method get_value:Int()
		Return Self.value
	End Method
	
	Method get_angle:Float()
		Return Self.angle
	End Method
	
	Method set_type(t:Int)
		Self.typ = t
	End Method
	
	Method reset_buffer()
		Self.x0 = 0
		Self.x1 = 0
		Self.x2 = 0
		
		Self.y0 = 0
		Self.y1 = 0
		Self.y2 = 0
		
		Self.fire10 = 0
		Self.fire11 = 0
		
		Self.fire20 = 0
		Self.fire21 = 0
	End Method
	
	Method set_port(p:Int)
		Self.port = p
	End Method
	
	Method set_is_available(b:Int)
		Self.is_available = b
	End Method
	
	Method ToString:String()
		Local s:String
		Select Self.typ
			Case ID_COMPUTER
				s = "Computer"
			Case ID_KEYBOARD
				s = "Keyboard " + Self.port
			Case ID_JOYSTICK
				s = "Joystick " + Self.port
		End Select
		s :+ "~t is available: " + Self.is_available
		Return s
	End Method
	
End Type
