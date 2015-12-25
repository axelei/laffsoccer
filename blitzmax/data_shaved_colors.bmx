SuperStrict

Const SHAVED_COLORS:Int = 7

Global shaved_color:Int[SHAVED_COLORS, 2]

RestoreData shaved_colors_label
For Local i:Int = 0 To SHAVED_COLORS -1
	For Local j:Int = 0 To 1
		ReadData shaved_color[i, j]
	Next
Next

#shaved_colors_label
DefData $000000, $000000	''0
DefData $A1785F, $7D5D4A	''1
DefData $BFC768, $999F54	''2
DefData $C79341, $94713B	''3
DefData $D6BE97, $AB997C	''4
DefData $916847, $765331	''5
DefData $423225, $312418	''6

