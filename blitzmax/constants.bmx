SuperStrict

''refresh rate
Const REFRATE:Int = 40

''subframes in a frame (used during replay)
Const SUBFRAMES:Int = 12  ''<<< MUST BE AN EVEN NUMBER (or highlights will not work properly)

''subframes in one second
Const SECOND:Int = REFRATE * SUBFRAMES

''frames in a replay or highlight (must be multiple of 32)
Const VFRAMES:Int = 256 				

''size of the replay vector
Const VSIZE:Int = VFRAMES * SUBFRAMES

''menu music
Const MM_ALL:Int = -2
Const MM_SHUFFLE:Int = -1

''teams
Const TEAM_SIZE:Int	= 11
Const BASE_TEAM:Int	= 16
Const FULL_TEAM:Int	= 32

''player role
Type PR
	Const GOALKEEPER:Int = 0
	Const RIGHT_BACK:Int = 1
	Const LEFT_BACK:Int = 2
	Const DEFENDER:Int = 3
	Const RIGHT_WINGER:Int = 4
	Const LEFT_WINGER:Int = 5
	Const MIDFIELDER:Int = 6
	Const ATTACKER:Int = 7
End Type

''tactics
Const TACT_DX:Int = 68
Const TACT_DY:Int = 40
Const BALL_ZONES:Int = 35

''pitch type
Type PT
	Const FROZEN:Int = 0
	Const MUDDY:Int  = 1
	Const WET:Int    = 2
	Const SOFT:Int   = 3
	Const NORMAL:Int = 4
	Const DRY:Int    = 5
	Const HARD:Int   = 6
	Const SNOWED:Int = 7
	Const WHITE:Int  = 8
	Const RANDOM:Int = 9
End Type

''weather effect
Type WE
	Const WIND:Int   = 0
	Const RAIN:Int   = 1
	Const SNOW:Int   = 2
	Const FOG:Int    = 3
	Const RANDOM:Int = 4
End Type

''weather intensity
Type WI
	Const NONE:Int   = 0
	Const LIGHT:Int  = 1
	Const STRONG:Int = 2
End Type

''time of the day
Type TI
	Const DAY:Int    = 0
	Const NIGHT:Int  = 1
	Const RANDOM:Int = 2
End Type

''sky
Type SK
	Const CLEAR:Int  = 0
	Const CLOUDY:Int = 1
End Type

''size of the pitch
Const PITCH_W:Int = 1700
Const PITCH_H:Int = 1800 

''center of the pitch
Const CENTER_X:Int = 846
Const CENTER_Y:Int = 918

''lines
Const GOAL_LINE:Int      = 640 
Const TOUCH_LINE:Int     = 510
Const GOAL_AREA_W:Int    = 252
Const GOAL_AREA_H:Int    = 58
Const PENALTY_AREA_W:Int = 572
Const PENALTY_AREA_H:Int = 174
Const CORNER_ARC:Int     = 18

''goals
Const GOAL_BTM_X:Int = -72
Const GOAL_BTM_Y:Int = +604

''benches
Const BENCH_X:Int      = -544
Const BENCH_Y_UP:Int   = -198
Const BENCH_Y_DOWN:Int = 38

''game modes
Type GM
	Const MENU_MAIN:Int                    = 1
	Const MENU_GAME_OPTIONS:Int            = 2
	Const MENU_MATCH_OPTIONS:Int           = 3
	Const MENU_CONTROL:Int                 = 4
	Const MENU_FRIENDLY:Int                = 5
	Const MENU_DIY_COMPETITION:Int         = 6
	Const MENU_LOAD_TEAMS:Int              = 7
	Const MENU_CLUBS_CONFEDERATION:Int     = 8
	Const MENU_CLUBS_COUNTRY:Int           = 9
	Const MENU_CLUBS_DIVISION:Int          = 10
	Const MENU_NATIONALS_CONFEDERATION:Int = 11
	Const MENU_SELECT_TEAMS:Int            = 12
	Const MENU_SELECT_TEAM:Int             = 13
	Const MENU_VIEW_SELECTED_TEAMS:Int     = 14
	Const MENU_SET_TEAM:Int                = 15
	Const MENU_EDIT_PLAYERS:Int            = 16
	Const MENU_EDIT_TEAM:Int               = 17
	Const MENU_MATCH_PRESENTATION:Int      = 18
	Const MENU_REPLAY_MATCH:Int            = 19
	Const MENU_DESIGN_DIY_LEAGUE:Int       = 20
	Const MENU_PLAY_LEAGUE:Int             = 21
	Const MENU_VIEW_STATISTICS:Int         = 22
	Const MENU_TOP_SCORERS:Int             = 23
	Const MENU_LEAGUE_INFO:Int             = 24
	Const MENU_SELECT_SQUAD_TO_VIEW:Int    = 25
	Const MENU_VIEW_TEAM:Int               = 26
	Const MENU_COMPETITION_WARNING:Int     = 27
	Const MENU_DESIGN_DIY_CUP:Int          = 28
	Const MENU_PLAY_CUP:Int                = 29
	Const MENU_CUP_INFO:Int                = 30
	Const MATCH_LOADING:Int                = 31
	Const MENU_TEST_MATCH:Int              = 32
	Const MENU_INTRO:Int                   = 33
	Const MENU_COPYING:Int                 = 34
	Const MENU_WARRANTY:Int                = 35
	Const MATCH_INTRO:Int                  = 36
	Const MATCH_STARTING_POSITIONS:Int     = 37
	Const MATCH_KICK_OFF:Int               = 38
	Const MATCH_MAIN:Int                   = 39
	Const MATCH_GOAL:Int                   = 40
	Const MATCH_CORNER_STOP:Int            = 41
	Const MATCH_CORNER_KICK:Int            = 42
	Const MATCH_GOAL_KICK_STOP:Int         = 43
	Const MATCH_GOAL_KICK:Int              = 44
	Const MATCH_KEEPER_STOP:Int            = 45
	Const MATCH_THROW_IN_STOP:Int          = 46
	Const MATCH_THROW_IN:Int               = 47
	Const MATCH_HALF_TIME_STOP:Int         = 48
	Const MATCH_HALF_TIME_POSITIONS:Int    = 49
	Const MATCH_HALF_TIME_WAIT:Int         = 50
	Const MATCH_FULL_TIME_STOP:Int         = 51
	Const MATCH_HALF_TIME_ENTER:Int        = 52
	Const MATCH_EXTRA_TIME_STOP:Int        = 53
	Const MATCH_HALF_EXTRA_TIME_STOP:Int   = 54
	Const MATCH_FULL_EXTRA_TIME_STOP:Int   = 55
	Const MATCH_BENCH_ENTER:Int            = 56
	Const MATCH_BENCH_SUBSTITUTIONS:Int    = 57
	Const MATCH_BENCH_FORMATION:Int        = 58
	Const MATCH_BENCH_TACTICS:Int          = 59
	Const MATCH_BENCH_EXIT:Int             = 60
	Const MATCH_REPLAY:Int                 = 61
	Const MATCH_PAUSE:Int                  = 62
	Const MATCH_END_POSITIONS:Int          = 63
	Const MATCH_END:Int                    = 64
	Const MATCH_SHOW_HIGHLIGHT:Int         = 65
	Const MENU_SELECT_TACTICS_TO_EDIT:Int  = 66
	Const MENU_EDIT_TACTICS:Int            = 67
	Const MENU_TACTICS_SAVE_WARNING:Int    = 68
	Const MENU_SAVE_TACTICS:Int            = 69
	Const MENU_TACTICS_ABORT_WARNING:Int   = 70
	Const MENU_TACTICS_IMPORT:Int          = 71
	Const MENU_TACTICS_LOAD:Int            = 72
	Const MENU_TRAINING_SETTINGS:Int       = 73
	Const TRAINING_LOADING:Int             = 74
	Const TRAINING_MAIN:Int                = 75
	Const QUIT:Int                         = 76
End Type
