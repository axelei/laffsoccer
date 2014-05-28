SuperStrict

''rounds float to the nearest integer
Function round:Int(f:Float)
	Return Sgn(f)*Int(Abs(f) +0.5)
End Function

''distance between two 2d point
Function dist:Float(x1:Float, y1:Float, x2:Float, y2:Float)
	Return hypo(x2-x1, y2-y1)
End Function

''hypotenuse
Function hypo:Float(diff_x:Float, diff_y:Float)
	Return Sqr( diff_x * diff_x + diff_y * diff_y )
End Function

''checks if x is inside a-b interval
Function is_in:Int(x:Int, a:Int, b:Int)
	If (b > a)
		Return (a <= x) And (x <= b)
	Else 
		Return (b <= x) And (x <= a)
	EndIf
End Function

Function bound:Int(value:Int, value_min:Int, value_max:Int)
	Return Min(Max(value, value_min), value_max)
End Function

''rotates value inside low-high interval, direction is usually +1 or -1
Function rotate:Int(value:Int, low:Int, high:Int, direction:Int)
	Return ((value -low + (high-low+1) + direction) Mod (high-low+1)) +low
End Function

''slides value inside low-high interval, direction is usually +1 or -1
Function slide:Int(value:Int, low:Int, high:Int, direction:Int)
	Return Min(Max(value + direction, low), high)
End Function

''shuffles a generic list
Function shuffle_list(list:TList)

	For Local i:Int = 1 To list.count()

		Local pos_1:Int = Rand(0, list.count()-1)
		Local pos_2:Int = Rand(0, list.count()-1)

		swap_elements(list, pos_1, pos_2)

	Next
								
End Function

''swap two generic objects in a generic list
Function list_swap_objects(list:TList, o1:Object, o2:Object)
	
	Local link1:TLink = list.FindLink(o1)
	Local link2:TLink = list.FindLink(o2)
	
	Local objtmp:Object = link2._value
	link2._value = link1._value
	link1._value = objtmp
	
End Function

''swap elements at n1, n2 position in a generic list
Function swap_elements(list:TList, n1:Int, n2:Int)

	Local link1:TLink
	Local link2:TLink
	Local objtmp:Object

	link1 = list_nth(list, n1)
	link2 = list_nth(list, n2)

	objtmp = link2._value
	link2._value = link1._value
	link1._value = objtmp

End Function

''returns the n-th element of a list
Function list_nth:TLink(list:TList, n:Int)
	Local link:TLink
	link = list.firstlink()
	For Local i:Int = 0 To list.count()-1
		If i = n Then Return link
		link = link.nextlink()
	Next
	RuntimeError("element not found!")
End Function

''returns the first word in the sentence
Function first_word:String(sentence:String)
	Local p:Int = sentence.find(" ")
	If (p = -1)
		Return sentence
	Else 
		Return Left(sentence, p)
	EndIf
End Function

''truncate string s at max_length
Function truncate:String(s:String, max_length:Int, fill:String="")
	If (Len(s) > max_length)
		Return Left(s, max_length -Len(fill)) +fill
	Else
		Return s
	EndIf
End Function


''draws image at position x,y
Function draw_image(image:TImage, x:Int, y:Int)
	If image = Null Return;
	Local w:Int = ImageWidth(image)
	Local h:Int = ImageHeight(image)
	
	Local ox:Float, oy:Float
	GetOrigin(ox, oy)

	If x >= (GraphicsWidth() -ox) Then Return
	If y >= (GraphicsHeight() -oy) Then Return	
	If x+w <= -ox Then Return
	If y+h <= -oy Then Return

	DrawImage image, x, y
End Function	

''draws rectangular subimage
Function draw_sub_image_rect(image:TImage, x:Float, y:Float, w:Float, h:Float, sx:Float, sy:Float, sw:Float, sh:Float)
	If image = Null Return;
	
	Local ox:Float, oy:Float
	GetOrigin(ox, oy)

	If x >= (GraphicsWidth() -ox) Then Return
	If y >= (GraphicsHeight() -oy) Then Return	
	If x+w <= -ox Then Return
	If y+h <= -oy Then Return

	DrawSubImageRect(image, x, y, w, h, sx, sy, sw, sh)
End Function	

''replaces color rgb0 by rgb1 in image, pixel by pixel
Function replace_image_color(image:TImage, rgb0:Int, rgb1:Int)

	Local x:Int, y:Int, w:Int, h:Int, pix:Int

	Local pixmap:TPixmap = LockImage(image)
	w = PixmapWidth(pixmap)
	h = PixmapHeight(pixmap)

	For x = 0 To w-1
		For y = 0 To h-1
			pix = ReadPixel(pixmap, x, y) 
			If pix = rgb0
				WritePixel(pixmap, x, y, rgb1)
			EndIf
		Next
	Next
	
	UnlockImage(image)
	
End Function

''set alha value in a 32-bit rgb color
Function set_alpha:Int(rgb:Int, a:Int) 
    Return (rgb & $FFFFFF) | (a Shl 24) 
End Function 

''loads a sound from file_name in dir_name using flags
Function load_sound:TSound(dir_name:String, file_name:String, flags:Int=0)

	''check if folder exists
	Select FileType(dir_name)
		Case 0
			RuntimeError("Folder not found: " + dir_name)
		Case 1
			RuntimeError("Error: " + dir_name + " is a file and not a folder")
	End Select
	
	Local sound:TSound

	''check if file exist
	If (FileType(dir_name + "/" + file_name) = 1)
		sound = LoadSound(dir_name + "/" + file_name, flags:Int)
	Else
		RuntimeError("File not found: " + dir_name + "/" + file_name)
	EndIf
	
	''check if loaded
	If (sound = Null)
		RuntimeError("Error while loading: " + dir_name + "/" + file_name)
	EndIf

	Return sound

End Function
