SuperStrict

Import "constants.bmx"
Import "t_grass.bmx"

Global grass_array:t_grass[9]

RestoreData l_grass_data
For Local i:Int = PT.FROZEN To PT.WHITE
	grass_array[i] = New t_grass
	ReadData grass_array[i].light_shadow
	ReadData grass_array[i].dark_shadow
	ReadData grass_array[i].friction
	ReadData grass_array[i].bounce
Next

''Grass colors and ball behaviour
#l_grass_data
''		light_shadow	dark_shadow		friction	bounce
DefData $58584C,		$3C3C34,		4,			70		''frozen
DefData $5C3800,		$442C00,		12,			55		''muddy
DefData $486C00,		$3C5800,		6,			60		''wet
DefData $3C5800,		$2C4400,		10,			60		''soft
DefData $486C00,		$3C5800,		8,			65		''normal
DefData $486C00,		$3C5800,		6,			65		''dry
DefData $684C00,		$463C00,		6,			70		''hard
DefData $58584C,		$3C3C34,		4,			70		''snowed
DefData $3C5800,		$2C4400,		10,			60		''white
