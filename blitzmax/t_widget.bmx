SuperStrict

Import "t_binding.bmx"
Import "common.bmx"

Type t_widget Abstract

	''geometry
	Field x:Int
	Field y:Int
	Field w:Int
	Field h:Int
	
	''image
	Field image:TImage
	Field frame_w:Int
	Field frame_h:Int
	Field frame_x:Int
	Field frame_y:Int
	
	''colors
	Field body:Int
	Field light_border:Int
	Field dark_border:Int
	
	''text
	Field text:String
	Field charset:Int		''10 or 14
	Field align:Int			''-1 = rigth, 0 = center, 1 = left
	Field text_offset_x:Int
	Field img_ucode:TImage
	
	''misc
	Field visible:Int
	Field active:Int  		''active on mouse passage
	Field selected:Int
	Field blinking:Int
	Field bindings:TList
	Field entry_mode:Int
	Field changed:Int
	
	Method New()
		Self.visible = True
		Self.active = False
		Self.selected = False
		Self.blinking = False
		Self.bindings = New TList
		Self.entry_mode = False
		Self.changed = False
	End Method
	
	Method set_geometry(pos_x:Int, pos_y:Int, width:Int, height:Int)
		Self.set_position(pos_x, pos_y)
		Self.set_size(width, height)
	End Method
	
	Method set_position(pos_x:Int, pos_y:Int)
		Self.x = pos_x
		Self.y = pos_y
	End Method
	
	Method set_size(width:Int, height:Int)
		Self.w = width
		Self.h = height
	End Method
	
	Method set_image(_image:TImage)
		Self.image = _image
	End Method
	
	Method set_size_by_text()
		Self.w = ucode_width(Self.text, Self.charset)
		Self.h = ucode_height(Self.charset)
	End Method

	Method set_frame(fw:Int, fh:Int, fx:Int, fy:Int)
		Self.frame_w = fw
		Self.frame_h = fh
		Self.frame_x = fx
		Self.frame_y = fy
	End Method
	
	Method set_colors(c_body:Int, c_light_border:Int, c_dark_border:Int)
		Self.body         = c_body
		Self.light_border = c_light_border
		Self.dark_border  = c_dark_border
	End Method

	Method set_text(t:String, a:Int=0, c:Int=0)
		Self.text		= t
		Self.align		= a
		
		If (c <> 0)
			Self.charset	= c
			
			Select (Self.charset)
				Case 10
					Self.img_ucode = img_ucode10
				Case 14
					Self.img_ucode = img_ucode14
			End Select
		EndIf
	End Method
	
	Method set_text_offset_x(n:Int)
		Self.text_offset_x = n
	End Method
	
	Method set_visible(b:Int)
		Self.visible = b
	End Method
	
	Method set_active(b:Int)
		Self.active = b
	End Method
	
	Method mouse_over:Int()
		If MouseX() < Self.x Return False
		If MouseX() > Self.x + Self.w Return False 
		If MouseY() < Self.y Return False
		If MouseY() > Self.y + Self.h Return False 
		Return True
	End Method
	
	Method update()
	End Method

	Method render() Abstract
	
	Method bind(_event_name:String, _method_name:String, _method_arguments:String[]=Null)
		Local binding:t_binding  = New t_binding
		binding.event_name       = _event_name
		binding.method_name      = _method_name
		binding.method_arguments = _method_arguments
		Self.bindings.AddLast(binding)
	End Method
	
	Method set_entry_mode(b:Int)
		Self.entry_mode = b
	End Method
	
	Method toggle_entry_mode()
		Self.entry_mode = Not Self.entry_mode
	End Method
	
	Method get_changed:Int()
		Return Self.changed
	End Method

	Method set_changed(b:Int)
		Self.changed = b
	End Method
End Type

