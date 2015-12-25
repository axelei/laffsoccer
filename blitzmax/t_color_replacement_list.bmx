SuperStrict

Type t_color_replacement_list Extends TList
	
	Method add(rgb_old:Int, rgb_new:Int)
		Local p:t_rgb_pair = New t_rgb_pair
		p.rgb_old = rgb_old
		p.rgb_new = rgb_new
		Self.addLast(p)
	End Method
	
End Type

Type t_rgb_pair
	Field rgb_old:Int
	Field rgb_new:Int
End Type

