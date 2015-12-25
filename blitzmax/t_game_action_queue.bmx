SuperStrict

Import "t_game_action.bmx"

Global game_action_queue:t_game_action_queue

Type t_game_action_queue Extends TList
	
	Method push(t:Int, m:Int = 0)
		Local a:t_game_action = New t_game_action
		a.set_type(t, m)
		Self.addLast(a)
	End Method
	
	Method pop:t_game_action()
		Return t_game_action(Self.removeFirst())
	End Method
	
	Method ToString:String()
		Local s:String
		Local l:TLink = Self.firstLink() 
		While l
			s = s + "ACTION: " + t_game_action(l.value()).ToString() + Chr(10)
			l = l.nextLink()
		Wend
		Return s
	End Method 
	
End Type
