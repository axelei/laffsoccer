SuperStrict

Global hair_map:Int[16, 20, 4]	''row, column, (frmx,frmy,posx,posy)

RestoreData hair_map_label
For Local j:Int = 0 To 19
	For Local i:Int = 0 To 15
		''frmx
		ReadData hair_map[i,j,0]
		''frmy
		ReadData hair_map[i,j,1]
		''posx
		ReadData hair_map[i,j,2]
		''posy
		ReadData hair_map[i,j,3]
	Next
Next


#hair_map_label
''      hair frame		drawing position 
''      frmx	frmy	posx	posy
''0
DefData 0,		0,		15,		3
DefData 1,		0,		16,		3
DefData 2,		0,		14,		4
DefData 3,		0,		13,		3
DefData 4,		0,		11,		3
DefData 5,		0,		12,		1
DefData 6,		0,		12,		2
DefData 7,		0,		15,		1

DefData 0,		0,		15,		3
DefData 1,		0,		16,		3
DefData 2,		0,		14,		4
DefData 3,		0,		13,		3
DefData 4,		0,		11,		3
DefData 5,		0,		12,		1
DefData 6,		0,		12,		2
DefData 7,		0,		15,		1

''1
DefData 0,		1,		15,		3
DefData 1,		1,		16,		3
DefData 2,		1,		14,		4
DefData 3,		1,		13,		3
DefData 4,		1,		11,		3
DefData 5,		1,		12,		1
DefData 6,		1,		12,		2
DefData 7,		1,		15,		1

DefData 0,		1,		15,		3
DefData 1,		1,		16,		3
DefData 2,		1,		14,		4
DefData 3,		1,		13,		3
DefData 4,		1,		11,		3
DefData 5,		1,		12,		1
DefData 6,		1,		12,		2
DefData 7,		1,		15,		1

''2
DefData 0,		2,		15,		3
DefData 1,		2,		16,		3
DefData 2,		2,		14,		4
DefData 3,		2,		13,		3
DefData 4,		2,		11,		3
DefData 5,		2,		12,		1
DefData 6,		2,		12,		2
DefData 7,		2,		15,		1

DefData 0,		2,		15,		3
DefData 1,		2,		16,		3
DefData 2,		2,		14,		4
DefData 3,		2,		13,		3
DefData 4,		2,		11,		3
DefData 5,		2,		12,		1
DefData 6,		2,		12,		2
DefData 7,		2,		15,		1

''3
DefData 0,		1,		14,		2
DefData 1,		1,		16,		2
DefData 2,		1,		14,		3
DefData 3,		1,		13,		2
DefData 4,		1,		11,		2
DefData 5,		1,		12,		1
DefData 6,		1,		12,		0
DefData 7,		1,		14,		0

DefData 0,		1,		14,		2
DefData 1,		1,		16,		2
DefData 2,		1,		14,		3
DefData 3,		1,		13,		2
DefData 4,		1,		11,		2
DefData 5,		1,		12,		1
DefData 6,		1,		12,		0
DefData 7,		1,		14,		0

''4
DefData 0,		3,		08,		11
DefData 1,		3,		10,		09
DefData 2,		3,		14,		07
DefData 3,		3,		19,		10
DefData 4,		3,		20,		13
DefData 5,		3,		16,		14
DefData 6,		3,		13,		19
DefData 7,		3,		10,		16

DefData 0,		3,		08,		11
DefData 1,		3,		10,		09
DefData 2,		3,		14,		07
DefData 3,		3,		19,		10
DefData 4,		3,		20,		13
DefData 5,		3,		16,		14
DefData 6,		3,		13,		19
DefData 7,		3,		10,		16

''5
DefData 0,		1,		15,		4
DefData 1,		1,		16,		4
DefData 2,		1,		14,		4
DefData 3,		1,		13,		4
DefData 4,		1,		11,		4
DefData 5,		1,		12,		3
DefData 6,		1,		12,		4
DefData 7,		1,		17,		4

DefData 0,		1,		15,		00
DefData 1,		1,		16,		00
DefData 2,		1,		14,		00
DefData 3,		1,		13,		00
DefData 4,		1,		11,		00
DefData 5,		1,		12,		-1
DefData 6,		1,		12,		00
DefData 7,		1,		17,		00

''6
DefData 0,		1,		20,		03
DefData 1,		1,		16,		02
DefData 2,		1,		12,		03
DefData 3,		1,		11,		03
DefData 4,		1,		08,		04
DefData 5,		1,		09,		01
DefData 6,		1,		11,		02
DefData 7,		1,		21,		01

DefData 0,		1,		20,		03
DefData 1,		1,		16,		02
DefData 2,		1,		12,		03
DefData 3,		1,		11,		03
DefData 4,		1,		08,		04
DefData 5,		1,		09,		01
DefData 6,		1,		11,		02
DefData 7,		1,		21,		01

''7
DefData 0,		4,		20,		11
DefData 1,		4,		17,		19
DefData 2,		4,		15,		20
DefData 3,		4,		11,		19
DefData 4,		4,		07,		12
DefData 5,		4,		07,		6
DefData 6,		4,		13,		4
DefData 7,		4,		20,		6

DefData 0,		4,		20,		14
DefData 1,		4,		17,		22
DefData 2,		4,		15,		23
DefData 3,		4,		11,		22
DefData 4,		4,		07,		15
DefData 5,		4,		07,		09
DefData 6,		4,		13,		07
DefData 7,		4,		20,		11

''8
DefData 0,		8,		16,		2
DefData 1,		8,		17,		2
DefData 2,		8,		15,		3
DefData 3,		8,		14,		2
DefData 4,		8,		12,		2
DefData 5,		8,		13,		1
DefData 6,		8,		13,		2
DefData 7,		8,		17,		1

DefData 0,		5,		18,		4
DefData 1,		5,		12,		4
DefData 2,		5,		18,		4
DefData 3,		5,		13,		4
DefData 0,		5,		18,		4
DefData 1,		5,		12,		4
DefData 2,		5,		21,		3
DefData 3,		5,		10,		3

''9
DefData 0,		9,		15,		2
DefData 1,		9,		18,		2
DefData 2,		9,		15,		4
DefData 3,		9,		13,		3
DefData 4,		9,		11,		2
DefData 5,		9,		12,		0
DefData 6,		9,		13,		2
DefData 7,		9,		17,		0

DefData 0,		5,		20,		3
DefData 1,		5,		11,		3
DefData 2,		5,		20,		2
DefData 3,		5,		11,		2
DefData 0,		5,		20,		7
DefData 1,		5,		11,		7
DefData 2,		5,		22,		6
DefData 3,		5,		09,		6

''10
DefData 0,		1,		14,		6
DefData 1,		1,		13,		6
DefData 2,		1,		15,		7
DefData 3,		1,		12,		5
DefData 4,		1,		12,		5
DefData 5,		1,		12,		4
DefData 6,		1,		13,		5
DefData 7,		1,		18, 	4

DefData 0,		5,		18,		-3
DefData 1,		5,		13,		-3
DefData 2,		5,		20,		-4
DefData 3,		5,		11,		-4
DefData 0,		1,		00,		0
DefData 1,		1,		00,		0
DefData 2,		6,		23,		4
DefData 3,		6,		08,		4

''11
DefData 0,		1,		13,		4
DefData 1,		1,		13,		4
DefData 2,		1,		00,		0
DefData 3,		1,		12,		4
DefData 4,		1,		11,		4
DefData 5,		1,		12,		3
DefData 6,		1,		14,		3
DefData 7,		1,		17,		3

DefData 0,		6,		19,		8
DefData 1,		6,		12,		8
DefData 2,		5,		22,		7
DefData 3,		5,		09,		7
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 2,		6,		23,		8
DefData 3,		6,		08,		8

''12
DefData 0,		1,		13,		4
DefData 1,		1,		13,		4
DefData 2,		1,		00,		0
DefData 3,		1,		12,		4
DefData 4,		1,		11,		4
DefData 5,		1,		12,		3
DefData 6,		1,		14,		3
DefData 7,		1,		17,		3

DefData 0,		7,		23,		17
DefData 1,		7,		08,		17
DefData 2,		7,		24,		17
DefData 3,		7,		07,		17
DefData 0,		7,		00,		0
DefData 1,		7,		00,		0
DefData 2,		7,		22,		20
DefData 3,		7,		09,		20

''13
DefData 0,		1,		13,		4
DefData 1,		1,		13,		4
DefData 2,		1,		00,		0
DefData 3,		1,		12,		4
DefData 4,		1,		11,		4
DefData 5,		1,		12,		3
DefData 6,		1,		14,		3
DefData 7,		1,		17,		3

DefData 4,		5,		21,		7
DefData 5,		5,		07,		7
DefData 2,		6,		23,		5
DefData 3,		6,		06,		5
DefData 0,		7,		23,		19
DefData 1,		7,		08,		19
DefData 2,		7,		25,		20
DefData 3,		7,		06,		20

''14
DefData 2,		1,		14,		4
DefData 2,		1,		14,		7
DefData 2,		1,		14,		7
DefData 2,		1,		13,		10
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

DefData 0,		6,		00,		0
DefData 1,		6,		00,		0
DefData 2,		6,		23,		2
DefData 3,		6,		06,		2
DefData 0,		7,		00,		0
DefData 1,		7,		00,		0
DefData 2,		7,		21,		20
DefData 3,		7,		10,		19

''15
DefData 2,		1,		14,		6
DefData 2,		1,		14,		3
DefData 2,		1,		14,		5
DefData 6,		1,		13,		7
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

DefData 0,		6,		00,		0
DefData 1,		6,		00,		0
DefData 2,		6,		23,		5
DefData 3,		6,		06,		5
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

''16
DefData 0,		1,		00,		0
DefData 1,		1,		00,		0
DefData 2,		1,		00,		0
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

DefData 2,		0,		15,		1
DefData 6,		0,		12,		-1
DefData 2,		1,		12,		4
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

''17
DefData 0,		1,		00,		0
DefData 1,		1,		00,		0
DefData 2,		1,		00,		0
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

DefData 6,		6,		16,		-1
DefData 7,		6,		13,		-2
DefData 2,		1,		00,		0
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

''18
DefData 0,		1,		00,		0
DefData 1,		1,		00,		0
DefData 2,		1,		00,		0
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

DefData 6,		7,		16,		-1
DefData 7,		7,		12,		-2
DefData 2,		1,		00,		0
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

''19
DefData 0,		1,		00,		0
DefData 1,		1,		00,		0
DefData 2,		1,		00,		0
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0

DefData 0,		1,		00,		0
DefData 1,		1,		00,		0
DefData 2,		1,		00,		0
DefData 3,		1,		00,		0
DefData 4,		1,		00,		0
DefData 5,		1,		00,		0
DefData 6,		1,		00,		0
DefData 7,		1,		00,		0
