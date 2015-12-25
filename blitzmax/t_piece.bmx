SuperStrict

Import "t_button.bmx"

Type t_piece Extends t_button
	Field key_wait:Int
	Field square:t_vec2d
	Field grid_x:Int
	Field grid_y:Int
	Field grid_w:Int
	Field grid_h:Int
	Field xmin:Int
	Field xmax:Int
	Field ymin:Int
	Field ymax:Int
	
	Method New()
		Self.active = True
		Self.key_wait = 0
		Self.square = New t_vec2d
	End Method
	
	Method set_square(_x:Int, _y:Int)
		Self.square.x = _x
		Self.square.y = _y
		Self.update_position()
	End Method
	
	Method set_grid_geometry(_x:Int, _y:Int, _w:Int, _h:Int)
		Self.grid_x = _x
		Self.grid_y = _y
		Self.grid_w = _w
		Self.grid_h = _h
		Self.update_position()
	End Method
	
	Method set_ranges(_xmin:Int, _xmax:Int, _ymin:Int, _ymax:Int)
		Self.xmin = _xmin
		Self.xmax = _xmax
		Self.ymin = _ymin
		Self.ymax = _ymax
		Self.update_position()
	EndMethod
	
	Method update_position()
		Self.x = Self.grid_x + Self.square.x * Self.grid_w / (Self.xmax -Self.xmin)
		Self.y = Self.grid_y + Self.square.y * Self.grid_h / (Self.ymax -Self.ymin)
	End Method

	Method update()
		
		If (Self.entry_mode = False)
			Return
		EndIf
		
		If (Self.key_wait > 0)
			Self.key_wait = Self.key_wait -1
		EndIf
		
		''for each key
		For Local i:Int = 1 To 255

			''if pressed
			If (KeyDown(i) = 1)
			
				If (i = KEY_UP) And (Not Self.key_wait)
					Self.square.y = Max(Self.square.y -1, Self.ymin)
					Self.set_changed(True)
					Self.key_wait = 4
				EndIf

				If (i = KEY_DOWN) And (Not Self.key_wait)
					Self.square.y = Min(Self.square.y +1, Self.ymax)
					Self.set_changed(True)
					Self.key_wait = 4
				EndIf

				If (i = KEY_LEFT) And (Not Self.key_wait)
					Self.square.x = Max(Self.square.x -1, Self.xmin)
					Self.set_changed(True)
					Self.key_wait = 4
				EndIf

				If (i = KEY_RIGHT) And (Not Self.key_wait)
					Self.square.x = Min(Self.square.x +1, Self.xmax)
					Self.set_changed(True)
					Self.key_wait = 4
				EndIf
				
			EndIf
			
		Next
		
		If (Self.get_changed())
			Self.update_position()
		EndIf
		
	End Method
	
	Method draw_animated_border()
		Const GL_MIN:Int = 36
		Const GL_MAX:Int = 200
		
		''graylevel
		Local gl:Int = Abs(((0.8*Abs(MilliSecs())) Mod (2*(GL_MAX-GL_MIN))) -(GL_MAX-GL_MIN)) +GL_MIN
		
		''border
		Local c_border:Int = RGB(gl, gl, gl)
		If (Self.entry_mode)
			c_border = RGB(GL_MAX, GL_MAX, 0)
		EndIf
		set_color(c_border, light)
		DrawOval(Self.x, Self.y, Self.w, Self.h)
		set_color($FFFFFF, light)
	
	End Method
	
End Type


