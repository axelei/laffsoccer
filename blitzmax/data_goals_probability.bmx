SuperStrict

Global goals_probability:Int[12,7]	''factor, goals

RestoreData goals_probability_label
For Local factor:Int = 0 To 10
	For Local goals:Int = 0 To 6
		ReadData goals_probability[factor, goals]
	Next
Next

#goals_probability_label
''goals	P0		P1		P2		P3		P4		P5		P6
DefData 1000,	0,		0,		0,		0,		0,		0
DefData 870,	100,	25,		4,		1,		0,		0
DefData 730,	210,	50,		7,		2,		1,		0
DefData 510,	320,	140,	20,		6,		4,		0
DefData 390,	370,	180,	40,		10,		7,		3
DefData 220,	410,	190,	150,	15,		10,		5
DefData 130,	390,	240,	200,	18,		15,		7
DefData 40,		300,	380,	230,	25,		15,		10
DefData 20,		220,	240,	220,	120,	100,	80
DefData 10,		150,	190,	190,	170,	150,	140
DefData 0,		100,	150,	200,	200,	200,	150
