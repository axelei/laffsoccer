SuperStrict

Type t_state
	Field timer:Int
	Field name:String
	
	Method set_name(_name:String)
		Self.name = _name
	End Method
	
	Method get_name:String()
		Return Self.name
	End Method
	
	Method do_actions()
		Self.timer :+ 1
	End Method
	
	Method check_conditions:t_state()
		Return Null
	End Method
	
	Method entry_actions()
		Self.timer = 0
	End Method
	
	Method exit_actions()
	End Method
	
	Method check_name:Int(name0:String)
		Return (Self.name = name0)
	End Method
	
	Method ToString:String()
		Print Self.name + ":" + timer
	End Method
End Type
