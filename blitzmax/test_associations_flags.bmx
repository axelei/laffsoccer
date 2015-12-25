SuperStrict

Import "t_associations_flags.bmx"

associations_list = t_associations_list.Create()
associations_flags = t_associations_flags.Create()

Graphics 336,176
While Not (KeyDown(KEY_ESCAPE) Or AppTerminate())
	Local i:Int = 0
	For Local l:String = EachIn associations_list
		DrawImage associations_flags.get_flag(l), 21 * (i Mod 16), Floor(i / 16) * 11
		i :+1
	Next
	Flip
Wend
