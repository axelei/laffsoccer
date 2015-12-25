SuperStrict

Global hair_colors:Int[9, 3]

RestoreData hair_colors
For Local i:Int = 0 To 8
	For Local j:Int = 0 To 2
		ReadData hair_colors[i,j] 
	Next
Next

#hair_colors
DefData $2A2A2A, $1A1A1A, $090909	''0 - black
DefData $A2A022, $81801A, $605F11	''1 - blond
DefData $A26422, $7B4C1A, $543411	''2 - brown
DefData $E48B00, $B26D01, $7F4E01	''3 - red
DefData $FFFD7E, $E5E33F, $CAC800	''4 - platinum
DefData $7A7A7A, $4E4E4E, $222222	''5 - gray
DefData $D5D5D5, $ADADAD, $848484	''6 - white
DefData $FF00A8, $722F2F, $421A1A	''7 - punk1
DefData $FDFB35, $808202, $595A05	''8 - punk2

