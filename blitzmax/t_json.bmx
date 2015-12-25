Rem

BlitzMax JSON stringify

Derived from javascript implementation by Douglas Crockford 
[https://github.com/douglascrockford/JSON-js/blob/master/json2.js]

TODO:
- escape sequences

EndRem

SuperStrict

Global JSON:t_json = New t_json

Type t_json
	
	Field gap:String
	Field indent:String
	Field context:String
	Field debug:Int
	
	Method stringify:String(value:Object, space:String = "", context:String = "")
		
		Local i:Int
		Self.gap = ""
		Self.indent = space
		Self.context = context
		
		''Make a fake root object containing our value under the key of '_'.
		''Return the result of stringifying the value.
	
		Local o:t_root = New t_root

		o._ = value
		
		Return Self.str("_", o)
		
	End Method
	
	Method str:String(key:String, holder:Object)
		Local i:Int, k:String, v:String
		Local mind:String = Self.gap
		Local partial:t_string_list = New t_string_list
		Local filter:TList
		
		Local id:TTypeId = TTypeId.ForObject(holder)
		Local o:Object, tt:TTypeId, value_type_id:TTypeId
		
		If (id.ExtendsType(TTypeId.ForName("TList")))
			o = TList(holder).valueAtIndex(Int(key))
			tt = TTypeId.ForObject(o)
			value_type_id = tt
		ElseIf (id.ExtendsType(TTypeId.ForName("TMap")))
			o = TMap(holder).valueForKey(key)
			tt = TTypeId.ForObject(o)
			value_type_id = tt
		Else
			Local ff:TField = id.findField(key)
			o = ff.Get(holder)
			tt = TTypeId.ForObject(o)
			value_type_id = ff._typeid
		EndIf
		
		If (tt <> Null)

			''filtering
			Local mt:TMethod = tt.findMethod("json_filter")
			If (mt <> Null)
				Local args:String[1]
				args[0] = Self.context
				filter = TList(mt.Invoke(o, args))
			EndIf
			
			Self.debug_out("Key:  " + key + "~tTTypeId: " + value_type_id.name())
			Select (value_type_id)
				Case ByteTypeId
					Return String(o)
					
				Case ShortTypeId
					Return String(o)
					
				Case IntTypeId
					Return String(o)
					
				Case LongTypeId
					Return String(o)
					
				Case FloatTypeId
					Return String(o)
					
				Case DoubleTypeId
					Return String(o)
					
				Case StringTypeId
					Return Self.quote(String(o))
					
				Default
					Self.gap = Self.gap + Self.indent
					
					''Array
					If (tt.ExtendsType(ArrayTypeId))
						If(tt._name <> "Null[]")
							Local length:Int = tt.ArrayLength(o)
							For Local i:Int = 0 To length -1
								Local p:String = Self.str_arr(i, o)
								partial.addLast(p)
							Next
						EndIf
					EndIf
					
					''TList
					If (tt.ExtendsType(TTypeId.ForName("TList")))
						Local length:Int = TList(o).count()
						For Local i:Int = 0 To length -1
							Local p:String = Self.str(i, o)
							partial.addLast(p)
						Next
					EndIf
					
					''Array / TList output
					If (tt.ExtendsType(ArrayTypeId) Or tt.ExtendsType(TTypeId.ForName("TList")))
						If (partial.count() = 0)
							v = "[]"
						Else If (Self.gap <> "")
							v = "[~n" + Self.gap + partial.join(",~n" + Self.gap) + "~n" + mind + "]"
						Else
							v = "[" + partial.join(",") + "]"
						EndIf
						Self.gap = mind
						Return v
					EndIf
					
					''TMap
					If (tt.ExtendsType(TTypeId.ForName("TMap")))
						For Local k:String = EachIn(TMap(o).Keys())
							v = Self.str(k, o)
							If (v <> "")
								Local s:String = Self.quote(k)
								If (Self.gap <> "")
									s = s + ": "
								Else
									s = s + ":"
								EndIf
								s = s + v
								partial.AddLast(s)
							EndIf
						Next
					Else
						''Object
						For Local f:TField = EachIn tt.EnumFields()
							
							''filtering
							Local skip:Int = False
							If (filter <> Null)
								skip = True
								For Local s:String = EachIn filter
									If (s = f._name)
										skip = False
									EndIf
								Next
							EndIf
							If (skip)
								Continue
							EndIf

							v = Self.str(f.Name(), o)
							If (v <> "")
								Local s:String = Self.quote(f.Name())
								If (Self.gap <> "")
									s = s + ": "
								Else
									s = s + ":"
								EndIf
								s = s + v
								partial.AddLast(s)
							EndIf
						Next
					EndIf
					
					''TMap / Object output
					If (partial.count() = 0)
						v = "{}"
					ElseIf (Self.gap <> "")
						v = "{~n" + Self.gap + partial.join(",~n" + Self.gap) + "~n" + mind + "}"
					Else
						v = "{" + partial.join(",") + "}"
					EndIf
            		Self.gap = mind
					Return v
			End Select
		Else
			Return "null"
		EndIf
		
	End Method
	
	Method str_arr:String(key:String, holder:Object)
		Local i:Int, k:String, v:String
		Local mind:String = Self.gap
		Local partial:t_string_list = New t_string_list
		Local filter:TList
		
		Local id:TTypeId = TTypeId.ForObject(holder)
		Local o:Object = id.getarrayelement(holder, Int(key))
		
		Local tt:TTypeId = TTypeId.ForObject(o)

		If (tt <> Null)
			
			''filtering
			Local mt:TMethod = tt.findMethod("json_filter")
			If (mt <> Null)
				Local args:String[1]
				args[0] = Self.context
				filter = TList(mt.Invoke(o, args))
			EndIf
			
			Select (id.name())
				Case "Byte[]"
					Return String(o)
					
				Case "Short[]"
					Return String(o)
					
				Case "Int[]"
					Return String(o)
					
				Case "Long[]"
					Return String(o)
					
				Case "Float[]"
					Return String(o)
					
				Case "Double[]"
					Return String(o)
					
				Case "String[]"
					Return Self.quote(String(o))
					
				Default
					Self.gap = Self.gap + Self.indent
					
					''TList[]
					If (tt.ExtendsType(TTypeId.ForName("TList")))
						Local length:Int = TList(o).count()
						For Local i:Int = 0 To length -1
							Local p:String = Self.str(i, o)
							partial.addLast(p)
						Next
						If (partial.count() = 0)
							v = "[]"
						Else If (Self.gap <> "")
							v = "[~n" + Self.gap + partial.join(",~n" + Self.gap) + "~n" + mind + "]"
						Else
							v = "[" + partial.join(",") + "]"
						EndIf
						Self.gap = mind
						Return v
					EndIf
					
					''TMap[]
					If (tt.ExtendsType(TTypeId.ForName("TMap")))
						For Local k:String = EachIn(TMap(o).Keys())
							v = Self.str(k, o)
							If (v <> "")
								Local s:String = Self.quote(k)
								If (Self.gap <> "")
									s = s + ": "
								Else
									s = s + ":"
								EndIf
								s = s + v
								partial.AddLast(s)
							EndIf
						Next
					Else
						''Object[]
						For Local f:TField = EachIn tt.EnumFields()
							
							''filtering
							Local skip:Int = False
							If (filter <> Null)
								skip = True
								For Local s:String = EachIn filter
									If (s = f._name)
										skip = False
									EndIf
								Next
							EndIf
							If (skip)
								Continue
							EndIf

							v = Self.str(f.Name(), o)
							If (v <> "")
								Local s:String = Self.quote(f.Name())
								If (Self.gap <> "")
									s = s + ": "
								Else
									s = s + ":"
								EndIf
								s = s + v
								partial.AddLast(s)
							EndIf
						Next
					EndIf
					
					''TMap[] / Object[] output
					If (partial.count() = 0)
						v = "{}"
					ElseIf (Self.gap <> "")
						v = "{~n" + Self.gap + partial.join(",~n" + Self.gap) + "~n" + mind + "}"
					Else
						v = "{" + partial.join(",") + "}"
					EndIf
            		Self.gap = mind
					Return v
			End Select
		Else
			Return "null"
		EndIf
		
	End Method
	
	Method quote:String(s:String)
		Return "~q" + s + "~q"
	End Method
	
	Method debug_out(s:String)
		If (Self.debug)
			Print s
		EndIf
	End Method
	
	Method print_types()
		For Local t:TTypeId = EachIn TTypeId.EnumTypes()
			Print t.name()
		Next
	End Method
	
End Type

Type t_root
	Field _:Object
End Type

Type t_string_list Extends TList
	Method join:String(separator:String)
		Local res:String = ""
		Local sep:String = ""
		For Local s:String = EachIn Self
			res = res + sep + s
			sep = separator
		Next
		Return res
	End Method
End Type
