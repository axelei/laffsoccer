SuperStrict

Type t_vec2d
	
	''cartesian coordinates
	Field x:Float
	Field y:Float
	
	''polar coordinates
	Field v:Float
	Field a:Float
	
	Function Create:t_vec2d(x0:Float, y0:Float, polar_coordinates:Int = False)
		Local c:t_vec2d = New t_vec2d
		If (polar_coordinates = True)
			c.set_va(x0, y0)
		Else
			c.set_xy(x0, y0)
		EndIf
		Return c
	End Function
	
	Method set_x(x0:Float)
		Self.x = x0
		Self.update_polar()
	End Method
	
	Method set_y(y0:Float)
		Self.y = y0
		Self.update_polar()
	End Method
	
	Method set_v(v0:Float)
		Self.v = v0
		Self.update_cartesian()
	End Method
	
	Method set_a(a0:Float)
		Self.a = a0
		Self.update_cartesian()
	End Method
	
	Method set_xy(x0:Float, y0:Float)
		Self.set_x(x0)
		Self.set_y(y0)
	End Method
	
	Method set_va(v0:Float, a0:Float)
		Self.set_v(v0)
		Self.set_a(a0)
	End Method
	
	Method diff:t_vec2d(v2:t_vec2d)
		Return t_vec2d.Create(Self.x -v2.x, Self.y -v2.y)
	End Method
		
	Method sum:t_vec2d(v2:t_vec2d)
		Return t_vec2d.Create(Self.x +v2.x, Self.y +v2.y)
	End Method
		
	Method update_polar()
		Self.v = Sqr(Self.x * Self.x + Self.y * Self.y)
		Self.a = ATan2(Self.y, Self.x)
	End Method
	
	Method update_cartesian()	
		Self.x = Self.v * Cos(Self.a)
		Self.y = Self.v * Sin(Self.a)
	End Method
	
End Type
