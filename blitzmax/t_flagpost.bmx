SuperStrict

Import "lib_main.bmx"
Import "t_sprite.bmx"
Import "t_location_settings.bmx"

Type t_flagpost Extends t_sprite
	
	Function Create:t_flagpost(side_x:Int, side_y:Int)
		Local fp:t_flagpost = New t_flagpost
		fp.img = load_image("images", "flagpost.png", MASKEDIMAGE, 0)
		fp.x = side_x * TOUCH_LINE
		fp.y = side_y * GOAL_LINE
		Return fp
	End Function

	Method draw(frame:Int)
		Local frame_x:Int = 0
		If (location_settings.wind.speed > 0 )
			frame_x = ((frame/SUBFRAMES) Shr (4 -location_settings.wind.speed)) Mod 2 + 1 + 2 * location_settings.wind.direction
		EndIf
		draw_sub_image_rect(img, x -18, y -28, 36, 30, 36 * frame_x, 0, 36, 30)
	End Method
	
End Type
