SuperStrict

Import "t_game_mode.bmx"

Type t_menu_test_match Extends t_game_mode

	Method New()
	
		team[HOME].ext = "cus"
		team[HOME].number		= 338
		team[HOME].load_from_file()
		
		team[HOME].control = CM_PLAYER
		team[HOME].input_device = input_devices.ValueAtIndex(0)
		
		team[AWAY].ext = "cus"
		team[AWAY].number		= 317
		team[AWAY].load_from_file()
		
		team[AWAY].control = CM_COMPUTER
		'team[AWAY].control = CM_PLAYER
		team[AWAY].input_device = input_devices.ValueAtIndex(1)
		'team[AWAY].input_device = ai[AWAY]
		
		menu.status = MS_FRIENDLY
		game_settings.music_vol = 0
		
	End Method

	Method update()
		game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_MATCH_PRESENTATION)
	End Method
	
End Type
