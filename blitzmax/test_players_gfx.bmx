SuperStrict

Import "common.bmx"

AppTitle = "YSoccer 2014 - Graphics test"


Local color_name:String[23]

color_name[0] = "ligth gray"
color_name[1] = "white"
color_name[2] = "black"
color_name[3] = "orange"
color_name[4] = "red"
color_name[5] = "blue"
color_name[6] = "brown"
color_name[7] = "light blue"
color_name[8] = "green"
color_name[9] = "yellow"
color_name[10] = "violet"
color_name[11] = "pink"
color_name[12] = "kombat blue"
color_name[13] = "steel"
color_name[14] = "mil. green"
color_name[15] = "dark blue"
color_name[16] = "garnet red"
color_name[17] = "light violet"
color_name[18] = "sky blue"
color_name[19] = "bright green"
color_name[20] = "dark yellow"
color_name[21] = "dark gray"
color_name[22] = "gold"


Local img_grass:TImage
Local timer:TTImer
Local gfx_menu:String[20,3]


SeedRnd MilliSecs()
crc_init(crc_table)


Local screen_w:Int = 1024
Local screen_h:Int = 768
If GraphicsModeExists(screen_w, screen_h)
	Graphics screen_w, screen_h, 0
Else
	RuntimeError "Graphic mode: "+screen_w+"x"+screen_h+" not available!"
EndIf


ball:t_ball = New t_ball
game_settings = New t_game_settings
game_settings.zoom = 1.0
location_settings = New t_location_settings
location_settings.time =  TI.NIGHT
location_settings.setup()


''input devices
input_devices = New t_input_devices

''keyboard
For Local i:Int = 0 To 1
	Local d:t_input = New t_keyboard
	d.set_port(i)
	input_devices.AddLast(d)
Next

''joysticks
joysticks = JoyCount()
For Local i:Int = 0 To joysticks -1
	Local d:t_input = New t_joystick
	d.set_port(i)
	input_devices.AddLast(d)
Next

Local menu_input:t_menu_input = New t_menu_input


''--- load and set graphics ---

Local dir:String = "images"

''grass
img_grass = load_image(dir, "grass.png", DYNAMICIMAGE, $0000FF) 
replace_image_color(img_grass, $FF4524FF, $FF609000)
replace_image_color(img_grass, $FF6C53FF, $FF709000)
replace_image_color(img_grass, $FF9A88FF, $FF909000)

''players	
Local rgb_pairs:t_color_replacement_list = New t_color_replacement_list
rgb_pairs.add($404040, location_settings.grass.light_shadow)
For Local i:Int = 0 To 3
	img_player_shadows[i] = load_and_edit_png("images/player/shadows/player_" + i + ".png", rgb_pairs, MASKEDIMAGE, $00F700)
	img_keeper_shadows[i] = load_and_edit_png("images/player/shadows/keeper_" + i + ".png", rgb_pairs, MASKEDIMAGE, $00F700)
Next


''menu items
gfx_menu[1,1] = "Time"
gfx_menu[2,1] = "Role"
gfx_menu[3,1] = "Style"
gfx_menu[4,1] = "1st col"
gfx_menu[5,1] = "2nd col"
gfx_menu[6,1] = "Short"
gfx_menu[7,1] = "Socks"
gfx_menu[8,1] = "Hair type"
gfx_menu[9,1] = "Hair color"
gfx_menu[10,1] = "Skin color"
gfx_menu[11,1] = "Column"
gfx_menu[12,1] = "Row"
gfx_menu[13,1] = "Anim."
gfx_menu[14,1] = "Anim.length"
gfx_menu[15,1] = "Anim.speed"


Local item:Int = 1

timer =	CreateTimer(refrate)
light = 0

Local animation:Int = True
Local anim_len:Int = 8
Local anim_spd:Int = 3
Local fmx:Int = 0
Local fmy:Int = 0
Local cursor_y:Int = fmy
Local fmx2:Int = 0
Local fmy2:Int = 0
Local frames:Int = 0
Local player_changed:Int = True


''kit
Local kit:t_kit = New t_kit
kit.style	= 2
kit.shirt1	= 5
kit.shirt2	= 2
kit.shorts	= 1
kit.socks	= 2

''team
Local tm:t_team = New t_team
ListAddLast(tm.kits, kit)
tm.kit_count = 1
tm.kit = 0

''player
Local player:t_player = New t_player
player.hair_type = 9
player.role = 1
player.team = tm

Local player_rows:Int[2]
player_rows[0] = 19
player_rows[1] = 16

Const displayed_rows:Int = 14

Local quit:Int = False
While (Not quit)

	frames = WaitTimer(timer)
		
	set_color($FFFFFF, light)
	TileImage img_grass
	
	''--- set menu values ---
	
	''time
	Select location_settings.time
		Case TI.DAY
			gfx_menu[1,2] = "day"
		Case TI.NIGHT
			gfx_menu[1,2] = "night"
	End Select
	
	''role
	Select player.role
		Case 0
			gfx_menu[2,2] = "keeper"
		Case 1
			gfx_menu[2,2] = "non-keeper"
	End Select
	
	''style
	Local kit:t_kit = player.team.kit_at_index(player.team.kit)
	Local name:String
	Select kit.style
		Case 0
			name = "plain"
		Case 1
			name = "col.sleeves"
		Case 2
			name = "vertical"
		Case 3
			name = "horizontal"
		Case 4
			name = "check"
		Case 5
			name = "vert.halves"	
		Case 6
			name = "strip"
		Case 7
			name = "spice"
		Case 8
			name = "armband"
		Case 9
			name = "large strips"
		Case 10
			name = "diagonal"
		Case 11
			name = "band"
		Case 12
			name = "line"
		Case 13
			name = "two_stripes"
		Case 14
			name = "double_stripe"
		Case 15
			name = "big_check"
		Case 16
			name = "big_v"
		Case 17
			name = "cross"
		Case 18
			name = "diagonal_half"
		Case 19
			name = "vert_strip"
		Case 20
			name = "side_lines"
		Default
			name = "unknown"
	End Select
	gfx_menu[3,2] = kit.style + " - " +name
	
	''1st col
	Local cl:Int
	cl = kit.shirt1
	gfx_menu[4,2] = cl + " - " + color_name[cl]
		
	''2nd col
	cl = kit.shirt2
	gfx_menu[5,2] = cl + " - " + color_name[cl]

	''short
	cl = kit.shorts
	gfx_menu[6,2] = cl + " - " + color_name[cl]
	
	''socks
	cl = kit.socks
	gfx_menu[7,2] = cl + " - " + color_name[cl]

	''hair type
	Select player.hair_type
		Case 0
			name = "101 'pelato"
		Case 1
			name = "102 'pelato con pizzetto"
		Case 2
			name = "201 'rasato"
		Case 3
			name = "301 'calvizia estesa"
		Case 4
			name = "302 'chierica"
		Case 5
			name = "303 'stempiato"
		Case 6
			name = "401 'taglio medio"
		Case 7
			name = "402 'dritti"
		Case 8
			name = "403 'spazzola"
		Case 9
			name = "501 'lisci folti (Cocu)"
		Case 10
			name = "502 'lisci riga in mezzo"
		Case 11
			name = "503 'lisci riga in mezzo con sfumatura posteriore"
		Case 12
			name = "504 'lisci indietro"
		Case 13
			name = "505 'lisci allungati all'indietro"
		Case 14
			name = "506 'lisci allungati con ciocche"
		Case 15
			name = "507 'lisci allungati con elastico"
		Case 16
			name = "508 'lisci lunghi pettinati"
		Case 17
			name = "509 'lisci lunghi con elastico"
		Case 18
			name = "510 'lisci lunghi con elastico nero"
		Case 19
			name = "511 'lisci lunghi con elastico colorato"
		Case 20
			name = "512 'lisci raccolti all'indietro con codino"
		Case 21
			name = "513 'lisci raccolti con codino alto"
		Case 22
			name = "514 'lisci corti con cerchietto"
		Case 23
			name = "515 'lisci lunghi con cerchietto"
		Case 24
			name = "516 'lisci gonfi"
		Case 25
			name = "601 'ricci medi"
		Case 26
			name = "602 'ricci allungati"
		Case 27
			name = "603 'ricci lunghi"
		Case 28
			name = "604 'ricci lunghi con elastico"
		Case 29
			name = "605 'ricci lunghi con cerchietto"
		Case 30
			name = "606 'ricci raccolti con codino"
		Case 31
			name = "701 'treccine corte su rasato"
		Case 32
			name = "702 'treccioni su rasato"
		Case 33
			name = "703 'trecce lunghe"
		Case 34
			name = "801 'cresta"
		Case 35
			name = "802 'nazi"
		Case 36
			name = "803 'mohicano"
		Case 37
			name = "804 'West style"
		Case 38
			name = "805 'Davids style"
		Case 39
			name = "806 'barbone"
		Case 40
			name = "807 'Valderrama style"
		Case 41
			name = "808 'Divin Codino"
		Default
			name = "unknown"
	End Select
	gfx_menu[8,2] = name

	''hair color
	Select player.hair_color
		Case 0
			name = "black"
		Case 1
			name = "blond"
		Case 2
			name = "brown"
		Case 3
			name = "red"
		Case 4
			name = "platinum"
		Case 5
			name = "gray"
		Case 6
			name = "white"
		Case 7
			name = "punk1"
		Case 8
			name = "punk2"
		Case 9
			name = "unknown"
	End Select
	gfx_menu[9, 2] = player.hair_color + " - " + name

	''skin color
	Select player.skin_color
		Case 0
			name = "pink"
		Case 1
			name = "black"
		Case 2
			name = "pale"
		Case 3
			name = "asiatic"
		Case 4
			name = "arab"
		Case 5
			name = "mulatto"
		Case 6
			name = "red"
		Case 7
			name = "free"
		Case 8
			name = "alien"
		Case 9
			name = "yoda"
		Default
			name = "unknown"
	End Select
	gfx_menu[10, 2] = player.skin_color + " - " + name

	''column
	gfx_menu[11,2] = fmx
	''row
	gfx_menu[12,2] = fmy

	''animation
	Select animation 
		Case 0
			gfx_menu[13,2] = "off"
		Case 1
			gfx_menu[13,2] = "horiz."
		Case 2
			gfx_menu[13,2] = "vertical"
	End Select

	''animation length 
	gfx_menu[14,2] = anim_len

	''animation length 
	gfx_menu[15,2] = anim_spd
	
	''draw menu
	For Local i:Int = 1 To 15
			If (i <= 7)
				set_color($00DC00, light)
			Else If (i <= 10)
				set_color($3033C1, light)
			Else
				set_color($E8EF2F, light)
			EndIf
		Textsh(25, 50 + 2.6 * TextHeight("Pp") * (i-1), gfx_menu[i,1], 1, 0, kit_color[2,0])

		If (i = item)
			Set_color($00FFFF, light)
		Else
		set_color($FCFCFC, light)
		EndIf
		Textsh(45, 70 + 2.6 * TextHeight("Pp") * (i-1), gfx_menu[i,2], 1, 0, kit_color[2,0])
	Next		

	set_color($FFFFFF, light)
	
	Local x0:Int = 300
	Local y0:Int = 30

	For Local c:Int = 0 To 7
		For Local r:Int = 0 To displayed_rows-1
			player.data[0].x = x0 +24 +50*c
			player.data[0].y = y0 +36 +50*r
			player.data[0].fmx = c
			player.data[0].fmy = r + fmy - cursor_y
			player.data[0].is_visible = True
			player.draw_shadow(0)
			player.draw(0)
		Next
	Next
	
	set_color($000000, light)
	DrawLine x0-21,		y0,		x0-21,		y0 +50*14
	DrawLine x0-20,		y0 +50.0 * displayed_rows * (fmy-cursor_y)/player_rows[player.role],	x0-20,	y0 +50.0 * displayed_rows * (fmy +displayed_rows -cursor_y)/player_rows[player.role]

	set_color($FCFC00, light)
	Select animation
		Case 0
			DrawLine x0-1 +50*fmx,			y0 -1 +50*cursor_y,			x0-1 +50*(fmx+1),		y0 -1 +50*cursor_y
			DrawLine x0-1 +50*fmx,			y0 -1 +50*cursor_y +50,		x0-1 +50*(fmx+1),		y0 -1 +50*cursor_y +50
			DrawLine x0-1 +50*fmx,			y0 -1 +50*cursor_y,			x0-1 +50*fmx,			y0 -1 +50*cursor_y +50
			DrawLine x0-1 +50*(fmx+1),		y0 -1 +50*cursor_y,			x0-1 +50*(fmx+1),		y0 -1 +50*cursor_y +50
		Case 1
			For Local i:Int = 0 To anim_len -1
				Local x:Int = (fmx + i) Mod 8
				DrawText i, x0-1 +50*(x), y0 -1 +50*cursor_y
				DrawLine x0-1 +50*(x), 		y0 -1 +50*cursor_y,			x0-1 +50*(x+1), 		y0 -1 +50*cursor_y
				DrawLine x0-1 +50*(x), 		y0 -1 +50*cursor_y +50,		x0-1 +50*(x+1),			y0 -1 +50*cursor_y +50
				DrawLine x0-1 +50*(x),		y0 -1 +50*cursor_y,			x0-1 +50*(x),			y0 -1 +50*cursor_y +50
				DrawLine x0-1 +50*(x+1),	y0 -1 +50*cursor_y,			x0-1 +50*(x+1),			y0 -1 +50*cursor_y +50
			Next
		Case 2
			For Local i:Int = 0 To anim_len -1
				Local y:Int = (cursor_y +i) Mod (player_rows[player.role])
				DrawText i, x0-1 +50*fmx, y0 -1 +50*(y)
				DrawLine x0-1 +50*fmx,		y0 -1 +50*(y),				x0-1 +50*(fmx+1),		y0 -1 +50*(y)
				DrawLine x0-1 +50*fmx,		y0 -1 +50*(y+1),			x0-1 +50*(fmx+1),		y0 -1 +50*(y+1)
				DrawLine x0-1 +50*fmx,		y0 -1 +50*(y),				x0-1 +50*fmx,			y0 -1 +50*(y+1)
				DrawLine x0-1 +50*(fmx+1),	y0 -1 +50*(y),				x0-1 +50*(fmx+1),		y0 -1 +50*(y+1)
			Next
	End Select
		
	frame = frame + 1
	
	light = slide(light, 0, 256, 16)
	
	''Giocatore Ingrandito	
	set_color($FFFFFF, light)
	Select animation
		Case 0
			fmx2 = 0
			fmy2 = 0
			
		Case 1 ''horizontal
			If (frame Mod (6-anim_spd) = 0)
				fmx2 = rotate(fmx2, 0, 5*anim_len -1, 1)
			EndIf
			fmy2 = 0
			
		Case 2 ''vertical
			fmx2 = 0
			If (frame Mod (6-anim_spd) = 0)
				fmy2 = rotate(fmy2, 0, 5*anim_len -1, 1)
			EndIf
			
	End Select
	
	For Local e:Float = 1.0 To 3.0
		Local m:Float = 2^e
		If (GetGraphicsDriver() = GLMax2DDriver())
			glMatrixMode(GL_PROJECTION)
			glLoadIdentity()
			gluOrtho2D(0, screen_w/m, screen_h/m, 0)
		EndIf
		player.data[0].x = 850/m
		player.data[0].y = 50 +10*e
		player.data[0].z = 0
		player.data[0].fmx = (fmx + Floor(fmx2/5.0)) Mod 8
		player.data[0].fmy = (fmy + Floor(fmy2/5.0)) Mod player_rows[player.role]
		player.draw_shadow(0)
		player.draw(0)
		DrawLine player.data[0].x -6, player.data[0].y -25, player.data[0].x +6, player.data[0].y -25
		DrawLine player.data[0].x, player.data[0].y -30, player.data[0].x, player.data[0].y
		DrawLine player.data[0].x -12, player.data[0].y, player.data[0].x +12, player.data[0].y
		DrawLine player.data[0].x -18, player.data[0].y -18, player.data[0].x +18, player.data[0].y +18
		DrawLine player.data[0].x -18, player.data[0].y +18, player.data[0].x +18, player.data[0].y -18
	Next
	If (GetGraphicsDriver() = GLMax2DDriver())
		glMatrixMode(GL_PROJECTION)
		glLoadIdentity()
		gluOrtho2D(0, screen_w, screen_h, 0)
	EndIf
	
	
	menu_input.x = 0
	menu_input.y = 0
	menu_input.fa = 0
	menu_input.fb = 0
	For Local i:t_input = EachIn input_devices
		i.read_input()
		
		''x movement
		Local x:Int = i.get_x()
		If (x <> 0)
			menu_input.x = x
		EndIf
		
		''y movement
		Local y:Int = i.get_y()
		If (y <> 0)
			menu_input.y = y
		EndIf
		
		''fire 1
		Local f1:Int = i.get_fire1()
		If (f1 <> 0)
			menu_input.fa = 1
		EndIf
		
		''fire 2
		Local f2:Int = i.get_fire2()
		If (f2 <> 0)
			menu_input.fb = 1
		EndIf
	Next
	
	
	If (menu_input.x <> 0 And menu_input.tx = 0)
		If (menu_input.fb)
			fmx = rotate(fmx, 0, 7, menu_input.x)
		Else
			Select item
				Case 1
					location_settings.time = rotate(location_settings.time, TI.DAY, TI.NIGHT, menu_input.x)
				Case 2
					player.role = rotate(player.role, 0, 1, menu_input.x)
					fmy = Min(fmy, player_rows[player.role]-1)
					player_changed = True
				Case 3
					kit.style = rotate(kit.style, 0, 20, menu_input.x)
					player_changed = True
				Case 4
					kit.shirt1 = rotate(kit.shirt1, 0, 22, menu_input.x)
					player_changed = True
				Case 5
					kit.shirt2 = rotate(kit.shirt2, 0, 22, menu_input.x)
					player_changed = True
				Case 6
					kit.shorts = rotate(kit.shorts, 0, 22, menu_input.x)
					player_changed = True
				Case 7
					kit.socks = rotate(kit.socks, 0, 22, menu_input.x)
					player_changed = True
				Case 8
					player.hair_type = rotate(player.hair_type, 0, 41, menu_input.x)
					player_changed = True
				Case 9
					player.hair_color = rotate(player.hair_color, 0, 8, menu_input.x)
					player_changed = True
				Case 10
					player.skin_color = rotate(player.skin_color, 0, 9, menu_input.x)
					player_changed = True
				Case 11
					fmx = rotate(fmx, 0, 7, menu_input.x)
				Case 12
					cursor_y = slide(cursor_y, 0, displayed_rows-1, menu_input.x)
					fmy = slide(fmy, 0, player_rows[player.role]-1, menu_input.x)
				Case 13
					animation = rotate(animation, 0, 2, menu_input.x)
				Case 14
					anim_len = slide(anim_len, 2, 8, menu_input.x)
				Case 15
					anim_spd = slide(anim_spd, 1, 5, menu_input.x)
				Default
					RuntimeError "undefined menu item"
			End Select
		EndIf
	EndIf

	''quit
	If (AppTerminate() Or KeyHit(KEY_ESCAPE))
		quit = True
	EndIf
	
	''render 
	If (player_changed)
		For Local sk:Int = 0 To 9
			img_player[0, sk] = Null
			img_keeper[0, sk] = Null
		Next

		player.load_image_by_skin()
		player.load_hair()
		player_changed = False 
	EndIf

	''item cycle
	If (menu_input.y <> 0 And menu_input.ty = 0)
		If (menu_input.fb)
			cursor_y = slide(cursor_y, 0, displayed_rows-1, menu_input.y)
			fmy = slide(fmy, 0, player_rows[player.role]-1, menu_input.y)
		Else
			item = rotate(item, 1, 15, menu_input.y)
		EndIf
	EndIf
	
	''x-y delays
	If (menu_input.x <> 0)
		If (menu_input.x0 = 0)
			menu_input.tx = 9
	Else If (menu_input.tx = 0)
			menu_input.tx = 4
		EndIf
	Else
		menu_input.tx = 0
	EndIf
	If (menu_input.y <> 0)
		If (menu_input.y0 = 0)
			menu_input.ty = 9
		Else If (menu_input.ty = 0)
			menu_input.ty = 4
		EndIf
	Else
		menu_input.ty = 0
	EndIf

	If (menu_input.tx > 0)
		menu_input.tx = menu_input.tx - 1
	EndIf
	If (menu_input.ty > 0)
		menu_input.ty = menu_input.ty - 1
	EndIf
	
	menu_input.x0 = menu_input.x
	menu_input.y0 = menu_input.y
	
	Flip
	
Wend

End

''TEXTSH: Draw text with shadow - c = shadow color
Function textsh(x:Int, y:Int, s$, cx:Int, cy:Int, c:Int)
	Local r0:Int, g0:Int, b0:Int
	GetColor(r0, g0, b0)
	set_color(c, light)
	Select cx
		Case 0
			DrawText s, x+1-0.5*TextWidth(s), y+1', s, cx, cy
		Case 1
			DrawText s, x+1, y+1
	End Select
	SetColor r0,g0,b0
	Select cx
		Case 0
			DrawText s, x-0.5*TextWidth(s),	 y',   s, cx, cy
		Case 1
			DrawText s, x,	 y',   s, cx, cy
	End Select
End Function

