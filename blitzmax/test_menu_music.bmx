SuperStrict

Import "t_menu_music.bmx"

Local mm:t_menu_music = New t_menu_music

SetAudioDriver("OpenAL")

mm.init("music")

Print
Print "Setting single track mode..."
mm.set_mode(0)
mm.print_playlist()

For Local i:Int = 1 To 40
	mm.update()
	Delay 100
Next

Print
Print "Setting shuffle mode..."
mm.set_mode(MM_SHUFFLE)
mm.print_playlist()

For Local i:Int = 1 To 80
	mm.update()
	Delay 100
Next

Print
Print "Setting play all mode..."
mm.set_mode(MM_ALL)
mm.print_playlist()

Print
Print "Press Esc to exit"

While Not KeyDown(KEY_ESCAPE)
	mm.update()
	Delay 1000
Wend
