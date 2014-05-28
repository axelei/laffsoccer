SuperStrict

'carriage return / line feed
Global CRLF:String = Chr(13) + Chr(10)

'width of unicode characters
Global ucode_10:Int[1536]
Global ucode_14:Int[1536]

'color replacement: crc table and colors vector 
Global crc_table:Int[256]


'TEXT10U	:draw a text with a charset of size 10
'caption	:text		
'x, y		:position
'img_ucode	:charset image
'align		:-1=right, 0=center, 1=left
Function text10u(caption:String, x0:Int, y:Int, img_ucode:TImage, align:Int)

	Local w:Int = ucode_width(caption, 10)
	
	'x position 
	Local x:Int
	Select align
		Case -1
			x = x0 - w
		Case 0
			x = x0 - 0.5*w
		Case 1
			x = x0
	End Select

	For Local i:Int = 0 To caption.length -1

		Local c:Int = Asc( Mid(caption, i+1, 1) )

		'carriage return/line feed
		If (Mid(caption, i+1, 2) = "~r~n")
			text10u(Right(caption, Len(caption)-i-2), x0, y+14, img_ucode, align)
			Exit
		EndIf

		DrawSubImageRect(img_ucode, x, y, 12, 16, 13*(c & $3F), 17*(c Shr 6), 12, 16)

		x = x + ucode_10[c]
	Next

End Function


'TEXT14U	:draw a text with a charset of size 14 
'caption	:text			
'x, y		:position
'img_ucode	:charset image
'align		:-1=right, 0=center, 1=left
Function text14u(caption:String, x:Int, y:Int, img_ucode:TImage, align:Int)

	Local w:Int = ucode_width(caption, 14)
	
	'x position 
	Select align
		Case -1
			x = x - w
		Case 0
			x = x - 0.5*w
		Case 1
			'do nothing
	End Select
	
	For Local i:Int = 0 To caption.length -1

		Local c:Int = Asc( Mid(caption, i+1, 1) )

		DrawSubImageRect(img_ucode, x, y, 16, 22, 16*(c & $3F), 23*(c Shr 6), 16, 22)
		
		x = x + ucode_14[c]
	Next

End Function


Function ucode_width:Int(text:String, charset:Int)
	Local w:Int = 0
	For Local i:Int = 0 To text.length -1
		Local c:Int = Asc( Mid(text, i+1, 1) )
		'carriage return/line feed
		If (Mid(text, i+1, 2) = "~r~n") Then Exit
		Select(charset)
			Case 10
				w = w + ucode_10[c]
			Case 14
				w = w + ucode_14[c]
		End Select
	Next
	Return w
End Function


Function ucode_height:Int(c:Int)
	Local w:Int
	Select(c)
		Case 10
			w = 16
		Case 14
			w = 26
	End Select
	Return w
End Function


'LOAD UNICODE TABLES: size = 10 or 14
Function load_ucode_table(size:Int)

	Local file_in:TStream
	
	file_in = ReadFile("images/ucode_" + size + ".txt")
	If file_in = Null Then RuntimeError("File not found: ucode_" + size + ".txt!")

	Local r:Int, b:Int, c:Int, s:Int, w:Int

	'row
	For r = 0 To 15

		'block
		For b = 0 To 7

			'column
			For c = 0 To 7

				s = ReadByte(file_in)
		
				If s => 48 And s <= 57
					s = s - 48
				Else If s => 65 And s <= 78
					s = s - 55
				Else
					s = 30
				EndIf
					
				Select size
					Case 10
						ucode_10[r*64 + 8*b + c] = s
					Case 14
						ucode_14[r*64 + 8*b + c] = s
				End Select

			Next

			If Eof(file_in) Then Exit

			'skip ':' or CR
			s = ReadByte(file_in)

		Next
			
		'if s = CR then skip LF
		If s = $0D 
			s = ReadByte(file_in)
		EndIf

	Next

End Function



'LIMIT STRING: limit the length of each row to cs characters
Function limit_string:String(s:String,cs:Int)
	
	'position of the last comma
	Local last_comma:Int = 0
	Local row_length:Int = 0

	For Local i:Int = 1 To Len(s)
		row_length = row_length + 1
		Local c:Int = Asc(Mid(s,i,1))
		If Mid(s, i, 2) = CRLF
			i = i + 1
			row_length = 0
		EndIf
		If c = 44 Then last_comma = i
		If row_length > cs
			s = insert_string(CRLF, s, last_comma)
			row_length = 0
		EndIf
	Next
	
	Return s

End Function



'INSERT STRING
Function insert_string:String(in:String, st:String, pos:Int)

	Return Left(st,pos) + in + Right(st,Len(st)-pos)

End Function



'LOAD IMAGE: load image and set transparent color
Function load_image:TImage(dir_name:String, file_name:String, flags:Int = 0, rgb:Int = 0)

	'check if folder exists
	Select FileType(dir_name)
		Case 0
			RuntimeError("Folder not found: " + dir_name)
		Case 1
			RuntimeError("Error: " + dir_name + " is a file and not a folder")
	End Select
	
	Local img_temp:TImage
	
	'check if file exist
	If FileType(dir_name + "/" + file_name) = 1

		'load image
		If rgb <> 0 
			set_mask_color(rgb)
		EndIf

		img_temp = LoadImage(dir_name + "/" + file_name, flags)

	Else
		RuntimeError("File not found: " + dir_name + "/" + file_name)
	EndIf
	
	'check if loaded
	If img_temp = Null 
		RuntimeError("Error while loading: " + dir_name + "/" + file_name)
	EndIf
	
	Return img_temp

End Function



'LOAD PIXMAP: load a pixmap
Function load_pixmap:TPixmap(dir_name:String, file_name:String)

	'check if folder exists
	Select FileType(dir_name)
		Case 0
			RuntimeError("Folder not found: " + dir_name)
		Case 1
			RuntimeError("Error: " + dir_name + " is a file and not a folder")
	End Select
	
	Local temp:TPixmap
	
	'check if file exist
	If FileType(dir_name + "/" + file_name) = 1

		temp = LoadPixmap(dir_name + "/" + file_name)

	Else
		RuntimeError("File not found: " + dir_name + "/" + file_name)
	EndIf
	
	'check if loaded
	If temp = Null 
		RuntimeError("Error while loading: " + dir_name + "/" + file_name)
	EndIf
	
	Return temp

End Function



'COPY IMAGE
Function copy_image:TImage(image:TImage)

	Local x:Int, y:Int, w:Int, h:Int, pix:Int

	w = ImageWidth(image)
	h = ImageHeight(image) 
	
	Local copy:TImage = CreateImage(w,h,1,DYNAMICIMAGE|MASKEDIMAGE)

	Local pixmap_image:TPixmap	= LockImage(image)
	Local pixmap_copy:TPixmap	= LockImage(copy)

	For x = 0 To w-1
		For y = 0 To h-1
			WritePixel(pixmap_copy, x, y, ReadPixel(pixmap_image, x, y))
		Next
	Next		

	UnlockImage(image)
	UnlockImage(copy)

	Return copy

EndFunction	



'IMAGE FROM PIXMAP
Function image_from_pixmap:TImage(pixmap:TPixmap, rgbmask:Int)

	Local x:Int, y:Int, w:Int, h:Int, pix:Int

	w = PixmapWidth(pixmap)
	h = PixmapHeight(pixmap) 
	
	Local image:TImage = CreateImage(w,h,1,MASKEDIMAGE)

	Local copy:TPixmap	= LockImage(image)

	For x = 0 To w-1
		For y = 0 To h-1
			pix = ReadPixel(pixmap, x, y)
			If pix & $FFFFFF = rgbmask
				WritePixel(copy, x, y, rgbmask)
			Else
				WritePixel(copy, x, y, pix)
			EndIf
		Next
	Next		

	UnlockImage(image)

	Return image

EndFunction	



'PIXMAP TO IMAGE
Function pixmap_to_image:TImage(pixmap:TPixmap)

	Local x:Int, y:Int, w:Int, h:Int, pix:Int

	w = PixmapWidth(pixmap)
	h = PixmapHeight(pixmap) 
	
	Local image:TImage = CreateImage(w,h,1,MASKEDIMAGE)

	Local copy:TPixmap	= LockImage(image)

	For x = 0 To w-1
		For y = 0 To h-1
			pix = ReadPixel(pixmap, x, y)
			WritePixel(copy, x, y, pix)
		Next
	Next		

	UnlockImage(image)

	Return image

EndFunction	



'COPY PIXMAP
Function copy_pixmap:TPixmap(pixmap:TPixmap)

	Local x:Int, y:Int, w:Int, h:Int, pix:Int

	w = PixmapWidth(pixmap)
	h = PixmapHeight(pixmap) 
	
	Local copy:TPixmap = CreatePixmap(w,h, PixmapFormat(pixmap))

	For x = 0 To w-1
		For y = 0 To h-1
			WritePixel(copy, x, y, ReadPixel(pixmap, x, y))
		Next
	Next		

	Return copy

EndFunction	


'REPLACE_PIXMAP_COLOR: replace color 'rbg0' with 'rgb1' 
Function replace_pixmap_color(pixmap:TPixmap, argb0:Int, argb1:Int)

	Local x:Int, y:Int, w:Int, h:Int, pix:Int

	w = PixmapWidth(pixmap)
	h = PixmapHeight(pixmap)

	For x = 0 To w-1
		For y = 0 To h-1
			pix = ReadPixel(pixmap, x, y) 
			If pix = argb0
				WritePixel(pixmap, x, y, argb1)' | (pix & $FF000000))
			EndIf
		Next
	Next
	
End Function


'MASK_COLOR: set alpha = 0 for each pixel = color rgb  
Function mask_color(image:TImage, rgb:Int)

	Local x:Int, y:Int, w:Int, h:Int, pix:Int

	Local pixmap:TPixmap = LockImage(image)
	w = PixmapWidth(pixmap)
	h = PixmapHeight(pixmap)

	For x = 0 To w-1
		For y = 0 To h-1
			pix = ReadPixel(pixmap, x, y) 
			If pix & $FFFFFF = rgb
				WritePixel(pixmap, x, y, pix & $FFFFFF)
			EndIf
		Next
	Next
	
	UnlockImage(image)
	
End Function



'IMAGE2SHADOW: create a shadow with the shape of image. mask = mask color, rgb = shadow color
Function image2shadow(image:TImage, rgb:Int)

	Local x:Int, y:Int, w:Int, h:Int, mask:Int, pix:Int

	Local pixmap:TPixmap = LockImage(image)
	
	w = PixmapWidth(pixmap)
	h = PixmapHeight(pixmap)

	mask = ReadPixel(pixmap, 0, 0) & $FFFFFF
	For y = 0 To h-1
		For x = 0 To w-1
			pix = ReadPixel(pixmap, x, y)
			If (pix & $FFFFFF) <> mask
				WritePixel(pixmap, x, y, rgb | (pix & $FF000000))
			EndIf
		Next
	Next

	UnlockImage(image)

End Function 	



'TILE IMAGE: blitz2d like tileimage
Function tile_image(img:TImage)
	SetColor 255,255,255
	TileImage img
End Function	


Function set_color(c:Int, light:Int)
	SetColor(red(c)*light/255.0, green(c)*light/255.0, blue(c)*light/255.0)
End Function 


'SET MASK COLOR: same as SetMaskColor but uses rgb value
Function set_mask_color(c:Int)
	SetMaskColor(red(c),green(c),blue(c))	
End Function



'CRC INIT
Function crc_init(table:Int Ptr)

	Local value:Int

	For Local i:Int = 0 To 255

		value = i

		For Local j:Int = 0 To 7
			If (value & $1)
				value = (value Shr 1) ~ $EDB88320
			Else
				value = value Shr 1
			EndIf
		Next
	
	table[i]=value
	
	Next

End Function



'CRC BANK
Function crc_bank:Int(bank:TBank, offset:Int, size:Int, table:Int Ptr)

	Local b:Int, crc:Int

	crc = $FFFFFFFF

	For Local i:Int = 0 To size-1
		b = PeekByte(bank,offset + i)
		crc = (crc Shr 8) ~ table[b ~ (crc & $FF)]
	Next
	
	Return ~crc

End Function



'RGB: gets rgb color from components
Function rgb:Int(red:Int, green:Int, blue:Int)
    Return blue | (green Shl 8) | (red Shl 16)
End Function



'RED: gets the red value from rgb color
Function red:Int(rgb:Int) 
    Return (rgb Shr 16) & $FF 
End Function 



'GREEN: gets the green value from rgb color
Function green:Int(rgb:Int) 
    Return (rgb Shr 8) & $FF 
End Function 



'BLUE: gets the blue value from rgb color
Function blue:Int(rgb:Int) 
    Return rgb & $FF 
End Function



'ALPHA: gets the alpha value from rgb color
Function alpha:Int(rgb:Int) 
    Return (rgb Shr 24) & $FF 
End Function 


Function rgb_to_xyz:Float[](rgb:Int)
	Local r:Float = red(rgb) / 255.0
	Local g:Float = green(rgb) / 255.0
	Local b:Float = blue(rgb) / 255.0
	
	If (r > 0.04045)
		r = ((r +0.055) / 1.055) ^ 2.4
	Else
		r = r / 12.92
	EndIf
	
	If (g > 0.04045)
		g = ((g +0.055) / 1.055) ^ 2.4
	Else
		g = g / 12.92
	EndIf
	
	If (b > 0.04045)
		b = ((b +0.055) / 1.055) ^ 2.4
	Else
		b = b / 12.92
	EndIf
	
	Local x:Float = 100 * (r * 0.4124 + g * 0.3576 + b * 0.1805)
	Local y:Float = 100 * (r * 0.2126 + g * 0.7152 + b * 0.0722)
	Local z:Float = 100 * (r * 0.0193 + g * 0.1192 + b * 0.9505)
	
	Return [x, y, z]
End Function


Function xyz_to_lab:Float[](xyz:Float[])
	Local x:Float = xyz[0] / 95.047
	Local y:Float = xyz[1] / 100.0
	Local z:Float = xyz[2] / 108.883
	
	If (x > 0.008856)
		x = x ^ (1.0 / 3.0)
	Else
		x = 7.787 * x +4.0 / 29.0
	EndIf
	
	If (y > 0.008856)
		y = y ^ (1.0 / 3.0)
	Else
		y = 7.787 * y +4.0 / 29.0
	EndIf
	
	If (z > 0.008856)
		z = z ^ (1.0 / 3.0)
	Else
		z = 7.787 * z +4.0 / 29.0
	EndIf
	
	Local l:Float = 116.0 * y -16.0
	Local a:Float = 500.0 * (x -y)
	Local b:Float = 200.0 * (y -z)
	
	Return [l, a, b]
End Function


Function lab_difference:Float(lab1:Float[], lab2:Float[])
	Local c1:Float = Sqr(lab1[1] * lab1[1] +lab1[2] * lab1[2])
	Local c2:Float = Sqr(lab2[1] * lab2[1] +lab2[2] * lab2[2])
	
	Local dc:Float = c1 -c2
	Local dl:Float = lab1[0] -lab2[0]
	Local da:Float = lab1[1] -lab2[1]
	Local db:Float = lab1[2] -lab2[2]
	
	Local dh:Float = Sqr(da * da + db * db - dc * dc)
	Local a:Float = dl
	Local b:Float = dc / (1 +0.045 * c1)
	Local c:Float = dh / (1 +0.015 * c1)
	
	Return Sqr(a * a + b * b + c * c)
End Function


Function rgb_difference:Float(rgb1:Int, rgb2:Int)
	Return lab_difference(xyz_to_lab(rgb_to_xyz(rgb1)), xyz_to_lab(rgb_to_xyz(rgb2)))
End Function


Function rgb_to_hsv:Float[](c:Int)
	Local r:Float = red(c) / 255.0
	Local g:Float = green(c) / 255.0
	Local b:Float = blue(c) / 255.0
	
	Local vmin:Float = Min(r, Min(g, b))
	Local vmax:Float = Max(r, Max(g, b))
	Local delta:Float = vmax -vmin
	
	Local h:Float = 0
	Local s:Float = 0
	Local v:Float = 100 * vmax
	
	If (delta > 0)
		s = 100 * delta / vmax
		
		If (r = vmax)
			h = (g -b) / delta
		Else If (g = vmax)
			h = 2 +(b -r) / delta
		Else If (b = vmax)
			h = 4 +(r -g) / delta
		EndIf
		
		h :* 60
		
		If (h < 0)
			h :+ 360
		EndIf
	EndIf
	
	Return [h, s, v]
End Function


Function hsv_to_rgb:Int(hsv:Float[])
	Local h:Float = hsv[0] / 60
	Local s:Float = hsv[1] / 100
	Local v:Float = hsv[2] / 100
	
	Local r:Float
	Local g:Float
	Local b:Float
	
	If (s = 0)
		r = v
		g = v
		b = v
	Else
		If (h = 6)
			h = 0
		EndIf
		
		Local i:Int = Floor(h)
		Local f:Float = h -i
		Local p:Float = v * (1 -s)
		Local q:Float = v * (1 -s * f)
		Local t:Float = v * (1 -s * (1 -f))
		
		Select (i)
			Case 0
				r = v
				g = t
				b = p
			Case 1
				r = q
				g = v
				b = p
			Case 2
				r = p
				g = v
				b = t
			Case 3
				r = p
				g = q
				b = v
			Case 4
				r = t
				g = p
				b = v
			Default
				r = v
				g = p
				b = q
		End Select
		
	EndIf
	
	r :* 255
	g :* 255
	b :* 255
	
	Return rgb(r, g, b)
End Function


Function grayscale:Int(c:Int)
	Local hsv:Float[] = rgb_to_hsv(c)
	hsv[1] = 0
	hsv[2] = 100 * (0.21 * red(c) / 255.0 +0.72 * green(c) / 255.0 +0.07 * blue(c) / 255.0)
	Return hsv_to_rgb(hsv)
End Function


'SHORT2C2: convert a numer stored as short in 2's compliment
Function short2c2:Int(x:Int)
	
	If x => $8000
		Return x - $10000
	Else
		Return x
	EndIf

End Function



'WRITE BANK: write some ascii text into a Unicode bank
Function write_bank(bank:TBank, offset:Int, s:String)

	For Local i:Int = 0 To Len(s)-1
		PokeShort(bank, 2*(offset + i), Asc(Mid(s, i+1 ,1)) )
	Next
	
End Function



'ROWS COUNTER: count rows in a string
Function rows_counter:Int(s:String)

	Local rows:Int = 1
	
	For Local i:Int = 1 To (Len(s) -1)
		'search for LINE FEEDs
		If Mid(s, i, 1) = Chr(10) Then rows = rows + 1 
	Next

	Return rows

End Function



'IS CHAR: return true if value is the ASCII code of a valid input character
Function is_char:Int(a:Int)

	'space
	If a = 32 Return True

	'apostrofo
	If a = 39 Return True

	'minus
	If a = 45 Return True
	
	'dot
	If a = 46 Return True
	
	'lowcase
	If a => 65 And a <= 90 Return True
	
	'upcase
	If a => 97 And a <= 122 Return True
	
	'accented
	If a => 192 And a <= 255 Return True
	
	'numbers
	If a => 48 And a <= 57 Return True

	Return False

End Function



'UPPER CASE
Function upper_case:String(c:Int)
	If c < 192 
		Return Upper(Chr(c))
	Else If c => 224 And c <= 255
		Return Chr(c-32)
	Else 
		Return Chr(c)
	EndIf
End Function



'WAIT NO KEY: wait until no key is pressed
Function wait_nokey()
	Local nokey:Int
	Repeat
		nokey = True
		For Local i:Int = 1 To 255
			If KeyDown(i) = 1 
				nokey = False
			EndIf
		Next
	Until nokey = True
	While (JoyDown(0, 0) Or JoyDown(0, 1)) 
	Wend
End Function


Function megabytes:String(n:Double)
	If n > 1000000
		n = 0.00001*n
		Return Int(0.1*n) + "." + (Int(n) Mod 10) +" MB"
	Else If n > 1000
		Return Int(0.001*n) + " kB"
	Else
		Return n + " B"
	EndIf
End Function


Function fade_rect(x0:Int, y0:Int, x1:Int, y1:Int, alpha:Float, rgb:Int, light:Int)
	
	SetBlend(ALPHABLEND)
	SetAlpha(alpha)
	
	set_color(rgb, light)
	DrawRect(x0, y0, x1 -x0, y1 -y0)
	
	SetBlend(MASKBLEND)
	SetAlpha(1)
	
End Function


'SWEEP COLOR
Function sweep_color:Int(color1:Int, color2:Int)
	Local r1:Int = red(color1)
	Local g1:Int = green(color1)
	Local b1:Int = blue(color1)
	Local r2:Int = red(color2)
	Local g2:Int = green(color2)
	Local b2:Int = blue(color2)

'	Local p:float = Abs(((0.3*Abs(MilliSecs())) Mod 200) - 100)/100.0

	'20-80%
	Local p:Float = (20 + Abs(((0.2*Abs(MilliSecs())) Mod 120) - 60))/100.0

	Local r:Int = r1 +p*(r2-r1)
	Local g:Int = g1 +p*(g2-g1)
	Local b:Int = b1 +p*(b2-b1)

	Return rgb(r,g,b)
End Function


Function Draw_Text(s:String, x:Int, y:Int, a:Int)
	
	Select a
		'right aligned
		Case -1
			DrawText s, x-TextWidth(s), y
		'centered
		Case 0
			DrawText s, x-0.5*TextWidth(s), y
		'left aligned
		Case 1
			DrawText s, x, y
	End Select
	
End Function	


'SOUND_FADESOUND: fade out sound channel from 'volume' to 0 in 'sec' seconds
'Function sound_fadeout(sound_channel:Int, volume:float, sec:Int)
'	For i:float = 1 To 0 Step -0.1
'		ChannelVolume( sound_channel, i * volume)
'		Delay sec*100
'	Next
'End Function


Function draw_border(bx:Int, by:Int, bw:Int, bh:Int, c_top_left_border:Int, c_bottom_right_border:Int, light:Int)

	'top left border
	set_color(c_top_left_border, light)
	DrawLine bx, 		by, 		bx+bw,		by,			False
	DrawLine bx+1, 		by+1, 		bx+bw-1,	by+1,		False
	DrawLine bx,		by+1,		bx,			by+bh,		False
	DrawLine bx+1,		by+2,		bx+1,		by+bh-1,	False

	'bottom right border
	set_color(c_bottom_right_border, light)
	DrawLine bx+bw-2,	by+2,		bx+bw-2,	by+bh-1,	False
	DrawLine bx+bw-1,	by+1,		bx+bw-1,	by+bh,		False
	DrawLine bx+2,		by+bh-2,	bx+bw-2,	by+bh-2,	False
	DrawLine bx+1,		by+bh-1,	bx+bw-1,	by+bh-1,	False
	
	set_color($FFFFFF, light)

End Function


''SWAP PNG PALETTE: swap palette in a png image stored in a bankstream
Function swap_png_palette(bank_stream:TBankStream, bank:TBank, palette_file:String) 

	''--- READ NEW PALETTE ---
	
	Local file_in:TStream = ReadStream(palette_file)
	If (Not file_in)
		RuntimeError("palette not found")
	EndIf
	
	Local line:String
	Local palette:Int[256]
	
	''JASC-PAL
	line = ReadLine(file_in)
	
	''color depth
	line = ReadLine(file_in)
	
	''palette size
	line = ReadLine(file_in)
	
	Local c:Int = Int(line)
	
	''read colors
	Local stop:Int, r:Int, g:Int, b:Int
	For Local i:Int = 0 To c -1
		line = ReadLine(file_in)
	
		''red
		line = Replace(line, " ", "-")
		stop = Instr(line, "-")
		r = Int(Left(line, stop -1))
		line = Right(line, Len(line) -stop)
	
		''green
		stop = Instr(line, "-")
		g = Int(Left(line, stop -1))
		line = Right(line, Len(line) -stop)
	
		''blue
		b = Int(line)
		palette[i] = rgb(r, g, b)
	
	Next
	
	CloseStream(file_in)
		
	
	''--- WRITE NEW PALETTE ---

	''search for the palette chunk
	SeekStream(bank_stream, 0)
	Local i0:Int = ReadByte(bank_stream)
	Local i1:Int = ReadByte(bank_stream)
	Local i2:Int = ReadByte(bank_stream)
	Local i3:Int = ReadByte(bank_stream)

	Local palette_found:Int = False
	
	While Not Eof(bank_stream)

		If ((Chr(i0) +Chr(i1) +Chr(i2) +Chr(i3)) = "PLTE")
			palette_found = True
			Exit
		EndIf

		i0 = i1
		i1 = i2
		i2 = i3
		i3 = ReadByte(bank_stream)
	
	Wend

	If (Not palette_found)
		RuntimeError("Palette not found inside image!")
	EndIf

	Local palette_offset:Int = StreamPos(bank_stream) -8

	''read data size
	SeekStream(bank_stream, palette_offset)
	Local data_size:Int = 0
	For Local i:Int = 0 To 3
		data_size = 256*data_size +ReadByte(bank_stream) 
	Next

	''write new palette
	For Local i:Int = 0 To (data_size/3)-1
		PokeByte(bank, palette_offset +8 +3*i, red(palette[i]))
		PokeByte(bank, palette_offset +8 +3*i +1, green(palette[i]))
		PokeByte(bank, palette_offset +8 +3*i +2, blue(palette[i]))
	Next

	''write new crc
	Local crc32:Int = crc_bank(bank, palette_offset +4, data_size +4, crc_table)
	For Local i:Int = 0 To 3
		PokeByte(bank, palette_offset +8 +data_size +i, (crc32 Shr (8*(3 -i))) & $FF)
	Next

End Function


Function draw_frame(x:Int, y:Int, w:Int, h:Int)
	
	Local r:Int = x + w
	Local b:Int = y + h
	
	''top
	DrawLine(x+5, y,   r-4, y)
	DrawLine(x+3, y+1, r-2, y+1)
	
	''top-left
	DrawLine(x+2, y+2, x+5, y+2)
	DrawLine(x+2, y+2, x+2, y+5)
	Plot(x+3, y+3)
	
	''top-right
	DrawLine(r-4, y+2, r-1, y+2)
	DrawLine(r-1, y+2, r-1, y+5)
	Plot(r-2, y+3)
	
	''left
	DrawLine(x,   y+5, x,   b-4)
	DrawLine(x+1, y+3, x+1, b-2)
	
	''right
	DrawLine(r,   y+3, r,   b-2)
	DrawLine(r+1, y+5, r+1, b-4)
	
	''bottom-left
	DrawLine(x+2, b-4, x+2, b-1)
	DrawLine(x+2, b-1, x+5, b-1)
	Plot(x+3, b-2)
	
	''bottom-right
	DrawLine(r-1, b-4, r-1, b-1)
	DrawLine(r-4, b-1, r-1, b-1)
	Plot(r-2, b-2)
	
	''bottom
	DrawLine(x+3, b,   r-2, b)
	DrawLine(x+5, b+1, r-4, b+1)

End Function
