SuperStrict

Import "common.bmx"

'dictionary
dictionary= New t_dictionary
dictionary.fetch_language(0)

Local extra_time:Int
Local penalties:Int
'SeedRnd(MilliSecs())
'ONE LEG, AWAY GOALS

Local cup:t_cup = New t_cup
'cup.away_goals = CS_AWAY_GOALS_OFF
'cup.away_goals = CS_AWAY_GOALS_AFTER_90_MINS
cup.away_goals = CS_AWAY_GOALS_AFTER_EXTRA_TIME
cup.rounds = 3;
For Local r:Int = 0 To 5
	cup.round[r].teams = 2^(cup.rounds-r)
	cup.round[r].legs = 2
Next
cup.round[0].extra_time = CS_EXTRA_TIME_OFF
cup.round[0].extra_time = CS_EXTRA_TIME_ON
'cup.round[0].extra_time = CS_EXTRA_TIME_IF_REPLAY

'cup.round[0].penalties = CS_PENALTIES_OFF
cup.round[0].penalties = CS_PENALTIES_ON
'cup.round[0].penalties = CS_PENALTIES_IF_REPLAY

cup.round[1].extra_time = CS_EXTRA_TIME_ON
cup.round[1].penalties = CS_PENALTIES_ON

cup.round[2].extra_time = CS_EXTRA_TIME_IF_REPLAY
cup.round[2].penalties = CS_PENALTIES_IF_REPLAY

Local teams:TList = New TList
For Local i:Int = 0 To 2^cup.rounds-1
	Local t:t_team = New t_team
	t.name = Chr(65+i)
	ListAddLast(teams, t)
Next
SeedRnd(2)
cup.initialize(teams)

cup.calendar_print()


While Not cup.ended
	Local a:Int = Rand(0,1)
	Local b:Int = Rand(0,1)
	cup.set_result(a, b, RT_AFTER_90_MINS)
	extra_time = cup.play_extra_time()
	Print "extra time " + extra_time
	If extra_time
		a = a + Rand(0,1)
		b = b + Rand(0,1)
		cup.set_result(a, b, RT_AFTER_EXTRA_TIME)
	EndIf
	penalties = cup.play_penalties()
	Print "penalties " + penalties
	If penalties
		Local p0:Int
		Local p1:Int
		Repeat 
			p0 = Rand(0,5)
			p1 = Rand(0,5)
		Until p0 <> p1
		cup.set_result(p0, p1, RT_PENALTIES)
	EndIf
	cup.next_match()
Wend

Return

SeedRnd(0)
cup.restart()

cup.calendar_print()


While Not cup.ended
	Local a:Int = Rand(0,1)
	Local b:Int = Rand(0,1)
	cup.set_result(a, b)
	extra_time = cup.play_extra_time()
	Print "extra time " + extra_time
	If extra_time
		a = a + Rand(0,1)
		b = b + Rand(0,1)
		cup.set_result(a, b, 1)
	EndIf
	penalties = cup.play_penalties()
	Print "penalties " + penalties
	If penalties
		Local p0:Int
		Local p1:Int
		Repeat 
			p0 = Rand(0,5)
			p1 = Rand(0,5)
		Until p0 <> p1
		cup.set_result(a+p0, a+p1)
	EndIf
	cup.next_match()
Wend
