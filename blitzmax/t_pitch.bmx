SuperStrict

Import "lib_main.bmx"
Import "t_location_settings.bmx"

Global pitch:t_pitch

Type t_pitch
	
	Field images:TImage[4, 4]
	
	Method New()
		
		Local pitch_name:String = location_settings.pitch_name()
	
		Local file_image:TStream
		For Local c:Int = 0 To 3
			For Local r:Int = 0 To 3
	
				''--- LOAD IMAGE INTO A BANKSTREAM ---	
				file_image = ReadStream("images/stadium/generic_" + String(c) + String(r) + ".png")
					
				''create bank & bankstream
				Local bank:TBank = CreateBank()
				Local bank_stream:TBankStream = CreateBankStream(bank)
			
				''copy file_image to bankstream
				CopyStream(file_image, bank_stream)
				CloseStream(file_image)
			
				Select (location_settings.time)
					Case TI.DAY
						Select (location_settings.sky)
							Case SK.CLEAR
								swap_png_palette(bank_stream, bank, "images/stadium/palettes/" + pitch_name + "_sunny.pal")
							Case SK.CLOUDY
								swap_png_palette(bank_stream, bank, "images/stadium/palettes/" + pitch_name + "_cloudy.pal")
						End Select
					Case TI.NIGHT 
						swap_png_palette(bank_stream, bank, "images/stadium/palettes/" + pitch_name + "_night.pal")
				End Select
			
				''load image
				SeekStream(bank_stream, 0)
				set_mask_color($00F700)
				Self.images[r, c] = LoadImage(bank_stream, MASKEDIMAGE) 
	
			Next
		Next	

	End Method
	
	Method draw()
		For Local c:Int = 0 To 3
			For Local r:Int = 0 To 3
				draw_image(Self.images[r, c], -CENTER_X +512*c, -CENTER_Y +512*r)
			Next
		Next
	End Method
	
End Type
