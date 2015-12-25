SuperStrict

Import "lib_main.bmx"

Type t_backgrounds
	Field map:tmap
	Field filenames:TList
	
	Method New()
		Self.map = New tmap
		Self.filenames = New TList
		
		Self.filenames.AddLast("menu_main.jpg")
		Self.filenames.AddLast("menu_friendly.jpg")
		Self.filenames.AddLast("menu_game_options.jpg")
		Self.filenames.AddLast("menu_match_options.jpg")
		Self.filenames.AddLast("menu_control.jpg")
		Self.filenames.AddLast("menu_edit.jpg")
		Self.filenames.AddLast("menu_competition.jpg")
		Self.filenames.AddLast("menu_match_presentation.jpg")
		Self.filenames.AddLast("menu_edit_players.jpg")
		Self.filenames.AddLast("menu_set_team.jpg")
		Self.filenames.AddLast("menu_edit_team.jpg")
		Self.filenames.AddLast("menu_training.jpg")
		
		For Local filename:String = EachIn Self.filenames
			Local img:TImage = load_image("images/backgrounds", filename, 0, 0)
			Self.map.insert(filename, img)
		Next
	End Method

	Method get:TImage(filename:String)
		Local image:TImage = TImage(Self.map.ValueForKey(filename))
		If (image = Null)
			RuntimeError("Background " + filename + " is not available")
		EndIf
		Return image
	End Method
End Type
