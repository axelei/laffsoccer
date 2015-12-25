SuperStrict

Import "t_vec2d.bmx"

'' create 2 vectors using polar coordinates
Local v1:t_vec2d = t_vec2d.Create(3, 0, True)
Local v2:t_vec2d = t_vec2d.Create(4, 90, True)

'' make cartesian difference
Local v3:t_vec2d = v2.diff(v1)

'' test polar values
If (v3.v = 5.0) And (v3.a = 126.869896)
	Print "TEST OK"
Else
	Print "TEST KO"
EndIf