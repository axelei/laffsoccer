SuperStrict

Import "lib_misc.bmx"

Type t_sprite
	
	Field img:TImage
	Field x:Int
	Field y:Int
	Field z:Int
	Field offset:Int
	Field is_visible:Int
	
	Method New()
		Self.is_visible = True
	End Method
	
	Method draw(frame:Int)
		draw_image(Self.img, Self.x, Self.y -Self.z -Self.offset)
	End Method
	
	Method set_is_visible(v:Int)
		Self.is_visible = v
	End Method
	
	Method get_y:Int(frame:Int)
		Return y
	End Method
	
End Type
