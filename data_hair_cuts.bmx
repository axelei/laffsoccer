SuperStrict

Global hair_cut:Int[42]

Function hair_type:Int(hair_code:Int)
	For Local i:Int = 0 To 41
		If hair_cut[i] = hair_code	Return i
	Next
End Function

RestoreData hair_cuts_label
For Local i:Int = 0 To 41
	ReadData hair_cut[i]
Next

#hair_cuts_label

''1 - BALD
DefData 101 ''pelato (Collina)
DefData 102 ''pelato con pizzetto (Chimenti, Veron)

''2 - SHAVED 
DefData 201 ''rasato (Ronaldo, Bojinov, Seedorf)

''3 - THINNING
DefData 301 ''calvizia estesa (Ballotta, Lombardo..)
DefData 302 ''chierica (Zidane, Di Canio)
DefData 303 ''stempiato (Corini)

''4 - SHORT
DefData 401 ''taglio medio (Giuly)
DefData 402 ''dritti (Harte, Amantino)
DefData 403 ''spazzola (Manfredini)

''5 - SMOOTH
DefData 501 ''folti (Cocu) ---> ATTENZIONE, QUESTO CORRISPONDE AL TAGLIO STANDARD DI SWOS
DefData 502 ''riga in mezzo (Deco)
DefData 503 ''riga in mezzo con sfumatura posteriore (Rothen, Materazzi)
DefData 504 ''indietro (O''Shea, Kanchelskis 1996)
DefData 505 ''allungati all''indietro (Toni)
DefData 506 ''allungati con ciocche (Heinze)
DefData 507 ''allungati con elastico (Totti, Baros, Van Der Meyde, Ibrahimovic..)
DefData 508 ''lunghi pettinati (Batistuta)
DefData 509 ''lunghi con elastico (Caniggia)
DefData 510 ''lunghi con elastico nero (Ujfalusi)
DefData 511 ''lunghi con elastico colorato (..)
DefData 512 ''raccolti all''indietro con codino (Savage, Prso)
DefData 513 ''raccolti con codino alto (Camoranesi)
DefData 514 ''corti con cerchietto (Frey)
DefData 515 ''lunghi con cerchietto (Rossini, Vieri..)
DefData 516 ''gonfi (Nedved)

''6 - CURLY
DefData 601 ''medi (Cisse')
DefData 602 ''allungati (Oliveira)
DefData 603 ''lunghi (Crespo)
DefData 604 ''lunghi con elastico (Ortega)
DefData 605 ''lunghi con cerchietto (?)
DefData 606 ''raccolti con codino (Ronaldinho)


''7 - PIGTAIL
DefData 701 ''treccine corte su rasato
DefData 702 ''treccioni su rasato
DefData 703 ''trecce lunghe (Rio Ferdinand)

''8 - SPECIAL
DefData 801 ''cresta (Beckham)
DefData 802 ''nazi
DefData 803 ''mohicano
DefData 804 ''West style
DefData 805 ''Davids style
DefData 806 ''barbone (Tommasi, Xavier)
DefData 807 ''Valderrama style
DefData 808 ''Divin Codino
