SuperStrict

Global shaved_table:Int[9, 10]

RestoreData shaved_table_label
For Local hc:Int = 0 To 8
	For Local sk:Int = 0 To 9
		ReadData shaved_table[hc, sk]
	Next
Next

#shaved_table_label
''SKIN:	|pink   |black  |pale   |asiat  |arab   |mulat  |red	|free	|alien	|yoda		  HAIR:
DefData 1,		6,		1,		1,		1,		5,		1,		1,		1,		1		''0 - black
DefData 2,		2,		2,		2,		2,		2,		2,		2,		2,		2		''1 - blond
DefData 5,		6,		5,		5,		5,		6,		5,		5,		5,		5		''2 - brown
DefData 3,		6,		3,		3,		1,		5,		3,		3,		3,		3		''3 - red
DefData 2,		6,		2,		2,		2,		5,		2,		2,		2,		2		''4 - platinum
DefData 1,		6,		1,		1,		1,		5,		1,		1,		1,		1		''5 - gray
DefData 4,		1,		4,		4,		4,		1,		4,		4,		4,		4		''6 - white
DefData 2,		2,		2,		2,		2,		2,		2,		2,		2,		2		''7 - punk1
DefData 5,		6,		5,		5,		5,		6,		5,		5,		5,		5		''8 - punk2


