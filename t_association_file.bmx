SuperStrict

Type t_association_file
	Field code:String
	Field confederation:String
	Field name:String
	Field is_available:Int
	
	Function Create:t_association_file(code:String, confederation:String, name:String)
		Local af:t_association_file = New t_association_file
		af.code = code
		af.confederation = confederation
		af.name = name
		af.is_available = False
		Return af
	End Function
	
	Method set_is_available(b:Int)
		Self.is_available = b
	End Method
End Type
