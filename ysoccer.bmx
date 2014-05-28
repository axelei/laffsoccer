Rem

YSoccer
Copyright (C) 2014
by Massimo Modica, Daniele Giannarini, Marco Modica

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

EndRem

SuperStrict

Import "t_game.bmx"

game = New t_game
game_action_queue = New t_game_action_queue

game_action_queue.push(AT_NEW_FOREGROUND, GM.MENU_INTRO)
game_action_queue.push(AT_FADE_IN)
game.loop()
