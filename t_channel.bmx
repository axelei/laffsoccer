SuperStrict

Global channel:t_channel

Type t_channel
	Field intro:TChannel
	Field crowd:TChannel
	Field bounce:TChannel
	Field kick:TChannel
	Field post:TChannel
	Field net:TChannel
	Field hold:TChannel
	Field deflect:TChannel
	Field whistle:TChannel
	Field missgoal:TChannel
	Field homegoal:TChannel
	Field chant:TChannel
	Field endgame:TChannel
	
	Method New()
		Self.intro    = AllocChannel()
		Self.crowd    = AllocChannel()
		Self.bounce   = AllocChannel()
		Self.kick     = AllocChannel()
		Self.post     = AllocChannel()
		Self.net      = AllocChannel()
		Self.hold     = AllocChannel()
		Self.deflect  = AllocChannel()
		Self.whistle  = AllocChannel()
		Self.missgoal = AllocChannel()
		Self.homegoal = AllocChannel()
		Self.chant    = AllocChannel()
		Self.endgame  = AllocChannel()
	End Method
End Type
