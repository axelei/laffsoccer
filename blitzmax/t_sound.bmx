SuperStrict

Import "lib_misc.bmx"

Global sound:t_sound

Type t_sound
	Field intro:TSound
	Field crowd:TSound
	Field bounce:TSound
	Field kick:TSound
	Field post:TSound
	Field net:TSound
	Field hold:TSound
	Field deflect:TSound
	Field whistle:TSound
	Field missgoal:TSound
	Field homegoal:TSound
	Field chant:TSound
	Field endgame:TSound
	
	Function Create:t_sound(dir:String)
		Local s:t_sound = New t_sound
		s.intro    = load_sound(dir, "intro.ogg")
		s.crowd    = load_sound(dir, "crowd.ogg", SOUND_LOOP)
		s.bounce   = load_sound(dir, "bounce.ogg")
		s.kick     = load_sound(dir, "kick.ogg")
		s.post     = load_sound(dir, "post.ogg")
		s.net      = load_sound(dir, "net.ogg")
		s.hold     = load_sound(dir, "hold.ogg")
		s.deflect  = load_sound(dir, "deflect.ogg")
		s.whistle  = load_sound(dir, "whistle.ogg")
		s.missgoal = load_sound(dir, "missgoal.ogg")
		s.homegoal = load_sound(dir, "homegoal.ogg")
		s.chant    = load_sound(dir, "chant.ogg")
		s.endgame  = load_sound(dir, "endgame.ogg")
		Return s
	End Function
	
End Type


