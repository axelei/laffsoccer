SuperStrict

Import "t_button.bmx"

Type t_face_button Extends t_button
	Field player:t_player
	
	Method draw_image()
		If Self.player
			Local d:Int = (Self.h -17)/2
			DrawSubImageRect(Self.player.facesh, Self.x + 6, Self.y +1 +d, 16, 18, 16*Self.player.health, 0, 16, 18)
			DrawSubImageRect(Self.player.face,	Self.x + 4,	Self.y -1 +d, 16, 18, 16*Self.player.health, 0, 16, 18)
		EndIf
	End Method
End Type

