SuperStrict

Import "constants.bmx"
Import "lib_main.bmx"
Import "t_sprite.bmx"

Type t_goal_top_a Extends t_sprite
	
	Method New()
		Self.img    = load_image("images", "goal-top-a.png", MASKEDIMAGE, 0)
		Self.x      = -72
		Self.y      = -GOAL_LINE
		Self.offset	= 50
	End Method
	
End Type

