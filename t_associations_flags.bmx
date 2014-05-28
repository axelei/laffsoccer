SuperStrict

Import "t_associations_list.bmx"
Import "lib_main.bmx"

Global associations_flags:t_associations_flags

Type t_associations_flags Extends TMap
	
	Function Create:t_associations_flags()
		Local af:t_associations_flags = New t_associations_flags
		af.load_flags("images/flags/tiny")
		Return af
	End Function
	
	Method load_flags(folder:String)
		For Local association:String = EachIn associations_list
			Local flag:TImage = load_image(folder, Lower(association) + ".png")
			Self.insert(association, flag)
		Next
	End Method
	
	Method get_flag:TImage(association:String)
		Local flag:TImage = TImage(Self.ValueForKey(association))
		
		''default value
		If (flag = Null)
			flag = TImage(Self.ValueForKey("CUS")) ''custom
		EndIf
		
		Return flag
	End Method
	
End Type
