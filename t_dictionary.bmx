SuperStrict

Global dictionary:t_dictionary

Type t_dictionary
	
	Field map:tmap
	Field language_files:TList
	
	Method New()
		Self.map = New tmap
		Self.language_files = New TList
		
		Local directory:Int = ReadDir("language/")
		Local file:String = NextFile(directory)
		While (file <> "")
			
			''if it is a file (and not a folder)
			If (FileType("language/" + file) = 1)
				If (Right(file,3) = ".po")
					ListAddLast(Self.language_files, file)
				EndIf
			EndIf
			
			file:String = NextFile(directory)
		Wend
		CloseDir(directory)
		
		Self.language_files.sort()
		
	End Method
	
	Method fetch_language(n:Int)
		If (n >= CountList(Self.language_files))
			RuntimeError("Cannot load language #" + n)
		EndIf
		
		Local file:String = String(Self.language_files.valueatindex(n))
		
		Local file_in:TStream
		file_in = ReadFile("littleendian::language/" + file)
		If (file_in = Null)
			RuntimeError("Cannot load: " + file)
		EndIf
		
		Local string_stream:TTextStream = TTextStream.Create(file_in, TTextStream.UTF8)
		Local line:String
		Local success:Int
		While (Not file_in.Eof())
			line = string_stream.ReadLine()
			If (Left(line, 5) = "msgid")
				
				''get message id
				Local s:Int = line.find(Chr(34))
				Local e:Int = line.findlast(Chr(34))
				Local msgid:String = Right(Left(line, e), e-s-1)
				
				''get message string
				line = string_stream.ReadLine()
				s = line.find(Chr(34))
				e = line.findlast(Chr(34))
				Local msgstr:String = Trim(Right(Left(line, e), e-s-1))
				
				''convert msgid back to utf8
				Local b:TBank = New TBank
				Local bs:TBankStream = TBankStream.Create(b)
				Local ts:TTextStream = TTextStream.Create(bs, TTextStream.UTF8)
				ts.WriteString(msgid)
				SeekStream(ts, 0)
				Local msgid_utf8:String = bs.ReadLine()
				
				If (msgstr = "")
					Self.map.insert(msgid_utf8, msgid)
				Else
					Self.map.insert(msgid_utf8, msgstr)
				EndIf
			EndIf
		Wend
		
		string_stream.Close
		CloseFile(file_in)
		
	End Method
	
	Method gettext:String(s:String)
		Return String(Self.map.ValueForKey(s))
	End Method
	
End Type
