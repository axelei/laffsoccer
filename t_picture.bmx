SuperStrict

Import "t_widget.bmx"

Type t_picture Extends t_widget

	Method set_geometry(pos_x:Int, pos_y:Int, width:Int, height:Int)
		Self.set_position(pos_x, pos_y)
		Self.set_size(width, height)
		Self.set_frame(width, height, 0, 0)
	End Method
	
	Method render()
		If (Self.visible = False) Return
		
		If Self.image
			DrawSubImageRect(Self.image, Self.x, Self.y, Self.frame_w, Self.frame_h,  Self.frame_x * Self.frame_w, Self.frame_y * Self.frame_h, Self.frame_w, Self.frame_h)
		EndIf
	End Method
	
End Type
