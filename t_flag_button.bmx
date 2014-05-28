SuperStrict

Import "t_button.bmx"

Type t_flag_button Extends t_button
	Field player:t_player
	
	Method draw_image()
		If (Self.player)
			set_color($303030, light) 
			DrawRect Self.x + 8, Self.y +5, 20, 10
			set_color($FFFFFF, light)
			DrawImage(associations_flags.get_flag(Self.player.nationality), Self.x +6, Self.y +3)
		EndIf
	End Method
End Type

