SuperStrict

Import "constants.bmx"
Import "lib_misc.bmx"

Global img_crowd:TImage[5]

Type t_crowd
	Field x:Int
	Field y:Int
	Field typ:Int
	Field rank:Int
	
	Method draw()
		draw_image(img_crowd[Self.typ], -CENTER_X +Self.x, -CENTER_Y +Self.y)
	End Method
	
End Type

