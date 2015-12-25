SuperStrict

Import "t_keyboard.bmx"
Import "t_joystick.bmx"

Type t_input_devices Extends TList
	Method ValueAtIndex:t_input(i:Int)
		Return t_input(Super.ValueAtIndex(i))
	End Method
	
	Method set_availability(n:Int)
		For Local d:t_input = EachIn Self
			d.set_is_available(n)
		Next
	End Method
	
	Method get_availability_count:Int()
		Local n:Int = 0
		For Local d:t_input = EachIn Self
			If (d.is_available)
				n :+ 1
			EndIf
		Next
		Return n
	End Method
	
	Method assign_first_available:t_input()
		For Local d:t_input = EachIn Self
			If (d.is_available)
				d.set_is_available(False)
				Return d
			EndIf
		Next
		Return Null
	End Method
	
	Method assign_next_available:t_input(curr:t_input)
		Local curr_link:TLink = Super.FindLink(curr)
		If (curr_link = Null)
			RuntimeError "item not found"
		EndIf
		curr_link = curr_link.NextLink()
		While (curr_link <> Null)
			Local d:t_input = t_input(curr_link._value)
			If (d.is_available)
				curr.set_is_available(True)
				d.set_is_available(False)
				Return d
			EndIf
			curr_link = curr_link.NextLink()
		Wend
		Return Null
	End Method
	
	Method rotate_available:t_input(curr:t_input, n:Int)
		Local curr_link:TLink = Super.FindLink(curr)
		If (curr_link = Null)
			RuntimeError "item not found"
		EndIf
		curr.set_is_available(True)
		For Local i:Int = 0 To Self.Count() -1
			Select n
				Case 1 ''forward
					curr_link = curr_link.NextLink()
					If (curr_link = Null)
						curr_link = Self.FirstLink()
					EndIf
				Case -1 ''backward
					curr_link = curr_link.PrevLink()
					If (curr_link = Null)
						curr_link = Self.LastLink()
					EndIf
				Default
					RuntimeError "invalid value"
			End Select
			Local d:t_input = t_input(curr_link._value)
			If (d.is_available)
				d.set_is_available(False)
				Return d
			EndIf
		Next
	End Method
	
	Method ToString:String()
		Local s:String = "Input devices:~n"
		For Local d:t_input = EachIn Self
			s :+ "- " + d.toString() + "~n"
		Next
		Return s
	End Method
End Type
