SuperStrict

Import "t_button.bmx"

Type t_team_input_device_button Extends t_button
	Field team:t_team
	
	Method New()
		Self.set_text_offset_x(42)
	End Method
	
	Method set_team(t:t_team)
		Self.team = t
	End Method
	
	Method draw_image()
		If (Self.team.input_device)
			DrawSubImageRect(Self.image, Self.x +4, Self.y +2, 36, 36, 36 * Self.team.input_device.typ, 0, 36, 36)
		EndIf
	End Method
	
	Method update()
		Super.update()
		If (Self.team.input_device)
			Self.set_visible(True)
			Select Self.team.input_device.typ
				Case t_input.ID_COMPUTER
					Self.set_text("", 1, 10)
				Case t_input.ID_KEYBOARD
					Self.set_text(dictionary.gettext("KEYBOARD") + " " + (Self.team.input_device.port +1), 1, 10)
				Case t_input.ID_JOYSTICK
					Self.set_text(dictionary.gettext("JOYSTICK") + " " + (Self.team.input_device.port +1), 1, 10)
			End Select
		Else
			Self.set_visible(False)
		EndIf
	End Method
	
End Type
