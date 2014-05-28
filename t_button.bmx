SuperStrict

Import "t_widget.bmx"

Type t_button Extends t_widget

	Const ratio:Float = 0.4
	
	Method New()
		Self.active = True
	End Method
	
	Method render()
		If (Self.visible = False) Return
		
		SetBlend ALPHABLEND
		SetAlpha(0.9)
	
		''body ($000000 = invisible)
		If Self.body <> $000000
			set_color(Self.body, light)
			DrawRect Self.x +2, Self.y +2, Self.w -4, Self.h -4
		EndIf
		
		''border ($000000 = invisible)
		If Self.light_border <> $000000
			draw_border(Self.x, Self.y, Self.w, Self.h, Self.light_border, Self.dark_border, light)
		EndIf
		
		set_color($FFFFFF, light)
		
		If Self.selected
			Self.draw_animated_border()
		EndIf
		
		Self.draw_image()
		
		SetBlend MASKBLEND
		SetAlpha(1)
		
		If (Self.blinking And ((Abs(MilliSecs()) Mod 800)  < 400))
			Return
		EndIf
		
		Select Self.charset
			Case 14
				Self.draw_14u()
			Case 10
				Self.draw_10u()
		End Select
		
	End Method
	
	Method draw_image()
		If Self.image
			DrawSubImageRect(Self.image, Self.x+2, Self.y+2, Self.frame_w, Self.frame_h,  Self.frame_x * Self.frame_w, Self.frame_y * Self.frame_h, Self.frame_w, Self.frame_h)
		EndIf
	End Method

	''draw a button with characters of height 10
	Method draw_10u() 
		
		set_color($FFFFFF, light)
		
		Local tx:Int = Self.x
		Select Self.align
			Case -1	''rigth
				tx = tx +Self.w -10
			Case 0	''center
				tx = tx +Floor(.5*Self.w)
			Case 1	''left
				tx = tx +10
		End Select	
	    text10u(Self.get_text(), tx +Self.text_offset_x, Self.y + Ceil(0.5*(Self.h -18)), Self.img_ucode, Self.align)
	
	End Method
	
	''draw a button with characters of height 14
	Method draw_14u()
	
		set_color($FFFFFF, light)
		
		Local tx:Int = Self.x
		Select Self.align
			Case -1	''rigth	
			    tx = tx +Self.w -14
			Case 0	''center
			    tx = tx +Floor(0.5*Self.w)
			Case 1	''left
			    tx = tx +14
		End Select	
	    text14u(Self.get_text(), tx +Self.text_offset_x, Self.y + Ceil(0.5*(Self.h -22)), Self.img_ucode, Self.align)
			
	End Method
	
	Method get_text:String()
		Return Self.text
	End Method

	Method draw_animated_border()
		If (Self.entry_mode)
			Return
		EndIf
		
		''graylevel
		Local gl:Int =  Abs(((ratio*Abs(MilliSecs())) Mod 200) -100) +100
	
		''border color 1
		Local bdr1:Int = RGB(gl,gl,gl)
	
		''border color 2
		gl = gl -50
		Local bdr2:Int = RGB(gl,gl,gl)
		
		draw_border(Self.x, Self.y, Self.w, Self.h, bdr1, bdr2, light)
	
	End Method

End Type
