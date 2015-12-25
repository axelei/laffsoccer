SuperStrict

Type t_wind					
	Field speed:Int     ''0=none, 1=normal, 2=strong
	Field direction:Int ''0=E, 1=S-E, 2=S, 3=S-W, 4=W, 5=N-W, 6=N, 7=N-E
	
	Method init(weather_intensity:Int)
		Self.speed = weather_intensity
		Self.direction = Rand(0, 7)
	End Method
End Type


