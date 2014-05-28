SuperStrict

Import "t_input.bmx"

Type t_joystick Extends t_input
	
	Const BUTTON_1:Int = 0
	Const BUTTON_2:Int = 1

	Method New()
		Self.typ = Self.ID_JOYSTICK
	End Method
	
	Method _read()
		Self.x0		= Self.read_x()
		Self.y0		= Self.read_y()
		Self.fire10	= Self.read_fire1()
		Self.fire20	= Self.read_fire2()
	End Method
	
	Method read_x:Int()
		Local x:Float = JoyX(Self.port)
		Return Sgn(x) * (Abs(x) > 0.5)
	End Method

	Method read_y:Int()
		Local y:Float = JoyY(Self.port)
		Return Sgn(y) * (Abs(y) > 0.5)
	End Method
	
	Method read_fire1:Int()
		Return JoyDown(Self.BUTTON_1, Self.port)
	End Method
	
	Method read_fire2:Int()
		Return JoyDown(Self.BUTTON_2, Self.port)
	End Method
	
End Type
