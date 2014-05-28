SuperStrict

Import "common.bmx"


''--- GLOBALS ---

''list of teams for a friendly or a competition
Global team_list:TList


''--- FUNCTIONS ---

Function backup_file(input_file:String, output_file:String)
	
	Local file_in:TStream
	Local file_out:TStream
	
	file_in = ReadFile(input_file)
	If (file_in = Null)
		RuntimeError("Cannot read:" + input_file)
	EndIf
	
	file_out = WriteFile(output_file)
	If (file_out = Null)
		RuntimeError("Cannot save:" + output_file)
	EndIf
	
	
	Local ln:String
	While Not(Eof(file_in)) 
	
		ln = ReadLine(file_in)
		WriteLine(file_out, ln)
	
	Wend
	
	CloseFile(file_in)
	CloseFile(file_out)
	
End Function


Function sort_by_names(list:TList)
	Local sorted:Int
	Local link_1:TLink
	Local link_2:TLink
	Local item_1:t_team
	Local item_2:t_team
	
	sorted = False
	While (Not sorted)
		sorted = True
		link_1 = list.firstlink()
		For Local i:Int = 1 To list.count() -1
			item_1 = t_team(link_1.value())
			link_2 = link_1.nextlink()
			item_2 = t_team(link_2.value())
			If (item_1.name > item_2.name)
				list.insertafterlink(item_1, link_2)
				RemoveLink(link_1)
				link_1 = link_2.nextlink()
				sorted = False
			Else
				link_1 = link_2
			EndIf
		Next
	Wend
	
End Function


Function compare_teams_by_stats:Int(o1:Object, o2:Object)
	''by points
	If (t_team(o1).points <> t_team(o2).points)
		Return t_team(o2).points -t_team(o1).points
	EndIf
	
	''by goals diff
	Local diff1:Int = t_team(o1).goals_for -t_team(o1).goals_against
	Local diff2:Int = t_team(o2).goals_for -t_team(o2).goals_against
	If (diff1 <> diff2)
		Return diff2 - diff1
	EndIf
	
	''by scored goals
	If (t_team(o1).goals_for <> t_team(o2).goals_for)
		Return t_team(o2).goals_for -t_team(o1).goals_for
	EndIf
	
	''by names
	If (t_team(o1).name < t_team(o2).name)
		Return -1
	Else
		Return t_team(o1).name > t_team(o2).name
	EndIf
End Function


''INLIST TEAM: verify if a team has been selected
Function inlist_team:Int(ext:String, number:Int)
	For Local team:t_team = EachIn team_list
		If (team.ext = ext)
			If (team.number = number)
				Return team.control
			EndIf
		EndIf
	Next
	Return CM_UNDEFINED
End Function


''DELCHG TEAM: delete a team from the team list / change control type
Function delchg_team:Int(ext:String, number:Int)
	For Local team:t_team = EachIn team_list
		If (team.ext = ext)
			If (team.number = number)
				Select (team.control)
					''computer > player/coach
					Case CM_COMPUTER
						team.control = CM_PLAYER
						Return CM_PLAYER

					''player/coach > coach
					Case CM_PLAYER
						team.control = CM_COACH
						Return CM_COACH

					''coach > delete
					Case CM_COACH
						ListRemove(team_list, team)
						Return 0
				End Select
			EndIf
		EndIf
	Next
	Return CM_UNDEFINED
End Function


''ADD TEAM: add a team to the team list
Function add_team:Int(name:String, ext:String, number:Int)
	If (inlist_team(ext, number) > 0)
		Return delchg_team(ext, number)
	EndIf
	Local team:t_team  = New t_team
	team.name    = name
	team.ext     = ext
	team.number  = number
	team.control = CM_COMPUTER
	ListAddLast team_list, team
	Return CM_COMPUTER
End Function


Function generate_score:Int(team_a:t_team, team_b:t_team, extra_time_result:Int = False)
	
	Local factor:Float = (team_a.offense_rating() -team_b.defense_rating() +300)/60.0
	
	Local a:Int, b:Int
	For Local goals:Int = 0 To 6
		a = goals_probability[Floor(factor), goals]
		b = goals_probability[Ceil(factor), goals]
		goals_probability[11, goals] = round(a + (b-a)*(factor-Floor(factor)))
		goals_probability[11, 6] = 1000
		For Local goals:Int = 0 To 5
			goals_probability[11, 6] = goals_probability[11, 6] - goals_probability[11, goals]
		Next
''		Print "prob. " + goals + " goal = " + prob[11,goals] + "/1000"
	Next
	
	Local r:Int = Rand(1,1000)
	Local sum:Int = 0
	Local goals:Int = -1
	While (sum < r)
		goals = goals +1
		sum = sum +goals_probability[11, goals]
	Wend
	
	If (extra_time_result)
		Return Floor(goals/3)
	Else
		Return goals
	EndIf
	
End Function


''RANK MATCH: returns an integer from 0 to 9
Function rank_match:Float(team_a:t_team, team_b:t_team)
	Return Floor((team_a.ranking() + 2 * team_b.ranking()) / 3.0)
End Function 


Function load_calendars()
	
	Local file_in:TStream
	Local file_name:String = "data/calendars/calendars.bin"
	
	file_in = ReadStream(file_name)
	If (file_in = Null)
		RuntimeError(" file not found: " + file_name)
	EndIf
	
	For Local i:Int = 0 To calendar.length -1
		calendar[i] = ReadByte(file_in)
	Next
	
	CloseStream(file_in)
	
End Function


Function read_mouse()
	mouse.button1 = mouse.button10
	mouse.button2 = mouse.button20
	mouse.button10 = MouseDown(1)
	mouse.button20 = MouseDown(2)
	
	mouse.x = mouse.x0
	mouse.y = mouse.y0
	mouse.x0 = MouseX()
	mouse.y0 = MouseY()
	mouse.moved = (mouse.x<>mouse.x0)Or(mouse.y<>mouse.y0)
End Function


Function draw_bar(perc_length:Float)
	Local center_x:Int = 0.5 * game_settings.screen_width
	Local center_y:Int = 0.5 * game_settings.screen_height
	Local radius1:Int = 40
	Local radius2:Int = 5
	
	For Local i:Int = 0 To 23
		Local a:Int = 15*i
		If (perc_length < 4.166*(i+1))
			SetColor(32, 112, 32)
		Else
			SetColor(64, 224, 64)
		EndIf
		DrawOval(round(center_x + radius1 * Cos(a)), round(center_y + radius1 * Sin(a)), radius2, radius2)
	Next
	
	FlushMem()
	Flip
End Function


Function record_action()
	
	''max HL_MAXNUMBER actions, if more then the oldest ones are overwritten
	Local offset:Int = (hl_recorded Mod HL_MAXNUMBER) * HL_BANKSIZE
	
	For Local i:Int = 1 To 2 * VFRAMES
		
		''ball
		PokeShort(bnk_highlights, offset, ball.data[frame].x)	; offset = offset + 2
		PokeShort(bnk_highlights, offset, ball.data[frame].y)	; offset = offset + 2
		PokeShort(bnk_highlights, offset, ball.data[frame].z)	; offset = offset + 2
		PokeByte(bnk_highlights, offset, ball.data[frame].fmx)	; offset = offset + 1
		
		''players
		For Local t:Int = HOME To AWAY
			For Local i:Int = 0 To TEAM_SIZE -1
				Local ply:t_player = team[t].player_at_index(i)
				PokeShort(bnk_highlights, offset, ply.data[frame].x)	; offset = offset + 2
				PokeShort(bnk_highlights, offset, ply.data[frame].y)	; offset = offset + 2
				PokeByte(bnk_highlights, offset, ply.data[frame].fmx)	; offset = offset + 1
				PokeByte(bnk_highlights, offset, ply.data[frame].fmy)	; offset = offset + 1
				PokeByte(bnk_highlights, offset, ply.data[frame].is_visible)	; offset = offset + 1
			Next
		Next
		
		''camera
		PokeShort(bnk_highlights, offset, vcamera_x[frame]) ; offset = offset + 2
		PokeShort(bnk_highlights, offset, vcamera_y[frame]) ; offset = offset + 2
		
		frame = (frame + SUBFRAMES/2) Mod VSIZE
		
	Next
	
	hl_recorded = hl_recorded + 1
	
End Function


Function compare_sprites_by_y:Int(o1:Object, o2:Object)
	Return t_sprite(o1).get_y(frame) -t_sprite(o2).get_y(frame)
End Function


Function compare_graphics_modes_by_dimensions:Int(o1:Object, o2:Object)
	
	''by width
	If (TGraphicsMode(o1).width <> TGraphicsMode(o2).width)
		Return TGraphicsMode(o1).width -TGraphicsMode(o2).width
	EndIf
	
	''by height
	Return TGraphicsMode(o1).height -TGraphicsMode(o2).height
	
End Function
