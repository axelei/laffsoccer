SuperStrict

Global substitution_rules:Int[8, 2]

RestoreData substitution_rules_label
For Local pt:Int = 0 To 7
	For Local alt:Int = 0 To 1
		ReadData substitution_rules[pt, alt]
	Next
Next

#substitution_rules_label
DefData 0, 0	''0 - goalkeeper
DefData 2, 3	''1 - right back
DefData 1, 3	''2 - left back
DefData 1, 2	''3 - defender
DefData 5, 6	''4 - right wing
DefData 4, 6	''5 - left wing
DefData 4, 5	''6 - midfielder
DefData 4, 5	''7 - attacker

