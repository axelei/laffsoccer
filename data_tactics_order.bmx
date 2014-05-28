SuperStrict

Import "constants.bmx"

Global tactics_order:Int[12, 32]

RestoreData tactics_order_label
For Local tc:Int = 0 To 11
	For Local pos:Int = 0 To 31
		If (pos < TEAM_SIZE)
			Local ply:Int
			ReadData ply
			tactics_order[tc, pos] = ply
		Else
			tactics_order[tc, pos] = pos
		EndIf
	Next
Next

#tactics_order_label
DefData	0,	1,	2,	3,	4,	5,	6,	7,	8,	9,	10	''442
DefData	0,	1,	2,	3,	6,	4,	5,	7,  9,  8,	10	''541
DefData	0,	1,	2,	3,	4,	5,	6,	7,	9,	8,	10	''451
DefData	0,	1,	2,	3,	6,	4,	5,	7,	8,	9,	10	''532
DefData	0,	1,	2,	4,	5,	3,	6,	7,	8,	9,	10	''352
DefData	0,	1,	2,	3,	4,	5,	6,	8,	7,	9,	10	''433
DefData	0,	1,	2,	3,	4,	6,	7,	5,	9,	10,	8	''424
DefData	0,	1,	2,	4,	5,	3,	6,	8,	7,	9,	10	''343
DefData	0,	1,	2,	3,	4,	5,	6,	7,	8,	9,	10	''sweep
DefData	0,	1,	2,	3,	6,	4,	7,	9,	5,	10,	8	''523
DefData	0,	1,	2,	4,	3,	6,	5,	7,	9,	10,	8	''attack
DefData	0,	5,	1,	2,	3,	4,	8,	6,	7,	9,	10	''defend
