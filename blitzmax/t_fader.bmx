SuperStrict

Import "t_widget.bmx"

Type t_fader Extends t_widget

	Field alpha:Float
	Field color:Int
	
	Method New()
		Self.alpha = 1
		Self.color = $000000
	End Method
	
	Method set_alpha_color(a:Float, c:Int = $000000)
		Self.alpha = a
		Self.color = c
	End Method

	Method render()
		If (Self.visible = False) Return
		
		fade_rect(Self.x, Self.y, Self.x + Self.w, Self.y + Self.h, Self.alpha, Self.color, light)
	End Method

End Type
