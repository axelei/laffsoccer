SuperStrict

Import "t_crowd.bmx"

Global crowd_list:t_crowd_list

Type t_crowd_list Extends TList
	
	Field limit:Int
	
	Function Create:t_crowd_list()
		Local cl:t_crowd_list = New t_crowd_list
		cl.load_crowds("images/stadium/crowd.txt")
		Return cl
	End Function
	
	Method load_crowds(file_name:String)
		
		Local file_in:TStream
		file_in = ReadStream("littleendian::" + file_name)
		If (file_in = Null)
			RuntimeError("Cannot load:" + file_name)
		EndIf
	
		Local ln:String
	
		''skip heading
		ln = ReadLine(file_in)
	
		While Not Eof(file_in)
			ln = ReadLine(file_in)
	
			If (ln.length > 0)
				Local crowd:t_crowd = New t_crowd
				crowd.x	   = Int(Trim(Mid(ln, 1, 6)))
				crowd.y    = Int(Trim(Mid(ln, 7, 6)))
				crowd.typ  = Asc(Trim(Mid(ln, 13, 6))) -97
				crowd.rank = Int(Trim(Mid(ln, 19, 6)))
				Self.AddLast(crowd)
			EndIf
		Wend
		
		CloseStream(file_in)
			
	End Method
	
	Method set_limit(l:Int)
		Self.limit = Max(0, l)
	End Method
	
	Method draw()
		For Local crowd:t_crowd = EachIn Self
			If (crowd.rank <= Self.limit)
				crowd.draw()
			EndIf
		Next
	End Method
		
End Type
