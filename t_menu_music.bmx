SuperStrict

Import "lib_misc.bmx"
Import "constants.bmx"

Type t_menu_music

	Field filelist:TList
	Field playlist:TList
	Field folder:String
	Field channel:TChannel
	Field current_track:Int
	Field disabled:Int
	
	Method New()
		Self.filelist = New TList
		Self.playlist = New TList
		Self.channel = New TChannel
		Self.current_track:Int = -1
		Self.disabled = True
	End Method
	
	Method init(target_folder:String)
		Self.folder = target_folder
		
		''load file names
		Local dir:Int = ReadDir(Self.folder)
		Local file:String = NextFile(dir)
		While (file <> "")
			''it is a file (and not a folder)
			If (FileType(Self.folder + "/" + file) = 1)
				Local ext:String = Lower(Right(file, 4))
				If (ext = ".wav" Or ext = ".ogg")
					ListAddLast(Self.filelist, file)
				EndIf
			EndIf
			file = NextFile(dir)
		Wend
		CloseDir(dir)

		If (Not Self.filelist.Count())
			Print ("Warning: no .wav or .ogg files found in " + folder)
			Self.disabled = True
			Return
		EndIf
		
		Self.disabled = False
		Self.filelist.Sort()
	End Method
	
	Method set_mode(mode:Int)
		If (Self.disabled)
			Return
		EndIf
		StopChannel(Self.channel)
		Self.current_track:Int = -1
		
		Select mode
			Case MM_ALL
				Self.playlist = Self.filelist.Copy()
				
			Case MM_SHUFFLE
				''seed random value generator
				SeedRnd MilliSecs()
				Rand(1)
				
				''shuffle
				Self.playlist = Self.filelist.Copy()
				For Local i:Int = 1 To 2*Self.playlist.Count()
					Local p1:Int = Rand(0, Self.playlist.Count() -1)
					Local p2:Int = Rand(0, Self.playlist.Count() -1)
					swap_elements(Self.playlist, p1, p2)
				Next
				
			''SINGLE TRACK
			Default
				Local selected:Int =  Min(mode, Self.filelist.Count() -1)
				Local track_name:String = String(Self.filelist.ValueAtIndex(selected))
				Self.playlist.Clear()
				ListAddLast(Self.playlist, track_name)
				
		End Select
		
		Self.disabled = False
		Self.update()
	End Method
	
	Method update()
		If (Self.disabled)
			Return
		EndIf
		
		If (Not ChannelPlaying(Self.channel))
			Self.current_track = rotate(Self.current_track, 0, Self.playlist.Count() -1, +1)
			Local track_name:String = String(Self.playlist.ValueAtIndex(Self.current_track))
			Local loop:Int = (Self.playlist.Count() = 1)
			Local sound:TSound = load_sound(Self.folder, track_name, loop)
			
			If (sound = Null)
				Self.disabled = True
				Return
			EndIf
			
			Self.channel = PlaySound(sound)
			
			''something went wrong
			If (Not ChannelPlaying(Self.channel))
				Self.disabled = True
			EndIf
		EndIf
	End Method
	
	Method set_volume(v:Float)
		SetChannelVolume(Self.channel, v)
	End Method
	
	Method get_current_track_name:String()
		If (Self.disabled)
			Return ""
		EndIf
		Return String(Self.playlist.ValueAtIndex(Self.current_track))
	End Method
	
	Method get_mode_min:Int()
		Return MM_ALL
	End Method
	
	Method get_mode_max:Int()
		Return Self.filelist.Count() -1
	End Method
	
	Method print_filelist()
		Print "Filelist:"
		For Local i:Int = 0 To Self.filelist.Count() -1
			Print i + ": " + String(Self.filelist.ValueAtIndex(i))
		Next
	End Method
	
	Method print_playlist()
		Print "Playlist:"
		For Local i:Int = 0 To Self.playlist.Count() -1
			Print i + ": " + String(Self.playlist.ValueAtIndex(i))
		Next
	End Method
		
End Type
