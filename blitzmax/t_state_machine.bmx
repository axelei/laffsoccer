SuperStrict
Import "t_state.bmx"
Type t_state_machine
	
	Field states:TList
	Field active_state:t_state
	
	Method New()
		states = New TList
	End Method
	
	Method get_active_state:t_state()
		Return Self.active_state
	End Method
	
	Method add_state(s:t_state)
		ListAddLast(Self.states, s)
	End Method
	
	Method think()
		If (Self.active_state = Null)
			Return
		EndIf
		
		Self.active_state.do_actions()
		
		Local new_state:t_state = active_state.check_conditions()
		If (new_state <> Null)
			Self.set_state(new_state.name)
		EndIf
	End Method
	
	Method set_state(name:String)
		If (name = Null)
			Self.active_state = Null
			Return
		EndIf
		
		If (Self.active_state)
			Self.active_state.exit_actions()
		EndIf
		
		Local found:Int = False
		For Local s:t_state = EachIn Self.states
			If (s.check_name(name))
				Self.active_state = s
				Self.active_state.entry_actions()
				found = True
			EndIf
		Next
		
		If (Not found)
			RuntimeError "Warning! Cannot find state: " + name
		EndIf
	End Method
End Type
