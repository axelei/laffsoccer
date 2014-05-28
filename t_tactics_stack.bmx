SuperStrict

Import "t_tactics.bmx"

Type t_tactics_stack Extends TList
	
	Method push(t:t_tactics)
		Local last_tactics:t_tactics = New t_tactics
		last_tactics.copy(t)
		Self.addFirst(last_tactics)
	End Method
	
	Method pop:t_tactics()
		Return t_tactics(Self.removeFirst())
	End Method
	
End Type

