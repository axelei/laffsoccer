SuperStrict

Import "t_button.bmx"

Type t_player_input_device_button Extends t_button
	Field player:t_player
	
	Method draw_image()
		If (Self.player)
			DrawSubImageRect(Self.image, Self.x+4, Self.y -1, 18, 18, 18*Self.player.input_device.typ, 36, 18, 18)
		EndIf
	End Method
	
	Method draw_10u() 
		set_color($FFFFFF, light)
		text10u(Self.get_text(), Self.x +26, Self.y + Ceil(0.5*(Self.h -18)), Self.img_ucode, 1)
	End Method
	
	Method update()
		Super.update()
		If (Self.player)
			If (Self.player.input_device.typ = t_input.ID_COMPUTER)
				Self.set_text("", 1, 10)
			Else
				Self.set_text(Self.player.input_device.port +1, 1, 10)
			EndIf
		EndIf
	End Method
	
End Type
