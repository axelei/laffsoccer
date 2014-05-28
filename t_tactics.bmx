SuperStrict

Import "t_vec2d.bmx"
Import "constants.bmx"

Type t_tactics
	
	Field name:String
	Field target:t_vec2d[TEAM_SIZE, BALL_ZONES]
	Field pairs:Int[TEAM_SIZE]
	Field based_on:Int
	
	Method New()
		For Local player:Int = 0 To TEAM_SIZE -1
			For Local ball_zone: Int = 0 To BALL_ZONES -1
				Self.target[player, ball_zone] = New t_vec2d
			Next
		Next
	End Method
	
	Method load_file(filename:String)
		Local file_tactics:TStream
		
		file_tactics = ReadFile(filename)
		If (Not file_tactics)
			RuntimeError("Cannot load from: " + filename)
		EndIf
		
		''name
		Self.name = ""
		Local end_of_name:Int = False
		For Local i:Int = 0 To 8
			Local b:Int = ReadByte(file_tactics)
			If (Not b) end_of_name = True
			If (Not end_of_name) Self.name :+ Chr(b)
		Next
		
		''targets
		For Local player:Int = 1 To TEAM_SIZE -1
			For Local ball_zone:Int = 0 To BALL_ZONES -1
				Local b:Int = ReadByte(file_tactics)
				
				''read unsigned values
				Local x:Int = b Shr 4	''0 to 14
				Local y:Int = b & 15	''0 to 15
				
				''convert to signed values
				x = x -7		''-7 to +7
				y = 2*y -15		''-15 to +15
				
				''convert to pitch coordinates
				Self.target[player, ball_zone].x = x * TACT_DX
				Self.target[player, ball_zone].y = y * TACT_DY
			Next
		Next
		
		''pairs
		For Local i:Int = 1 To TEAM_SIZE -1
			Self.pairs[i] = ReadByte(file_tactics)
		Next
		
		''base tactics
		Self.based_on = ReadByte(file_tactics)
		
		CloseFile(file_tactics)
	End Method
	
	Method save_file(filename:String)
		Local file_tactics:TStream
		
		file_tactics = WriteFile(filename)
		If (Not file_tactics)
			RuntimeError("Cannot save to: " + filename)
		EndIf
		
		''name
		Local s:String = LSet(Self.name, 9)
		For Local i:Int = 0 To 8
			If (i < Len(Self.name))
				WriteByte(file_tactics, Self.name[i])
			Else
				WriteByte(file_tactics, 0)
			EndIf
		Next
		
		''targets
		For Local player:Int = 1 To TEAM_SIZE -1
			For Local ball_zone:Int = 0 To BALL_ZONES -1
				
				''convert from coordinates
				Local x:Int = Self.target[player, ball_zone].x / TACT_DX
				Local y:Int = Self.target[player, ball_zone].y / TACT_DY
				
				''convert to unsigned values
				x = x +7
				y = (y +15)/2
				
				WriteByte(file_tactics, (x Shl 4) +y)
			Next
		Next
		
		''pairs
		For Local i:Int = 1 To TEAM_SIZE -1
			WriteByte(file_tactics, Self.pairs[i])
		Next
		
		''base tactics
		WriteByte(file_tactics, Self.based_on)
		
		CloseFile(file_tactics)
	End Method
	
	Method ToString:String()
		Local s:String
		
		s :+ LSet(Self.name, 8) + " " + Chr(10)
		
		''target positions
		For Local ball_zone:Int = 0 To BALL_ZONES -1
			s :+ Replace(RSet(ball_zone, 2), " ", 0) + ":"
			For Local player:Int = 1 To TEAM_SIZE -1
				
				''convert from coordinates
				Local x:Int = Self.target[player, ball_zone].x / TACT_DX
				Local y:Int = Self.target[player, ball_zone].y / TACT_DY
				
				''convert to unsigned values
				x = x +7
				y = (y +15)/2
				
				s :+ " " + Replace(RSet(x, 2), " ", 0)
				s :+ "-" + Replace(RSet(y, 2), " ", 0)
			Next
			s :+ Chr(10)
		Next
		
		''editor pairs
		s :+ "Pairs:"
		For Local player:Int = 1 To TEAM_SIZE -1
			Local i:Int = Self.pairs[player]
			If (i = 255)
				i = 0
			Else
				i = i+1
			EndIf
			s :+ " " + Replace(RSet(i, 2), " ", 0)
		Next
		s :+ Chr(10)
		
		''base tactics
		s :+ "Based on: " + Self.based_on + Chr(10)
		
		Return s
		
	End Method
	
	Method copy(t:t_tactics)
		Self.name = t.name
		
		''targets
		For Local player:Int = 1 To TEAM_SIZE -1
			For Local ball_zone:Int = 0 To BALL_ZONES -1
				Self.target[player, ball_zone].x = t.target[player, ball_zone].x
				Self.target[player, ball_zone].y = t.target[player, ball_zone].y
			Next
		Next
		
		''pairs
		For Local i:Int = 1 To TEAM_SIZE -1
			Self.pairs[i] = t.pairs[i]
		Next
		
		''base tactics
		Self.based_on = t.based_on
		
	End Method
	
	Method add_delete_pair(p1:Int, p2:Int)
		
		''old pairs
		Local old_pair1:Int = Self.pairs[p1]
		Local old_pair2:Int = Self.pairs[p2]
		
		''delete pair
		If (old_pair1 = old_pair2) And (old_pair1 <> 255)
			Self.pairs[p1] = 255	
			Self.pairs[p2] = 255
			Return
		EndIf
		
		''delete pairs
		For Local i:Int = 1 To TEAM_SIZE -1
			If (Self.pairs[i] = old_pair1)
				Self.pairs[i] = 255
			EndIf
			If (Self.pairs[i] = old_pair2)
				Self.pairs[i] = 255
			EndIf
		Next
		
		''add pair
		Local new_pair_value:Int = 0
		Local found:Int
		Repeat
			found = True
			For Local i:Int = 1 To TEAM_SIZE -1
				If (Self.pairs[i] = new_pair_value)
					found = False
				EndIf
			Next
			If (Not found)
				new_pair_value = new_pair_value +1
			EndIf
		Until (found)
		
		Self.pairs[p1] = new_pair_value
		Self.pairs[p2] = new_pair_value
		
	End Method
	
	Method is_paired:Int(p:Int)
		Return (Self.pairs[p] <> 255)
	End Method
	
	Method get_paired:Int(p:Int)
		Local pair_value:Int = Self.pairs[p]
		If (pair_value = 255)
			RuntimeError("this player is not paired")
		EndIf
		For Local i:Int = 1 To TEAM_SIZE -1
			If (i <> p) And (Self.pairs[i] = pair_value)
				Return i
			EndIf
		Next
		RuntimeError("paired player not found")
	End Method
	
End Type
