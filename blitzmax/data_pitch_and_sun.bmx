SuperStrict

Global pitch_and_sun:Int[12, 10] ''month, pitch

RestoreData pitch_and_sun_label
For Local month:Int = 0 To 11
	For Local pitch:Int = 0 To 9
		ReadData pitch_and_sun[month, pitch]
	Next
Next

#pitch_and_sun_label
''PITCH	frozen	muddy	wet		soft	normal	dry		hard	snowed	white	cloudy	''MONTH
DefData	20,		20,		20,		5,		10,		0,		5,		10,		10,		50		''january
DefData	20,		20,		20,		5,		10,		0,		5,		10,		10,		40		''february
DefData	10,		20,		30,		10,		15,		0,		5,		5,		5,		30		''march
DefData	0,		15,		35,		20,		25,		0,		5,		0,		0,		20		''april
DefData	0,		10,		30,		25,		25,		5,		5,		0,		0,		10		''may
DefData	0,		5,		20,		30,		35,		5,		5,		0,		0,		10		''june
DefData	0,		0,		20,		30,		30,		10,		10,		0,		0,		10		''july
DefData	0,		0,		10,		30,		30,		15,		15,		0,		0,		5		''august
DefData	0,		5,		20,		25,		35,		5,		10,		0,		0,		10		''september
DefData	5,		15,		20,		20,		30,		0,		5,		5,		0,		20		''october
DefData	5,		20,		20,		15,		25,		0,		5,		5,		5,		30		''november
DefData	10,		20,		20,		10,		15,		0,		5,		10,		10,		40		''december

