SuperStrict

Global associations_list:t_associations_list

Type t_associations_list Extends TList
	
	Function Create:t_associations_list()
		Local l:t_associations_list = New t_associations_list
		l.load_associations("codes/associations.txt")
		Return l
	End Function
	
	Method load_associations(file_name:String)
		
		Local file_in:TStream
		file_in = ReadStream("littleendian::" + file_name)
		If (file_in = Null)
			RuntimeError("Cannot load:" + file_name)
		EndIf
		
		Local ln:String
		
		While Not Eof(file_in)
			ln = ReadLine(file_in)
			Self.AddLast(Left(ln, 3))
		Wend
		
		CloseStream(file_in)
		
	End Method
	
	Method rotate:String(curr:String, n:Int)
		Local curr_link:TLink = Super.FindLink(curr)
		If (curr_link = Null)
			RuntimeError "item not found"
		EndIf
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
		Return String(curr_link.Value())
	End Method
	
End Type
