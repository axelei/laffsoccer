SuperStrict

Global skin_colors:Int[10, 3]

RestoreData skin_colors_label
For Local i:Int = 0 To 9
	For Local j:Int = 0 To 2
		ReadData skin_colors[i,j] 
	Next
Next

#skin_colors_label
DefData $F89150, $B85B26, $78311A	''0 - pink
DefData $613E21, $3C2611, $140A01	''1 - black
DefData $F7AE80, $B77651, $905440	''2 - pale
DefData $F8BF50, $B88D26, $83641A	''3 - asiatic
DefData $D98B59, $B45C29, $7C3F1C	''4 - arab
DefData $C97E41, $8D643B, $634629	''5 - mulatto
DefData $F37C58, $A9573E, $5E3123	''6 - red
DefData $F89150, $B85B26, $78311A	''7 - free
DefData $D6D6D6, $BEBEBD, $A5A5A4	''8 - alien
DefData $34DF00, $0A9300, $075B01	''9 - yoda
