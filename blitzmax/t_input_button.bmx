SuperStrict

Import "t_button.bmx"

Type t_input_button Extends t_button
	Field entry_string:String
	Field entry_limit:Int
	Field backspace_wait:Int
	
	Method New()
		Self.active = True
		Self.backspace_wait = 0
		Self.bind("fire1_down", "bc_widget_edit")
	End Method
	
	Method set_entry_mode(n:Int)
		If (Self.entry_mode = False And n = True)
			Self.entry_string = Self.text
			FlushKeys()
		EndIf
		If (Self.entry_mode = True And n = False)
			If (Self.text <> Self.entry_string)
				Self.set_changed(True)
			EndIf
			Self.text = Self.entry_string
		EndIf
		Self.entry_mode = n
	End Method
	
	Method set_entry_limit(n:Int)
		Self.entry_limit = n
	End Method

	Method update()
		
		If Self.entry_mode = False
			Return
		EndIf
		
		If (Self.backspace_wait > 0)
			Self.backspace_wait = Self.backspace_wait -1
		EndIf
		
		''for each key
		For Local i:Int = 1 To 255

			''if pressed
			If KeyDown(i) = 1 

				If (i = KEY_ESCAPE)
					''disable entry mode without applying changes
					Self.entry_mode = False
				EndIf
				
				If (i = KEY_BACKSPACE)
					If (Not Self.backspace_wait)
						Self.entry_string = Left(Self.entry_string, Max(Len(Self.entry_string)-1, 0))
						Self.backspace_wait = 4
					EndIf
				EndIf
				
				If i = KEY_ENTER
					Self.set_entry_mode(False)
				EndIf
			EndIf
			
		Next

		''get ascii chars
		Local value:Int = GetChar() 'GetKey()
		If is_char(value) And (Len(Self.entry_string) < Self.entry_limit)
			Self.entry_string = Self.entry_string + upper_case(value)
		EndIf
	End Method
	
	Method get_text:String()
		If (Self.entry_mode = True)
			Return Self.entry_string + "_"
		Else
			Return Self.text
		EndIf
	End Method
	
End Type

