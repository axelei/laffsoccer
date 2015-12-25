SuperStrict

Import "t_widget.bmx"

Type t_board Extends t_widget

	Field view_opponent:Int
	Field compare_tactics:Int
	Field img_pieces:TImage
	Field img_num:TImage
	Field team_a:t_team
	Field team_b:t_team
	
	Method New()
		Self.image = load_image("images", "board.png")
		Self.w = ImageWidth(Self.image)
		Self.h = ImageHeight(Self.image)
		Self.img_pieces = load_image("images", "pieces.png", MASKEDIMAGE, $00FF00)
		Self.img_num	= load_image("images", "number.png", DYNAMICIMAGE|MASKEDIMAGE, $0000FF)
		
		Self.view_opponent = False
		Self.compare_tactics = False
		Self.team_a = Null
		Self.team_b = Null
	End Method
	
	Method set_compare_tactics(ct:Int)
		Self.compare_tactics = ct
	End Method
	
	Method set_view_opponent(vo:Int)
		Self.view_opponent = vo
	End Method
	
	Method set_teams(a:t_team, b:t_team=Null)
		Self.team_a = a
		Self.team_b = b
	End Method		
	
	Method render()
		If (Self.visible = False) Return
		
		DrawImage(Self.image, Self.x, Self.y)
		
		If (Not Self.compare_tactics) Or (Self.view_opponent)

			Local team_to_show:t_team
			If Not Self.view_opponent
				team_to_show = Self.team_a
			Else
				team_to_show = Self.team_b
			EndIf
			
			Local base_tactics:Int = tactics_array[team_to_show.tactics].based_on
	
			For Local ply:Int = 0 To team_size-1
	
				''name
				''fix x-y position & alignement
				Local tx:Int, ty:Int, align:Int
				Select board_tactics[base_tactics, ply, 0]
					Case +2
						tx = Self.x +10
						align = 1
					Case +1
						tx = Self.x +0.5 * Self.w -7
						align = -1
					Case 0
						tx = Self.x +0.5 * Self.w
						align = 0
					Case -1
						tx = Self.x +0.5 * Self.w +7
						align = 1
					Case -2
						tx = Self.x + Self.w -10
						align = -1
				End Select
				If Not Self.view_opponent
					ty = Self.y +Self.h -14 -18 -28 * board_tactics[base_tactics, ply, 1]
				Else
					ty = Self.y +14 +28 * board_tactics[base_tactics, ply, 1]
				EndIf
				
				Local player:t_player = t_player(team_to_show.players.valueatindex(ply))
	
				''surname (or name)
				Local abbrev:String = ""
	
				If Len(player.surname) > 0
					If Len(player.surname) > 10
						abbrev = Left(player.surname, 8) + "."
					Else
						abbrev = player.surname
					EndIf
				Else
					If Len(player.name) > 10
						abbrev = Left(player.name, 8) + "."
					Else
						abbrev = player.name
					EndIf
				EndIf
				text10u(abbrev, tx, ty, img_ucode10, align)
			Next
	
		Else
			''pieces of both teams
			For Local tm:Int = 0 To 1
	
				Local team_to_show:t_team
				If tm = 0
					team_to_show = Self.team_a
				Else
					team_to_show = Self.team_b
				EndIf
				
				Local base_tactics:Int = tactics_array[team_to_show.tactics].based_on
				
				Local tx:Int, ty:Int
				For Local ply:Int = 0 To team_size-1
					Select board_tactics[base_tactics, ply, 0]
						Case +2
							tx = Self.x +26  
						Case +1
							tx = Self.x +0.5 * Self.w -36 -20
						Case 0
							tx = Self.x +0.5 * Self.w -8
						Case -1
							tx = Self.x +0.5 * Self.w +36
						Case -2
							tx = Self.x + Self.w -26 -20
					End Select
					If tm = Self.view_opponent					
						ty = Self.y + Self.h - 14 -14 -28 * board_tactics[base_tactics, ply, 1]
						DrawSubImageRect(Self.img_pieces, tx, ty, 20, 14, 0, 14*(ply>0), 20, 14)
					Else
						ty = Self.y + 14 + 28 * board_tactics[base_tactics, ply, 1]
						DrawSubImageRect(Self.img_pieces, tx, ty, 20, 14, 20, 14*(ply>0), 20, 14)
					EndIf
						
					Local player:t_player = t_player(team_to_show.players.valueatindex(ply))
	
					Local f0:Int = player.number Mod 10
					Local f1:Int = (player.number - f0) / 10 Mod 10 
				
					Local dx:Int = tx +10
					Local dy:Int = ty +2
				
					Local w0:Int, w1:Int
					If f1 > 0
						w0 = 6 - 2*(f0=1)
						w1 = 6 - 2*(f1=1)
						dx = dx - (w0 + 2 + w1)/2
						DrawSubImageRect(img_num, dx, dy, 8, 10, f1 * 8, 0, 8, 10)
						dx = dx + w1 + 2
						DrawSubImageRect(img_num, dx, dy, 8, 10, f0 * 8, 0, 8, 10)
					Else
						w0 = 6
						DrawSubImageRect(img_num, dx-w0/2, dy, 8, 10, f0 * 8, 0, 8, 10)
					EndIf
				Next
			Next
		EndIf
	End Method
End Type

