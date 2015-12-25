SuperStrict

Type t_grass
	Field light_shadow:Int
	Field dark_shadow:Int
	Field friction:Float
	Field bounce:Int
	
	Method copy(other:t_grass)
		Self.light_shadow = other.light_shadow
		Self.dark_shadow = other.dark_shadow
		Self.friction = other.friction
		Self.bounce = other.bounce
	End Method
End Type
