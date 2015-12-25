SuperStrict

Import "common.bmx"

''camera
Global camera:t_camera

''follow
Const CF_NONE:Int 	= 0
Const CF_BALL:Int	= 1
Const CF_TARGET:Int	= 2
	
''speed
Const CS_NORMAL:Int = 0
Const CS_FAST:Int	= 1
Const CS_WARP:Int	= 2
	
Type t_camera

	Field x:Float	''position
	Field y:Float
	Field vx:Float	''speed
	Field vy:Float
	Field offx:Int	''offset
	Field offy:Int
	
	Method set_x(x0:Int)
		x = x0
	End Method
	
	Method set_y(y0:Int)
		y = y0
	End Method
	
	''follow: CF_NONE, CF_BALL, CF_TARGET
	''speed: CS_NORMAL, CS_FAST, CS_WARP
	Method updatex:Float(follow:Int, speed:Int = CS_NORMAL, target_x:Int = 0, limit:Int = True)
	
		Select follow
	
			Case CF_NONE
				''do nothing
	
			Case CF_BALL
				If (ball.v * Cos(ball.a) <> 0)
					Self.offx = game_settings.screen_width / 20.0 * Cos(ball.a) ''"remember" last direction of movement
				EndIf
			
				''speed
				If (Abs(Self.vx) > Abs(ball.v * Cos(ball.a)))
					''decelerate
					Self.vx = (1 - 5.0/SECOND) * Self.vx
				Else 
					''accelerate
					Self.vx = Self.vx + (2.5/SECOND) * Sgn(Self.offx) * Abs(Self.vx - ball.v * Cos(ball.a))
				EndIf
				
				Self.x = Self.x + Self.vx / SECOND
				
				''near the point "ball+offset"
				If (Abs(ball.x + Self.offx - (Self.x - CENTER_X + game_settings.screen_width/(2*game_settings.zoom/100.0))) => 10)
					Local f:Float = ball.x +Self.offx -(Self.x -CENTER_X +game_settings.screen_width/(2*game_settings.zoom/100.0))
					Self.x = Self.x + (10.0/SECOND) * (1 + speed) * Sgn(f) * Sqr(Abs(f)) 
				EndIf
	
			Case CF_TARGET
				Self.x = Self.x + (10.0/SECOND) * (1 + speed) * Sgn(target_x -(Self.x -CENTER_X +game_settings.screen_width/(2*game_settings.zoom/100.0))) * Sqr( Abs(target_x -(Self.x -CENTER_X +game_settings.screen_width/(2*game_settings.zoom/100.0))))
	
		End Select
			
		''keep inside pitch
		Local xmin:Int, xmax:Int
	
		xmin = 0
		xmax = PITCH_W - game_settings.screen_width/(game_settings.zoom/100.0)
		
		If ((game_settings.screen_width < 1600) And limit)
			xmin = CENTER_X - TOUCH_LINE - game_settings.screen_width/(8*game_settings.zoom/100.0)
			xmax = CENTER_X + TOUCH_LINE + game_settings.screen_width/(8*game_settings.zoom/100.0) - game_settings.screen_width/(game_settings.zoom/100.0) 
		EndIf
	
		If (Self.x < xmin)
			Self.x = xmin
		EndIf
			
		If (Self.x > xmax)
			Self.x = xmax
		EndIf
	
		Return Self.x
	
	End Method
	
	''follow: CF_NONE, CF_BALL, CF_TARGET
	''speed: CS_NORMAL, CS_FAST, CS_WARP
	Method updatey:Float(follow:Int, speed:Int = CS_NORMAL, target_y:Int = 0)
	
		Select follow
	
			Case CF_NONE
				''do nothing
			
			Case CF_BALL
				If (ball.v * Cos(ball.a) <> 0)
					Self.offy = game_settings.screen_height / 20.0 * Sin(ball.a) ''"remember" last direction of movement
				EndIf
			
				''speed
				If (Abs(Self.vy) > Abs(ball.v * Sin(ball.a)))
					''decelerate
					Self.vy = (1 - 5.0/SECOND) * Self.vy
				Else
					''accelerate
					Self.vy = Self.vy + (2.5/SECOND) * Sgn(Self.offy) * Abs(Self.vy - ball.v * Sin(ball.a))
				EndIf
				
				Self.y = Self.y + Self.vy / SECOND
				
				''near the point "ball+offset"
				If (Abs(ball.y + Self.offy - (Self.y - CENTER_Y + game_settings.screen_height/(2*game_settings.zoom/100.0))) => 10)
					Local f:Float = ball.y +Self.offy -(Self.y -CENTER_Y +game_settings.screen_height/(2*game_settings.zoom/100.0))
					Self.y = Self.y + (10.0/SECOND) * (1 + speed) * Sgn(f) * Sqr(Abs(f)) 
				EndIf
		
			Case CF_TARGET
				Self.y = Self.y + (10.0/SECOND) * (1 + speed) * Sgn(target_y -(Self.y -CENTER_Y +game_settings.screen_height/(2*game_settings.zoom/100.0))) * Sqr( Abs(target_y -(Self.y -CENTER_Y +game_settings.screen_height/(2*game_settings.zoom/100.0))))
		
		End Select
		
		''keep inside pitch
		Local ymin:Int, ymax:Int
		ymin = 0
		ymax = PITCH_H - game_settings.screen_height/(game_settings.zoom/100.0)
	
		If (Self.y < ymin)
			Self.y = ymin
		EndIf
		
		If (Self.y > ymax)
			Self.y = ymax
		EndIf
		
		Return Self.y
	
	End Method

End Type	
