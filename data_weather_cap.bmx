SuperStrict

Import "constants.bmx"

Global weather_cap:Int[9,4]

RestoreData weather_cap_label
For Local pitch:Int = PT.FROZEN To PT.WHITE
	For Local effect:Int = WE.WIND To WE.FOG
		ReadData weather_cap[pitch, effect]
	Next
Next

''intensity cap for each pitch type
#weather_cap_label
''		wind		rain		snow		fog		
DefData	WI.STRONG,	WI.NONE,	WI.LIGHT,	WI.NONE		''frozen
DefData WI.STRONG,	WI.STRONG,	WI.NONE,	WI.STRONG	''muddy
DefData WI.STRONG,	WI.STRONG,	WI.NONE,	WI.STRONG	''wet
DefData WI.STRONG,	WI.LIGHT,	WI.NONE,	WI.STRONG	''soft
DefData WI.STRONG,	WI.NONE,	WI.NONE,	WI.STRONG	''normal
DefData WI.STRONG,	WI.NONE,	WI.NONE,	WI.NONE		''dry
DefData WI.STRONG,	WI.NONE,	WI.NONE,	WI.STRONG	''hard
DefData WI.STRONG,	WI.NONE,	WI.STRONG,	WI.STRONG	''snowed
DefData WI.STRONG,	WI.NONE,	WI.STRONG,	WI.STRONG	''white
