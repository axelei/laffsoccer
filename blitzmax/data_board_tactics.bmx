SuperStrict

Global board_tactics:Int[18, 11, 2]

RestoreData board_tactics_label
For Local tac:Int = 0 To 17
	For Local ply:Int = 0 To 10
		For Local k:Int = 0 To 1
			ReadData board_tactics[tac, ply, k]
		Next
	Next
Next

#board_tactics_label
'442
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
'541
DefData	0,0,	-2,3,	0,1,	1,2,	2,3,	-1,5,	-1,2,	0,4,	1,5,	0,6,	0,8
'451
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	1,5,	2,6,	-1,5,	0,8
'532
DefData	0,0,	-2,3,	0,1,	1,2,	2,3,	-2,5,	-1,2,	0,6,	2,5,	-1,8,	1,8
'352
DefData	0,0,	-1,2,	0,1,	1,4,	1,2,	-2,5,	-1,4,	0,6,	2,5,	-1,8,	1,8
'433
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,5,	0,4,	1,7,	2,5,	-1,7,	0,8
'424
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	-1,4,	1,4,	2,6,	0,7,	0,8
'343
DefData	0,0,	-1,2,	0,1,	0,4,	1,2,	-2,5,	0,6,	1,7,	2,5,	-1,7,	0,8
'sweep
DefData	0,0,	-2,3,	0,1,	0,2,	2,3,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
'523
DefData	0,0,	-2,3,	0,1,	1,2,	2,3,	-2,7,	-1,2,	1,5,	2,7,	-1,5,	0,8
'attack
DefData	0,0,	-1,2,	0,1,	-1,5,	1,2,	-2,7,	1,5,	0,6,	2,7,	-1,8,	1,8
'defend
DefData	0,0,	-1,2,	-1,1,	1,1,	1,2,	-2,3,	-1,5,	1,5,	2,3,	0,6,	0,8
'user a
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
'user b
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
'user c
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
'user d
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
'user e
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
'user f
DefData	0,0,	-2,2,	-1,1,	1,1,	2,2,	-2,6,	0,4,	0,5,	2,6,	-1,8,	1,8
