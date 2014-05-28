SuperStrict

Import "t_widget.bmx"

Type t_label Extends t_widget

	Method render()
		If (Self.visible = False) Return
		
		Select Self.charset
			Case 14
				Self.draw_14u()
			Case 10
				Self.draw_10u()
		End Select
	End Method
	
	''draw with characters of height 10
	Method draw_10u() 
		
		set_color($FFFFFF, light)
	
		Local tx:Int = Self.x
		Select Self.align
			Case -1	''rigth	
			    tx = tx +Self.w -10
			Case 0	''center
			    tx = tx +Floor(0.5*Self.w)
			Case 1	''left
			    tx = tx +10
		End Select	
	    text10u(Self.text, tx, Self.y + Ceil(0.5*(Self.h -18)), Self.img_ucode, Self.align)
	
	End Method
	
	''draw with characters of height 14
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
	    text14u(Self.text, tx, Self.y + Ceil(0.5*(Self.h -22)), Self.img_ucode, Self.align)
			
	End Method

End Type
