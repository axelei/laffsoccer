SuperStrict

Import "lib_main.bmx"
Import "t_sprite.bmx"

Type t_goal_top_b Extends t_sprite

	Method New()
		Self.img    = load_image("images", "goal-top-b.png", MASKEDIMAGE, 0)
		Self.x      = -68
		Self.y      = -661
		Self.offset	= 11
	End Method
	
End Type
